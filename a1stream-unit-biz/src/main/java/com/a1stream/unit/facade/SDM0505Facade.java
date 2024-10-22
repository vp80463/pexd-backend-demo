package com.a1stream.unit.facade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.common.constants.MstCodeConstants.JudgementStatus;
import com.a1stream.common.constants.MstCodeConstants.SystemParameterType;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.domain.bo.unit.SDM050501BO;
import com.a1stream.domain.bo.unit.SDM050501PrintBO;
import com.a1stream.domain.form.unit.SDM050501Form;
import com.a1stream.domain.vo.CmmPromotionOrderVO;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.SysUserAuthorityVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.a1stream.unit.service.SDM0505Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.base.util.StringUtils;
import com.ymsl.solid.report.exporter.PdfReportExporter;
import com.ymsl.solid.web.download.DownloadResponseView;

import jakarta.annotation.Resource;

/**
* 功能描述: Upload Certification
*
* mid2303
* 2024年9月10日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/09/10   Ruan Hansheng   New
*/
@Component
public class SDM0505Facade {

    @Resource
    private SDM0505Service sdm0505Service;

    @Resource
    private HelperFacade helperFacade;

    @Resource
    private PdfReportExporter pdfExporter;

    public SDM050501BO getInitResult(SDM050501Form form) {

        SDM050501BO result = sdm0505Service.getInitResult(form);

        SystemParameterVO systemParameterVO = sdm0505Service.getSystemParameterVO(CommonConstants.CHAR_DEFAULT_SITE_ID, SystemParameterType.PROMOTION_PICTURE_URL);
        String picturePathInternal = systemParameterVO.getParameterValue();
        result.setPicturePathInternal(picturePathInternal);

        systemParameterVO = sdm0505Service.getSystemParameterVO(CommonConstants.CHAR_DEFAULT_SITE_ID, SystemParameterType.PROMOTION_PICTURE_URL_INTERNAL);
        String url = systemParameterVO.getParameterValue();
        result.setUrl(url);

        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);
        result.setUploadEndDate(sysDate.compareTo(result.getUploadEndDate()) > 0 ? CommonConstants.CHAR_ONE : CommonConstants.CHAR_ZERO);

        SysUserAuthorityVO sysUserAuthorityVO = sdm0505Service.getSysUserAuthorityVO(form.getUserId());
        String roleList = sysUserAuthorityVO.getRoleList();

