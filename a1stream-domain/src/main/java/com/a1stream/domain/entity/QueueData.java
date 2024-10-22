package com.a1stream.domain.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.ymsl.solid.jpa.usertype.StringJsonUserType;
import com.ymsl.solid.jpa.uuid.SnowflakeIdGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="queue_data")
public class QueueData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="q_id", unique=true, nullable=false)
    private Long qId;

    @Column(name = "interf_cd", length=40)
    private String interfCd;

    @Column(name="site_id", length=10)
    private String siteId;

    @Column(name="send_times")
    private Integer sendTimes;

    @Column(name="status", length=40)
    private String status;

    @Column(name="related_table", length=256)
    private String relatedTable;

    @Column(name="related_id")
    private Long relatedId;

    @Column(name="body")
    @Type(StringJsonUserType.class)
    private String body;

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
}
