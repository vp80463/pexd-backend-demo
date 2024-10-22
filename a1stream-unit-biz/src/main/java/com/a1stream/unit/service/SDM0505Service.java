package com.a1stream.unit.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.JudgementStatus;
import com.a1stream.common.constants.MstCodeConstants.SystemParameterType;
import com.a1stream.common.manager.RunShellManager;
import com.a1stream.domain.bo.unit.SDM050501BO;
import com.a1stream.domain.entity.CmmPromotionOrder;
import com.a1stream.domain.form.unit.SDM050501Form;
import com.a1stream.domain.repository.CmmPromotionOrderRepository;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.repository.SysUserAuthorityRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.CmmPromotionOrderVO;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.SysUserAuthorityVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;

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
@Service
public class SDM0505Service {

    @Resource
    private CmmPromotionOrderRepository cmmPromotionOrderRepository;

    @Resource
    private SysUserAuthorityRepository sysUserAuthorityRepository;

    @Resource
    private SystemParameterRepository systemParameterRepository;

    @Resource
    private RunShellManager runShellManager;

    @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepository;

    public SDM050501BO getInitResult(SDM050501Form form) {

        return cmmPromotionOrderRepository.getInitResult(form);
    }

    public SysUserAuthorityVO getSysUserAuthorityVO(String userId) {

        return BeanMapUtils.mapTo(sysUserAuthorityRepository.findFirstByUserId(userId), SysUserAuthorityVO.class);
    }

    public SystemParameterVO getSystemParameterVO(String siteId, String systemParameterTypeId) {

        return BeanMapUtils.mapTo(systemParameterRepository.findSystemParameterBySiteIdAndSystemParameterTypeId(siteId, systemParameterTypeId), SystemParameterVO.class);
    }

    public CmmPromotionOrderVO getCmmPromotionOrderVO(Long promotionOrderId) {

        return BeanMapUtils.mapTo(cmmPromotionOrderRepository.findByPromotionOrderId(promotionOrderId), CmmPromotionOrderVO.class);
    }

    public void uploadPicture(CmmPromotionOrderVO cmmPromotionOrderVO) {

        if (null != cmmPromotionOrderVO) {
            cmmPromotionOrderRepository.save(BeanMapUtils.mapTo(cmmPromotionOrderVO, CmmPromotionOrder.class));
        }
    }

    public void ajaxResponse(SDM050501Form form) {

        SDM050501BO formData = form.getFormData();
        CmmPromotionOrderVO cmmPromotionOrderVO = this.getCmmPromotionOrderVO(formData.getPromotionOrderId());
        String ajaxType = formData.getAjaxType();
        switch(ajaxType) {
            case "sdWithDraw": 
                cmmPromotionOrderVO.setJugementStatus(JudgementStatus.REJECTBYSD);
                break;
            case "sdApprove":
                cmmPromotionOrderVO.setJugementStatus(JudgementStatus.APPROVEDBYSD);
                break;
            case "sdReject":
                cmmPromotionOrderVO.setJugementStatus(JudgementStatus.WAITINGYMVNCHECKING);
                cmmPromotionOrderVO.setRejectReason(formData.getRejectReason());
                break;
            case "acctApprove":
                cmmPromotionOrderVO.setJugementStatus(JudgementStatus.APPROVEDBYYMVN);
                updateDbPath(cmmPromotionOrderVO);
                break;
            case "acctReject":
                cmmPromotionOrderVO.setJugementStatus(JudgementStatus.REJECTBYACCOUNT);
                cmmPromotionOrderVO.setRejectReason(formData.getRejectReason());
                break;
            case "commit":
                cmmPromotionOrderVO.setJugementStatus(JudgementStatus.WAITINGYMVNCHECKING);
                cmmPromotionOrderVO.setInvoiceMemo(formData.getInvoiceMemo());
                cmmPromotionOrderVO.setVerificationCodeMemo(formData.getVerificationCodeMemo());
                cmmPromotionOrderVO.setLinkMemo(formData.getLinkMemo());
                break;
            default:
                break;
        }

        if (null != cmmPromotionOrderVO) {
            cmmPromotionOrderRepository.save(BeanMapUtils.mapTo(cmmPromotionOrderVO, CmmPromotionOrder.class));
        }
    }

    public void checkExitPath(String path, List<String> waittingApproveList) {
        if(StringUtils.isNotBlankText(path)) {
            waittingApproveList.add(path);
        }
    }

    public void updateDbPath(CmmPromotionOrderVO order) {

        List<String> waittingApproveList = new ArrayList<>();
        checkExitPath(order.getInvoiceUploadPath(), waittingApproveList);
        checkExitPath(order.getGiftReceiptUploadPath1(), waittingApproveList);
        checkExitPath(order.getGiftReceiptUploadPath2(), waittingApproveList);
        checkExitPath(order.getGiftReceiptUploadPath3(), waittingApproveList);
        checkExitPath(order.getIdCardPath(), waittingApproveList);
        checkExitPath(order.getRegistrationCardPath(), waittingApproveList);
        checkExitPath(order.getLuckyDrawVoucherPath(), waittingApproveList);
        checkExitPath(order.getOthersPath1(), waittingApproveList);
        checkExitPath(order.getOthersPath2(), waittingApproveList);
        checkExitPath(order.getOthersPath3(), waittingApproveList);

        String previous = "/promotion_picture_path/";
        StringBuilder pathsBuilder = new StringBuilder();
        for (int i = 0; i < waittingApproveList.size(); i++) {
            String waittingApprove = waittingApproveList.get(i);
            StringBuilder sb = new StringBuilder();
            sb.append(previous);
            sb.append(waittingApprove);
            
            if (i > 0) {
                pathsBuilder.append(CommonConstants.CHAR_COMMA);
            }
            pathsBuilder.append(sb);
        }
        String paths = pathsBuilder.toString();
        
        String currentDateTime = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMDHMS);
        String testCmd = "execForApprove.sh" + CommonConstants.CHAR_SPACE + paths + CommonConstants.CHAR_SPACE + currentDateTime;
        // 调用shell进行压缩处理
        runShellManager.runShell(SystemParameterType.PROMOTION_BATCH_URL, testCmd);
    }

    public CmmSiteMasterVO getCmmSiteMasterVO(String siteId) {
        return BeanMapUtils.mapTo(cmmSiteMasterRepository.findFirstBySiteId(siteId), CmmSiteMasterVO.class);
    }
}
