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
public class usualPerformance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false)
    private Long courseId;

    @Column(nullable = false)
    private Long stuId;

    @Column(nullable = false)
    private String stuNumber;

    @Column(nullable = false)
    private String stuName;

    @Column(nullable = false)
    private Integer score;

    @Column(length = 19 ,nullable = false)
    private String date;
}
