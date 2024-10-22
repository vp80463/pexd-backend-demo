package com.a1stream.domain.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.a1stream.common.constants.CommonConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

/**
 * @author dong zhen
 */
@Entity
@Table(name="sys_menu")
@Setter
@Getter
public class SysMenu implements Serializable {

    @Serial
    private static final long serialVersionUID = -6501893578762278271L;

    @Id
    @Column(name = "menu_id")
    private String menuId;

    @Column(name="site_id", nullable=false, length=20)
    private String siteId;

    @Column(name="menu_code", length=40)
    private String menuCode;

    @Column(name="menu_name", length=50)
    private String menuName;

    @Column(name="parent_menu_id", length=64)
    private String parentMenuId;

    @Column(name="url_type", length=64)
    private String urlType;

    @Column(name="menu_seq")
    private Integer menuSeq = CommonConstants.INTEGER_ZERO;

    @Column(name="effective_date")
    private LocalDateTime effectivDate;

    @Column(name="expired_date")
    private LocalDateTime expiredDate;

    @Column(name="link_url", length=200)
    private String linkUrl;

    @Column(name="visitable_flag", length=1)
    private String visitableFlag;

    @Column(name="window_target", length=64)
    private String windowTarget;

    @Column(name="menu_pic", length=64)
    private String menuPic;

    @Column(name="menu_label", length=500)
    private String menuLabel;

    @Column(name="extend_list")
    private String extendList;

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