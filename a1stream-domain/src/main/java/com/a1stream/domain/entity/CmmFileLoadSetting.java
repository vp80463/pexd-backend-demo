package com.a1stream.domain.entity;

import com.a1stream.common.constants.CommonConstants;
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
@Table(name="cmm_file_load_setting")
@Setter
@Getter
public class CmmFileLoadSetting extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="file_load_id", unique=true, nullable=false)
    private Long fileLoadId;

    @Column(name="type_id", length=20)
    private String typeId;

    @Column(name="language_id", length=20)
    private String languageId;

    @Column(name="program_id", length=20)
    private String programId;

    @Column(name="file_path", length=256)
    private String filePath;

    @Column(name="file_load_api_url", length=256)
    private String fileLoadApiUrl;

    @Column(name="file_valid_api_url", length=256)
    private String fileValidApiUrl;

    @Column(name="file_save_api_url", length=256)
    private String fileSaveApiUrl;

    @Column(name="file_redownload_api_url", length=256)
    private String fileRedownloadApiUrl;

    @Column(name="template_json")
    private String templateJson;

    @Column(name="title_json")
    private String titleJson;

    @Column(name="delete_flag", length=1)
    private String deleteFlag;

    @Column(name="remarks", length=256)
    private String remarks;

    @Column(name="file_size")
    private Integer fileSize = CommonConstants.INTEGER_ZERO;

    @Column(name="file_rows")
    private Integer fileRows = CommonConstants.INTEGER_ZERO;

    @Column(name="start_row")
    private Integer startRow = CommonConstants.INTEGER_ZERO;

    @Column(name="start_sheet")
    private Integer startSheet = CommonConstants.INTEGER_ZERO;

    @Column(name="redownload_flag", length=1)
    private String redownloadFlag;

    @Column(name="background_save_flag", length=1)
    private String backgroundSaveFlag;

    @Column(name="import_file_type", length=10)
    private String importFileType;
}
