package com.a1stream.ifs.bo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecialClaimPartDetailBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String partNo;

    private String primaryPartsFlag;
}