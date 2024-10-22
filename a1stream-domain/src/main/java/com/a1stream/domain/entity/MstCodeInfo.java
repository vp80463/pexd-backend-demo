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
@Table(name="mst_code_info")
@Setter
@Getter
public class MstCodeInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="code_dbid", unique=true, nullable=false, length=40)
    private String codeDbid;

    @Column(name="code_id", nullable=false, length=10)
    private String codeId;

    @Column(name="description", length=40)
    private String description;

    @Column(name="key1", length=40)
    private String key1;

    @Column(name="key2", length=40)
    private String key2;

    @Column(name="mainten_flag", nullable=false, length=1)
    private String maintenFlag;

    @Column(name="code_data1", length=256)
    private String codeData1;

    @Column(name="code_data2")
    private String codeData2;

    @Column(name="code_data3", length=256)
    private String codeData3;

    @Column(name="code_data4", length=256)
    private String codeData4;

    @Column(name="code_data5", length=256)
    private String codeData5;

    @Column(name="code_data6", length=256)
    private String codeData6;


}
