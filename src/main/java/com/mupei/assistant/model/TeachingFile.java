package com.mupei.assistant.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Table(name = "teacherfile")
@DynamicInsert
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TeachingFile extends UploadFile {
    //发布时间
    @Column(length = 10)
    private String releasedTime;
}
