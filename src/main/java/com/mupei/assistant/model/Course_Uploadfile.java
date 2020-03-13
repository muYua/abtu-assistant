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
public class Course_Uploadfile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;

	@Column(nullable = false)
	private Long courseId;

	@Column(nullable = false)
	private Long fileId;

	//创建日期
	@Column(length = 10, nullable = false)
	private String createDate;

	public Course_Uploadfile(Long courseId, Long fileId, String createDate) {
		this.courseId = courseId;
		this.fileId = fileId;
		this.createDate = createDate;
	}	
}
