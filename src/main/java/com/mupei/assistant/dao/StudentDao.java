package com.mupei.assistant.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mupei.assistant.model.Student;

public interface StudentDao extends CrudRepository<Student, Long> {
	//查询文件相关的学生信息
	@Query(value = "SELECT * FROM student stu WHERE stu.id IN (SELECT stu_id stu.id FROM student_upload_file WHERE uploadfile_id = ?1)", nativeQuery = true)
	Student findStuInfoByUploadFileId(Long fileId);
}
