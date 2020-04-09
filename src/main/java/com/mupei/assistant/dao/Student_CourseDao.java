package com.mupei.assistant.dao;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;

import com.mupei.assistant.model.Student_Course;

public interface Student_CourseDao extends CrudRepository<Student_Course, Long> {

	Student_Course findByCourseIdAndStuId(Long courseId, Long id);

	ArrayList<Student_Course> findByCourseId(Long courseId);

	ArrayList<Student_Course> findByStuId(Long stuId);
}
