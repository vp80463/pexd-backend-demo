package com.a1stream.domain.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.ymsl.solid.jpa.usertype.StringJsonUserType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="sys_user_authority")
@Setter
@Getter
public class SysUserAuthority implements Serializable {

    @Serial
    private static final long serialVersionUID = -6501893578762278271L;

    @Id
    @Column(name = "authority_id", length=64, nullable=false)
    private String menuId;

    @Column(name="site_id",length=64)
    private String siteId;

    @Column(name="user_id", length=64, nullable=false)
    private String userId;

    @Column(name="role_list")
    @Type(StringJsonUserType.class)
    private String roleList;

    @Version
    @Column(name = "update_count")
    private Integer updateCount;

    @LastModifiedBy
    @Column(name = "last_updated_by")
    private String lastUpdatedBy;

    @LastModifiedDate
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @CreatedBy
    @Column(name = "created_by", length=60)
    private String createdBy;

    @CreatedDate
    @Column(name = "date_created", length=60)
    private LocalDateTime dateCreated;
}