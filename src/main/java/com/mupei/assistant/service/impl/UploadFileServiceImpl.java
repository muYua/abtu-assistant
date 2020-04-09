package com.mupei.assistant.service.impl;

import com.mupei.assistant.dao.*;
import com.mupei.assistant.model.*;
import com.mupei.assistant.utils.JsonUtil;
import com.mupei.assistant.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mupei.assistant.service.UploadFileService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Slf4j
@Service
public class
UploadFileServiceImpl implements UploadFileService {
    @Autowired
    private UploadFileDao uploadFileDao;
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private StuClass_UploadFileDao stuClass_uploadFileDao;
    @Autowired
    private StuClass_StudentDao stuClass_studentDao;
    @Autowired
    private TimeUtil timeUtil;
    @Autowired
    private JsonUtil jsonUtil;
    @Value("${file.uploadFolder}")
    private String uploadFolder;
    @Value("${domainName}")
    private String DomainName;

    @Override
    public void uploadFiles(MultipartFile[] files, Long roleId, Long courseId, Long classId, String sort) {
        //处理文件
        for(MultipartFile file : files) {
            String fileName = file.getOriginalFilename();// 获取到上传文件的名字
//            String filePath = request.getSession().getServletContext().getRealPath("upload") + File.separator + "file";//存储在"项目名/upload/file"
            String sortName;
            switch (sort) {
                case "l":
                    sortName = "TeachingFiles";
                    break;
                case "ht":
                    sortName = "HomeworkFiles"+ File.separator + "teacher";
                    break;
                case "hs":
                    sortName = "HomeworkFiles"+ File.separator + "student";
                    break;
                case "i":
                    sortName = "ImageFiles";
                    break;
                case "s":
                    sortName = "SignInFiles";
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + sort);
            }
            String filePath = uploadFolder + File.separator + sortName + File.separator +
                    courseId + File.separator + classId; //存储上传文件的路径
            File dir = new File(filePath, Objects.requireNonNull(fileName));
            if (!dir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                dir.mkdirs();
            }
            //存储到本地
            try {
                file.transferTo(dir);
                String directory = dir.getCanonicalPath();//获取文档路径（完全路径）
                log.debug("【uploadFile/uploadFiles】文件上传成功，路径directory：{}", directory);
            } catch (IllegalStateException | IOException e) {
                log.error("【uploadFile/uploadFiles】上传文件时，本地存储失败！");
                e.printStackTrace();
                return;
            }
            Long fileSize = file.getSize();//文件大小
            String currentTime = timeUtil.getCurrentTime();
            //记录文件(实例化uploadFile)
            UploadFile uploadFile = new UploadFile(fileName, fileSize, filePath, sort, currentTime, roleId);
            UploadFile save = uploadFileDao.save(uploadFile);
            //stuClass <-> uploadFile
            StuClass_UploadFile stuClass_uploadFile = new StuClass_UploadFile(classId, save.getId(), currentTime);
            stuClass_uploadFileDao.save(stuClass_uploadFile);
        }

    }

    @Override
    public ArrayList<Object> getSignInInfoByDate(Long courseId, Long classId, String date, String sort, Integer pageNo,
                                                 Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.Direction.ASC, "id");//分页所需参数
        //course<->class => class<->file => files
        ArrayList<UploadFile> files = uploadFileDao.findUploadFiles(courseId, classId, date, sort, pageable);
        ArrayList<Object> list = new ArrayList<>();
        for(UploadFile file : files) {
            //file<->roleId<->student => stuNumber
            String stuNumber = studentDao.findById(file.getId()).map(Student::getStuNumber).orElse(null);
            //student<->class => classNickname
            String classNickname = stuClass_studentDao.findByClassIdAndStuId(classId, file.getId())
                    .map(StuClass_Student::getClassNickname).orElse(null);
            //fileUrl
            String fileUrl = DomainName + "/static/SignInFiles/" + courseId + "/" + classId + "/" +file.getFileName();
            try { //存入数据(通过数据类型转换完成)
                //file => json => map
                @SuppressWarnings("unchecked")
                HashMap<String, Object> map = jsonUtil.parse(jsonUtil.stringify(file), HashMap.class);
                //map.put
                map.put("stuNumber", stuNumber);
				map.put("classNickname", classNickname);
				map.put("fileUrl", fileUrl);
                //map => json => object => list.add(object)
                list.add(jsonUtil.parse(jsonUtil.stringify(map), Object.class));
            } catch (IOException e) {
                e.printStackTrace();
                log.error("【uploadFile/getSignInInfoByDate】数据转换出错！");
            }
        }
        log.debug("【uploadFile/getSignInInfoByDate】获取签到文件信息，课程：{}，文件类型：{}，日期：{}, 信息：{}", courseId, sort, date, list);
        return list;
    }

    @Override
    public Long getFilesCountByDate(Long courseId, Long classId, String date, String sort) {
        return uploadFileDao.countUploadFiles(courseId, classId, date, sort);
    }

    @Override
    public ArrayList<UploadFile> getFilesByDate(Long courseId, Long classId, String date, String sort, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.Direction.ASC, "id");//分页所需参数
        //course<->class => class<->file => files
        return uploadFileDao.findUploadFiles(courseId, classId, date, sort, pageable);
    }

    @Override
    public ArrayList<Object> getHomeworkFilesByDate(Long courseId, Long classId, String date, String sort, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.Direction.ASC, "id");//分页所需参数
        ArrayList<UploadFile> files = uploadFileDao.findUploadFiles(courseId, classId, date, sort, pageable);
        ArrayList<Object> list = new ArrayList<>();
        files.forEach(file -> {
            String stuNumber = null, classNickname = null, roleName = null, sortName = null;
            if ("hs".equals(sort)) {
                sortName = "HomeworkFiles/student";
                //file<-> roleId -> student => stuNumber
                stuNumber = studentDao.findById(file.getRoleId()).map(Student::getStuNumber).orElse(null);
                //student<->class => classNickname
                classNickname = stuClass_studentDao.findByClassIdAndStuId(classId, file.getId())
                        .map(StuClass_Student::getClassNickname).orElse(null);
            } else if ("ht".equals(sort)) {
                sortName = "HomeworkFiles/teacher";
                //file -> role => roleName
                roleName = roleDao.findById(file.getRoleId()).map(Role::getName).orElse(null);
            }
            //fileUrl
            String fileUrl = DomainName + "/static/" + sortName + "/" + courseId + "/" + classId + "/" +file.getFileName();
            try { //存入数据(通过数据类型转换完成)
                //file => json => map
                @SuppressWarnings("unchecked")
                HashMap<String, Object> map = jsonUtil.parse(jsonUtil.stringify(file), HashMap.class);
                //map.put
                if(stuNumber != null)  map.put("stuNumber", stuNumber);
                if(classNickname != null)  map.put("classNickname", classNickname);
                if(roleName != null)  map.put("roleName", roleName);
                map.put("fileUrl", fileUrl);
                //map => json => object => list.add(object)
                list.add(jsonUtil.parse(jsonUtil.stringify(map), Object.class));
            } catch (IOException e) {
                e.printStackTrace();
                log.error("【uploadFile/getHomeworkFilesByDate】数据转换出错！");
            }
        });
        log.debug("【uploadFile/getHomeworkFilesByDate】获取作业文件，文件类型：{}，课程：{}，日期：{}, 信息：{}", sort, courseId, date, list);
        return list;
    }
}
