package com.mupei.assistant.dao;

import com.mupei.assistant.model.StuClass_Message;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface StuClass_MessageDao extends CrudRepository<StuClass_Message, Long> {

    void deleteByClassId(Long classId);

    boolean existsByClassId(Long classId);

    ArrayList<StuClass_Message> findByClassId(Long classId);
}
