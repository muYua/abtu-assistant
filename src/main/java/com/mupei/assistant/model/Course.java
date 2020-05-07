package com.mupei.assistant.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@DynamicInsert
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @JsonProperty("courseId")//序列化重命名
    private Long id;

    //课程名称
    @Column(length = 30, nullable = false)
    private String courseName;

    //主讲教师
//    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")//只进行一次完整属性持久化，其余相同的实体用id替代
    @JsonIgnoreProperties({"password", "email", "phone", "qq", "activated", "regTime",
            "loginTime", "loginIP", "education", "title"})//序列化时忽略的属性
    @ManyToOne(targetEntity = Teacher.class)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(targetEntity = StuClass.class, fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "course")//通过级联，删除班级
    private Set<StuClass> stuClasses = new HashSet<>();

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(targetEntity = UsualPerformance.class, fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "course")
    private Set<UsualPerformance> usualPerformances = new HashSet<>();

    public Course(String courseName, Teacher teacher) {
        this.courseName = courseName;
        this.teacher = teacher;
    }

    public Course(Long id, String courseName, Teacher teacher) {
        this.id = id;
        this.courseName = courseName;
        this.teacher = teacher;
    }

}
