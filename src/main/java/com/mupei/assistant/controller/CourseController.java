package com.mupei.assistant.controller;

import com.mupei.assistant.model.Course;
import com.mupei.assistant.service.CourseService;
import com.mupei.assistant.vo.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;

@RequestMapping("/course")
@RestController //@Controller + @ResponseBody
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping("/getCourseList")
    public Json getCourseList(@RequestParam Long teacherId) {
        ArrayList<Course> courses = courseService.getCourseList(teacherId);
        return new Json(true, courses);
    }

    @GetMapping("/getCourseListByStuId")
    public Json getCourseListByStuId(@RequestParam Long stuId) {
        ArrayList<Course> courses = courseService.getCourseListByStuId(stuId);
        return new Json(true, courses);
    }

    @GetMapping("/getCourseByPage")
    public Json getCourseByPage(@RequestParam Long teacherId, @RequestParam Integer pageNo,
                                @RequestParam Integer pageSize) {
        ArrayList<Object> course = courseService.getCourseByPage(teacherId, pageNo, pageSize);
        Long count = courseService.getCourseCount(teacherId);
        boolean success = true;
        int code = 0;
        String msg = "";
        if (course == null) {
            success = false;
            code = -1;
            msg = "获取课程数据失败！";
        }
        return new Json(success, code, course, count, msg);
    }

    @GetMapping("/getCourseInfoOfStudent")
    public Json getCourseInfoOfStudent(@RequestParam Long stuId) {
        ArrayList<Object> courses = courseService.getCourseInfoOfStudent(stuId);
        boolean success = true;
        int code = 0;
        String msg = "";
        if (courses == null) {
            success = false;
            code = -1;
            msg = "获取课程数据失败！";
        }
        return new Json(success, code, courses, 0L, msg);
    }

    //map(courseId、courseName)
    @PostMapping("/insertCourse")
    public Json insertCourse(@RequestParam Long teacherId, @RequestParam String courseName) {
        HashMap<String, Object> map = courseService.insertCourse(teacherId, courseName);
        return new Json(true, map);
    }

    //map(courseName)
    @PostMapping("/addCourseOfStudent")
    public Json addCourseOfStudent(@RequestParam Long courseId, @RequestParam Long classId, @RequestParam Long stuId) {
        HashMap<String, Object> map = courseService.addCourseOfStudent(courseId, classId, stuId);
        return new Json(true, map);
    }

    @DeleteMapping("/deleteCourse")
    public Json deleteCourse(@RequestParam Long courseId) {
        courseService.deleteCourse(courseId);
        return new Json(true);
    }


}
