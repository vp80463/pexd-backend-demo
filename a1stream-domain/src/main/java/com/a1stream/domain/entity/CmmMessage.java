package com.a1stream.domain.entity;

import java.io.Serial;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.a1stream.common.model.BaseEntity;
import com.ymsl.solid.jpa.usertype.StringJsonUserType;
import com.ymsl.solid.jpa.uuid.SnowflakeIdGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="cmm_message")
@Setter
@Getter
public class CmmMessage extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="cmm_message", unique=true, nullable=false)
    private Long cmmMessage;

    @Column(name="message", length=1000)
    private String message;

    @Column(name="role_type", length=64)
    private String roleType;

    @Column(name="sys_role_id_list")
    @Type(StringJsonUserType.class)
    private String sysRoleIdList;
}
