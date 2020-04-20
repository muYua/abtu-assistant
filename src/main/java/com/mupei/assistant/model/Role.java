package com.mupei.assistant.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"id", "email"}), @UniqueConstraint(columnNames = {"id", "phone"})})
@Inheritance(strategy = InheritanceType.JOINED)
@DynamicInsert//实现default赋值
//@DynamicUpdate//BUG,2020.3.10
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;
  
  @Column(nullable = false)
  private String password;
  
  //昵称
  @Column(length = 32, nullable = false)
  private String nickname;
  
  //姓名
  @Column(length = 13)
  private String name;
  
  @Column(length = 45, nullable = false, unique = true)
  private String email;
  
  //手机号码
  @Column(length = 11, unique = true)
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

  @ToString.Exclude
  @JsonIgnore
  @OneToMany(targetEntity = Message.class, mappedBy = "role")
  private Set<Message> messages = new HashSet<>();

  @ToString.Exclude
  @JsonIgnore
  @OneToMany(targetEntity = UploadFile.class, mappedBy = "role")
  private Set<UploadFile> uploadFiles = new HashSet<>();
}