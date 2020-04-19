package com.mupei.assistant.dao;

import java.util.ArrayList;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mupei.assistant.model.Course;

public interface CourseDao extends CrudRepository<Course, Long> {
    @Query("FROM Course c WHERE c.teacher.id = ?1")
	ArrayList<Course> findByTeacherId(Long teacherId, Pageable pageable);

	@Query("FROM Course c WHERE c.teacher.id = ?1")
    ArrayList<Course> findByTeacherId(Long teacherId);

	@Query(value = "SELECT COUNT(c.id) FROM Course c WHERE c.teacher.id = ?1")
    Long countByTeacherId(Long teacherId, Sort sort);
}
