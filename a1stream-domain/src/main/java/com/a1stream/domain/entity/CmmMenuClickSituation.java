package com.a1stream.domain.entity;

import com.a1stream.common.model.BaseEntity;
import com.ymsl.solid.jpa.uuid.SnowflakeIdGenerator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serial;


/**
 * @author dong zhen
 */
@Entity
@Table(name="cmm_menu_click_situation")
@Setter
@Getter
public class CmmMenuClickSituation extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="menu_click_situation_id", unique=true, nullable=false)
    private Long menuClickSituationId;

    @Column(name="custom_status", length=1)
    private String customStatus;

    @Column(name="priority")
    private Integer priority;

    @Column(name="menu_cd", length=256)
    private String menuCd;

    @Column(name="user_id", length=64)
    private String userId;

    @Column(name="click_count")
    private Integer clickCount;
}
