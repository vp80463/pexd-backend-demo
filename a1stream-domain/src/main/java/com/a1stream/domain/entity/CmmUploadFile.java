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
@Table(name="cmm_upload_file")
@Setter
@Getter
public class CmmUploadFile extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="file_id", unique=true, nullable=false)
    private Long fileId;

    @Column(name="business_type", length=40)
    private String businessType;

    @Column(name="business_id")
    private Long businessId;

    @Column(name="file_name", length=256)
    private String fileName;

    @Column(name="ori_file_name", length=256)
    private String oriFileName;

    @Column(name="upload_path", length=256)
    private String uploadPath;

    @Column(name="upload_date", length=20)
    private String uploadDate;

    @Column(name="confusion_name", length=256)
    private String confusionName;
}
