package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class HomePageMessageBO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1454178098357776782L;

    private String key;

    /**
     * 标题
     */
    private String name;

    /**
     * 消息列表
     */
    private List<HomePageMessageItemBO> list;
}