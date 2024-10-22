package com.a1stream.domain.entity;

import java.util.Date;

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
@Table(name="api_error")
@Setter
@Getter
public class ApiError extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="error_id", unique=true, nullable=false)
    private Long errorId;

    @Column(name="process_time", nullable=false, length=29)
    private Date processTime;

    @Column(name="request_type", nullable=false, length=20)
    private String requestType;

    @Column(name="request_body")
    private String requestBody;


}
