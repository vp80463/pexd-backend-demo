package com.a1stream.domain.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="cmm_site_master")
public class CmmSiteMaster implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="site_id", length=10)
    private String siteId;

    @Column(name="site_cd", length=10)
    private String siteCd;

    @Column(name="site_nm", length=256)
    private String siteNm;

    @Column(name="web_server_root", length=256)
    private String webServerRoot;

    @Column(name="site_type_id", length=40)
    private String siteTypeId;

    @Column(name="active_flag", length=1)
    private String activeFlag;

    @Column(name="do_flag", length=1)
    private String doFlag;

    @Column(name="local_data_source", length=40)
    private String localDataSource;

    @Column(name="ap_server_flag", length=1)
    private String apServerFlag;

    //---------------------------------------
    @Column(name="update_program", length=20)
    private String updateProgram;

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

    @Column(name="ymvn_flag", nullable=false)
    private String ymvnFlag;
}
