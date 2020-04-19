package com.mupei.assistant.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Table(name = "homeworkfile_t")
@DynamicInsert
@Getter
@Setter
@ToString
@NoArgsConstructor
public class HomeworkWithTeacherFile extends UploadFile {
    @Column(length = 10)
    private String releasedTime;
}
