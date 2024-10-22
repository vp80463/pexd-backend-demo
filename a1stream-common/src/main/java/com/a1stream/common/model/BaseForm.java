package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class BaseForm implements Serializable {

    @Serial
    private static final long serialVersionUID = 3946853620437297719L;

    private Integer currentPage;

    private Integer pageSize;

    private Integer total;

    private String siteId;

    /**
     * 个人 ID
     */
    private Long personId;

    private String personNm;

    private String userId;
}
