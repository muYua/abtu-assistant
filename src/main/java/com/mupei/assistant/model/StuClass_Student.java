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
@NoArgsConstructor//无参构造函数，new时自动调用
public class StuClass_Student {
    //该班级非通常意义上的班级
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false)
    private Long classId;

    @Column(nullable = false)
    private Long stuId;

    //学生的班级昵称
    @Column(length = 13)
    private String classNickname;

    public StuClass_Student(Long classId, Long stuId) {
        this.classId = classId;
        this.stuId = stuId;
    }
}
