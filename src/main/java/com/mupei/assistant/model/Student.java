package com.mupei.assistant.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@DynamicInsert
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties({"password","email","phone","qq","activated","regTime"})//序列化时忽略的属性
public class Student extends Role {
  //学号
  @Column(length = 12)
  private String stuNumber;
  
  //所属学校
  @Column(length = 55)
  private String school;
  
  //院系
  @Column(length = 20)
  private String department;
  
  //专业
  @Column(length = 20)
  private String major;
  
  //入学年份
  @Column(length = 4)
  private String enrollmentYear;

  @ToString.Exclude
  @JsonIgnore
  @OneToMany(targetEntity = UsualPerformance.class, mappedBy = "student")
  private Set<UsualPerformance> usualPerformances = new HashSet<>();
}
