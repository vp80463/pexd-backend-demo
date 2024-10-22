package com.a1stream.unit.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.JudgementStatus;
import com.a1stream.common.constants.MstCodeConstants.SystemParameterType;
import com.a1stream.common.manager.RunShellManager;
import com.a1stream.domain.bo.unit.SDM050401BO;
import com.a1stream.domain.entity.CmmPromotionOrder;
import com.a1stream.domain.entity.CmmPromotionOrderZipHistory;
import com.a1stream.domain.entity.CmmUnitPromotionList;
import com.a1stream.domain.form.unit.SDM050401Form;
import com.a1stream.domain.repository.CmmPromotionOrderRepository;
import com.a1stream.domain.repository.CmmPromotionOrderZipHistoryRepository;
import com.a1stream.domain.repository.CmmUnitPromotionListRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.a1stream.domain.repository.SysUserAuthorityRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.CmmPromotionOrderVO;
import com.a1stream.domain.vo.CmmPromotionOrderZipHistoryVO;
import com.a1stream.domain.vo.CmmUnitPromotionListVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.domain.vo.SysUserAuthorityVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.CollectionUtils;
import com.ymsl.solid.base.util.DateUtils;
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
@Service
public class SDM0504Service {

    @Resource
    private CmmPromotionOrderRepository cmmPromotionOrderRepository;

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    @Resource
    private CmmUnitPromotionListRepository cmmUnitPromotionListRepository;

    @Resource
    private SerializedProductRepository serializedProductRepository;

    @Resource
    private SysUserAuthorityRepository sysUserAuthorityRepository;

    @Resource
    private SystemParameterRepository systemParameterRepository;

    @Resource
    private CmmPromotionOrderZipHistoryRepository cmmPromotionOrderZipHistoryRepository;

    @Resource
    private RunShellManager runShellManager;

    public Page<SDM050401BO> getPromotionJudgement(SDM050401Form form) {

        return cmmPromotionOrderRepository.getPromotionJudgement(form);
    }

    public MstFacilityVO getMstFacilityVO(Long facilityId) {

        return BeanMapUtils.mapTo(mstFacilityRepository.findByFacilityId(facilityId), MstFacilityVO.class);
    }

    public CmmUnitPromotionListVO getCmmUnitPromotionListVO(Long promotionListId) {

        return BeanMapUtils.mapTo(cmmUnitPromotionListRepository.findByPromotionListId(promotionListId), CmmUnitPromotionListVO.class);
    }

    public SerializedProductVO getSerializedProductVO(String siteId, String frameNo) {

        return BeanMapUtils.mapTo(serializedProductRepository.findBySiteIdAndFrameNo(siteId, frameNo), SerializedProductVO.class);
    }

    public SysUserAuthorityVO getSysUserAuthorityVO(String userId) {

        return BeanMapUtils.mapTo(sysUserAuthorityRepository.findFirstByUserId(userId), SysUserAuthorityVO.class);
    }

    public SystemParameterVO getSystemParameterVO(String siteId, String systemParameterTypeId, String parameterValue) {

        return BeanMapUtils.mapTo(systemParameterRepository.findBySiteIdAndSystemParameterTypeIdAndParameterValue(siteId, systemParameterTypeId, parameterValue), SystemParameterVO.class);
    }

    public SystemParameterVO getSystemParameterVO(String siteId, String systemParameterTypeId) {

        return BeanMapUtils.mapTo(systemParameterRepository.findSystemParameterBySiteIdAndSystemParameterTypeId(siteId, systemParameterTypeId), SystemParameterVO.class);
    }

    public SDM050401Form zipXml(SDM050401Form form) {
                
        SystemParameterVO systemParameterVO = this.getSystemParameterVO(SystemParameterType.INITIALBATCHFLAG, CommonConstants.CHAR_ONE, CommonConstants.CHAR_DEFAULT_SITE_ID);
        if (null != systemParameterVO) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00525"));
        }

        List<SDM050401BO> allTableDataList = form.getAllTableDataList();
        if (CollectionUtils.isEmpty(allTableDataList)) {
            return form;
        }

