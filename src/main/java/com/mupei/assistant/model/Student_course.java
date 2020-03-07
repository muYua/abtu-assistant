package com.mupei.assistant.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@ToString
public class Student_course {
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
  @Column(nullable = false)
  private String content;
}