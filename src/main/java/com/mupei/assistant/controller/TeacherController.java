package com.mupei.assistant.controller;

import com.mupei.assistant.model.Teacher;
import com.mupei.assistant.model.TeacherInfo;
import com.mupei.assistant.vo.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mupei.assistant.service.TeacherService;

@RequestMapping("/teacher")
@RestController
public class TeacherController {
	@Autowired
	private TeacherService teacherService;

	@GetMapping("/getTeacherInfo")
	public Json getTeacherInfo(@RequestParam Long id) {
		TeacherInfo teacherInfo = teacherService.getTeacherInfo(id);
		return new Json(teacherInfo != null, teacherInfo);
	}

	@PutMapping("/updateTeacherInfo")
	public Json updateTeacherInfo(Teacher teacher) {
		return new Json(teacherService.updateTeacherInfo(teacher));
	}

}
