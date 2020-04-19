package com.mupei.assistant.dao;

import com.mupei.assistant.model.StuClass_Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface StuClass_MessageDao extends CrudRepository<StuClass_Message, Long> {

    @Query("DELETE FROM StuClass_Message sm WHERE sm.stuClass.id = ?1")
    void deleteByClassId(Long classId);

    @Query("SELECT COUNT(sm.id) FROM StuClass_Message sm WHERE sm.stuClass.id = ?1")
    boolean existsByClassId(Long classId);

    @Query("FROM StuClass_Message sm WHERE sm.stuClass.id = ?1")
    ArrayList<StuClass_Message> findByClassId(Long classId);
}
