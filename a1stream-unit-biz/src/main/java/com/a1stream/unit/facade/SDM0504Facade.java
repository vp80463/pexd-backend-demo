package com.a1stream.unit.facade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.JudgementStatus;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.domain.bo.unit.SDM050401BO;
import com.a1stream.domain.form.unit.SDM050401Form;
import com.a1stream.domain.vo.CmmPromotionOrderZipHistoryVO;
import com.a1stream.domain.vo.CmmUnitPromotionListVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.domain.vo.SysUserAuthorityVO;
import com.a1stream.unit.service.SDM0504Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

/**
* 功能描述: YMVN Check Promotion Judgement
*
* mid2303
* 2024年9月3日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/09/03   Ruan Hansheng   New
*/
@Component
public class SDM0504Facade {

    @Resource
    private SDM0504Service sdm0504Service;

    @Resource
    private HelperFacade helperFacade;

    public Page<SDM050401BO> getPromotionJudgement(SDM050401Form form) {

        Long pointId = form.getPointId();
        if (!ObjectUtils.isEmpty(pointId)) {
            MstFacilityVO mstFacilityVO = sdm0504Service.getMstFacilityVO(pointId);
            if (null == mstFacilityVO) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.point"), form.getPointDesc(), CodedMessageUtils.getMessage("label.tablePointInfo")}));
            }
        }

        Long promotionListId = form.getPromotionListId();
        if (!ObjectUtils.isEmpty(promotionListId)) {
            CmmUnitPromotionListVO cmmUnitPromotionListVO = sdm0504Service.getCmmUnitPromotionListVO(promotionListId);
            if (null == cmmUnitPromotionListVO) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.promotion"), form.getPromotion(), CodedMessageUtils.getMessage("label.tablePromotionList")}));
            }
        }

        String frameNo = form.getFrameNo();
        if (StringUtils.isNotBlankText(frameNo)) {
            SerializedProductVO serializedProductVO = sdm0504Service.getSerializedProductVO(form.getSiteId(), form.getFrameNo());
            if (null == serializedProductVO) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.frameNo"), form.getFrameNo(), CodedMessageUtils.getMessage("label.tableSerializedProductInfo")}));
            }
        }

        Page<SDM050401BO> resultPage = sdm0504Service.getPromotionJudgement(form);
        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(JudgementStatus.CODE_ID);
        List<SDM050401BO> content = new ArrayList<>(resultPage.getContent());
        for (SDM050401BO bo : content) {
            bo.setInvoiceUploadPathFlag(StringUtils.isNotBlankText(bo.getInvoiceUploadPath()) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
            bo.setRegistrationCardPathFlag(StringUtils.isNotBlankText(bo.getRegistrationCardPath()) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
            bo.setGiftReceiptUploadPathFlag(StringUtils.isNotBlankText(bo.getGiftReceiptUploadPath1()) || StringUtils.isNotBlankText(bo.getGiftReceiptUploadPath2()) || StringUtils.isNotBlankText(bo.getGiftReceiptUploadPath3()) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
            bo.setLuckyDrawVoucherPathFlag(StringUtils.isNotBlankText(bo.getLuckyDrawVoucherPath()) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
            bo.setIdCardPathFlag(StringUtils.isNotBlankText(bo.getIdCardPath()) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
            bo.setOthersPathFlag(StringUtils.isNotBlankText(bo.getOthersPath1()) || StringUtils.isNotBlankText(bo.getOthersPath2()) || StringUtils.isNotBlankText(bo.getOthersPath3()) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
            bo.setJudgement(codeMap.get(bo.getJudgement()));
        }

        return resultPage;
    }

    public SDM050401Form getUserType(SDM050401Form form) {

        SysUserAuthorityVO sysUserAuthorityVO = sdm0504Service.getSysUserAuthorityVO(form.getUserId());
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
            form.setUserType("ACCT");
        } else if (!containsACCT && containsYMVNSD) {
            form.setUserType("YMVNSD");
        } else if (containsACCT && containsYMVNSD) {
            form.setUserType("YMVNSD-ACCT");
        } else {
            form.setUserType("DEALER");
        }
        return form;
    }

    public SDM050401Form zipXml(SDM050401Form form) {

        return sdm0504Service.zipXml(form);
    }

    public SDM050401Form downloadXml(SDM050401Form form) {

        String zipNm = form.getZipNm();
        if (null == zipNm) {
            return form;
        }

        Long promotionListId = form.getPromotionListId();
        CmmPromotionOrderZipHistoryVO cmmPromotionOrderZipHistoryVO = sdm0504Service.getCmmPromotionOrderZipHistoryVO(promotionListId, zipNm);

        // 下载文件
        form.setCmmPromotionOrderZipHistoryId(cmmPromotionOrderZipHistoryVO.getPromotionOrderZipHistoryId());
        return form;
    }

    public SDM050401Form sendToYmvn(SDM050401Form form) {

        return sdm0504Service.sendToYmvn(form);
    }

    public SDM050401Form approve(SDM050401Form form) {

        return sdm0504Service.approve(form);
    }

    public SDM050401Form reject(SDM050401Form form) {

        return sdm0504Service.reject(form);
    }
}
