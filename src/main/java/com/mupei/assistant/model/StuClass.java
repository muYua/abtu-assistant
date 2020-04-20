package com.mupei.assistant.model;

import javax.persistence.*;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "stuclass")
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

	// 课程ID
	@ManyToOne(targetEntity = Course.class)
	@JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
	private Course course;

	public StuClass(String className, Course course) {
		this.className = className;
		this.course = course;
	}

}
