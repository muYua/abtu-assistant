package com.mupei.assistant.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mupei.assistant.model.Teacher;
import org.springframework.transaction.annotation.Transactional;

public interface TeacherDao extends CrudRepository<Teacher, Long>{
    @Modifying
    @Transactional
    @Query("update Teacher t set "
            + "t.password = CASE WHEN :#{#teacher.activated} IS NULL THEN t.password ELSE :#{#teacher.password} END ,"
            + "t.name = CASE WHEN :#{#teacher.name} IS NULL THEN t.name ELSE :#{#teacher.name} END ,"
            + "t.email = CASE WHEN :#{#teacher.email} IS NULL THEN t.email ELSE :#{#teacher.email} END ,"
            + "t.phone = CASE WHEN :#{#teacher.phone} IS NULL THEN t.phone ELSE :#{#teacher.phone} END ,"
            + "t.image = CASE WHEN :#{#teacher.image} IS NULL THEN t.image ELSE :#{#teacher.image} END ,"
            + "t.qq = CASE WHEN :#{#teacher.qq} IS NULL THEN t.qq ELSE :#{#teacher.qq} END ,"
            + "t.sort = CASE WHEN :#{#teacher.sort} IS NULL THEN t.sort ELSE :#{#teacher.sort} END ,"
            + "t.regTime = CASE WHEN :#{#teacher.regTime} IS NULL THEN t.regTime ELSE :#{#teacher.regTime} END ,"
            + "t.loginTime = CASE WHEN :#{#teacher.loginTime} IS NULL THEN t.loginTime ELSE :#{#teacher.loginTime} END ,"
            + "t.loginIP = CASE WHEN :#{#teacher.loginIP} IS NULL THEN t.loginIP ELSE :#{#teacher.loginIP} END ,"
            + "t.activated = CASE WHEN :#{#teacher.activated} IS NULL THEN t.activated ELSE :#{#teacher.activated} END ,"
            + "t.teacherNumber = CASE WHEN :#{#teacher.teacherNumber} IS NULL THEN t.teacherNumber ELSE :#{#teacher.teacherNumber} END ,"
            + "t.school = CASE WHEN :#{#teacher.school} IS NULL THEN t.school ELSE :#{#teacher.school} END ,"
            + "t.education = CASE WHEN :#{#teacher.education} IS NULL THEN t.education ELSE :#{#teacher.education} END ,"
            + "t.title = CASE WHEN :#{#teacher.title} IS NULL THEN t.title ELSE :#{#teacher.title} END "
            + "where t.id = :#{#teacher.id}")
    Integer update(Teacher teacher);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO teacher(education, school, teacher_number, title, id)" +
            " VALUES (:#{#teacher.education}, :#{#teacher.school}, :#{#teacher.teacherNumber}, :#{#teacher.title}, :roleId)"
            , nativeQuery = true)
    Integer save(Teacher teacher, Long roleId);
}
