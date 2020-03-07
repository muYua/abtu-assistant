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
public class Student extends Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;
  
  //学号
  @Column(length = 12)
  private String stuNumber;
  
  @Column(length = 55)
  private String school;
  
  //院系
  @Column(length = 20)
  private String department;
  
  //专业
  @Column(length = 20)
  private String major;
  
  //班级
  @Column(length = 28)
  private String stuClass;
  
  //入学年份
  @Column(length = 4)
  private String enrollmentYear;
}
