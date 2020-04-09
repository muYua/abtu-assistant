package com.mupei.assistant.dao;

import java.util.ArrayList;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import com.mupei.assistant.model.Course;

public interface CourseDao extends CrudRepository<Course, Long> {

	ArrayList<Course> findByTeacherId(Long teacherId, Pageable pageable);

    ArrayList<Course> findByTeacherId(Long teacherId);

    Long countByTeacherId(Long teacherId, Sort sort);
}
