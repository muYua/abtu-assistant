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
public class UploadFile {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;
  
  @Column(nullable = false)
  private String fileName;
  
  @Column(nullable = false)
  private Long fileSize;
  
  @Column(nullable = false)
  private String filePath;
  
  //文件类别，课堂文件、作业文件、签到图片、头像
  @Column(columnDefinition = "enum('l','h','s','i') not null")
  private String sort;
  
  @Column(length = 19, nullable = false)
  private String createTime;
  
  @Column
  private Long teacherId;
}