        List<String> waittingApproveList = new ArrayList<>();
        for (SDM050401BO bo : allTableDataList) {
            checkExitPath(bo.getInvoiceUploadPath(), waittingApproveList);
            checkExitPath(bo.getGiftReceiptUploadPath1(), waittingApproveList);
            checkExitPath(bo.getGiftReceiptUploadPath2(), waittingApproveList);
            checkExitPath(bo.getGiftReceiptUploadPath3(), waittingApproveList);
            checkExitPath(bo.getIdCardPath(), waittingApproveList);
            checkExitPath(bo.getRegistrationCardPath(), waittingApproveList);
            checkExitPath(bo.getLuckyDrawVoucherPath(), waittingApproveList);
            checkExitPath(bo.getOthersPath1(), waittingApproveList);
            checkExitPath(bo.getOthersPath2(), waittingApproveList);
            checkExitPath(bo.getOthersPath3(), waittingApproveList);
        }

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
        String testCmd = "execForZip.sh" + CommonConstants.CHAR_SPACE + paths + CommonConstants.CHAR_SPACE + currentDateTime;
        // 调用shell进行压缩处理
        runShellManager.runShell(SystemParameterType.PROMOTION_BATCH_URL, testCmd);

        // 获取压缩目录的路径
        systemParameterVO = this.getSystemParameterVO(CommonConstants.CHAR_DEFAULT_SITE_ID, SystemParameterType.PROMOTION_XML_ROOT_DIR);
        String xmlRootDir = systemParameterVO.getParameterValue();
        String zipNm = currentDateTime + ".zip";

        List<CmmPromotionOrderZipHistoryVO> cmmPromotionOrderZipHistoryVOList = new ArrayList<>();
        for (SDM050401BO bo : allTableDataList) {
            CmmPromotionOrderZipHistoryVO cmmPromotionOrderZipHistoryVO = new CmmPromotionOrderZipHistoryVO();
            cmmPromotionOrderZipHistoryVO.setPromotionListId(bo.getPromotionOrderId());
            cmmPromotionOrderZipHistoryVO.setPromotionOrderId(bo.getPromotionOrderId());
            cmmPromotionOrderZipHistoryVO.setZipPath(xmlRootDir);
            cmmPromotionOrderZipHistoryVO.setZipNm(zipNm);
            cmmPromotionOrderZipHistoryVO.setSiteId(form.getSiteId());
            cmmPromotionOrderZipHistoryVOList.add(cmmPromotionOrderZipHistoryVO);
        }

        cmmPromotionOrderZipHistoryRepository.saveInBatch(BeanMapUtils.mapListTo(cmmPromotionOrderZipHistoryVOList, CmmPromotionOrderZipHistory.class));

