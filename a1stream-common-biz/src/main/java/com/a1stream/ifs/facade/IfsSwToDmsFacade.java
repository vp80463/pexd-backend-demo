package com.a1stream.ifs.facade;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.ifs.bo.SvAuthorizationNoBO;
import com.a1stream.ifs.bo.SvBigBikeBO;
import com.a1stream.ifs.bo.SvClaimJudgeResultBO;
import com.a1stream.ifs.bo.SvJobFlatRateBO;
import com.a1stream.ifs.bo.SvJobMasterBO;
import com.a1stream.ifs.bo.SvModifiedSpecialClaimResultBO;
import com.a1stream.ifs.bo.SvPaymentBO;
import com.a1stream.ifs.bo.SvRegisterDocBO;
import com.a1stream.ifs.bo.SvRegisterDocChangeBO;
import com.a1stream.ifs.bo.SvSpecialClaimApplicationBO;
import com.a1stream.ifs.bo.SvSpecialClaimMasterBO;
import com.a1stream.ifs.bo.SvSpecialClaimTargetMcBO;
import com.a1stream.ifs.bo.SvWarrantyBO;
import com.a1stream.ifs.bo.WarrantyPartsBO;
import com.alibaba.fastjson.JSONArray;

import jakarta.annotation.Resource;

@Component
public class IfsSwToDmsFacade {

    @Resource
    private SvAuthorizationNoFacade authNoFac;

    @Resource
    private SvBigBikeModelInfoFacade bigBikeModelFac;

    @Resource
    private SvJudgeResultFacade judgeResultFac;

    @Resource
    private SvJobFlatRateFacade jobFlatRateFac;

    @Resource
    private SvJobMasterFacade jobMasterFac;

    @Resource
    private SvModifiedSpecialClaimResultFacade modSpecClaimRstFac;

    @Resource
    private SvPaymentFacade paymentFac;

    @Resource
    private SvRegisterDocFacade registDocFac;

    @Resource
    private SvSpecialClaimApplicationFacade specialClaimAppFac;

    @Resource
    private SvSpecialClaimTargetMcFacade specialClaimTargetMcFac;

    @Resource
    private SvSpecialClaimMasterFacade specialClaimMstFac;

    @Resource
    private SvWarrantyFacade warrantyFac;

    @Resource
    private SvWarrantyPartsFacade warrantyPartsFac;

    public void doBusinessProcess(String interfCd, String detail) {

        switch(interfCd) {
            case InterfCode.IX_SV_AUTHORIZATIONNO:
                authNoFac.importAuthorizationNos(JSONArray.parseArray(detail, SvAuthorizationNoBO.class));
                break;
            case InterfCode.IX_SV_BIGBIKEMODELINFO:
                bigBikeModelFac.importBigBikeModels(JSONArray.parseArray(detail, SvBigBikeBO.class));
                break;
            case InterfCode.IX_SV_CLAIMJUDGERESULT:
                judgeResultFac.importSvClaimJudgeResult(JSONArray.parseArray(detail, SvClaimJudgeResultBO.class));
                break;
            case InterfCode.IX_SV_COUPONJUDGERESULT:
                judgeResultFac.importSvFreeCouponJudgeResult(JSONArray.parseArray(detail, SvClaimJudgeResultBO.class));
                break;
            case InterfCode.IX_SV_JOBFLATRATE:
                jobFlatRateFac.importJobFlatRateData(JSONArray.parseArray(detail, SvJobFlatRateBO.class));
                break;
            case InterfCode.IX_SV_JOBMASTER:
                jobMasterFac.importJobMasterData(JSONArray.parseArray(detail, SvJobMasterBO.class));
                break;
            case InterfCode.IX_SV_MODIFIEDSPECIALCLAIMRESULT:
                modSpecClaimRstFac.importModifiedSpecialClaimResult(JSONArray.parseArray(detail, SvModifiedSpecialClaimResultBO.class));
                break;
            case InterfCode.IX_SV_PAYMENT:
                paymentFac.importServicePayments(JSONArray.parseArray(detail, SvPaymentBO.class));
                break;
            case InterfCode.IX_SV_REGISTERDOC:
                // TODO
                registDocFac.importRegisterDocuments(JSONArray.parseArray(detail, SvRegisterDocBO.class));
                break;
            case InterfCode.IX_SV_REGISTERDOCCHANGE:
                // TODO
                registDocFac.importRegisterDocumentChanges(JSONArray.parseArray(detail, SvRegisterDocChangeBO.class));
                break;
            case InterfCode.IX_SV_SPECIALCLAIMAPPLICATION:
                specialClaimAppFac.importSpecialClaimApplication(JSONArray.parseArray(detail, SvSpecialClaimApplicationBO.class));
                break;
            case InterfCode.IX_SV_SPECIALCLAIMMASTER:
                specialClaimMstFac.importSpecialClaim(JSONArray.parseArray(detail, SvSpecialClaimMasterBO.class));
                break;
            case InterfCode.IX_SV_SPECIALCLAIMTARGETMC:
                specialClaimTargetMcFac.importSpecialClaimTargetMc(JSONArray.parseArray(detail, SvSpecialClaimTargetMcBO.class));
                break;
            case InterfCode.IX_SV_WARRANTY:
                warrantyFac.importWarranty(JSONArray.parseArray(detail, SvWarrantyBO.class));
                break;
            case InterfCode.IX_SV_WARRANTY_PARTS:
                warrantyPartsFac.importWarrantyParts(JSONArray.parseArray(detail, WarrantyPartsBO.class));
                break;
        }
    }
}
