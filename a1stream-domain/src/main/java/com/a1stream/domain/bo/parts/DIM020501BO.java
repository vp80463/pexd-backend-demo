package com.a1stream.domain.bo.parts;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DIM020501BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private Long workzoneId;

    private String workzoneCd;

    private String workzoneNm;
}
