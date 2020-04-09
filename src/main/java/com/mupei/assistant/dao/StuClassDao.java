package com.mupei.assistant.dao;

import java.util.ArrayList;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import com.mupei.assistant.model.StuClass;

public interface StuClassDao extends CrudRepository<StuClass, Long> {

	ArrayList<StuClass> findByCourseId(Long courseId);

	ArrayList<StuClass> findByCourseId(Long courseId, Pageable pageable);

	Long countByCourseId(Long courseId, Sort sort);

    void deleteByCourseId(Long courseId);
}
