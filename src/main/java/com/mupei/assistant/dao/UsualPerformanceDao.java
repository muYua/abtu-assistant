package com.mupei.assistant.dao;

import com.mupei.assistant.model.UsualPerformance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface UsualPerformanceDao extends CrudRepository<UsualPerformance, Long> {

    @Query("FROM UsualPerformance u WHERE u.course.id = ?1 AND u.student.id = ?2")
    ArrayList<UsualPerformance> findByCourseIdAndStuId(Long courseId, Long stuId);
}
