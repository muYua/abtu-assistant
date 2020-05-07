package com.mupei.assistant.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "stuclass_uploadfile")
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

	@ManyToOne(targetEntity = StuClass.class)
	@JoinColumn(name = "stuclass_id", referencedColumnName = "id", nullable = false)
	private StuClass stuClass;

	@ManyToOne(targetEntity = UploadFile.class, cascade = CascadeType.ALL)//班级文件级联删除
	@JoinColumn(name = "uploadfile_id", referencedColumnName = "id", nullable = false)
	private UploadFile uploadFile;

	@Column(nullable = false, length = 19)
	private String createDate;

	public StuClass_UploadFile(StuClass stuClass, UploadFile uploadFile, String createDate) {
		this.stuClass = stuClass;
		this.uploadFile = uploadFile;
		this.createDate = createDate;
	}
}
