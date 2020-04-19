package com.mupei.assistant.dao;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mupei.assistant.model.Student_Course;

public interface Student_CourseDao extends CrudRepository<Student_Course, Long> {
    @Query("FROM Student_Course sc WHERE sc.course.id = ?1")
    ArrayList<Student_Course> findByCourseId(Long courseId);

    @Query("FROM Student_Course sc WHERE sc.course.id = ?1 AND sc.student.id = ?2")
	Student_Course findByCourseIdAndStuId(Long courseId, Long stuId);

    @Query("FROM Student_Course sc WHERE sc.student.id = ?1")
	ArrayList<Student_Course> findByStuId(Long stuId);
}
