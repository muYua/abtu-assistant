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
public class Message {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;
  
  //消息内容
  @Column(columnDefinition = "text not null")
  private String content;
  
  //消息类别，作业、通知
  @Column(columnDefinition = "enum('h','m') not null")
  private String sort;
  
  @Column(length = 19, nullable = false)
  private String createTime;
  
  @Column
  private Long teacherId;
}