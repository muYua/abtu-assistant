package com.mupei.assistant.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
public class StuClass {//非通俗意义上的班级，便于教师管理学生群体，由教师建立班级
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;

	// 班级名称
	@Column(length = 28, nullable = false)
	private String className;

	// 教师ID
	@Column(nullable = false)
	private Long teacherId;

	// 课程ID
	@Column(nullable = false)
	private Long courseId;

	public StuClass(String className, Long teacherId, Long courseId){
		this.className = className;
		this.teacherId = teacherId;
		this.courseId = courseId;
	}
}
