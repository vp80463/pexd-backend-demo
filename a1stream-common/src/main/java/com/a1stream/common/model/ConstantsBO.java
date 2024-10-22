package com.a1stream.common.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConstantsBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codeDbid;

    private String codeData1;

    private String codeData2;

    private String codeData3;

    private String codeData4;

    private String siteId;

    private String key1;

    private int seq;

    public ConstantsBO() {}

    public ConstantsBO(String codeDbid, String codeData1, int seq) {
        this.codeDbid = codeDbid;
        this.codeData1 = codeData1;
        this.seq = seq;
    }

    public ConstantsBO(String codeDbid, String codeData1, String codeData2, int seq) {
        this.codeDbid = codeDbid;
        this.codeData1 = codeData1;
        this.codeData2 = codeData2;
        this.seq = seq;
    }

    public ConstantsBO(String codeDbid, String codeData1, String codeData2,String codeData3, int seq) {
        this.codeDbid = codeDbid;
        this.codeData1 = codeData1;
        this.codeData2 = codeData2;
        this.codeData3 = codeData3;
        this.seq = seq;
    }

    public ConstantsBO(String codeDbid, String codeData1, String codeData2,String codeData3,String codeData4, int seq) {
        this.codeDbid = codeDbid;
        this.codeData1 = codeData1;
        this.codeData2 = codeData2;
        this.codeData3 = codeData3;
        this.codeData4 = codeData4;
        this.seq = seq;
    }

    public ConstantsBO(String codeDbid, String codeData1, String codeData2,String codeData3,String codeData4, String siteId, int seq) {
        this.codeDbid = codeDbid;
        this.codeData1 = codeData1;
        this.codeData2 = codeData2;
        this.codeData3 = codeData3;
        this.codeData4 = codeData4;
        this.siteId = siteId;
        this.seq = seq;
    }

    public ConstantsBO(String codeDbid, String codeData1, int seq, String key1) {
        this.codeDbid = codeDbid;
        this.codeData1 = codeData1;
        this.key1 = key1;
        this.seq = seq;
    }

}