package com.mupei.assistant.dao;

import com.mupei.assistant.model.StuClass_Student;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface StuClass_StudentDao extends CrudRepository<StuClass_Student, Long> {
    void deleteByClassId(Long id);

    boolean existsByClassId(Long classId);

    ArrayList<StuClass_Student> findByClassId(Long classId);

    ArrayList<StuClass_Student> findByStuId(Long stuId);

    Optional<StuClass_Student> findByClassIdAndStuId(Long classId, Long stuId);
}
