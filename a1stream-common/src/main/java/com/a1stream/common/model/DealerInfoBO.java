package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class DealerInfoBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8347165974764049659L;

    /**
     * 经销商ID
     */
    private String siteId;

    /**
     * 经销商CD
     */
    private String siteCd;

    /**
     * 经销商名称
     */
    private String siteNm;
}
