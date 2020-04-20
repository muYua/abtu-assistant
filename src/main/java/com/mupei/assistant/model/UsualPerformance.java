package com.mupei.assistant.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Table(name = "usualperformance")
@DynamicInsert
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UsualPerformance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(targetEntity = Course.class)
    @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
    private Course course;

//    @JsonIgnoreProperties({"password","email","phone","qq","activated","regTime"})//序列化时忽略的属性
    @ManyToOne(targetEntity = Student.class)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private Integer score;

    @Column(length = 19 ,nullable = false)
    private String date;

}
