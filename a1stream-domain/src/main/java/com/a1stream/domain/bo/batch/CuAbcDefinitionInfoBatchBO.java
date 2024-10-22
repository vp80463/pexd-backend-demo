package com.a1stream.domain.bo.batch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CuAbcDefinitionInfoBatchBO {

    private String abcid;
    private String abctype;

    private String percentage;

    private String costfrom;

    private String costto;

    private String type;

    private String productcategoryid;

    private Long cmmproductcategoryid;
}