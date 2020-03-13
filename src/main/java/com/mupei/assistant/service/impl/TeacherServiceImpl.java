package com.mupei.assistant.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mupei.assistant.dao.CourseDao;
import com.mupei.assistant.dao.Course_MessageDao;
import com.mupei.assistant.dao.Course_UploadfileDao;
import com.mupei.assistant.dao.MessageDao;
import com.mupei.assistant.dao.TeacherDao;
import com.mupei.assistant.dao.UploadFileDao;
import com.mupei.assistant.model.Course;
import com.mupei.assistant.model.Course_Message;
import com.mupei.assistant.model.Course_Uploadfile;
import com.mupei.assistant.model.Message;
import com.mupei.assistant.model.UploadFile;
import com.mupei.assistant.service.TeacherService;
import com.mupei.assistant.utils.TimeUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TeacherServiceImpl implements TeacherService {
	@Autowired
	private MessageDao messageDao;
	@Autowired
	private CourseDao courseDao;
	@Autowired
	private TeacherDao teacherDao;
	@Autowired
	private UploadFileDao uploadFileDao;
	@Autowired
	private Course_MessageDao course_MessageDao;
	@Autowired
	private Course_UploadfileDao course_UploadfileDao;
	@Autowired
	private TimeUtil timeUtil;
	@Value("${file.uploadFolder}")
	private String uploadFolder;

	@Override
	@Transactional
	public void sendMessage(Long courseId, Message message, String sort) {
		message.setSort(sort);// 类别
		String currentTime = timeUtil.getCurrentTime();
		message.setCreateTime(currentTime);
		// 得到教师ID并存入消息表
		Optional<Course> course = courseDao.findById(courseId);
		Optional<Long> map = course.map(Course::getTeacherId);//获取course的teacherId属性
		if (map.isPresent()) {		
			message.setTeacherId(map.get());
		} else {
			log.error("【sendMessage】查询数据出现异常，课程{}的教师ID为空！", courseId);
			return;
		}
		// 数据序列化
		Message save = messageDao.save(message);
		// 关系维护
		Course_Message course_Message = new Course_Message(courseId, save.getId(), currentTime.substring(0, 10));
		course_MessageDao.save(course_Message);
	}

		
	@Override
	public void uploadFiles(MultipartFile[] files, Long courseId, String sort, String relativePath) {
		//处理文件
        for(MultipartFile file : files){
            String fileName = file.getOriginalFilename();// 获取到上传文件的名字
//            String filePath = request.getSession().getServletContext().getRealPath("upload") + File.separator + "file";//存储在"项目名/upload/file"
            String filePath = uploadFolder; //存储上传文件的路径    
            
            File dir = new File(filePath, Objects.requireNonNull(fileName));
            if (!dir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                dir.mkdirs();
            }
            //存储到本地
            try {
				file.transferTo(dir);
				String directory = dir.getCanonicalPath();//获取文档路径（完全路径）
				log.debug("【uploadTeachingFiles】文件上传成功，路径directory：{}", directory);
			} catch (IllegalStateException | IOException e) {
				log.error("【uploadTeachingFiles】上传文件时，本地存储失败！");
				e.printStackTrace();
				return;
			}
            Long fileSize = file.getSize();//文件大小		
			String currentTime = timeUtil.getCurrentTime();
			//获取教师ID
			Optional<Course> course = courseDao.findById(courseId);
			Optional<Long> map = course.map(Course::getTeacherId);//获取course的teacherId属性
			Long teacherId;
			if (map.isPresent()) {
				teacherId = map.get(); 
			} else {
				log.error("【uploadTeachingFiles】查询数据出现异常，课程{}的教师ID为空！", courseId);
				return;
			}
			//记录文件
			UploadFile uploadFile = new UploadFile(fileName, fileSize, filePath, sort, currentTime, teacherId);
			UploadFile save = uploadFileDao.save(uploadFile);
			//关系维护
			Course_Uploadfile course_Uploadfile = new Course_Uploadfile(courseId, save.getId(), currentTime.substring(0, 10));
			course_UploadfileDao.save(course_Uploadfile);
        }		
	}


	@Override
	public ArrayList<UploadFile> getFiles(Long courseId, String createDate, String sort, Integer pageNo,
			Integer pageSize) {	
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Direction.ASC, "id");//分页所需参数
		return teacherDao.findUploadFilesWithCourse(courseId, createDate, sort, pageable);
	}
	
	@Override
	public Long getFilesCount(Long courseId, String createDate, String sort) {
		return teacherDao.countUploadFilesWithCourse(courseId, createDate, sort);
	}
}
