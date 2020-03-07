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
  
  @Column
  private Long teacherId;
    
}
