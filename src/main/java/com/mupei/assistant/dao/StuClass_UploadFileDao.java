package com.mupei.assistant.dao;

import com.mupei.assistant.model.StuClass_UploadFile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface StuClass_UploadFileDao extends CrudRepository<StuClass_UploadFile, Long> {

    @Query("SELECT COUNT(sf.id) FROM StuClass_UploadFile sf WHERE sf.stuClass.id = ?1")
    boolean existsByClassId(Long classId);

    @Query("DELETE FROM StuClass_UploadFile sf WHERE sf.stuClass.id = ?1")
    void deleteByClassId(Long classId);
}
