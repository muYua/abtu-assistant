package com.mupei.assistant.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mupei.assistant.model.Student;
import org.springframework.transaction.annotation.Transactional;

public interface StudentDao extends CrudRepository<Student, Long> {
	@Modifying
	@Transactional
	@Query("update Student stu set "
			+ "stu.password = CASE WHEN :#{#student.activated} IS NULL THEN stu.password ELSE :#{#student.password} END ,"
			+ "stu.name = CASE WHEN :#{#student.name} IS NULL THEN stu.name ELSE :#{#student.name} END ,"
			+ "stu.email = CASE WHEN :#{#student.email} IS NULL THEN stu.email ELSE :#{#student.email} END ,"
			+ "stu.phone = CASE WHEN :#{#student.phone} IS NULL THEN stu.phone ELSE :#{#student.phone} END ,"
			+ "stu.image = CASE WHEN :#{#student.image} IS NULL THEN stu.image ELSE :#{#student.image} END ,"
			+ "stu.qq = CASE WHEN :#{#student.qq} IS NULL THEN stu.qq ELSE :#{#student.qq} END ,"
			+ "stu.sort = CASE WHEN :#{#student.sort} IS NULL THEN stu.sort ELSE :#{#student.sort} END ,"
			+ "stu.regTime = CASE WHEN :#{#student.regTime} IS NULL THEN stu.regTime ELSE :#{#student.regTime} END ,"
			+ "stu.loginTime = CASE WHEN :#{#student.loginTime} IS NULL THEN stu.loginTime ELSE :#{#student.loginTime} END ,"
			+ "stu.loginIP = CASE WHEN :#{#student.loginIP} IS NULL THEN stu.loginIP ELSE :#{#student.loginIP} END ,"
			+ "stu.activated = CASE WHEN :#{#student.activated} IS NULL THEN stu.activated ELSE :#{#student.activated} END ,"
			+ "stu.stuNumber = CASE WHEN :#{#student.stuNumber} IS NULL THEN stu.stuNumber ELSE :#{#student.stuNumber} END ,"
			+ "stu.school = CASE WHEN :#{#student.school} IS NULL THEN stu.school ELSE :#{#student.school} END ,"
			+ "stu.department = CASE WHEN :#{#student.department} IS NULL THEN stu.department ELSE :#{#student.department} END ,"
			+ "stu.major = CASE WHEN :#{#student.major} IS NULL THEN stu.major ELSE :#{#student.major} END ,"
			+ "stu.enrollmentYear = CASE WHEN :#{#student.enrollmentYear} IS NULL THEN stu.enrollmentYear ELSE :#{#student.enrollmentYear} END "
			+ "where stu.id = :#{#student.id}")
	Integer update(Student student);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO student(department, enrollment_year, major, school, stu_number, id)" +
			" VALUES (:#{#student.department}, :#{#student.enrollmentYear}, :#{#student.major}, :#{#student.school}, :#{#student.stuNumber}, :roleId)"
			, nativeQuery = true)
	Integer save(Student student, Long roleId);
}