        form.setZipNm(zipNm);
        return form;
    }

    public CmmPromotionOrderZipHistoryVO getCmmPromotionOrderZipHistoryVO(Long promotionListId, String zipNm) {

        return BeanMapUtils.mapTo(cmmPromotionOrderZipHistoryRepository.findByPromotionListIdAndZipNm(promotionListId, zipNm), CmmPromotionOrderZipHistoryVO.class);
    }

    public SDM050401Form sendToYmvn(SDM050401Form form) {

        String promotionCd = form.getPromotionCd();
        String testCmd = "execForSend.sh" + CommonConstants.CHAR_SPACE + promotionCd;

        // 调用shell进行发送处理
        runShellManager.runShell(SystemParameterType.PROMOTION_BATCH_URL, testCmd);

        // 更新batchFlag
        CmmUnitPromotionListVO cmmUnitPromotionListVO = this.getCmmUnitPromotionListVO(form.getPromotionListId());
        cmmUnitPromotionListVO.setBatchFlag(CommonConstants.CHAR_Y);
        cmmUnitPromotionListRepository.save(BeanMapUtils.mapTo(cmmUnitPromotionListVO, CmmUnitPromotionList.class));

        return form;
    }

    public List<CmmPromotionOrderVO> getCmmPromotionOrderVOList(List<Long> promotionOrderIdList) {

        return BeanMapUtils.mapListTo(cmmPromotionOrderRepository.findByPromotionOrderIdIn(promotionOrderIdList), CmmPromotionOrderVO.class);
    }

    public SDM050401Form approve(SDM050401Form form) {

        List<SDM050401BO> allTableDataList = form.getAllTableDataList();
        if (CollectionUtils.isEmpty(allTableDataList)) {
            return form;
        }
        List<Long> promotionOrderIdList = allTableDataList.stream().map(bo -> bo.getPromotionOrderId()).collect(Collectors.toList());
        List<CmmPromotionOrderVO> cmmPromotionOrderVOList = this.getCmmPromotionOrderVOList(promotionOrderIdList);
        Map<Long, CmmPromotionOrderVO> promotionOrderMap = cmmPromotionOrderVOList.stream().collect(Collectors.toMap(CmmPromotionOrderVO::getPromotionOrderId, Function.identity()));
        for (SDM050401BO bo : allTableDataList) {
            CmmPromotionOrderVO vo = promotionOrderMap.get(bo.getPromotionOrderId());
            String userType = form.getUserType();
            if ("YMVNSD".equals(userType)) {
                vo.setJugementStatus(JudgementStatus.APPROVEDBYSD);
            } else if ("ACCT".equals(userType)) {
                vo.setJugementStatus(JudgementStatus.APPROVEDBYYMVN);
                updateDbPath(vo);
            } else if ("YMVNSD-ACCT".equals(userType)) {
                if (JudgementStatus.WAITINGYMVNCHECKING.equals(vo.getJugementStatus())) {
                    vo.setJugementStatus(JudgementStatus.APPROVEDBYSD);
                } else if (JudgementStatus.APPROVEDBYSD.equals(vo.getJugementStatus())) {
                    vo.setJugementStatus(JudgementStatus.APPROVEDBYYMVN);
                } else if (JudgementStatus.REJECTBYACCOUNT.equals(vo.getJugementStatus())) {
                    vo.setJugementStatus(JudgementStatus.APPROVEDBYYMVN);
                }
            }
        }

        cmmPromotionOrderRepository.saveInBatch(BeanMapUtils.mapListTo(cmmPromotionOrderVOList, CmmPromotionOrder.class));

        return form;
    }

    public SDM050401Form reject(SDM050401Form form) {

        List<SDM050401BO> allTableDataList = form.getAllTableDataList();
        if (CollectionUtils.isEmpty(allTableDataList)) {
            return form;
        }
        List<Long> promotionOrderIdList = allTableDataList.stream().map(bo -> bo.getPromotionOrderId()).collect(Collectors.toList());
        List<CmmPromotionOrderVO> cmmPromotionOrderVOList = this.getCmmPromotionOrderVOList(promotionOrderIdList);
        Map<Long, CmmPromotionOrderVO> promotionOrderMap = cmmPromotionOrderVOList.stream().collect(Collectors.toMap(CmmPromotionOrderVO::getPromotionOrderId, Function.identity()));
        for (SDM050401BO bo : allTableDataList) {
            CmmPromotionOrderVO vo = promotionOrderMap.get(bo.getPromotionOrderId());
            String userType = form.getUserType();
            if ("YMVNSD".equals(userType)) {
                vo.setJugementStatus(JudgementStatus.APPROVEDBYSD);
            } else if ("ACCT".equals(userType)) {
                vo.setJugementStatus(JudgementStatus.REJECTBYACCOUNT);
            } else if ("YMVNSD-ACCT".equals(userType)) {
                if (JudgementStatus.WAITINGYMVNCHECKING.equals(vo.getJugementStatus())) {
                    vo.setJugementStatus(JudgementStatus.REJECTBYSD);
                } else if (JudgementStatus.APPROVEDBYSD.equals(vo.getJugementStatus())) {
                    vo.setJugementStatus(JudgementStatus.REJECTBYACCOUNT);
                }
            }
            vo.setRejectReason(form.getRejectReason());
        }

        cmmPromotionOrderRepository.saveInBatch(BeanMapUtils.mapListTo(cmmPromotionOrderVOList, CmmPromotionOrder.class));

        return form;
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
}
