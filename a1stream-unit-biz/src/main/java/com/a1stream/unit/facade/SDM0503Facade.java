package com.a1stream.unit.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.JudgementStatus;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.domain.bo.unit.SDM050301BO;
import com.a1stream.domain.form.unit.SDM050301Form;
import com.a1stream.domain.vo.CmmUnitPromotionListVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.unit.service.SDM0503Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

/**
* 功能描述: Dealer check Promotion Result
*
* mid2303
* 2024年8月29日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/08/29   Ruan Hansheng   New
*/
@Component
public class SDM0503Facade {

    @Resource
    private SDM0503Service sdm0503Service;

    @Resource
    private HelperFacade helperFacade;

    public Page<SDM050301BO> getPromotionResult(SDM050301Form form) {

        Long pointId = form.getPointId();
        if (!ObjectUtils.isEmpty(pointId)) {
            MstFacilityVO mstFacilityVO = sdm0503Service.getMstFacilityVO(pointId);
            if (null == mstFacilityVO) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.point"), form.getPointDesc(), CodedMessageUtils.getMessage("label.tablePointInfo")}));
            }
        }

        Long promotionListId = form.getPromotionListId();
        if (!ObjectUtils.isEmpty(promotionListId)) {
            CmmUnitPromotionListVO cmmUnitPromotionListVO = sdm0503Service.getCmmUnitPromotionListVO(promotionListId);
            if (null == cmmUnitPromotionListVO) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.promotion"), form.getPromotion(), CodedMessageUtils.getMessage("label.tablePromotionList")}));
            }
        }

        String frameNo = form.getFrameNo();
        if (StringUtils.isNotBlankText(frameNo)) {
            SerializedProductVO serializedProductVO = sdm0503Service.getSerializedProductVO(form.getSiteId(), form.getFrameNo());
            if (null == serializedProductVO) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.frameNo"), form.getFrameNo(), CodedMessageUtils.getMessage("label.tableSerializedProductInfo")}));
            }
        }

        Page<SDM050301BO> resultPage = sdm0503Service.getPromotionResult(form);
        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(JudgementStatus.CODE_ID);
        List<SDM050301BO> content = new ArrayList<>(resultPage.getContent());
        for (SDM050301BO bo : content) {
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
}