        JSONArray jsonArray = JSON.parseArray(roleList);
        Set<String> roleIdSet = new HashSet<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String roleId = jsonObject.getString("roleId");
            roleIdSet.add(roleId);
        }

        boolean containsACCT = roleIdSet.contains("1") || roleIdSet.contains("2") || roleIdSet.contains("22") || roleIdSet.contains("23");
        boolean containsYMVNSD = roleIdSet.contains("17") || roleIdSet.contains("38") || roleIdSet.contains("10") || roleIdSet.contains("31");

        if (containsACCT && !containsYMVNSD) {
            result.setUserType("ACCT");
            result.setAcctApproveDisabled(false);
            result.setAcctRejectDisabled(false);
            if (!JudgementStatus.APPROVEDBYSD.equals(result.getJugementStatus())) {
                result.setAcctRejectDisabled(true);
                result.setAcctApproveDisabled(true);
                result.setPictureDisabled(true);
            }
        } else if (!containsACCT && containsYMVNSD) {
            result.setUserType("YMVNSD");
            result.setSdApproveDisabled(false);
            result.setSdRejectDisabled(false);
            result.setSdWithDrawDisabled(false);
            if (JudgementStatus.REJECTBYACCOUNT.equals(result.getJugementStatus())) {
                result.setSdWithDrawDisabled(true);
                result.setSdRejectDisabled(false);
            } else if (JudgementStatus.APPROVEDBYSD.equals(result.getJugementStatus())) {
                result.setSdRejectDisabled(true);
                result.setSdApproveDisabled(true);
            } else if (JudgementStatus.WAITINGYMVNCHECKING.equals(result.getJugementStatus())) {
                result.setSdWithDrawDisabled(true);
            } else if (JudgementStatus.APPROVEDBYYMVN.equals(result.getJugementStatus())) {
                result.setSdWithDrawDisabled(true);
            } else {
                result.setSdWithDrawDisabled(true);
                result.setSdRejectDisabled(true);
                result.setSdApproveDisabled(true);
                result.setPictureDisabled(true);
            }
        } else if (containsACCT && containsYMVNSD) {
            result.setUserType("YMVNSD-ACCT");
            result.setAcctApproveDisabled(false);
            result.setAcctRejectDisabled(false);
            result.setSdApproveDisabled(false);
            result.setSdRejectDisabled(false);
            result.setSdWithDrawDisabled(false);
            if (JudgementStatus.REJECTBYACCOUNT.equals(result.getJugementStatus())) {
                result.setAcctRejectDisabled(true);
                result.setSdWithDrawDisabled(true);
                result.setSdRejectDisabled(true);
            } else if (JudgementStatus.WAITINGYMVNCHECKING.equals(result.getJugementStatus())) {
                result.setSdWithDrawDisabled(true);
            } else if (JudgementStatus.APPROVEDBYSD.equals(result.getJugementStatus())) {
                result.setSdRejectDisabled(true);
                result.setSdApproveDisabled(true);
            } else {
                result.setSdWithDrawDisabled(true);
                result.setSdRejectDisabled(true);
                result.setSdApproveDisabled(true);
                result.setAcctRejectDisabled(true);
                result.setAcctApproveDisabled(true);
                result.setPictureDisabled(true);
            }
        } else {
            result.setUserType("DEALER");
            result.setGiftReceiptDisabled(false);
            result.setCommitDisabled(false);
            if (!JudgementStatus.WAITINGUPLOAD.equals(result.getJugementStatus()) && !JudgementStatus.REJECTBYSD.equals(result.getJugementStatus())) {
                result.setCommitDisabled(true);
                result.setPictureDisabled(true);
            }
            if (!CommonConstants.CHAR_ONE.equals(result.getUploadEndDate())) {
                result.setCommitDisabled(true);
                result.setPictureDisabled(true);
            }
        }

        result.setInvoiceUploadPathFlag(StringUtils.isNotBlankText(result.getInvoiceUploadPath()) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
        result.setRegistrationCardPathFlag(StringUtils.isNotBlankText(result.getRegistrationCardPath()) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
        result.setGiftReceiptUploadPathFlag(StringUtils.isNotBlankText(result.getGiftReceiptUploadPath1()) || StringUtils.isNotBlankText(result.getGiftReceiptUploadPath2()) || StringUtils.isNotBlankText(result.getGiftReceiptUploadPath3()) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
        result.setLuckyDrawVoucherPathFlag(StringUtils.isNotBlankText(result.getLuckyDrawVoucherPath()) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
        result.setIdCardPathFlag(StringUtils.isNotBlankText(result.getIdCardPath()) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
        result.setOthersPathFlag(StringUtils.isNotBlankText(result.getOthersPath1()) || StringUtils.isNotBlankText(result.getOthersPath2()) || StringUtils.isNotBlankText(result.getOthersPath3()) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);

        result.setGiftNmCheck1(StringUtils.isNotBlankText(result.getGiftNm1()) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
        result.setGiftNmCheck2(StringUtils.isNotBlankText(result.getGiftNm2()) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
        result.setGiftNmCheck3(StringUtils.isNotBlankText(result.getGiftNm3()) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);

        result.setSiteId(form.getSiteId());

        return result;
    }

    public void uploadPicture(SDM050501Form form) {

        
        CmmPromotionOrderVO cmmPromotionOrderVO = sdm0505Service.getCmmPromotionOrderVO(form.getPromotionOrderId());
        String uploadType = form.getUploadType();
        String uploadPath = form.getUploadPath();
        switch(uploadType) {
            case "invoice": 
                cmmPromotionOrderVO.setInvoiceUploadPath(uploadPath);
                break;
            case "giftReceipt1":
                cmmPromotionOrderVO.setGiftReceiptUploadPath1(uploadPath);
                break;
            case "giftReceipt2":
                cmmPromotionOrderVO.setGiftReceiptUploadPath2(uploadPath);
                break;
            case "giftReceipt3":
                cmmPromotionOrderVO.setGiftReceiptUploadPath3(uploadPath);
                break;
            case "idCard":
                cmmPromotionOrderVO.setIdCardPath(uploadPath);
                break;
            case "registrationCard":
                cmmPromotionOrderVO.setRegistrationCardPath(uploadPath);
                break;
            case "luckyDrawVoucher":
                cmmPromotionOrderVO.setLuckyDrawVoucherPath(uploadPath);
                break;
            case "others1":
                cmmPromotionOrderVO.setOthersPath1(uploadPath);
                break;
            case "others2":
                cmmPromotionOrderVO.setOthersPath2(uploadPath);
                break;
            case "others3":
                cmmPromotionOrderVO.setOthersPath3(uploadPath);
                break;
            default:
                break;
        }
        sdm0505Service.uploadPicture(cmmPromotionOrderVO);
    }

    public void ajaxResponse(SDM050501Form form) {

        sdm0505Service.ajaxResponse(form);
    }

    public DownloadResponseView printGiftReceipt(SDM050501Form form) {

        List<SDM050501PrintBO> dataList = new ArrayList<>();

        SDM050501PrintBO printBO = new SDM050501PrintBO();

        //dataEdit
        CmmSiteMasterVO cmmSiteMasterVO = sdm0505Service.getCmmSiteMasterVO(form.getSiteId());
        if (!Nulls.isNull(cmmSiteMasterVO)) {
            printBO.setDealerName(cmmSiteMasterVO.getSiteNm());
        }

        printBO.setDealerCode(form.getSiteId());

        SDM050501BO formData = form.getFormData();
        if (!Nulls.isNull(formData)) {
            printBO.setPromotionName(formData.getPromotionNm());
            printBO.setCustomer(formData.getCustomerNm());
            printBO.setModelName(formData.getModelNm());
            printBO.setFrameNo(formData.getFrameNo());
            printBO.setGiftName(this.giftNameEdit(formData));
        }
        dataList.add(printBO);

        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_GIFTRECEIPT_REPORT,
                                                   dataList,
                                                   CommonConstants.CHAR_BLANK);

        return new DownloadResponseView(rp);
    }

    private String giftNameEdit(SDM050501BO form) {

        StringBuilder builder = new StringBuilder();
        String[] giftNames = {form.getGiftNm1(), form.getGiftNm2(), form.getGiftNm3()};
        String[] giftChecks = {form.getGiftNmCheck1(), form.getGiftNmCheck2(), form.getGiftNmCheck3()};

        for (int i = 0; i < giftNames.length; i++) {

            if (CommonConstants.CHAR_Y.equals(giftChecks[i])) {

                if (builder.length() > 0) {

                    builder.append(CommonConstants.CHAR_SPACE)
                           .append(CommonConstants.CHAR_AND)
                           .append(CommonConstants.CHAR_SPACE);
                }

                builder.append(giftNames[i]);
            }
        }
        return builder.isEmpty() ? null : builder.toString();
    }
}
