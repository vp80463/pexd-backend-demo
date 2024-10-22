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
@Table(name="cmm_announce")
@Setter
@Getter
public class CmmAnnounce extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name = "announce_id", unique=true, nullable = false)
    private Long announceId;

    @Column(name = "site_id", length = 10, nullable = false)
    private String siteId;

    @Column(name = "notify_type_id", length = 40)
    private String notifyTypeId;

    @Column(name = "title", length = 256)
    private String title;

    @Column(name = "cover_url", length = 256)
    private String coverUrl;

    @Column(name = "from_date", length = 8)
    private String fromDate;

    @Column(name = "to_date", length = 8)
    private String toDate;
}
