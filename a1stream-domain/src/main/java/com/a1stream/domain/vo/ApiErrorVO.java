package com.a1stream.domain.vo;

import java.util.Date;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ApiErrorVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long errorId;

    private Date processTime;

    private String requestType;

    private String requestBody;


}
