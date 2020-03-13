package com.mupei.assistant.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mupei.assistant.model.Message;
import com.mupei.assistant.model.UploadFile;
import com.mupei.assistant.service.TeacherService;
import com.mupei.assistant.vo.Json;

@RequestMapping("/teacher")
@RestController // @Controller + @ResponseBody
public class TeacherController {

	@Autowired
	private TeacherService teacherService;

	/**
	 * 查看课堂考勤记录
	 * 
	 * @param courseId 课程ID
	 * @param date     考勤日期（yyyy-MM-dd）
	 * @param pageNo   当前页码数
	 * @param pageSize 每页显示数据条数
	 * @return
	 */
	@GetMapping("/checkSignIn")
	public Json checkSignIn(@RequestParam Long courseId, @RequestParam String date, @RequestParam Integer pageNo,
			@RequestParam Integer pageSize) {
		ArrayList<UploadFile> files = teacherService.getFiles(courseId, date, "s", pageNo, pageSize);
		Long count = teacherService.getFilesCount(courseId, date, "s");
		Json json = new Json(true, 0, files, count, "考勤记录");
		return json;
	}

	/**
	 * 发送课后作业
	 * 
	 * @param courseId 课程ID（确定具体班级，查看课后作业通过点开课程查看）
	 * @param message  作业属性，teacherId、content、createTime、sort(h)
	 * @return
	 */
	@PostMapping("/sendHomework")
	public Json sendHomework(@RequestParam Long courseId, Message message) {
		teacherService.sendMessage(courseId, message, "h");
		Json json = new Json(true);
		return json;
	}

	/**
	 * 上传作业文件
	 * 
	 * @param file     文件
	 * @param courseId 课程ID
	 * @return
	 */
	@PostMapping("/uploadHomework")
	public Json uploadHomework(@RequestParam MultipartFile[] files, @RequestParam Long courseId) {
		teacherService.uploadFiles(files, courseId, "h", "homework");
		Json json = new Json(true);
		return json;
	}

	/**
	 * 获取作业文件信息
	 * 
	 * @param courseId 课程ID
	 * @param date     日期
	 * @param pageNo   当前页码数
	 * @param pageSize 每页显示数据条数
	 * @return
	 */
	@GetMapping("/getHomework")
	public Json getHomework(@RequestParam Long courseId, @RequestParam String date, @RequestParam Integer pageNo,
			@RequestParam Integer pageSize) {
		ArrayList<UploadFile> files = teacherService.getFiles(courseId, date, "h", pageNo, pageSize);
		Long count = teacherService.getFilesCount(courseId, date, "h");
		Json json = new Json(true, 0, files, count, "作业文件信息");
		return json;
	}

	/**
	 * 获取课堂文件信息
	 * 
	 * @param courseId 课程ID
	 * @param date     日期
	 * @param pageNo   当前页码数
	 * @param pageSize 每页显示数据条数
	 * @return
	 */
	@GetMapping("/getTeachingFiles")
	public Json getTeachingFiles(@RequestParam Long courseId, @RequestParam String date, @RequestParam Integer pageNo,
			@RequestParam Integer pageSize) {
		ArrayList<UploadFile> files = teacherService.getFiles(courseId, date, "l", pageNo, pageSize);
		Long count = teacherService.getFilesCount(courseId, date, "l");
		Json json = new Json(true, 0, files, count, "课堂文件信息");
		return json;
	}

	/* 批阅作业文件 */
//	checkHomework

	/* 下载作业文件 */
//	downloadHomework

	/**
	 * 上传课堂文件
	 * 
	 * @param file     文件
	 * @param courseId 课程ID
	 * @return
	 */
	@PostMapping("/sendTeachingFiles")
	public Json sendTeachingFiles(@RequestParam MultipartFile[] files, @RequestParam Long courseId) {
		teacherService.uploadFiles(files, courseId, "l", "TeachingFiles");
		Json json = new Json(true);
		return json;
	}

	/**
	 * 编辑平时成绩
	 * 
	 * @param courseId  课程ID
	 * @param studentId 学生ID
	 * @param date      日期
	 * @return
	 */
	@PutMapping("/eidtUsualPerformance")
	public Json updateUsualPerformance(@RequestParam Long courseId, @RequestParam Long studentId,
			@RequestParam String date) {
//		teacherService.updateUsualPerformance( courseId, "l", "TeachingFiles");
		Json json = new Json(true);
		return json;
	}

	/**
	 * 发送通知
	 * 
	 * @param courseId
	 * @param message  通知属性，teacherId、content、createTime、sort(m)
	 * @return
	 */
	@PostMapping("/sendMessage")
	public Json sendMessage(@RequestParam Long courseId, Message message) {
		teacherService.sendMessage(courseId, message, "m");
		Json json = new Json();
		json.setSuccess(true);
		return json;
	}
}
