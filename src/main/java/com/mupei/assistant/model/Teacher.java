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
public class Teacher extends Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;
  
  //所属学校
  @Column(length = 55)
  private String school;
  
  //教工号
  @Column(length = 16)
  private String teacherNumber;
  
  //学历
  @Column(length = 25)
  private String education;
  
  ///职称
  @Column(length = 25)
  private String title;
}