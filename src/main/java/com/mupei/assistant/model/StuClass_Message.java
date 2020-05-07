package com.mupei.assistant.model;

import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "stuclass_messsage")
@DynamicInsert
@Getter
@Setter
@ToString
@NoArgsConstructor//无参构造函数，new时自动调用
public class StuClass_Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(targetEntity = StuClass.class)
    @JoinColumn(name = "stuclass_id", referencedColumnName = "id", nullable = false)
    private StuClass stuClass;

    @ManyToOne(targetEntity = Message.class, cascade = CascadeType.ALL)//班级消息级联删除，删除调试失败
    @JoinColumn(name = "message_id", referencedColumnName = "id", nullable = false)
    private Message message;

    @Column(nullable = false, length = 19)
    private String createDate;

    public StuClass_Message(StuClass stuClass, Message message, String createDate) {
        this.stuClass = stuClass;
        this.message = message;
        this.createDate = createDate;
    }
}
