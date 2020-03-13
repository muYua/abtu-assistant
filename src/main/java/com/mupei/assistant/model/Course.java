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
public class Course {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;
  
  //课程编号
  @Column(length = 30, nullable = false)
  private String courseNumber;
  
  //课程名称
  @Column(length = 30, nullable = false)
  private String courseName;
  
  //主讲教师
  @Column(nullable = false)
  private Long teacherId;
    
}
