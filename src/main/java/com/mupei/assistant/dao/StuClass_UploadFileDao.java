package com.mupei.assistant.dao;

import com.mupei.assistant.model.StuClass_UploadFile;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface StuClass_UploadFileDao extends CrudRepository<StuClass_UploadFile, Long> {

    @Modifying
    @Query("DELETE FROM StuClass_UploadFile sf WHERE sf.stuClass.id = ?1")
    void deleteByClassId(Long classId);

    @Modifying
    @Query("DELETE FROM StuClass_UploadFile sf WHERE sf.uploadFile.id = ?1")
    void deleteByFileId(Long fileId);
}
