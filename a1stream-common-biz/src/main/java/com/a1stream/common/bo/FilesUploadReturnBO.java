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
public class FilesUploadReturnBO implements Serializable {

    @Serial
    private static final long serialVersionUID = -698757213753868070L;

    /**
     * 名称
     */
    private String name;

    /**
     * 链接
     */
    private String url;
}