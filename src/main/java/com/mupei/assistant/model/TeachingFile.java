package com.mupei.assistant.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Table
@DynamicInsert
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TeachingFile extends UploadFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    //发布时间
    @Column(length = 10)
    private String releasedTime;
}
