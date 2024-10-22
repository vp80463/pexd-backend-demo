package com.a1stream.common.bo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToDoListBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderType;

    private String orderNo;

    private String orderStatus;

    private String employeeName;

    private String orderDate;

    private String brand;

    private Long orderId;
}