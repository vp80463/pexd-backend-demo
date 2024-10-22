package com.a1stream.ifs.bo;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvJobFlatRateBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String flatrateGroupCode;
    private String flatrateGroupName;
    private List<SvJobFlatRateDetailBO> jobs;
    private List<SvJobFlatRateModelTypeBO> modelTypes;
}