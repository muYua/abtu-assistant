package com.mupei.assistant.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "uploadfile")
@DynamicInsert
@Getter
@Setter
@ToString
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
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

	//根据路径生成,路径标识
	@Column(nullable = false)
	private String md5;

	// 文件类别，课堂文件、作业文件(教师下发、学生上传)、签到图片、头像
	@Column(columnDefinition = "enum('l','ht','hs','s','i') not null")
	private String sort;

	@Column(length = 19, nullable = false)
	private String createTime;

	@JsonIgnoreProperties({"password","email","phone","qq","activated","regTime"})//序列化时忽略的属性
	@ManyToOne(targetEntity = Role.class)
	@JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
	private Role role;

	public UploadFile(String fileName, Long fileSize, String filePath, String md5, String sort, String createTime, Role role) {
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.filePath = filePath;
		this.md5 = md5;
		this.sort = sort;
		this.createTime = createTime;
		this.role = role;
	}
}