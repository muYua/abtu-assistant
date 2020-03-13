package com.mupei.assistant.dao;

import org.springframework.data.repository.CrudRepository;

import com.mupei.assistant.model.Course;

public interface CourseDao extends CrudRepository<Course, Long> {
	
}
