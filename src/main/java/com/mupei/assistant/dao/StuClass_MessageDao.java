package com.mupei.assistant.dao;

import com.mupei.assistant.model.StuClass_Message;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface StuClass_MessageDao extends CrudRepository<StuClass_Message, Long> {

    @Modifying
    @Query("DELETE FROM StuClass_Message sm WHERE sm.stuClass.id = ?1")
    void deleteByClassId(Long classId);

    @Query("FROM StuClass_Message sm WHERE sm.stuClass.id = ?1")
    ArrayList<StuClass_Message> findByClassId(Long classId);

    @Modifying
    @Query("DELETE FROM StuClass_Message sm WHERE sm.stuClass.id IN (SELECT c.id FROM StuClass c WHERE c.course.id = ?1)")
    void deleteByCourseId(Long courseId);
}
