package com.mupei.assistant.controller;

import com.mupei.assistant.model.Student;
import com.mupei.assistant.service.StudentService;
import com.mupei.assistant.vo.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RequestMapping("/student")
@RestController
public class StudentController {
    @Autowired
    private StudentService studentService;

    @GetMapping("/getStudentInfos")
    public Json getStudentInfos(@RequestParam Long classId) {
        ArrayList<Student> students = studentService.getStudentInfos(classId);
        boolean success = true;
        int code = 0;
        String msg = null;
        if (students == null) {
            success = false;
            code = -1;
            msg = "获取学生数据失败！";
        }
        return new Json(success, code, students, 0L, msg);
    }
}
