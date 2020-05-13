package com.mupei.assistant.dao;

import com.mupei.assistant.model.UsualPerformance;
import com.mupei.assistant.model.UsualPerformanceInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface UsualPerformanceDao extends CrudRepository<UsualPerformance, Long> {

    @Query("FROM UsualPerformance u WHERE u.course.id = ?1 AND u.student.id = ?2 ORDER BY u.date ASC")
    ArrayList<UsualPerformance> findByCourseIdAndStuId(Long courseId, Long stuId);

    @Query("SELECT u.student.id as stuId, u.student.stuNumber as stuNumber, u.student.name as name, u.course.id as courseId, COUNT(u.score) as count, AVG(u.score) as avg FROM UsualPerformance u WHERE u.course.id = ?1 AND u.student.id IN " +
            "(SELECT cs.student.id FROM StuClass_Student cs WHERE cs.stuClass.id = ?2) GROUP BY u.student.id ORDER BY u.student.stuNumber ASC")
    ArrayList<UsualPerformanceInfo> findByCourseIdAndClassId(Long courseId, Long classId);

    @Modifying
    @Query(value = "INSERT INTO usualperformance(course_id, student_id, date, score) VALUES (?1, ?2, ?3, ?4)", nativeQuery = true)
    Integer insert(Long courseId, Long stuId, String date, Integer score);

    @Modifying
    @Query("update UsualPerformance u set "
            + "u.date = CASE WHEN :#{#usualPerformance.date} IS NULL THEN u.date ELSE :#{#usualPerformance.date} END ,"
            + "u.score = CASE WHEN :#{#usualPerformance.score} IS NULL THEN u.score ELSE :#{#usualPerformance.score} END ,"
            + "u.course.id = CASE WHEN :#{#usualPerformance.course.id} IS NULL THEN u.course.id ELSE :#{#usualPerformance.course.id} END ,"
            + "u.student.id = CASE WHEN :#{#usualPerformance.student.id} IS NULL THEN u.student.id ELSE :#{#usualPerformance.student.id} END "
            + "where u.id = :#{#usualPerformance.id}")
    Integer update(UsualPerformance usualPerformance);

    @Modifying
    @Query("update UsualPerformance u set u.score = ?2 where u.id = ?1")
    Integer update(Long id, Integer score);
}
