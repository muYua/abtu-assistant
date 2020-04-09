package com.mupei.assistant.dao;

import com.mupei.assistant.model.StuClass_UploadFile;
import org.springframework.data.repository.CrudRepository;

public interface StuClass_UploadFileDao extends CrudRepository<StuClass_UploadFile, Long> {

    boolean existsByClassId(Long classId);

    void deleteByClassId(Long classId);
}
