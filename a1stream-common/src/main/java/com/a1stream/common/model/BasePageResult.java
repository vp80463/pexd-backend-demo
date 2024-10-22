package com.a1stream.common.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasePageResult<T> implements Serializable {

    private static final long serialVersionUID = -7510957805618971536L;

    private Integer currentPage;

    private Integer pageSize;

    private Integer total;

    private T data;

    private Object otherProperty;
}
