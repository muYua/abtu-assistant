package com.mupei.assistant.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Table(name = "stuclass_student"
        , uniqueConstraints = {@UniqueConstraint(columnNames = {"student_id", "stuclass_id"})})
@DynamicInsert
@Getter
@Setter
@ToString
@NoArgsConstructor//无参构造函数，new时自动调用
public class StuClass_Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(targetEntity = StuClass.class)
    @JoinColumn(name = "stuclass_id", referencedColumnName = "id", nullable = false)
    private StuClass stuClass;

    @JsonIgnoreProperties({"password","email","phone","qq","activated","regTime"})//序列化时忽略的属性
    @ManyToOne(targetEntity = Student.class)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    //学生的班级昵称
    @Column(length = 13)
    private String classNickname;

    public StuClass_Student(StuClass stuClass, Student student) {
        this.stuClass = stuClass;
        this.student = student;
    }

}
