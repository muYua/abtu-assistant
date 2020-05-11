package com.mupei.assistant.controller;

import com.mupei.assistant.model.StuClass;
import com.mupei.assistant.model.Student;
import com.mupei.assistant.model.StudentInfo;
import com.mupei.assistant.service.StudentService;
import com.mupei.assistant.vo.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RequestMapping("/student")
@RestController
public class StudentController {
    @Autowired
    private StudentService studentService;

    @GetMapping("/getStudentInfos")
    public Json getStudentInfos(@RequestParam Long classId) {
        ArrayList<Student> students = studentService.getStudentInfos(classId);
        boolean success = students != null;
        return new Json(success, success?0:-1, students, 0L, success?null:"获取学生数据失败！");
    }

    @GetMapping("/getStudentInfo")
    public Json getStudentInfo(@RequestParam Long stuId) {
        StudentInfo studentInfo = studentService.getStudentInfo(stuId);
        return new Json(studentInfo != null, studentInfo);
    }

    @PutMapping("/updateStudentInfo")
    public Json updateStudentInfo(Student student) {
        return new Json(studentService.updateStudentInfo(student));
    }
}
