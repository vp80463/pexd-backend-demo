package com.a1stream.domain.bo.batch;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvServiceRequestJobIFBO implements Serializable{

    private static final long serialVersionUID = 1L;

    private String jobCode;
}
