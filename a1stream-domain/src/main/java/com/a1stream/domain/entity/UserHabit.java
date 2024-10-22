package com.a1stream.domain.entity;

import org.hibernate.annotations.GenericGenerator;

import com.a1stream.common.model.BaseEntity;
import com.ymsl.solid.jpa.uuid.SnowflakeIdGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="user_habit")
@Setter
@Getter
public class UserHabit extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="user_habit_id", unique=true, nullable=false)
    private Long userHabitId;

    @Column(name="user_id", length=64)
    private String userId;

    @Column(name="user_habit_type_id", length=40)
    private String userHabitTypeId;

    @Column(name="seq_no", length=20)
    private String seqNo;

    @Column(name="habit_content")
    private String habitContent;
}
