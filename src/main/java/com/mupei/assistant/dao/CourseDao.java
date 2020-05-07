package com.mupei.assistant.dao;

import java.util.ArrayList;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mupei.assistant.model.Course;
import org.springframework.transaction.annotation.Transactional;

public interface CourseDao extends CrudRepository<Course, Long> {
    @Query("FROM Course c WHERE c.teacher.id = ?1")
	ArrayList<Course> findByTeacherId(Long teacherId, Pageable pageable);

	@Query("FROM Course c WHERE c.teacher.id = ?1")
    ArrayList<Course> findByTeacherId(Long teacherId);

	@Query("SELECT COUNT(c.id) FROM Course c WHERE c.teacher.id = ?1")
    Long countByTeacherId(Long teacherId, Sort sort);

    @Modifying
    @Transactional
    @Query("update Course c set "
            + "c.courseName = CASE WHEN :#{#course.courseName} IS NULL THEN c.courseName ELSE :#{#course.courseName} END ,"
            + "c.teacher.id = CASE WHEN :#{#course.teacher.id} IS NULL THEN c.teacher.id ELSE :#{#course.teacher.id} END "
            + "where c.id = :#{#course.id}")
    Integer update(Course course);
}
