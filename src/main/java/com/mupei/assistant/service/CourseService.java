package com.mupei.assistant.service;

import com.mupei.assistant.model.Course;

import java.util.ArrayList;
import java.util.HashMap;

public interface CourseService {

    ArrayList<Course> getCourseList(Long teacherId);

    HashMap<String, Object> insertCourse(Long teacherId, String courseName);

    ArrayList<Object> getCourseByPage(Long teacherId, Integer pageNo, Integer pageSize);

    Long getCourseCount(Long teacherId);

    void deleteCourse(Long courseId);

    ArrayList<Course> getCourseListByStuId(Long stuId);

    HashMap<String, Object> addCourseOfStudent(Long courseId, Long classId, Long stuId);

    ArrayList<Object> getCourseInfoOfStudent(Long stuId);

    void deleteCourseOfStudent(Long stuId, Long courseId);
}
