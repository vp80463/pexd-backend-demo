package com.a1stream.domain.entity;

import com.a1stream.common.model.BaseEntity;
import com.ymsl.solid.jpa.usertype.StringJsonUserType;
import com.ymsl.solid.jpa.uuid.SnowflakeIdGenerator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import java.io.Serial;
import java.time.LocalDateTime;


/**
 * @author dong zhen
 */
@Entity
@Table(name="cmm_message_remind")
@Setter
@Getter
public class CmmMessageRemind extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="message_remind_id", unique=true, nullable=false)
    private Long messageRemindId;

    @Column(name="business_type", length=40)
    private String businessType;

    @Column(name="create_user", length=64)
    private String createUser;

    @Column(name="create_date")
    private LocalDateTime createDate;

    @Column(name="read_type", length=10)
    private String readType;

    @Column(name="read_user", length=64)
    private String readUser;

    @Column(name="read_date")
    private LocalDateTime readDate;

    @Column(name="message", length=1000)
    private String message;

    @Column(name="read_category_type", length=40)
    private String readCategoryType;

    @Column(name="role_type", length=64)
    private String roleType;

    @Column(name="sys_role_id_list")
    @Type(StringJsonUserType.class)
    private String sysRoleIdList;
}
