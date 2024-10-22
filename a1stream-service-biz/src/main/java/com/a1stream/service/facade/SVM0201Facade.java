package com.a1stream.service.facade;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.common.constants.PJConstants.ServiceRequestStatus;
import com.a1stream.common.constants.PJConstants.WarrantyPolicyType;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.service.SVM020101BO;
import com.a1stream.domain.bo.service.SVM020102BO;
import com.a1stream.domain.bo.service.SVM020102FreeCouponBO;
import com.a1stream.domain.bo.service.SVM0201PrintBO;
import com.a1stream.domain.bo.service.SVM0201PrintDetailBO;
import com.a1stream.domain.form.service.SVM020101Form;
import com.a1stream.domain.form.service.SVM020102Form;
import com.a1stream.domain.vo.CmmConditionVO;
import com.a1stream.domain.vo.CmmServiceDemandVO;
import com.a1stream.domain.vo.CmmSymptomVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.domain.vo.ServiceOrderVO;
import com.a1stream.domain.vo.ServiceRequestBatteryVO;
import com.a1stream.domain.vo.ServiceRequestJobVO;
import com.a1stream.domain.vo.ServiceRequestPartsVO;
import com.a1stream.domain.vo.ServiceRequestVO;
import com.a1stream.service.service.SVM0201Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.report.exporter.PdfReportExporter;
import com.ymsl.solid.report.exporter.cfg.PdfExportConfiguration;
import com.ymsl.solid.report.exporter.cfg.PdfExportConfiguration.Builder;
import com.ymsl.solid.web.download.DownloadResponseView;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid1966
*/
@Component
public class SVM0201Facade {

    @Resource
    private SVM0201Service svm0201Service;

    @Resource
    private HelperFacade helperFacade;

    @Resource
    public void setPdfReportExporter(PdfReportExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
    }
    private PdfReportExporter pdfExporter;

