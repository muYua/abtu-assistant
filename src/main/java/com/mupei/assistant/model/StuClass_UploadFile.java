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
@NoArgsConstructor//无参构造函数，new时自动调用
public class StuClass_UploadFile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;

	@Column(nullable = false)
	private Long classId;
	
	@Column(nullable = false)
	private Long fileId;

	@Column(nullable = false, length = 19)
	private String createDate;

	public StuClass_UploadFile(Long classId, Long fileId, String createDate) {
		this.classId = classId;
		this.fileId = fileId;
		this.createDate = createDate;
	}
}
