package com.mupei.assistant.dao;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mupei.assistant.model.UploadFile;

public interface UploadFileDao extends CrudRepository<UploadFile,Long>{
	//分页查询文件
	//course => class => class<->file => files 班级文件
	@Query("SELECT COUNT(f) FROM UploadFile AS f WHERE f.sort = ?4 AND f.id IN "
			+ "(SELECT cf.uploadFile.id FROM StuClass_UploadFile AS cf WHERE EXISTS "
			+ "(SELECT c.id FROM StuClass AS c WHERE c.course.id = ?1 AND c.id = cf.stuClass.id) "
			+ "AND cf.stuClass.id = ?2 AND cf.createDate like ?3%) ")
	Long countUploadFiles(Long courseId, Long classId, String createDate, String sort);

	@Query("SELECT f FROM UploadFile AS f WHERE f.sort = ?4 AND f.id IN "
			+ "(SELECT cf.uploadFile.id FROM StuClass_UploadFile AS cf WHERE EXISTS "
			+ "(SELECT c.id FROM StuClass AS c WHERE c.course.id = ?1 AND c.id = cf.stuClass.id) "
			+ "AND cf.stuClass.id = ?2 AND cf.createDate like ?3%) ")
	ArrayList<UploadFile> findUploadFiles(Long courseId, Long classId, String createDate, String sort, Pageable pageable);

	Boolean existsByMd5(String checkMd5);
}
