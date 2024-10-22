package com.a1stream.ifs.bo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpWholeSaleSignModelBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dealerCode;
    private String consigneeCode;
    private String status;
}