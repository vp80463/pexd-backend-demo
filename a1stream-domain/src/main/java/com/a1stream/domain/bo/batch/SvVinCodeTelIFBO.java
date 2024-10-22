package com.a1stream.domain.bo.batch;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvVinCodeTelIFBO implements Serializable{

    private static final long serialVersionUID = 1L;

    private String queueId;
    private String VinHin;
    private String TelNo;
    private String TelLastDigits;
    private String OwnerChangedFlg;
    private String OwnerChangedDate;
    private String SalesDate;
    private String CreateAuthor;
    private String CreateDate;
    private String UpdateAuthor;
    private String UpdateDate;
    private String UpdateProgram;
    private String UpdateCounter;
}
