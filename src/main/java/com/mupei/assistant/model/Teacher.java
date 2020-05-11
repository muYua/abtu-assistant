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
@JsonIgnoreProperties({"password", "email", "phone", "qq", "activated", "regTime", "loginTime", "loginIP"})//序列化时忽略的属性
public class Teacher extends Role {
  //教工号
  @Column(length = 16)
  private String teacherNumber;

  //所属学校
  @Column(length = 55)
  private String school;
  
  //学历
  @Column(length = 25)
  private String education;
  
  ///职称
  @Column(length = 25)
  private String title;

  @ToString.Exclude
  @JsonIgnore
  @OneToMany(targetEntity = Course.class, fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "teacher")
  private Set<Course> courses = new HashSet<>();
}