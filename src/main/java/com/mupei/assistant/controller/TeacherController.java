package com.mupei.assistant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mupei.assistant.service.TeacherService;

@RequestMapping("/teacher")
@RestController // @Controller + @ResponseBody
public class TeacherController {
	@Autowired
	private TeacherService teacherService;

}
