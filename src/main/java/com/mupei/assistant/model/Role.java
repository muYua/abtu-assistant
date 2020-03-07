package com.mupei.assistant.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@ToString
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;
  
  @Column(length = 16, nullable = false)
  private String password;
  
  //昵称
  @Column(length = 32, nullable = false)
  private String nickname;
  
  //姓名
  @Column(length = 13)
  private String name;
  
  @Column(length = 45, nullable = false)
  private String email;
  
  //手机号码
  @Column(length = 11)
  private String phone;
  
  //头像
  @Column(columnDefinition = "varchar(255) not null default '/images/head-portraits/admin.jpg'")
  private String image;
  
  @Column(length = 11)
  private String qq;
  
  //用户类型，管理员、教师、学生
  @Column(columnDefinition = "enum('a','t','s') not null default 's'")
  private String sort;
  
  @Column(length = 19, nullable = false)
  private String regTime;
  
  @Column(length = 19)
  private String loginTime;
  
  @Column(length = 15)
  private String loginIP;
  
  //注册激活标志位
  @Column(columnDefinition = "tinyint(1) not null default '0'")
  private Boolean activated;
}