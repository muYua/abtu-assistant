package com.mupei.assistant.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table
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
  
  @Column(nullable = false)
  private Long stuId;
  
  @Column(nullable = false)
  private Long courseId;

  //考勤记录
  @Column
  private Long fileId;
  
  //平时成绩
  @Column(length = 3)
  private String usualPerformance;

  public Student_Course(Long stuId, Long courseId) {
    this.stuId = stuId;
    this.courseId = courseId;
  }

}