package com.mupei.assistant.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
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

  //创建人
  @JsonIgnoreProperties({"password","email","phone","qq","activated","regTime"})//序列化时忽略的属性
  @ManyToOne(targetEntity = Role.class)
  @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
  private Role role;

  public Message(String content, String sort, String createTime, Role role) {
    this.content = content;
    this.sort = sort;
    this.createTime = createTime;
    this.role = role;
  }

}