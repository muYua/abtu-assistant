package com.mupei.assistant.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "student_course"
        , uniqueConstraints = {@UniqueConstraint(columnNames = {"student_id", "course_id"})})
@DynamicInsert
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Student_Course {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  @ManyToOne(targetEntity = Student.class)
  @JsonIgnoreProperties({"password", "email", "phone", "qq", "activated", "regTime",
          "loginTime", "loginIP", "department", "major", "enrollmentYear"})//序列化时忽略的属性
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  @ManyToOne(targetEntity = Course.class)
  @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
  private Course course;

  //考勤记录
  @Column(name = "signinfile_id")
  private Long signInFileId;
  
  //最终平时成绩
  @Column
  private Integer usualPerformances;

  public Student_Course(Student student, Course course) {
    this.student = student;
    this.course = course;
  }
}