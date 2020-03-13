package com.mupei.assistant.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table
@DynamicInsert
@Getter
@Setter
@ToString
@NoArgsConstructor
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

	// 文件类别，课堂文件、作业文件、签到图片、头像
	@Column(columnDefinition = "enum('l','h','s','i') not null")
	private String sort;

	@Column(length = 19, nullable = false)
	private String createTime;

	@Column
	private Long teacherId;

	public UploadFile(String fileName, Long fileSize, String filePath, String sort, String createTime, Long teacherId) {
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.filePath = filePath;
		this.sort = sort;
		this.createTime = createTime;
		this.teacherId = teacherId;
	}
		
}