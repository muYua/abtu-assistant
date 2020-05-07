package com.mupei.assistant.dao;

import com.mupei.assistant.model.StuClass_Student;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface StuClass_StudentDao extends CrudRepository<StuClass_Student, Long> {
    @Modifying
    @Query("DELETE FROM StuClass_Student ss WHERE ss.stuClass.id = ?1")
    void deleteByClassId(Long classId);

    @Query("FROM StuClass_Student ss WHERE ss.stuClass.id = ?1")
    ArrayList<StuClass_Student> findByClassId(Long classId);

    @Query("FROM StuClass_Student ss WHERE ss.student.id = ?1")
    ArrayList<StuClass_Student> findByStuId(Long stuId);

    @Query("FROM StuClass_Student ss WHERE ss.stuClass.id = ?1 AND ss.student.id =?2")
    Optional<StuClass_Student> findByClassIdAndStuId(Long classId, Long stuId);

    @Modifying
    @Query("DELETE FROM StuClass_Student ss WHERE ss.student.id = ?1")
    void deleteByStuId(Long stuId);
}
