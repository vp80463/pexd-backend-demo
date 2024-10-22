package com.a1stream.common.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.a1stream.common.listener.model.PJAuditableEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Data;

@EntityListeners({AuditingEntityListener.class, PJAuditableEntityListener.class})
@MappedSuperclass
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 7561634251350296552L;

    @Column(name="site_id", nullable=false, length=20)
    private String siteId;

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
}