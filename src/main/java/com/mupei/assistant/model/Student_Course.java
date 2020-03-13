package com.mupei.assistant.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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
public class Student_Course {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;
  
  @Column(nullable = false)
  private Long stuId;
  
  @Column(nullable = false)
  private Long courseId;
  
  //考勤日期
  @Column(length = 10, nullable = false)
  private String date;
  
  //考勤记录
  @Column
  private String content;
  
  //平时成绩
  @Column(length = 3)
  private String usualPerformance;
}