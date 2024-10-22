package com.a1stream.common.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class HomePageAnnounceBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8311588909947024412L;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告图片地址
     */
    private String coverUrl;
}