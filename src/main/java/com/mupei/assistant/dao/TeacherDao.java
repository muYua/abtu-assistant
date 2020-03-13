package com.mupei.assistant.dao;

import java.util.ArrayList;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mupei.assistant.model.Teacher;
import com.mupei.assistant.model.UploadFile;

public interface TeacherDao extends CrudRepository<Teacher, Long>{
	//分页查询文件
	@Query(value = "SELECT * FROM upload_file WHERE sort = ?3 IN (SELECT * FROM course_uploadfile WHERE courseId = ?1 AND createDate = ?2)", nativeQuery = true)
	ArrayList<UploadFile> findUploadFilesWithCourse(Long courseId, String createDate, String sort, Pageable pageable);
	@Query(value = "SELECT COUNT(*) FROM upload_file WHERE sort = ?3 IN (SELECT * FROM course_uploadfile WHERE courseId = ?1 AND createDate = ?2)", nativeQuery = true)
	Long countUploadFilesWithCourse(Long courseId, String createDate, String sort);	
}
