package com.mupei.assistant.dao;

import java.util.ArrayList;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mupei.assistant.model.StuClass;

public interface StuClassDao extends CrudRepository<StuClass, Long> {

	@Query("FROM StuClass c WHERE c.course.id = ?1")
	ArrayList<StuClass> findByCourseId(Long courseId);

	@Query("FROM StuClass c WHERE c.course.id = ?1")
	ArrayList<StuClass> findByCourseId(Long courseId, Pageable pageable);

	@Query("SELECT COUNT(c.id) FROM StuClass c WHERE c.course.id = ?1")
	Long countByCourseId(Long courseId, Sort sort);

	@Query("SELECT c FROM StuClass c WHERE c.course.id = ?1 AND c.id IN" +
			" (SELECT cs.stuClass.id FROM StuClass_Student cs WHERE cs.student.id = ?2)")
    StuClass findByCourseIdAndStuId(Long courseId, Long stuId);
}