    private static final String POLICY_TYPE_1_YEARS = "1 Years/10000 km";
    private static final String POLICY_TYPE_2_YEARS = "2 Years";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);
    private static final String PARTNO_STR = "partNo";
    private static final String PARTNAME_STR = "partName";
    private static final String QTY_STR = "qty";

    public List<SVM020101BO> findServiceRequestList(SVM020101Form form) {

        this.validateData(form);

        List<SVM020101BO> resultList = svm0201Service.findServiceRequestList(form);

        // 数据库为codeDbid 前台显示为codeData1
        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(ServiceCategory.CODE_ID, ServiceRequestStatus.CODE_ID);

        for(SVM020101BO bo : resultList) {

            bo.setCategory(codeMap.get(bo.getRequestType()));
            bo.setStatus(codeMap.get(bo.getRequestStatus()));

            if (StringUtils.equals(bo.getServiceCategoryId(), ServiceCategory.CLAIMBATTERY.getCodeDbid())) {
                handleClaimBattery(bo);
            } else {
                handleOtherCategory(bo);
            }
        }

        return resultList;
    }

    public void updateServiceRequest(SVM020101Form form) {

        List<SVM020101BO> tableDataList = form.getTableDataList();

        List<SVM020101BO> selectedTableDataList = tableDataList.stream()
                                                               .filter(bo -> CommonConstants.CHAR_Y.equals(bo.getSelectFlag()))
                                                               .collect(Collectors.toList());

        List<Long> serviceRequestIdList = selectedTableDataList.stream().map(SVM020101BO::getServiceRequestId).collect(Collectors.toList());
        List<ServiceRequestVO> serviceRequestVOList = svm0201Service.getServiceRequestList(serviceRequestIdList);
        Map<Long, ServiceRequestVO> serviceRequestVOMap = serviceRequestVOList.stream().collect(Collectors.toMap(ServiceRequestVO::getServiceRequestId, Function.identity()));
        List<ServiceRequestVO> updateList = new ArrayList<>();

        for(SVM020101BO bo : selectedTableDataList) {

            ServiceRequestVO vo = serviceRequestVOMap.get(bo.getServiceRequestId());

            if(vo != null) {

                vo.setRequestStatus(ServiceRequestStatus.ISSUED.getCodeDbid());
                updateList.add(vo);
            }
        }

        svm0201Service.updateIssue(updateList);

        // 更新之后进行 I/F 处理(待开发)
    }

    public SVM020102Form initialScreen(SVM020102Form form, PJUserDetails uc) {

        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(ServiceCategory.CODE_ID, ServiceRequestStatus.CODE_ID);
        SVM020102Form initForm = svm0201Service.getInitList(form, codeMap);

        MstFacilityVO mstFacilityVO = svm0201Service.findByFacilityId(form.getPointId());
        ServiceRequestVO serviceRequestVO = svm0201Service.findServiceRequestVO(form.getServiceRequestId());
        ServiceOrderVO serviceOrderVO = svm0201Service.findServiceOrderVO(form.getServiceOrderId());
        SerializedProductVO serializedProductVO = svm0201Service.findSerializedProductVO(form.getSerializedProductId());
        CmmSymptomVO cmmSymptomVO = serviceRequestVO != null && serviceRequestVO.getSymptomId() != null ? svm0201Service.findCmmSymptomVO(serviceRequestVO.getSymptomId()) : null;
        CmmConditionVO cmmConditionVO = serviceRequestVO != null && serviceRequestVO.getConditionId() != null ? svm0201Service.findCmmConditionVO(serviceRequestVO.getConditionId()) : null;
        MstProductVO mstProductVO = serviceRequestVO != null && serviceRequestVO.getMainDamagePartsId() != null ? svm0201Service.findMstProductVO(serviceRequestVO.getMainDamagePartsId()) : null;

        initForm.setCategory(codeMap.get(form.getCategoryId()));
        // DealerCode 需Cd + Nm 待变更
        initForm.setDealerCode(uc.getDealerCode());
        initForm.setStatus(codeMap.get(form.getStatus()));
        initForm.setModel(form.getModel());
        initForm.setPlateNo(form.getPlateNo());
        initForm.setPolicyType(form.getPolicyType());
        initForm.setServiceRequestId(form.getServiceRequestId());
        initForm.setServiceOrderId(form.getServiceOrderId());
        initForm.setSerializedProductId(form.getSerializedProductId());
        initForm.setCategoryId(form.getCategoryId());
        initForm.setPointId(form.getPointId());

        if(mstFacilityVO != null) {

            initForm.setPoint(mstFacilityVO.getFacilityCd() + CommonConstants.CHAR_SPACE + mstFacilityVO.getFacilityNm());
        }

        if (serviceRequestVO != null) {
            initForm.setRequestNo(serviceRequestVO.getRequestNo());
            initForm.setRequestDate(serviceRequestVO.getRequestDate());
            initForm.setFreeCoupon(serviceRequestVO.getServiceDemandId());
            initForm.setCampaignNo(serviceRequestVO.getCampaignNo());
            initForm.setFactoryReceiptNo(serviceRequestVO.getFactoryReceiptNo());
            initForm.setFactoryReceiptDate(serviceRequestVO.getFactoryReceiptDate());
            initForm.setMileage(serviceRequestVO.getMileage());

            if (form.getCategoryId().equals(ServiceCategory.FREECOUPON.getCodeDbid())) {
                initForm.setServiceDate(serviceRequestVO.getServiceDate());
            }

            if (!form.getCategoryId().equals(ServiceCategory.FREECOUPON.getCodeDbid())) {
                initForm.setBulletinNo(serviceRequestVO.getBulletinNo());
                initForm.setProblemDate(serviceRequestVO.getSituationHappenDate());
                initForm.setProblemComment(serviceRequestVO.getProblemComment());
                initForm.setReasonComment(serviceRequestVO.getReasonComment());
                initForm.setRepairComment(serviceRequestVO.getRepairComment());
                initForm.setShopComment(serviceRequestVO.getShopComment());
                initForm.setAuthorizationNo(serviceRequestVO.getAuthorizationNo());
            }
        }

        if (serviceOrderVO != null) {
            initForm.setServiceOrderNo(serviceOrderVO.getOrderNo());
            initForm.setConsumer(serviceOrderVO.getConsumerFullNm());
            initForm.setSoldDate(serviceOrderVO.getSoldDate());

            if (!form.getCategoryId().equals(ServiceCategory.FREECOUPON.getCodeDbid())) {
                initForm.setRepairDate(serviceOrderVO.getSettleDate());
                initForm.setReception(safeString(serviceOrderVO.getCashierCd()) + CommonConstants.CHAR_SPACE + safeString(serviceOrderVO.getCashierNm()));
                initForm.setMechanic(safeString(serviceOrderVO.getMechanicCd()) + CommonConstants.CHAR_SPACE + safeString(serviceOrderVO.getMechanicNm()));
            }
        }

        if (serializedProductVO != null) {
            initForm.setFrameNo(serializedProductVO.getFrameNo());
            initForm.setEngineNo(serializedProductVO.getEngineNo());
        }

        if (cmmSymptomVO != null && !form.getCategoryId().equals(ServiceCategory.FREECOUPON.getCodeDbid())) {
            initForm.setSymptom(safeString(cmmSymptomVO.getSymptomCd()) + CommonConstants.CHAR_SPACE + safeString(cmmSymptomVO.getDescription()));
        }

        if (cmmConditionVO != null && !form.getCategoryId().equals(ServiceCategory.FREECOUPON.getCodeDbid())) {
            initForm.setCondition(safeString(cmmConditionVO.getConditionCd()) + CommonConstants.CHAR_SPACE + safeString(cmmConditionVO.getDescription()));
        }

        if (mstProductVO != null && !form.getCategoryId().equals(ServiceCategory.FREECOUPON.getCodeDbid())) {
            initForm.setMainDamageParts(mstProductVO.getProductCd() + CommonConstants.CHAR_SPACE + mstProductVO.getLocalDescription());
        }

        return initForm;
    }

    public void confirm02Screen(SVM020102Form form) {

        // service_request更新
        ServiceRequestVO serviceRequestVO = svm0201Service.findServiceRequestVO(form.getServiceRequestId());
        serviceRequestVO.setProblemComment(form.getProblemComment());
        serviceRequestVO.setReasonComment(form.getReasonComment());
        serviceRequestVO.setRepairComment(form.getRepairComment());
        serviceRequestVO.setShopComment(form.getShopComment());
        serviceRequestVO.setMileage(form.getMileage());

        // service_request_job更新
        List<SVM020102BO> repairJobDetailList = form.getRepairJobDetailList();
        List<Long> serviceRequestJobIds = repairJobDetailList.stream().map(SVM020102BO::getServiceRequestJobId).collect(Collectors.toList());
        List<ServiceRequestJobVO> jobs = svm0201Service.getServiceRequestJobList(serviceRequestJobIds);
        Map<Long, ServiceRequestJobVO> serviceRequestJobMap = jobs.stream().collect(Collectors.toMap(ServiceRequestJobVO::getServiceRequestJobId, Function.identity()));
        List<ServiceRequestJobVO> jobUpdateList = new ArrayList<>();

        for(SVM020102BO bo : repairJobDetailList) {

            ServiceRequestJobVO vo = serviceRequestJobMap.get(bo.getServiceRequestJobId());

            if(vo != null) {
                vo.setSelectFlag(bo.isJobSelectFlag() ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
                jobUpdateList.add(vo);
            }
        }

        // service_request_parts更新
        List<SVM020102BO> repairPartsDetailList = form.getRepairPartsDetailList();
        List<Long> serviceRequestPartsIds = repairPartsDetailList.stream().map(SVM020102BO::getServiceRequestPartsId).collect(Collectors.toList());
        List<ServiceRequestPartsVO> parts = svm0201Service.getServiceRequestPartsList(serviceRequestPartsIds);
        Map<Long, ServiceRequestPartsVO> serviceRequestPartsMap = parts.stream().collect(Collectors.toMap(ServiceRequestPartsVO::getServiceRequestPartsId, Function.identity()));
        List<ServiceRequestPartsVO> partsUpdateList = new ArrayList<>();

        for(SVM020102BO bo : repairPartsDetailList) {

            ServiceRequestPartsVO vo = serviceRequestPartsMap.get(bo.getServiceRequestPartsId());

            if(vo != null) {
                vo.setSelectFlag(bo.isPartsSelectFlag() ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
                partsUpdateList.add(vo);
            }
        }

        // service_request_battery更新
        List<SVM020102BO> repairBatteryDetailList = form.getRepairBatteryDetailList();
        List<Long> serviceRequestBatteryIds = repairBatteryDetailList.stream().map(SVM020102BO::getServiceRequestBatteryId).collect(Collectors.toList());
        List<ServiceRequestBatteryVO> batterys = svm0201Service.getServiceRequestBatteryList(serviceRequestBatteryIds);
        Map<Long, ServiceRequestBatteryVO> serviceRequestBatteryMap = batterys.stream().collect(Collectors.toMap(ServiceRequestBatteryVO::getServiceRequestBatteryId, Function.identity()));
        List<ServiceRequestBatteryVO> batteryUpdateList = new ArrayList<>();

        for(SVM020102BO bo : repairBatteryDetailList) {

            ServiceRequestBatteryVO vo = serviceRequestBatteryMap.get(bo.getServiceRequestBatteryId());

            if(vo != null) {
                vo.setSelectFlag(bo.isBatterySelectFlag() ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
                batteryUpdateList.add(vo);
            }
        }

        svm0201Service.updateConfirm(serviceRequestVO, jobUpdateList, partsUpdateList, batteryUpdateList);
    }

    private void handleClaimBattery(SVM020101BO bo) {

        bo.setPolicyType(POLICY_TYPE_2_YEARS);
        bo.setModel(CommonConstants.CHAR_BLANK);
        bo.setPlateNo(CommonConstants.CHAR_BLANK);
    }

    private void handleOtherCategory(SVM020101BO bo) {


        if(ObjectUtils.isEmpty(bo.getWarrantyType())){

            bo.setPolicyType(CommonConstants.CHAR_BLANK);
        } else if (bo.getWarrantyType().equals(WarrantyPolicyType.OLD.getCodeDbid())) {

            bo.setPolicyType(POLICY_TYPE_1_YEARS);
        } else if (bo.getWarrantyType().equals(WarrantyPolicyType.BIGBIKE.getCodeDbid())) {

            bo.setPolicyType(POLICY_TYPE_2_YEARS);
        } else if (bo.getWarrantyType().equals(WarrantyPolicyType.NEW.getCodeDbid())) {

            Integer year = getDifferenceOfYears(bo);
            bo.setPolicyType(year + "Years/" + bo.getWarrantyProductUsage() + "km");
        } else if (bo.getWarrantyType().equals(WarrantyPolicyType.EV.getCodeDbid())) {

            bo.setPolicyType(POLICY_TYPE_2_YEARS);
        }
    }

    private Integer getDifferenceOfYears(SVM020101BO bo) {

        LocalDate fromDate = LocalDate.parse(bo.getFromDate(), formatter);
        LocalDate toDate = LocalDate.parse(bo.getToDate(), formatter);

        Integer yearsBetween = (int)ChronoUnit.YEARS.between(fromDate, toDate);

        if(yearsBetween < 1) {
            return 1;
        } else if(yearsBetween == 1) {
            return 2;
        } else if(yearsBetween >= 2) {
            return 3;
        } else {
            return yearsBetween;
        }
    }

    private void validateData(SVM020101Form form) {

        // serviceOrderNo 存在性check
        if(!ObjectUtils.isEmpty(form.getServiceOrderNo())) {

            ServiceOrderVO vo = svm0201Service.findServiceOrderVO(form);

            if(vo == null) {

                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10237", new String[] {form.getServiceOrderNo(), CodedMessageUtils.getMessage("label.tableServiceOrder")}));
            }
        }

        // requestDate check
        LocalDate dateFrom = LocalDate.parse(form.getDateFrom(), formatter);
        LocalDate dateTo = LocalDate.parse(form.getDateTo(), formatter);

        if(ChronoUnit.DAYS.between(dateFrom, dateTo) > 184) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00339", new String[] {CodedMessageUtils.getMessage("label.requestDate"), CodedMessageUtils.getMessage("label.requestDateTo")}));
        }
    }

    public List<SVM020102FreeCouponBO> findCmmServiceDemandVOList() {

        List<CmmServiceDemandVO> freeCouponList = svm0201Service.findCmmServiceDemandVOList();
        List<SVM020102FreeCouponBO> valueList = new ArrayList<>();

        for(CmmServiceDemandVO vo : freeCouponList) {

            SVM020102FreeCouponBO bo = BeanMapUtils.mapTo(vo, SVM020102FreeCouponBO.class);
            valueList.add(bo);
        }
        return valueList;
    }

    private String safeString(String str) {
        return str == null ? CommonConstants.CHAR_BLANK : str;
    }

    public DownloadResponseView printPartsClaimTag(SVM020102Form form) {

        Long serviceRequestId = form.getServiceRequestId();

        if (Nulls.isNull(serviceRequestId)) {
            return null;
        }

        String categoryId = form.getCategoryId();

        List<String> claimList = Arrays.asList(ServiceCategory.CLAIM.getCodeDbid(),
                                               ServiceCategory.SPECIALCLAIM.getCodeDbid());

        //categoryId = S012CLAIM / S012SPECIALCLAIM 出力 PartsClaimTag.pdf
        if (!claimList.contains(categoryId)) {

            return null;

        }

        List<SVM0201PrintBO> dataList = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();

        //HeaderDataEdit
        SVM0201PrintBO printBO = svm0201Service.getPartsClaimTagPrintHeaderData(serviceRequestId);

        if (!Nulls.isNull(printBO)) {

            MstFacilityVO mstFacilityVO = svm0201Service.findByFacilityId(form.getPointId());

            if (!Nulls.isNull(mstFacilityVO)) {

                printBO.setFacilityCode(mstFacilityVO.getFacilityCd());
                printBO.setFacilityName(mstFacilityVO.getFacilityNm());

            }

            printBO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS)));

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

            //ClaimDate格式化
            if (StringUtils.isNotBlank(printBO.getClaimDate())) {
                printBO.setClaimDate(LocalDate.parse(printBO.getClaimDate(), dateFormatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //SaleDate格式化
            if (StringUtils.isNotBlank(printBO.getSaleDate())) {
                printBO.setSaleDate(LocalDate.parse(printBO.getSaleDate(), dateFormatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //Details
            List<SVM0201PrintDetailBO> details = svm0201Service.getPartsClaimTagPrintDetailList(serviceRequestId);

            if (!details.isEmpty()) {

                details.stream().forEach(item -> {
                    //PartNo格式化
                    if (StringUtils.isNotBlank(item.getPartNo())) {
                        item.setPartNo(PartNoUtil.format(item.getPartNo()));
                    }
                    //Qty四舍五入
                    if (!Nulls.isNull(item.getQty())) {
                        item.setQty(item.getQty().setScale(0, java.math.RoundingMode.HALF_UP));
                    }
                });
                printBO.setDetails(details);

                //parameters编辑
                for (int i = 1; i <= Math.min(details.size(), 30); i++) {
                    SVM0201PrintDetailBO item = details.get(i - 1);
                    parameters.put(PARTNO_STR + i, item.getPartNo());
                    parameters.put(PARTNAME_STR + i, item.getPartName());
                    parameters.put(QTY_STR + i, item.getQty());
                }

            }
        }

        dataList.add(printBO);

        Builder builder = PdfExportConfiguration.newBuilder();
        builder.templateFileName(FileConstants.JASPER_PARTSCLAIMTAG_REPORT)
               .reportData(dataList)
               .parameters(parameters);

        DownloadResponse rp = pdfExporter.generate(builder);

        return new DownloadResponseView(rp);
    }


    public DownloadResponseView printPartsClaimForBatteryClaimTag(SVM020102Form form) {

        Long serviceRequestId = form.getServiceRequestId();

        if (Nulls.isNull(serviceRequestId)) {
            return null;
        }

        String categoryId = form.getCategoryId();

        //categoryId = S012CLAIMBATTERY 出力 PartsClaimForBatteryClaimTag.pdf
        if (!StringUtils.equals(ServiceCategory.CLAIMBATTERY.getCodeDbid(), categoryId)) {

            return null;

        }

        List<SVM0201PrintBO> dataList = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();

        //HeaderDataEdit
        SVM0201PrintBO printBO = svm0201Service.getPartsClaimForBatteryClaimTagPrintHeaderData(serviceRequestId);

        if (!Nulls.isNull(printBO)) {

            MstFacilityVO mstFacilityVO = svm0201Service.findByFacilityId(form.getPointId());

            if (!Nulls.isNull(mstFacilityVO)) {

                printBO.setFacilityCode(mstFacilityVO.getFacilityCd());
                printBO.setFacilityName(mstFacilityVO.getFacilityNm());

            }

            printBO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS)));

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

            //ClaimDate格式化
            if (StringUtils.isNotBlank(printBO.getClaimDate())) {
                printBO.setClaimDate(LocalDate.parse(printBO.getClaimDate(), dateFormatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //SaleDate格式化
            if (StringUtils.isNotBlank(printBO.getSaleDate())) {
                printBO.setSaleDate(LocalDate.parse(printBO.getSaleDate(), dateFormatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //Details
            List<SVM0201PrintDetailBO> details = svm0201Service.getPartsClaimTagPrintDetailList(serviceRequestId);

            if (!details.isEmpty()) {

                details.stream().forEach(item -> {
                    //PartNo格式化
                    if (StringUtils.isNotBlank(item.getPartNo())) {
                        item.setPartNo( PartNoUtil.format(item.getPartNo()));
                    }
                    //Qty四舍五入
                    if (!Nulls.isNull(item.getQty())) {
                        item.setQty(item.getQty().setScale(0, java.math.RoundingMode.HALF_UP));
                    }
                });
                printBO.setDetails(details);

                //parameters编辑
                for (int i = 1; i <= Math.min(details.size(), 30); i++) {
                    SVM0201PrintDetailBO item = details.get(i - 1);
                    parameters.put(PARTNO_STR + i, item.getPartNo());
                    parameters.put(PARTNAME_STR + i, item.getPartName());
                    parameters.put(QTY_STR + i, item.getQty());
                }

            }
        }
        dataList.add(printBO);

        Builder builder = PdfExportConfiguration.newBuilder();
        builder.templateFileName(FileConstants.JASPER_PARTSCLAIMFORBATTERYCLAIMTAG_REPORT)
               .reportData(dataList)
               .parameters(parameters);

        DownloadResponse rp = pdfExporter.generate(builder);

        return new DownloadResponseView(rp);
    }

}