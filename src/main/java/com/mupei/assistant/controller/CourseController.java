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
        boolean success = course !=null;
        return new Json(success, success?0:-1, course, count, success?null:"获取课程数据失败!");
    }

    @GetMapping("/getCourseInfoOfStudent")
    public Json getCourseInfoOfStudent(@RequestParam Long stuId) {
        ArrayList<Object> courses = courseService.getCourseInfoOfStudent(stuId);
        boolean success = courses !=null;
        return new Json(success, success?0:-1, courses, 0L, success?null:"获取课程数据失败!");
    }

    //map(courseId、courseName)
    @PostMapping("/insertCourse")
    public Json insertCourse(@RequestParam Long teacherId, @RequestParam String courseName) {
        HashMap<String, Object> map = courseService.insertCourse(teacherId, courseName);
        return new Json(true, map);
    }

    @PutMapping("/updateCourse/{courseId}")
    public Json updateCourse(@PathVariable Long courseId, @RequestParam Long teacherId, @RequestParam String courseName) {
        courseService.updateCourse(courseId, teacherId, courseName);
        return new Json(true);
    }

    //map(courseName)
    @PostMapping("/addCourseOfStudent")
    public Json addCourseOfStudent(@RequestParam Long courseId, @RequestParam Long classId, @RequestParam Long stuId) {
        HashMap<String, Object> map = courseService.addCourseOfStudent(courseId, classId, stuId);
        if(map.get("msg") != null) return new Json(false, map.get("msg").toString());
        return new Json(true, map);
    }

    @DeleteMapping("/deleteCourse")
    public Json deleteCourse(@RequestParam Long courseId) {
        courseService.deleteCourse(courseId);
        return new Json(true);
    }

    @DeleteMapping("/deleteCourseOfStudent/{stuId}/{courseId}")
    public Json deleteCourseOfStudent(@PathVariable Long stuId, @PathVariable Long courseId) {
        courseService.deleteCourseOfStudent(stuId, courseId);
        return new Json(true);
    };

}
