package com.a1stream.domain.form.master;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.master.CMQ060302BO;
import com.a1stream.domain.vo.CmmSpecialClaimStampingVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMQ060302Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String campaignNo;
    private String campaignTitle;
    private String campaignType;
    private String campaignDescription;
    private String effectiveDate;
    private String expiredDate;
    private Long campaignId;

    List<CMQ060302BO> repairDetailOne;
    List<CMQ060302BO> repairDetailTwo;
    List<CMQ060302BO> problemDetailOne;
    List<CMQ060302BO> problemDetailTwo;
    List<CmmSpecialClaimStampingVO> stampingStyleDetail;
}
