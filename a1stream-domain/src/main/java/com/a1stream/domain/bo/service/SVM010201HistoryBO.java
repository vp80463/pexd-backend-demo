package com.a1stream.domain.bo.service;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM010201HistoryBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String processTime;
    private String staffCd;
    private String staffNm;
    private String operation;
}
