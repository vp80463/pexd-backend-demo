package com.a1stream.domain.bo.service;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long productId;
    private String productCd;
}
