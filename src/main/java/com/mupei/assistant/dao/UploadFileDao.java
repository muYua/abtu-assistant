package com.mupei.assistant.dao;

import java.util.ArrayList;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mupei.assistant.model.UploadFile;

public interface UploadFileDao extends CrudRepository<UploadFile,Long>{
	//分页查询文件
	//course => class => class<->file => files
	@Query(value = "SELECT COUNT(*) FROM upload_file AS f WHERE EXISTS "
			+ "(SELECT * FROM stu_class_upload_file AS cf WHERE EXISTS "
			+ "(SELECT * FROM stu_class AS c WHERE c.course_id = ?1 AND c.id = cf.class_id) "
			+ "AND class_id = ?2 AND create_date like ?3% AND cf.file_id = f.id) "
			+ "AND sort = ?4", nativeQuery = true)
	Long countUploadFiles(Long courseId, Long classId, String createDate, String sort);
	@Query(value = "SELECT * FROM upload_file AS f WHERE EXISTS "
			+ "(SELECT * FROM stu_class_upload_file AS cf WHERE EXISTS "
			+ "(SELECT * FROM stu_class AS c WHERE c.course_id = ?1 AND c.id = cf.class_id) "
			+ "AND class_id = ?2 AND create_date like ?3% AND cf.file_id = f.id) "
			+ "AND sort = ?4", nativeQuery = true)
	ArrayList<UploadFile> findUploadFiles(Long courseId, Long classId, String createDate, String sort, Pageable pageable);
}
