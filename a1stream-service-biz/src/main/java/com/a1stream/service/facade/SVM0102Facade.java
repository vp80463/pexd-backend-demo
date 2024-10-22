package com.a1stream.service.facade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.CommonConstants.WarrantyClaimDefaultPara;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.MstCodeConstants.SalesOrderStatus;
import com.a1stream.common.constants.PJConstants.BatteryOriginalSource;
import com.a1stream.common.constants.PJConstants.BrandType;
import com.a1stream.common.constants.PJConstants.ConsumerSerialProRelationType;
import com.a1stream.common.constants.PJConstants.DropShipType;
import com.a1stream.common.constants.PJConstants.OrgRelationType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.Relationship;
import com.a1stream.common.constants.PJConstants.ReservationStatus;
import com.a1stream.common.constants.PJConstants.SalesOrderPriorityType;
import com.a1stream.common.constants.PJConstants.SdStockStatus;
import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.common.constants.PJConstants.ServiceDemand;
import com.a1stream.common.constants.PJConstants.ServiceOrderStatus;
import com.a1stream.common.constants.PJConstants.SettleType;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.common.constants.PJConstants.WarrantyPolicyType;
import com.a1stream.common.facade.ConsumerFacade;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.common.facade.ValueListFacade;
import com.a1stream.common.logic.ConstantsLogic;
import com.a1stream.common.logic.ConsumerLogic;
import com.a1stream.common.logic.InventoryLogic;
import com.a1stream.common.logic.ValidateLogic;
import com.a1stream.common.manager.CallNewIfsManager;
import com.a1stream.common.model.BaseConsumerForm;
import com.a1stream.common.model.PartsVLBO;
import com.a1stream.common.model.PartsVLForm;
import com.a1stream.common.model.ServiceJobVLBO;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.batch.SvServiceHistoryIFBO;
import com.a1stream.domain.bo.batch.SvServiceHistoryItemIFBO;
import com.a1stream.domain.bo.batch.SvVinCodeTelIFBO;
import com.a1stream.domain.bo.common.CmmConsumerBO;
import com.a1stream.domain.bo.service.BatteryBO;
import com.a1stream.domain.bo.service.JobDetailBO;
import com.a1stream.domain.bo.service.PartDetailBO;
import com.a1stream.domain.bo.service.SVM010201BO;
import com.a1stream.domain.bo.service.SVM010201FreeCouponConditionBO;
import com.a1stream.domain.bo.service.SVM010201FreeCouponDetailBO;
import com.a1stream.domain.bo.service.SVM010201HistoryBO;
import com.a1stream.domain.bo.service.SVM010201ServiceHistoryBO;
import com.a1stream.domain.bo.service.SVM0102PrintBO;
import com.a1stream.domain.bo.service.SVM0102PrintServiceHistoryBO;
import com.a1stream.domain.bo.service.SVM0102PrintServiceJobBO;
import com.a1stream.domain.bo.service.SVM0102PrintServicePartBO;
import com.a1stream.domain.bo.service.SituationBO;
import com.a1stream.domain.bo.service.SpecialClaimBO;
import com.a1stream.domain.form.service.SVM010201Form;
import com.a1stream.domain.parameter.service.ConsumerPolicyParameter;
import com.a1stream.domain.parameter.service.SVM010201Parameter;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmConsumerSerialProRelationVO;
import com.a1stream.domain.vo.CmmRegistrationDocumentVO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmServiceDemandDetailVO;
import com.a1stream.domain.vo.CmmServiceDemandVO;
import com.a1stream.domain.vo.CmmServiceHistoryVO;
import com.a1stream.domain.vo.CmmSpecialClaimSerialProVO;
import com.a1stream.domain.vo.CmmWarrantyModelPartVO;
import com.a1stream.domain.vo.CmmWarrantySerializedProductVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.QueueDataVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.domain.vo.ServiceOrderBatteryVO;
import com.a1stream.domain.vo.ServiceOrderEditHistoryVO;
import com.a1stream.domain.vo.ServiceOrderFaultVO;
import com.a1stream.domain.vo.ServiceOrderJobVO;
import com.a1stream.domain.vo.ServiceOrderVO;
import com.a1stream.domain.vo.ServicePackageItemVO;
import com.a1stream.domain.vo.ServiceScheduleVO;
import com.a1stream.service.service.SVM0102Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ymsl.plugins.userauth.util.JsonUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.report.exporter.PdfReportExporter;
import com.ymsl.solid.web.download.DownloadResponseView;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

/**
* 功能描述:Service Order明细画面
*
* @author mid1341
*/
@Component
public class SVM0102Facade {

    @Resource
    private SVM0102Service svm0102Service;
    @Resource
    private ConstantsLogic constantsLogic;
    @Resource
    private ValidateLogic validateLogic;
    @Resource
    private ConsumerLogic consumerLogic;
    @Resource
    private InventoryLogic inventoryLogic;
    @Resource
    private CallNewIfsManager callNewIfsManager;
    @Value("${ifs.request.url}")
    private String ifsRequestUrl;
    @Resource
    public void setPdfReportExporter(PdfReportExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
    }
    private PdfReportExporter pdfExporter;
    @Resource
    private HelperFacade helperFacade;
    @Resource
    private OrderCmmMethod orderCmmMethod;
    @Resource
    private ConsumerFacade consumerFacade;
    @Resource
    private ValueListFacade valueListFacade;

    public SVM010201BO getServiceDetailByIdOrNo(Long serviceOrderId, String orderNo, Long pointId, String frameNo, String plateNo, PJUserDetails uc) {
        //orderId和orderNo均无值时，仅初始化; 有值时，获取service明细
        SVM010201BO result = Objects.isNull(serviceOrderId) && StringUtils.isBlank(orderNo) ? this.initForNewCreate(uc) : this.initForUpdate(serviceOrderId, orderNo, pointId, uc.getDealerCode());
        //基础数据设置
        this.initForBasicInfo(result, uc);

        //当frameNo有值时，代表从MC master画面跳转过来，需要带出MC信息
        if (Objects.isNull(serviceOrderId) && StringUtils.isNotBlank(frameNo)) {
            result.setFrameNo(frameNo);
            result = this.getMotorDetailByPlateOrFrame(result, uc.getDoFlag(), uc.getDealerCode());
        }
        //当PlateNo有值时，代表从ServiceReservation画面跳转过来，需要带出MC信息
        else if (Objects.isNull(serviceOrderId) && StringUtils.isNotBlank(plateNo)) {
            result.setPlateNo(plateNo);
            result = this.getMotorDetailByPlateOrFrame(result, uc.getDoFlag(), uc.getDealerCode());
        }

        return result;
    }

    public SVM010201BO getMotorDetailByPlateOrFrame(SVM010201BO model, String doFlag, String siteId){

        String plateNo = model.getPlateNo();
        String frameNo = model.getFrameNo();

        CmmSerializedProductVO cmmSerializedProduct = orderCmmMethod.findCmmSerializedProductByFrameOrPlate(frameNo, plateNo);

        //车辆不存在时，报错
        if (Objects.isNull(cmmSerializedProduct)) {throw new BusinessCodedException(ComUtil.t("M.E.00303", new String[] {StringUtils.isBlank(frameNo) ? ComUtil.t("label.numberPlate") : ComUtil.t("label.frameNumber"), StringUtils.isBlank(frameNo) ? plateNo : frameNo , ComUtil.t("common.label.vehicleInfo")}));}
        //车辆依然在库时，报错
        this.validateMcStatus(cmmSerializedProduct.getSalesStatus());

        //构建result motorInfo + batteryList ,将画面传递的数据覆盖
        SVM010201BO result = BeanMapUtils.mapTo(model, SVM010201BO.class);

        //获取车辆+owner信息
        this.getMotorAndConsumerDetail(result, siteId, cmmSerializedProduct, doFlag);

        //获取电池明细
        if (StringUtils.equals(result.getEvFlag(), CommonConstants.CHAR_Y)) {

            result.setBatteryList(orderCmmMethod.findServiceBatteryByMotorId(cmmSerializedProduct.getSerializedProductId()));
        }

        return result;
    }

    public void newOrModifyServiceOrder(SVM010201Form model, PJUserDetails uc) {

        if (Objects.isNull(model.getOrderInfo().getServiceOrderId())) {

            this.registerOperationForServiceOrder(model, uc);
        }
        else {

            this.updateOperationForServiceOrder(model, uc);
        }
    }

    public SpecialClaimBO getSpecialClaimDetail(Long specialClaimId, Long cmmSerializedProId, String modelCd, String taxPeriod) {
        return orderCmmMethod.findSpecialClaimRepairList(specialClaimId, cmmSerializedProId, modelCd, taxPeriod);
    }

    public ServiceJobVLBO getFreeCouponDetail(String jobCd, String modelCd, String taxPeriod) {
        return svm0102Service.getFreeCouponDetail(jobCd, modelCd, taxPeriod);
    }

    public SVM010201BO getServicePackageDetail(SVM010201Form model, String taxPeriod) {

        SVM010201BO result = new SVM010201BO();

        List<ServicePackageItemVO> servicePackageItemList = svm0102Service.getServicePackageDetail(model.getOrderInfo().getServicePackageId());

        result.setJobList(this.generateJobListByPackage(model, taxPeriod, servicePackageItemList.stream().filter(item -> StringUtils.equals(item.getProductClassification(), ProductClsType.SERVICE.getCodeDbid())).toList()));
        result.setPartList(this.generatePartListByPackage(model, taxPeriod, servicePackageItemList.stream().filter(item -> StringUtils.equals(item.getProductClassification(), ProductClsType.PART.getCodeDbid())).toList()));

        return result;
    }

    public void cancelOperationForServiceOrder(SVM010201Form model, PJUserDetails uc) {

        SVM010201Parameter para = new SVM010201Parameter();

        this.validateServiceOrderExistence(model, para);
        //修改相关VO
        this.prepareServiceOrderForCancel(para);
        this.prepareSalesOrderForCancel(para);
        this.prepareSpecialClaimSerialProForCancel(para);
        this.prepareServiceOrderPartListForCancel(para);
        //追加操作记录
        this.prepareServiceOrderEditHisForCancel(para, uc);

        svm0102Service.cancelServiceOrder(para);
    }

    public void settleOperationForServiceOrder(SVM010201Form model, PJUserDetails uc) {

        SVM010201Parameter para = new SVM010201Parameter();

        //设置当前通用参数
        para.setDoFlag(uc.getDoFlag());
        //前置验证
        this.validateForSettle(model, para);
        //修改相关VO
        this.prepareServiceOrderForSettle(model, para, uc);
        this.prepareSalesOrderForUpdate(model, para, uc);//无论有没有parts，均创建salesOrder
        this.prepareServiceOrderFaultList(model, para);
        this.prepareServiceOrderJobList(model, para);
        this.prepareServiceOrderPartListForUpdate(model, para);
        this.prepareServiceOrderBatteryList(model, para);
        this.prepareConsumerPrivacyPolicyResult(model, para);
        this.prepareConsumerBasicInfoForSettle(model, para);
        this.prepareServiceScheduleForSettle(para);
        this.prepareSerializedPro(model, para);
        this.prepareBattery(model, para);

        //追加操作记录
        this.prepareServiceOrderEditHisForSettle(para, uc);
        //外键赋值
        this.setForeignKey(para);

        svm0102Service.settleServiceOrder(para, uc);
        //异步处理，人车更新/registerDoc/serviceHistory
        callNewIfsManager.callNewIfsService(ifsRequestUrl, InterfCode.DMS_TO_DMS_SV_SERVICESETTLE, JSON.toJSON(para.getServiceOrder().getServiceOrderId()).toString());
    }

    public void asynOperationAfterSettle(Long serviceOrderId) {

        ServiceOrderVO serviceOrder = orderCmmMethod.timelyFindServiceOrderById(serviceOrderId);

        SVM010201Parameter para = new SVM010201Parameter();

        para.setServiceOrder(serviceOrder);

        //更新人车关系
        this.prepareConsumerMotorRelation(serviceOrder, para);
        //更新regsiterDoc
        this.prepareRegisterDoc(serviceOrder, para);
        //生成serviceHistory，创建Q表数据，to My-YAMAHA
        this.prepareServiceHistory(serviceOrder, para);

        svm0102Service.doAfterServiceOrder(para);
    }

    public List<SVM010201HistoryBO> exportOrderHistoryList(Long serviceOrderId) {

        List<SVM010201HistoryBO> result = svm0102Service.searchServiceEditHistoryByOrderId(serviceOrderId);

        for (SVM010201HistoryBO item : result) {
            item.setProcessTime(StringUtils.substring(item.getProcessTime(), 0, 19));
        }

        return result;
    }

    private List<PartDetailBO> generatePartListByPackage(SVM010201Form model, String taxPeriod, List<ServicePackageItemVO> partItemList){

        if (partItemList.isEmpty()) {return new ArrayList<>();}

        Map<Long, PartsVLBO> partDetailMap = this.searchPartDetailInfoForPackage(model, taxPeriod, partItemList.stream().map(ServicePackageItemVO::getProductId).toList());

        List<PartDetailBO> result = new ArrayList<>();
        PartDetailBO value;
        PartsVLBO partInfo;

        for (ServicePackageItemVO packageItem : partItemList) {

            if (!partDetailMap.containsKey(packageItem.getProductId())) {continue;}

            partInfo = partDetailMap.get(packageItem.getProductId());

            value = new PartDetailBO();

            value.setServiceCategoryId(model.getOrderInfo().getServiceCategoryId());
            value.setServiceDemandId(model.getOrderInfo().getServiceDemandId());
            value.setServiceDemandContent(model.getOrderInfo().getServiceDemand());
            value.setPartId(Long.valueOf(partInfo.getId()));
            value.setPartCd(partInfo.getCode());
            value.setPartNm(partInfo.getDesc());
            value.setSupersedingPartId(StringUtils.isBlank(partInfo.getSupersedingPartsId()) ? null : Long.valueOf(partInfo.getSupersedingPartsId()));
            value.setSupersedingPartCd(partInfo.getSupersedingPartsCd());
            value.setSupersedingPartsCdFmt(partInfo.getSupersedingPartsCdFmt());
            value.setSupersedingPartNm(partInfo.getSupersedingPartsNm());
            value.setStandardPrice(partInfo.getStdRetailPrice());
            value.setQty(packageItem.getQty());
            value.setOnHandQty(partInfo.getOnHandQty());
            value.setLocationCd(partInfo.getMainLocationCd());
            value.setBatteryFlag(partInfo.getBatteryFlag());
            value.setServicePackageId(model.getOrderInfo().getServicePackageId());
            value.setServicePackageCd(model.getOrderInfo().getServicePackageCd());
            value.setServicePackageNm(model.getOrderInfo().getServicePackageNm());
            value.setTaxRate(partInfo.getTaxRate());

            if (!Objects.isNull(value.getSellingPrice()) && !Objects.isNull(value.getQty())) {

                value.setAmount(value.getSellingPrice().multiply(value.getQty()));
            }

            if (StringUtils.isNotBlank(value.getServiceCategoryId())) {
                value.setSettleTypeId(constantsLogic.getConstantsByCodeDbId(ServiceCategory.class.getDeclaredFields(), value.getServiceCategoryId()).getCodeData2());
            }

            result.add(value);
        }

        return result;
    }

    private List<JobDetailBO> generateJobListByPackage(SVM010201Form model, String taxPeriod, List<ServicePackageItemVO> jobItemList){

        if (jobItemList.isEmpty()) {return new ArrayList<>();}

        Map<Long, ServiceJobVLBO> jobDetailMap = this.searchJobDetailInfoForPackage(model, taxPeriod, jobItemList.stream().map(ServicePackageItemVO::getProductId).toList());

        List<JobDetailBO> result = new ArrayList<>();
        JobDetailBO value;
        ServiceJobVLBO jobInfo;

        for (ServicePackageItemVO packageItem : jobItemList) {

            if (!jobDetailMap.containsKey(packageItem.getProductId())) {continue;}

            jobInfo = jobDetailMap.get(packageItem.getProductId());

            value = new JobDetailBO();

            value.setServiceCategoryId(model.getOrderInfo().getServiceCategoryId());
            value.setServiceDemandId(model.getOrderInfo().getServiceDemandId());
            value.setServiceDemandContent(model.getOrderInfo().getServiceDemand());
            value.setJobId(Long.valueOf(jobInfo.getId()));
            value.setJobCd(jobInfo.getCode());
            value.setJobNm(jobInfo.getDesc());
            value.setManhour(new BigDecimal(jobInfo.getManHours()));
            value.setStandardPrice(jobInfo.getStdRetailPrice());
            value.setSellingPrice(jobInfo.getStdRetailPrice());
            value.setTaxRate(jobInfo.getVatRate());
            value.setServicePackageId(model.getOrderInfo().getServicePackageId());
            value.setServicePackageCd(model.getOrderInfo().getServicePackageCd());
            value.setServicePackageNm(model.getOrderInfo().getServicePackageNm());

            if (StringUtils.isNotBlank(value.getServiceCategoryId())) {
                value.setSettleTypeId(constantsLogic.getConstantsByCodeDbId(ServiceCategory.class.getDeclaredFields(), value.getServiceCategoryId()).getCodeData2());
            }

            result.add(value);
        }

        return result;
    }

    private Map<Long, PartsVLBO> searchPartDetailInfoForPackage(SVM010201Form model, String taxPeriod, List<Long> partIdList){

        PartsVLForm partsVLForm = new PartsVLForm();

        partsVLForm.setFacilityId(model.getOrderInfo().getPointId());
        partsVLForm.setPartIds(partIdList);
        partsVLForm.setPageSize(CommonConstants.INTEGER_ZERO);
        partsVLForm.setCurrentPage(CommonConstants.INTEGER_ZERO);
        partsVLForm.setAutoFillFlag(CommonConstants.CHAR_Y);

        List<PartsVLBO> partList = (List<PartsVLBO>) valueListFacade.findPartsList(partsVLForm, model.getSiteId(), taxPeriod).getList();

        return partList.stream().collect(Collectors.toMap(key -> Long.valueOf(key.getId()), value -> value));
    }

    private Map<Long, ServiceJobVLBO> searchJobDetailInfoForPackage(SVM010201Form model, String taxPeriod, List<Long> jobIdList){

        List<ServiceJobVLBO> result;

        //DO店 + repair category时，按model type取job
        if (StringUtils.equals(model.getOrderInfo().getDoFlag(), CommonConstants.CHAR_Y)
                && StringUtils.equals(model.getOrderInfo().getServiceCategoryId(), ServiceCategory.REPAIR.getCodeDbid())) {

            result = svm0102Service.findServiceJobByModelTypeListWithJobId(model.getOrderInfo().getServiceCategoryId(), model.getOrderInfo().getModelTypeId(), taxPeriod, jobIdList);
        }
        //其他情况均按Model code取job
        else {

            result = svm0102Service.findServiceJobByModelCdListWithJobId(model.getOrderInfo().getServiceCategoryId(), model.getOrderInfo().getModelCd(), taxPeriod, jobIdList);
        }

        return result.stream().collect(Collectors.toMap(key -> Long.valueOf(key.getId()), value -> value));
    }

    private void registerOperationForServiceOrder(SVM010201Form model, PJUserDetails uc) {

        SVM010201Parameter para = new SVM010201Parameter();
        //设置当前通用参数
        para.setDoFlag(uc.getDoFlag());
        //前置验证
        this.validateForRegister(model, para);
        //创建相关VO
        this.prepareServiceOrderForRegister(model, para, uc);
        this.prepareSalesOrderForRegister(model, para, uc);//无论有没有parts，均创建salesOrder
        this.prepareServiceOrderFaultList(model, para);
        this.prepareServiceOrderJobList(model, para);
        this.prepareServiceOrderPartListForRegister(model, para);
        this.prepareServiceOrderBatteryList(model, para);
        this.prepareConsumerPrivacyPolicyResult(model, para);
        //register特有逻辑：关联预定表/锁死Special Claim Master数据
        this.prepareServiceScheduleForRegister(para);
        this.prepareSpecialClaimSerialProForRegister(para);
        //追加操作记录
        this.prepareServiceOrderEditHisForRegister(para, uc);
        //外键赋值
        this.setForeignKey(para);

        svm0102Service.registerServiceOrder(para);
    }

    private void updateOperationForServiceOrder(SVM010201Form model, PJUserDetails uc) {

        SVM010201Parameter para = new SVM010201Parameter();
        //设置当前通用参数
        para.setDoFlag(uc.getDoFlag());
        //前置验证
        this.validateForUpdate(model, para);
        //修改相关VO
        this.prepareServiceOrderForUpdate(model, para, uc);
        this.prepareSalesOrderForUpdate(model, para, uc);//无论有没有parts，均创建salesOrder
        this.prepareServiceOrderFaultList(model, para);
        this.prepareServiceOrderJobList(model, para);
        this.prepareServiceOrderPartListForUpdate(model, para);
        this.prepareServiceOrderBatteryList(model, para);
        this.prepareConsumerPrivacyPolicyResult(model, para);
        //追加操作记录
        this.prepareServiceOrderEditHisForUpdate(para, uc);
        //外键赋值
        this.setForeignKey(para);
        //part明细的处理逻辑设计库存实时加减，放在service中
        svm0102Service.updateServiceOrder(para);
    }

    private void prepareBattery(SVM010201Form model, SVM010201Parameter para) {

        //如果所有的电池都不需要更换，不做处理
        if (model.getBatteryList().stream().noneMatch(battery -> StringUtils.isNotBlank(battery.getNewBatteryNo()))) {return;}

        //追加Loc车辆表，因为LOC的电池数据附属于Loc的车辆
        this.prepareLocMotor(para, model.getSiteId());

        for (BatteryBO batteryDetail : model.getBatteryList()) {

            if (StringUtils.isBlank(batteryDetail.getNewBatteryNo())) {continue;}

            //修改Common旧电池
            CmmBatteryVO oldCmmBattery = this.prepareOldCmmBatteryMst(para, batteryDetail);
            //追加Common新电池
            CmmBatteryVO newCmmBattery = this.prepareNewCmmBatteryMst(para, batteryDetail, oldCmmBattery, model.getOrderInfo().getServiceCategoryId(), model.getOrderInfo().getCmmSerializedProductId());

            //修改Local旧电池
            this.prepareOldLocBatteryMst(para, batteryDetail, model.getSiteId());
            //追加Local新电池
            this.prepareNewLocBatteryMst(para, newCmmBattery, para.getSerializedProduct().getSerializedProductId(), model.getSiteId());
        }
    }

    private void prepareLocMotor(SVM010201Parameter para, String siteId) {

        SerializedProductVO serializedPro = svm0102Service.findSerializedProductByCmmId(para.getCmmSerializedProduct().getSerializedProductId(), siteId);

        if (Objects.isNull(serializedPro)) {

            serializedPro = SerializedProductVO.copyFromCmm(para.getCmmSerializedProduct(), siteId);
        }

        para.setSerializedProduct(serializedPro);
    }

    private void prepareNewLocBatteryMst(SVM010201Parameter para, CmmBatteryVO newCmmBattery, Long serializedProductId, String siteId) {

        para.getBatteryMstList().add(BatteryVO.copyFromCmm(newCmmBattery, siteId, serializedProductId));
    }

    private CmmBatteryVO prepareNewCmmBatteryMst(SVM010201Parameter para, BatteryBO batteryDetail, CmmBatteryVO oldBattery, String serviceCategoryId, Long cmmSerializedProductId) {

        CmmBatteryVO result = CmmBatteryVO.create();

        result.setBatteryNo(batteryDetail.getNewBatteryNo());
        result.setBatteryCd(batteryDetail.getNewPartCd());
        result.setBatteryStatus(SdStockStatus.SHIPPED.getCodeDbid());
        result.setSellingPrice(batteryDetail.getSellingPrice());
        result.setSaleDate(ComUtil.date2str(LocalDate.now()));
        result.setOriginalFlag(StringUtils.equals(serviceCategoryId, ServiceCategory.CLAIM.getCodeDbid()) ? oldBattery.getOriginalFlag() : BatteryOriginalSource.SERVICE);
        result.setServiceCalculateDate(oldBattery.getServiceCalculateDate());
        result.setSerializedProductId(cmmSerializedProductId);
        result.setFromDate(ComUtil.date2str(LocalDate.now()));
        result.setToDate(CommonConstants.MAX_DATE);
        result.setPositionSign(batteryDetail.getBatteryType());
        result.setProductId(batteryDetail.getNewPartId());
        result.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);

        para.getCmmBatteryMstList().add(result);

        return result;
    }

    private CmmBatteryVO prepareOldCmmBatteryMst(SVM010201Parameter para, BatteryBO batteryDetail) {

        CmmBatteryVO result = svm0102Service.findCmmBatteryById(batteryDetail.getCurrentBatteryId());
        result.setToDate(ComUtil.date2str(LocalDate.now().minusDays(1)));
        para.getCmmBatteryMstList().add(result);

        return result;
    }

    private void prepareOldLocBatteryMst(SVM010201Parameter para, BatteryBO batteryDetail, String siteId) {

        BatteryVO result = svm0102Service.findBatteryByCmmId(siteId, batteryDetail.getCurrentBatteryId());

        if (!Objects.isNull(result)) {

            result.setToDate(ComUtil.date2str(LocalDate.now().minusDays(1)));
            para.getBatteryMstList().add(result);
        }
    }

    private void prepareServiceHistory(ServiceOrderVO serviceOrder, SVM010201Parameter para) {

        //获取服务单的明细信息
        List<SalesOrderItemVO> partList = svm0102Service.timelyFindServiceOrderPartListBySalesOrderId(serviceOrder.getRelatedSalesOrderId())
                                                        .stream()
                                                        .filter(part -> BigDecimal.ZERO.compareTo(part.getActualQty()) < 0)
                                                        .toList();

        List<ServiceOrderJobVO> jobList = svm0102Service.timelyFindServiceOrderJobListByOrderId(serviceOrder.getServiceOrderId());

        para.setCmmServiceHistory(this.prepareServiceHistoryVO(serviceOrder, partList));
        para.getQueueDataList().add(QueueDataVO.create(serviceOrder.getSiteId(), InterfCode.DMS_TO_MYY_SV_SERVICEHISTORY, "service_order", serviceOrder.getServiceOrderId(), JsonUtils.toString(List.of(this.prepareServiceHistoryIFBO(serviceOrder, para.getCmmServiceHistory().getServiceHistoryId())).stream().toList())));
        para.getQueueDataList().add(QueueDataVO.create(serviceOrder.getSiteId(), InterfCode.DMS_TO_MYY_SV_SERVICEHISTORYITEM, "service_order", serviceOrder.getServiceOrderId(), JsonUtils.toString(this.prepareServiceHistoryItemIFBO(serviceOrder, para.getCmmServiceHistory().getServiceHistoryId(), partList, jobList).stream().toList())));
    }

    private SvVinCodeTelIFBO prepareVinCodeTelIFBOByOwnerChange(Long cmmConsumerMotorRelationId, ServiceOrderVO serviceOrder) {

        SvVinCodeTelIFBO result = new SvVinCodeTelIFBO();

        result.setQueueId(cmmConsumerMotorRelationId.toString());
        result.setVinHin(serviceOrder.getFrameNo());
        result.setTelNo(serviceOrder.getMobilePhone());
        result.setTelLastDigits(result.getTelNo().length() >= 4 ? result.getTelNo().substring(result.getTelNo().length() - 4) : result.getTelNo());
        result.setOwnerChangedFlg(CommonConstants.CHAR_ONE);
        result.setOwnerChangedDate(ComUtil.nowDate());
        result.setSalesDate(serviceOrder.getSoldDate());
        result.setUpdateAuthor(serviceOrder.getLastUpdatedBy());
        result.setUpdateDate(serviceOrder.getLastUpdated().toString());
        result.setCreateAuthor(serviceOrder.getCreatedBy());
        result.setCreateDate(serviceOrder.getDateCreated().toString());
        result.setUpdateProgram(serviceOrder.getUpdateProgram());
        result.setUpdateCounter(CommonConstants.CHAR_ZERO);

        return result;
    }

    private List<SvServiceHistoryItemIFBO> prepareServiceHistoryItemIFBO(ServiceOrderVO serviceOrder, Long serviceHistoryId, List<SalesOrderItemVO> partList, List<ServiceOrderJobVO> jobList) {

        List<SvServiceHistoryItemIFBO> result = new ArrayList<>();

        SvServiceHistoryItemIFBO serviceHistoryItemIFBO;

        for (SalesOrderItemVO partDetail : partList) {

            serviceHistoryItemIFBO = new SvServiceHistoryItemIFBO();

            serviceHistoryItemIFBO.setQueueId(partDetail.getSalesOrderItemId().toString());
            serviceHistoryItemIFBO.setServiceHistoryItemId(partDetail.getSalesOrderItemId().toString());
            serviceHistoryItemIFBO.setSiteId(partDetail.getSiteId());
            serviceHistoryItemIFBO.setCmmProductId(partDetail.getAllocatedProductId().toString());
            serviceHistoryItemIFBO.setSiteProductId(partDetail.getAllocatedProductId().toString());
            serviceHistoryItemIFBO.setProductContent(partDetail.getAllocatedProductNm());
            serviceHistoryItemIFBO.setProductClassificationId(ProductClsType.PART.getCodeDbid());
            serviceHistoryItemIFBO.setQty(partDetail.getActualQty().toString());
            serviceHistoryItemIFBO.setStandardPrice(partDetail.getStandardPrice().toString());
            serviceHistoryItemIFBO.setSellingPrice(partDetail.getSellingPriceNotVat().toString());
            serviceHistoryItemIFBO.setAmount(partDetail.getActualAmtNotVat().toString());
            serviceHistoryItemIFBO.setServiceHistoryId(serviceHistoryId.toString());
            serviceHistoryItemIFBO.setUpdateAuthor(serviceOrder.getLastUpdatedBy());
            serviceHistoryItemIFBO.setUpdateDate(serviceOrder.getLastUpdated().toString());
            serviceHistoryItemIFBO.setCreateAuthor(serviceOrder.getCreatedBy());
            serviceHistoryItemIFBO.setCreateDate(serviceOrder.getDateCreated().toString());
            serviceHistoryItemIFBO.setUpdateProgram(serviceOrder.getUpdateProgram());
            serviceHistoryItemIFBO.setUpdateCounter(CommonConstants.CHAR_ZERO);
            serviceHistoryItemIFBO.setServiceCategoryId(partDetail.getServiceCategoryId());
            serviceHistoryItemIFBO.setServiceDemandId(partDetail.getServiceDemandId().toString());

            result.add(serviceHistoryItemIFBO);
        }

        for (ServiceOrderJobVO jobDetail : jobList) {

            serviceHistoryItemIFBO = new SvServiceHistoryItemIFBO();

            serviceHistoryItemIFBO.setQueueId(jobDetail.getServiceOrderJobId().toString());
            serviceHistoryItemIFBO.setServiceHistoryItemId(jobDetail.getServiceOrderJobId().toString());
            serviceHistoryItemIFBO.setSiteId(jobDetail.getSiteId());
            serviceHistoryItemIFBO.setCmmProductId(jobDetail.getJobId().toString());
            serviceHistoryItemIFBO.setSiteProductId(jobDetail.getJobId().toString());
            serviceHistoryItemIFBO.setProductContent(jobDetail.getJobNm());
            serviceHistoryItemIFBO.setProductClassificationId(ProductClsType.SERVICE.getCodeDbid());
            serviceHistoryItemIFBO.setQty(CommonConstants.CHAR_ONE);
            serviceHistoryItemIFBO.setStandardPrice(jobDetail.getStandardPrice().toString());
            serviceHistoryItemIFBO.setSellingPrice(jobDetail.getSellingPrice().toString());
            serviceHistoryItemIFBO.setAmount(jobDetail.getSellingPrice().toString());
            serviceHistoryItemIFBO.setServiceHistoryId(serviceHistoryId.toString());
            serviceHistoryItemIFBO.setUpdateAuthor(serviceOrder.getLastUpdatedBy());
            serviceHistoryItemIFBO.setUpdateDate(serviceOrder.getLastUpdated().toString());
            serviceHistoryItemIFBO.setCreateAuthor(serviceOrder.getCreatedBy());
            serviceHistoryItemIFBO.setCreateDate(serviceOrder.getDateCreated().toString());
            serviceHistoryItemIFBO.setUpdateProgram(serviceOrder.getUpdateProgram());
            serviceHistoryItemIFBO.setUpdateCounter(CommonConstants.CHAR_ZERO);
            serviceHistoryItemIFBO.setServiceCategoryId(jobDetail.getServiceCategoryId());
            serviceHistoryItemIFBO.setServiceDemandId(jobDetail.getServiceDemandId().toString());

            result.add(serviceHistoryItemIFBO);
        }
        return result;
    }

    private CmmServiceHistoryVO prepareServiceHistoryVO(ServiceOrderVO serviceOrder, List<SalesOrderItemVO> partList) {

        CmmServiceHistoryVO cmmServiceHistory = CmmServiceHistoryVO.create();

        cmmServiceHistory.setSiteId(serviceOrder.getSiteId());
        cmmServiceHistory.setOrderNo(serviceOrder.getOrderNo());
        cmmServiceHistory.setOrderDate(serviceOrder.getOrderDate());
        cmmServiceHistory.setServiceCategory(serviceOrder.getServiceCategoryId());
        cmmServiceHistory.setServiceCategoryContent(serviceOrder.getServiceCategoryContent());
        cmmServiceHistory.setServiceDemand(serviceOrder.getServiceDemandId());
        cmmServiceHistory.setServiceDemandContent(serviceOrder.getServiceDemandContent());
        cmmServiceHistory.setServiceSubject(serviceOrder.getServiceSubject());
        cmmServiceHistory.setSerializedProductId(serviceOrder.getCmmSerializedProductId());
        cmmServiceHistory.setConsumerId(serviceOrder.getConsumerId());
        cmmServiceHistory.setReplacedPart(partList.isEmpty() ? null : partList.stream().map(SalesOrderItemVO::getAllocatedProductNm).collect(Collectors.joining(CommonConstants.CHAR_COMMA)));

        return cmmServiceHistory;
    }

    private SvServiceHistoryIFBO prepareServiceHistoryIFBO(ServiceOrderVO serviceOrder, Long serviceHistoryId) {

        SvServiceHistoryIFBO result = new SvServiceHistoryIFBO();

        result.setQueueId(serviceHistoryId.toString());
        result.setServiceHistoryId(serviceHistoryId.toString());
        result.setSiteId(serviceOrder.getSiteId());
        result.setSiteOrderId(serviceOrder.getServiceOrderId().toString());
        result.setServiceDate(serviceOrder.getOrderDate());
        result.setOrderNo(serviceOrder.getOrderNo());
        result.setCmmProductId(serviceOrder.getModelId().toString());
        result.setSiteProductId(serviceOrder.getModelId().toString());
        result.setCmmSerializedProductId(serviceOrder.getCmmSerializedProductId().toString());
        result.setSiteSerializedProductId(serviceOrder.getCmmSerializedProductId().toString());
        result.setNoPlate(serviceOrder.getPlateNo());
        result.setFrameNo(serviceOrder.getEngineNo());
        result.setCmmConsumerId(serviceOrder.getConsumerId().toString());
        result.setSiteConsumerId(serviceOrder.getConsumerId().toString());
        result.setServiceCategoryId(serviceOrder.getServiceCategoryId());
        result.setServiceCategoryContent(serviceOrder.getServiceCategoryContent());
        result.setServiceDemandContent(serviceOrder.getServiceDemandContent());
        result.setServiceDemandId(serviceOrder.getServiceDemandId().toString());
        result.setServiceSubjectContent(serviceOrder.getServiceSubject());
        result.setUpdateAuthor(serviceOrder.getLastUpdatedBy());
        result.setUpdateDate(serviceOrder.getLastUpdated().toString());
        result.setCreateAuthor(serviceOrder.getCreatedBy());
        result.setCreateDate(serviceOrder.getDateCreated().toString());
        result.setUpdateProgram(serviceOrder.getUpdateProgram());
        result.setUpdateCounter(CommonConstants.CHAR_ZERO);
        result.setMileage(serviceOrder.getMileage().toString());
        result.setFacilityCode(serviceOrder.getFacilityCd());
        result.setFacilityName(serviceOrder.getFacilityNm());
        result.setOperationStartDate(ComUtil.date2timeFormat(serviceOrder.getOperationStartTime(), CommonConstants.DB_DATE_FORMAT_YMD));
        result.setOperationStartTime(ComUtil.date2timeHM(serviceOrder.getOperationStartTime()));
        result.setOperationFinishDate(ComUtil.date2timeFormat(serviceOrder.getOperationFinishTime(), CommonConstants.DB_DATE_FORMAT_YMD));
        result.setOperationFinishTime(ComUtil.date2timeHM(serviceOrder.getOperationFinishTime()));
        result.setMechanicPic(serviceOrder.getMechanicNm());
        result.setTotalServiceAmount(serviceOrder.getServiceAmt().add(serviceOrder.getPartsAmt()).toString());
        result.setJobAmount(serviceOrder.getServiceAmt().toString());
        result.setPartsAmount(serviceOrder.getPartsAmt().toString());
        result.setCommentForCustomer(serviceOrder.getMechanicComment());
        result.setCustomerComment(CommonConstants.CHAR_BLANK);

        return result;
    }

    private void prepareRegisterDoc(ServiceOrderVO serviceOrder, SVM010201Parameter para) {

        CmmRegistrationDocumentVO cmmRegisterDoc = svm0102Service.findRegisterDocByMotorId(serviceOrder.getCmmSerializedProductId());

        if (Objects.isNull(cmmRegisterDoc)) {
            //new
            cmmRegisterDoc = new CmmRegistrationDocumentVO();

            cmmRegisterDoc.setSiteId(serviceOrder.getSiteId());
            cmmRegisterDoc.setSerializedProductId(serviceOrder.getCmmSerializedProductId());
            cmmRegisterDoc.setConsumerId(serviceOrder.getConsumerId());
            cmmRegisterDoc.setFacilityId(serviceOrder.getFacilityId());
            cmmRegisterDoc.setRegistrationDate(ComUtil.date2str(LocalDate.now()));
            cmmRegisterDoc.setRegistrationTime(ComUtil.date2timeHMS(LocalDateTime.now()));
            cmmRegisterDoc.setCreatedBy(CommonConstants.CHAR_IFS);
        }

        //如果存在，更新如下信息
        cmmRegisterDoc.setUseType(serviceOrder.getUseTypeId());
        cmmRegisterDoc.setServiceOrderId(serviceOrder.getServiceOrderId());

        cmmRegisterDoc.setLastUpdatedBy(CommonConstants.CHAR_IFS);
        cmmRegisterDoc.setUpdateProgram(CommonConstants.CHAR_IFS);

        para.setCmmRegistrationDocument(cmmRegisterDoc);
    }

    private void prepareSerializedPro(SVM010201Form model, SVM010201Parameter para) {

        CmmSerializedProductVO cmmSerializedPro = para.getCmmSerializedProduct();

        if (StringUtils.isBlank(cmmSerializedPro.getStuDate())) {
            cmmSerializedPro.setStuDate(model.getOrderInfo().getSoldDate());
        }

        cmmSerializedPro.setPlateNo(model.getOrderInfo().getPlateNo());

        para.setCmmSerializedProduct(cmmSerializedPro);
    }

    private void prepareConsumerMotorRelation(ServiceOrderVO serviceOrder, SVM010201Parameter para) {

        CmmConsumerSerialProRelationVO consumerMotorRelation = svm0102Service.findCMRelationByMotorIdAndConsumerId(serviceOrder.getCmmSerializedProductId(), serviceOrder.getConsumerId());

        if (Objects.isNull(consumerMotorRelation)) {
            //创建人车关系
            consumerMotorRelation = this.registerConsumerMotorRelation(serviceOrder.getCmmSerializedProductId(), serviceOrder.getConsumerId(), serviceOrder.getRelationTypeId());
            para.getCmmConsumerSerialProRelationListForSave().add(consumerMotorRelation);

            //如果是owner，插入Queue
            if (StringUtils.equals(serviceOrder.getRelationTypeId(), ConsumerSerialProRelationType.OWNER.getCodeDbid())) {
                para.getQueueDataList().add(QueueDataVO.create(serviceOrder.getSiteId(), InterfCode.DMS_TO_MYY_SV_VINCODETEL, "cmm_consumer_serial_pro_relation", consumerMotorRelation.getConsumerSerializedProductRelationId(), JsonUtils.toString(List.of(this.prepareVinCodeTelIFBOByOwnerChange(consumerMotorRelation.getConsumerSerializedProductRelationId(), serviceOrder)).stream().toList())));
            }
        }
        else {
            //更新人车关系
            para.getCmmConsumerSerialProRelationListForSave().add(this.updateConsumerMotorRelation(consumerMotorRelation, serviceOrder.getRelationTypeId()));

            //如果是user变更为owner，插入Queue
            if (StringUtils.equals(consumerMotorRelation.getConsumerSerializedProductRelationTypeId(), ConsumerSerialProRelationType.USER.getCodeDbid())
                    && StringUtils.equals(serviceOrder.getRelationTypeId(), ConsumerSerialProRelationType.OWNER.getCodeDbid())) {
                para.getQueueDataList().add(QueueDataVO.create(serviceOrder.getSiteId(), InterfCode.DMS_TO_MYY_SV_VINCODETEL, "cmm_consumer_serial_pro_relation", consumerMotorRelation.getConsumerSerializedProductRelationId(), JsonUtils.toString(List.of(this.prepareVinCodeTelIFBOByOwnerChange(consumerMotorRelation.getConsumerSerializedProductRelationId(), serviceOrder)).stream().toList())));
            }
        }

        //Owner时，需要将原有的owner更新成user
        if (StringUtils.equals(serviceOrder.getRelationTypeId(), ConsumerSerialProRelationType.OWNER.getCodeDbid())) {

            CmmConsumerSerialProRelationVO ownerRelation = svm0102Service.findOwnerRelationByMotorId(serviceOrder.getCmmSerializedProductId());

            //车辆有owner，且不是当前consumer
            if (!Objects.isNull(ownerRelation) && !ownerRelation.getConsumerId().equals(serviceOrder.getConsumerId())) {
                //改为user
                para.getCmmConsumerSerialProRelationListForSave().add(this.updateConsumerMotorRelation(ownerRelation, ConsumerSerialProRelationType.USER.getCodeDbid()));
            }
        }
    }

    private CmmConsumerSerialProRelationVO registerConsumerMotorRelation(Long serializedProId, Long consumerId, String relationType) {

        CmmConsumerSerialProRelationVO result = CmmConsumerSerialProRelationVO.create();

        result.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);
        result.setSerializedProductId(serializedProId);
        result.setConsumerId(consumerId);
        result.setConsumerSerializedProductRelationTypeId(relationType);
        result.setOwnerFlag(StringUtils.equals(relationType, ConsumerSerialProRelationType.OWNER.getCodeDbid()) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
        result.setCreatedBy(CommonConstants.CHAR_IFS);
        result.setLastUpdatedBy(CommonConstants.CHAR_IFS);
        result.setUpdateProgram(CommonConstants.CHAR_IFS);

        return result;
    }

    private CmmConsumerSerialProRelationVO updateConsumerMotorRelation(CmmConsumerSerialProRelationVO result, String relationType) {

        result.setConsumerSerializedProductRelationTypeId(relationType);

        result.setLastUpdatedBy(CommonConstants.CHAR_IFS);
        result.setUpdateProgram(CommonConstants.CHAR_IFS);

        return result;
    }

    public List<SVM010201ServiceHistoryBO> listServiceHistory(Long cmmSerializedProId, String siteId){

        if (Objects.isNull(cmmSerializedProId)){return new ArrayList<>();}

        return svm0102Service.findServiceHistoryByMotorId(cmmSerializedProId, siteId);
    }

    public SVM010201FreeCouponConditionBO getFreeCouponHistory(Long cmmSerializedProId, String modelCd, String stdDate){

        SVM010201FreeCouponConditionBO result = new SVM010201FreeCouponConditionBO();

        if (Objects.isNull(cmmSerializedProId)){return result;}

        CmmSerializedProductVO serialPro = orderCmmMethod.findCmmSerializedProductById(cmmSerializedProId);
        CmmWarrantySerializedProductVO warrantySerialPro = svm0102Service.findCmmWarrantySerializedProductBySerializedId(cmmSerializedProId);

        result.setWarrantyType(Objects.isNull(warrantySerialPro) ? CommonConstants.CHAR_BLANK : warrantySerialPro.getWarrantyProductClassification());
        result.setWarrantyConditionNow(this.getWarrantyCondition(warrantySerialPro));
        result.setEvFlag(serialPro.getEvFlag());

        String[] fsc = this.setFscResultOneToNine(cmmSerializedProId, stdDate);

        int fscLength = fsc.length;

        //大车型只有1枚coupon
        if (svm0102Service.findBigBikeExistByModleCd(StringUtils.substring(modelCd, 0, 6))) {

            result.getFreeCouponResult().add(new SVM010201FreeCouponDetailBO("FirstFSC", fscLength >= 1 ? fsc[0] : CommonConstants.CHAR_N));
        }
        else {

            //电车有6枚Coupon，普通车有9枚Coupon
            result.getFreeCouponResult().add(new SVM010201FreeCouponDetailBO("FirstFSC", fscLength >= 1 ? fsc[0] : CommonConstants.CHAR_N));
            result.getFreeCouponResult().add(new SVM010201FreeCouponDetailBO("SecondFSC", fscLength >= 2 ? fsc[1] : CommonConstants.CHAR_N));
            result.getFreeCouponResult().add(new SVM010201FreeCouponDetailBO("ThirdFSC", fscLength >= 3 ? fsc[2] : CommonConstants.CHAR_N));
            result.getFreeCouponResult().add(new SVM010201FreeCouponDetailBO("FourthFSC", fscLength >= 4 ? fsc[3] : CommonConstants.CHAR_N));
            result.getFreeCouponResult().add(new SVM010201FreeCouponDetailBO("FifthFSC", fscLength >= 5 ? fsc[4] : CommonConstants.CHAR_N));
            result.getFreeCouponResult().add(new SVM010201FreeCouponDetailBO("SixthFSC", fscLength >= 6 ? fsc[5] : CommonConstants.CHAR_N));

            if (!StringUtils.equals(serialPro.getEvFlag(), CommonConstants.CHAR_Y)) {

                result.getFreeCouponResult().add(new SVM010201FreeCouponDetailBO("SeventhFSC", fscLength >= 7 ? fsc[6] : CommonConstants.CHAR_N));
                result.getFreeCouponResult().add(new SVM010201FreeCouponDetailBO("EighthFSC", fscLength >= 8 ? fsc[7] : CommonConstants.CHAR_N));
                result.getFreeCouponResult().add(new SVM010201FreeCouponDetailBO("NinthFSC", fscLength >= 9 ? fsc[8] : CommonConstants.CHAR_N));
            }
        }

        return result;
    }

    private String getWarrantyCondition(CmmWarrantySerializedProductVO cmmWarrantySerialPro) {

        String result = CommonConstants.CHAR_BLANK;

        if (Objects.isNull(cmmWarrantySerialPro)) {return result;}

        String warrantyType = cmmWarrantySerialPro.getWarrantyProductClassification();

        if (warrantyType.equals(WarrantyPolicyType.OLD.getCodeDbid())) {

            result = ComUtil.t("M.E.10266");

        } else if (warrantyType.equals(WarrantyPolicyType.BIGBIKE.getCodeDbid())
                || warrantyType.equals(WarrantyPolicyType.EV.getCodeDbid())) {

            result = ComUtil.t("M.E.10308");

        }else if (warrantyType.equals(WarrantyPolicyType.NEW.getCodeDbid())) {

            Integer yearPerid = this.getDifferenceYearsToCurrentTime(ComUtil.str2date(cmmWarrantySerialPro.getFromDate()), ComUtil.str2date(cmmWarrantySerialPro.getToDate()));

            result = ComUtil.t("M.E.10268", new String[] {Integer.toString(yearPerid > 2 ? 3 : yearPerid), cmmWarrantySerialPro.getWarrantyProductUsage()});
        }

        return result;
    }

    private void prepareSpecialClaimSerialProForRegister(SVM010201Parameter para) {
        //SpecialClaim Service一旦创建，需要锁死同一回收批次下的master数据
        if (!StringUtils.equals(para.getServiceOrder().getServiceCategoryId(), ServiceCategory.SPECIALCLAIM.getCodeDbid())) {return;}

        List<CmmSpecialClaimSerialProVO> specialClaimSerialProList = svm0102Service.findSpecialClaimSerialProListByBulletinNo(para.getServiceOrder().getBulletinNo(), para.getServiceOrder().getCmmSerializedProductId());

        for (CmmSpecialClaimSerialProVO specialClaimSerialPro : specialClaimSerialProList) {

            specialClaimSerialPro.setDealerCd(para.getServiceOrder().getSiteId());
            specialClaimSerialPro.setFacilityCd(para.getServiceOrder().getFacilityCd());
            specialClaimSerialPro.setApplyDate(LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)));
            specialClaimSerialPro.setClaimFlag(CommonConstants.CHAR_Y);
        }

        para.setCmmSpecialClaimSerialProList(specialClaimSerialProList);
    }

    private void prepareSpecialClaimSerialProForCancel(SVM010201Parameter para) {
        //SpecialClaim Service取消时，需要放开回收批次的master数据
        if (!StringUtils.equals(para.getServiceOrder().getServiceCategoryId(), ServiceCategory.SPECIALCLAIM.getCodeDbid())) {return;}

        List<CmmSpecialClaimSerialProVO> specialClaimSerialProList = svm0102Service.findSpecialClaimSerialProListByBulletinNo(para.getServiceOrder().getBulletinNo(), para.getServiceOrder().getCmmSerializedProductId());

        for (CmmSpecialClaimSerialProVO specialClaimSerialPro : specialClaimSerialProList) {

            specialClaimSerialPro.setDealerCd(null);
            specialClaimSerialPro.setFacilityCd(null);
            specialClaimSerialPro.setApplyDate(null);
            specialClaimSerialPro.setClaimFlag(CommonConstants.CHAR_N);
        }

        para.setCmmSpecialClaimSerialProList(specialClaimSerialProList);
    }

    private void prepareServiceScheduleForRegister(SVM010201Parameter para) {

        ServiceScheduleVO serviceSchedule = svm0102Service.findTodayCompleteServiceScheduleByPlateNo(para.getServiceOrder().getSiteId(), para.getServiceOrder().getFacilityId(), para.getServiceOrder().getPlateNo());

        if (Objects.isNull(serviceSchedule)) {return;}

        serviceSchedule.setReservationStatus(ReservationStatus.COMPLETED.getCodeDbid());
        serviceSchedule.setServiceOrderId(para.getServiceOrder().getServiceOrderId());

        para.setServiceSchedule(serviceSchedule);
    }

    private void prepareServiceScheduleForSettle(SVM010201Parameter para) {

        ServiceScheduleVO serviceSchedule = svm0102Service.findServiceScheduleByServiceOrderId(para.getServiceOrder().getServiceOrderId());

        if (Objects.isNull(serviceSchedule)) {return;}

        serviceSchedule.setServiceOrderSettledFlag(CommonConstants.CHAR_Y);

        para.setServiceSchedule(serviceSchedule);
    }

    private void prepareServiceOrderEditHisForRegister(SVM010201Parameter para, PJUserDetails uc) {
        para.setServiceOrderEditHistory(this.setValueForServiceOrderEditHistory(ServiceOrderEditHistoryVO.create(uc.getDealerCode(), para.getServiceOrder().getServiceOrderId()) , uc, "Created SO"));
    }

    private void prepareServiceOrderEditHisForUpdate(SVM010201Parameter para, PJUserDetails uc) {
        para.setServiceOrderEditHistory(this.setValueForServiceOrderEditHistory(ServiceOrderEditHistoryVO.create(uc.getDealerCode(), para.getServiceOrder().getServiceOrderId()) , uc, "Edited SO"));
    }

    private void prepareServiceOrderEditHisForSettle(SVM010201Parameter para, PJUserDetails uc) {
        para.setServiceOrderEditHistory(this.setValueForServiceOrderEditHistory(ServiceOrderEditHistoryVO.create(uc.getDealerCode(), para.getServiceOrder().getServiceOrderId()) , uc, "Settled SO"));
    }

    private void prepareServiceOrderEditHisForCancel(SVM010201Parameter para, PJUserDetails uc) {
        para.setServiceOrderEditHistory(this.setValueForServiceOrderEditHistory(ServiceOrderEditHistoryVO.create(uc.getDealerCode(), para.getServiceOrder().getServiceOrderId()) , uc, "Cancelled SO"));
    }

    public void prepareServiceOrderEditHisForPrint(Long serviceOrderId, PJUserDetails uc) {
        svm0102Service.saveServiceOrderEditHistoryVO(this.setValueForServiceOrderEditHistory(ServiceOrderEditHistoryVO.create(uc.getDealerCode(), serviceOrderId) , uc, "Printed SO"));
    }

    private ServiceOrderEditHistoryVO setValueForServiceOrderEditHistory(ServiceOrderEditHistoryVO result, PJUserDetails uc, String operationDesc) {

        result.setOperationDesc(operationDesc);
        result.setOperatorCd(uc.getPersonCode());
        result.setOperatorNm(uc.getPersonName());
        result.setCmmOperatorId(uc.getPersonId());

        return result;
    }

    private void setForeignKey(SVM010201Parameter para) {

        para.getServiceOrder().setRelatedSalesOrderId(para.getSalesOrder().getSalesOrderId());
        para.getSalesOrder().setRelatedSvOrderId(para.getServiceOrder().getServiceOrderId());

        //获取Situation中symptomCd和faultId的map
        this.setFaultIdForJobAndPart(para);
    }

    private void setFaultIdForJobAndPart(SVM010201Parameter para) {

        //获取IdMap
        Map<Long, Long> symptomFaultIdMap = para.getSituationListCheckList().stream()
                                                               .filter(situation -> !Objects.isNull(situation.getServiceOrderFaultId()))
                                                               .collect(Collectors.toMap(SituationBO::getSymptomId, SituationBO::getServiceOrderFaultId));

        //补上新增行数据（新增后才采番）
        for (ServiceOrderFaultVO serviceFault : para.getSituationListForSave()) {

            symptomFaultIdMap.put(serviceFault.getSymptomId(), serviceFault.getServiceOrderFaultId());
        }

        //赋值jobDetail
        for (ServiceOrderJobVO serviceOrderJob : para.getJobListForSave()) {

            if (Objects.isNull(serviceOrderJob.getSymptomId())) {continue;}

            serviceOrderJob.setServiceOrderFaultId(symptomFaultIdMap.get(serviceOrderJob.getSymptomId()));
        }
        //赋值partDetail
        for (SalesOrderItemVO serviceOrderPart : para.getPartListForSave()) {

            if (Objects.isNull(serviceOrderPart.getSymptomId())) {continue;}

            serviceOrderPart.setServiceOrderFaultId(symptomFaultIdMap.get(serviceOrderPart.getSymptomId()));
        }
    }

    private void prepareConsumerBasicInfoForSettle(SVM010201Form model, SVM010201Parameter para){

        BaseConsumerForm consumerModel = new BaseConsumerForm();

        consumerModel.setSiteId(model.getSiteId());
        consumerModel.setLastNm(model.getOrderInfo().getLastName());
        consumerModel.setMiddleNm(model.getOrderInfo().getMiddleName());
        consumerModel.setFirstNm(model.getOrderInfo().getFirstName());
        consumerModel.setMobilePhone(model.getOrderInfo().getMobilephone());
        consumerModel.setEmail(model.getOrderInfo().getEmail());
        consumerModel.setConsumerId(model.getOrderInfo().getConsumerId());

        para.setConsumerBaseInfo(consumerModel);
    }

    private void prepareConsumerPrivacyPolicyResult(SVM010201Form model, SVM010201Parameter para) {

        para.setConsumerPrivacyPolicyResult(consumerFacade.prepareConsumerPrivacyPolicyResult(new ConsumerPolicyParameter(model.getSiteId()
                                                                                                                        , model.getOrderInfo().getPointCd()
                                                                                                                        , model.getOrderInfo().getLastName()
                                                                                                                        , model.getOrderInfo().getMiddleName()
                                                                                                                        , model.getOrderInfo().getFirstName()
                                                                                                                        , model.getOrderInfo().getMobilephone()
                                                                                                                        , model.getOrderInfo().getPolicyResultFlag()
                                                                                                                        , model.getOrderInfo().getPolicyFileName())));
    }

    private void prepareSalesOrderForRegister(SVM010201Form model, SVM010201Parameter para, PJUserDetails uc) {
        para.setSalesOrder(this.setValueForSalesOrder(SalesOrderVO.create() , model, uc, CommonConstants.OPERATION_STATUS_NEW));
    }

    private void prepareSalesOrderForUpdate(SVM010201Form model, SVM010201Parameter para, PJUserDetails uc) {
        para.setSalesOrder(this.setValueForSalesOrder(orderCmmMethod.findSalesOrderById(para.getServiceOrder().getRelatedSalesOrderId()), model, uc, CommonConstants.OPERATION_STATUS_UPDATE));
    }

    private void prepareSalesOrderForCancel(SVM010201Parameter para) {
        SalesOrderVO salesOrder = orderCmmMethod.findSalesOrderById(para.getServiceOrder().getRelatedSalesOrderId());
        salesOrder.setOrderStatus(SalesOrderStatus.CANCELLED);
        para.setSalesOrder(salesOrder);
    }

    private SalesOrderVO setValueForSalesOrder(SalesOrderVO result, SVM010201Form model, PJUserDetails uc, String operationStatus) {

        if (StringUtils.equals(operationStatus, CommonConstants.OPERATION_STATUS_NEW)) {

            result.setSiteId(uc.getDealerCode());
            result.setOrderDate(model.getOrderInfo().getOrderDate());
            result.setOrderStatus(SalesOrderStatus.SP_WAITINGALLOCATE);
            result.setAllocateDueDate(result.getOrderDate());
            result.setFacilityId(model.getOrderInfo().getPointId());
            result.setOrderToType(OrgRelationType.CONSUMER.getCodeDbid());
            result.setProductClassification(ProductClsType.PART.getCodeDbid());
            result.setOrderPriorityType(SalesOrderPriorityType.SOEO.getCodeDbid());
            result.setOrderSourceType(ProductClsType.SERVICE.getCodeDbid());
            result.setEntryFacilityId(result.getFacilityId());
            result.setDropShipType(DropShipType.NOTDROPSHIP);
            result.setBoCancelFlag(CommonConstants.CHAR_N);
            result.setDemandExceptionFlag(CommonConstants.CHAR_N);
            result.setAccEoFlag(CommonConstants.CHAR_N);
            result.setBoFlag(CommonConstants.CHAR_Y);
            result.setCustomerIfFlag(CommonConstants.CHAR_N);
            result.setDiscountOffRate(BigDecimal.ZERO);
            result.setDepositAmt(BigDecimal.ZERO);
            result.setEntryPicId(uc.getPersonId());
            result.setEntryPicNm(uc.getPersonName());
            result.setShipCompleteFlag(CommonConstants.CHAR_Y);
        }

        result.setSalesPicId(uc.getPersonId());
        result.setSalesPicNm(uc.getPersonName());

        result.setCmmConsumerId(model.getOrderInfo().getConsumerId());
        result.setMobilePhone(model.getOrderInfo().getMobilephone());
        result.setConsumerNmFirst(model.getOrderInfo().getFirstName());
        result.setConsumerNmMiddle(model.getOrderInfo().getMiddleName());
        result.setConsumerNmLast(model.getOrderInfo().getLastName());
        result.setConsumerNmFull(new StringBuilder().append(result.getConsumerNmLast()).append(CommonConstants.CHAR_SPACE).append(result.getConsumerNmMiddle()).append(CommonConstants.CHAR_SPACE).append(result.getConsumerNmFirst()).toString() );
        result.setEmail(model.getOrderInfo().getEmail());

        return result;
    }

    private String getUseTypeByMotorId(Long cmmSerializedProductId) {

        CmmRegistrationDocumentVO registrationDoc = svm0102Service.findRegisterDocByMotorId(cmmSerializedProductId);

        return Objects.isNull(registrationDoc) ? CommonConstants.CHAR_BLANK : registrationDoc.getUseType();
    }

    private void prepareServiceOrderPartListForRegister(SVM010201Form model, SVM010201Parameter para) {

        List<SalesOrderItemVO> saveList = new ArrayList<>();

        Integer seqNo = CommonConstants.INTEGER_ZERO;

        //处理新增行
        for (PartDetailBO part : model.getPartList().getInsertRecords()) {
            saveList.add(this.setSalesOrderItemVOForRegister(SalesOrderItemVO.create(model.getSiteId(), para.getSalesOrder().getSalesOrderId()), part, seqNo));
            seqNo++;
        }

        para.setPartListForSave(saveList);
    }

    private void prepareServiceOrderPartListForUpdate(SVM010201Form model, SVM010201Parameter para) {

         List<SalesOrderItemVO> saveList = new ArrayList<>();
         Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = new HashMap<>();
         SalesOrderItemVO servicePart;

         Integer seqNo = para.getPartListCheckList().stream()
                                                    .filter(part ->!Objects.isNull(part.getSeqNo())) .map(PartDetailBO::getSeqNo)
                                                    .max(Integer::compareTo)
                                                    .orElse(0);

         //处理新增行
         for (PartDetailBO part : model.getPartList().getInsertRecords()) {

             servicePart = SalesOrderItemVO.create(model.getSiteId(), para.getSalesOrder().getSalesOrderId());

             saveList.add(this.setSalesOrderItemVOForRegister(servicePart, part, seqNo));
             seqNo++;
         }

         Map<Long, SalesOrderItemVO> partInDBMap = (model.getPartList().getUpdateRecords().isEmpty() && model.getPartList().getRemoveRecords().isEmpty())
                                                     ? new HashMap<>()
                                                     : svm0102Service.timelyFindServiceOrderPartListByIds(Stream.concat(model.getPartList().getUpdateRecords().stream().map(PartDetailBO::getSalesOrderItemId)
                                                                                                                      , model.getPartList().getRemoveRecords().stream().map(PartDetailBO::getSalesOrderItemId)).toList())
                                                                     .stream()
                                                                     .collect(Collectors.toMap(SalesOrderItemVO::getSalesOrderItemId, value -> value));
         //处理修改行
         for (PartDetailBO part : model.getPartList().getUpdateRecords()) {

             servicePart = partInDBMap.get(part.getSalesOrderItemId());

             if (Objects.isNull(servicePart)) {continue;}

             BigDecimal oldQty = servicePart.getActualQty();
             BigDecimal newQty = part.getQty();
             BigDecimal difQty = oldQty.subtract(newQty).abs();

             //追加Qty
             if (newQty.compareTo(oldQty) > 0) {

                 servicePart.setWaitingAllocateQty(servicePart.getWaitingAllocateQty().add(difQty));
             }
             //扣减Qty
             else if (newQty.compareTo(oldQty) < 0) {

                 BigDecimal remainQty = this.doPartDetailModifyDeduct(model.getSiteId(), model.getOrderInfo().getPointId(), stockStatusVOChangeMap, servicePart, difQty);

                 if (remainQty.compareTo(BigDecimal.ZERO) > 0) {

                     throw new BusinessCodedException(ComUtil.t("M.E.10250"));
                 }
             }

             saveList.add(this.setSalesOrderItemVOForUpdate(servicePart, part));
         }
         //处理删除行
         for (PartDetailBO part : model.getPartList().getRemoveRecords()) {

             servicePart = partInDBMap.get(part.getSalesOrderItemId());

             if (Objects.isNull(servicePart)) {continue;}

             orderCmmMethod.doPartDetailDelete(model.getSiteId(), model.getOrderInfo().getPointId(), stockStatusVOChangeMap, servicePart);

             saveList.add(servicePart);
         }

         para.setPartListForSave(saveList);
         para.setStockStatusVOChangeMap(stockStatusVOChangeMap);
    }

    private void prepareServiceOrderPartListForCancel(SVM010201Parameter para){

        List<SalesOrderItemVO> salesOrderItemList = svm0102Service.timelyFindServiceOrderPartListBySalesOrderId(para.getSalesOrder().getSalesOrderId())
                                                                  .stream()
                                                                  .filter(part -> part.getActualQty().compareTo(BigDecimal.ZERO) > 0)
                                                                  .toList();

        Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = orderCmmMethod.prepareServiceOrderPartListForCancel(salesOrderItemList, para.getServiceOrder().getSiteId(), para.getServiceOrder().getFacilityId());

        para.setPartListForSave(salesOrderItemList);
        para.setStockStatusVOChangeMap(stockStatusVOChangeMap);
    }

    private BigDecimal doPartDetailModifyDeduct(String siteId, Long pointId, Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap, SalesOrderItemVO servicePart, BigDecimal reduceQty) {

        //优先扣减waitAllocate
        if (servicePart.getWaitingAllocateQty().compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal minusQty = servicePart.getWaitingAllocateQty().compareTo(reduceQty) > 0 ? reduceQty : servicePart.getWaitingAllocateQty();

            servicePart.setWaitingAllocateQty(servicePart.getWaitingAllocateQty().subtract(minusQty));
            servicePart.setCancelQty(servicePart.getCancelQty().add(minusQty));

            reduceQty = reduceQty.subtract(minusQty);
        }
        //其次扣减BO
        if (reduceQty.compareTo(BigDecimal.ZERO) > 0 && servicePart.getBoQty().compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal minusQty = servicePart.getBoQty().compareTo(reduceQty) >= 0 ? reduceQty : servicePart.getBoQty();

            servicePart.setBoQty(servicePart.getBoQty().subtract(minusQty));
            servicePart.setCancelQty(servicePart.getCancelQty().add(minusQty));

            reduceQty = reduceQty.subtract(minusQty);

            inventoryLogic.generateStockStatusVOMap(siteId, pointId, servicePart.getAllocatedProductId(), minusQty.negate(), SpStockStatus.BO_QTY.getCodeDbid(), stockStatusVOChangeMap);
        }
        //最后扣减Allocated
        if (reduceQty.compareTo(BigDecimal.ZERO) > 0 && servicePart.getAllocatedQty().compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal minusQty = servicePart.getAllocatedQty().compareTo(reduceQty) >= 0 ? reduceQty : servicePart.getAllocatedQty();

            servicePart.setAllocatedQty(servicePart.getAllocatedQty().subtract(minusQty));
            servicePart.setCancelQty(servicePart.getCancelQty().add(minusQty));

            reduceQty = reduceQty.subtract(minusQty);

            inventoryLogic.generateStockStatusVOMap(siteId, pointId, servicePart.getAllocatedProductId(), minusQty.negate(), SpStockStatus.ALLOCATED_QTY.getCodeDbid(), stockStatusVOChangeMap);
            inventoryLogic.generateStockStatusVOMap(siteId, pointId, servicePart.getAllocatedProductId(), minusQty, SpStockStatus.ONHAND_QTY.getCodeDbid(), stockStatusVOChangeMap);
        }

        return reduceQty;
    }

    private SalesOrderItemVO setSalesOrderItemVOForRegister(SalesOrderItemVO result, PartDetailBO part, Integer seqNo) {

        result.setServiceCategoryId(part.getServiceCategoryId());
        result.setSettleTypeId(part.getSettleTypeId());
        result.setServiceDemandId(part.getServiceDemandId());
        result.setServiceDemandContent(part.getServiceDemandContent());
        result.setSymptomId(part.getSymptomId());
        result.setServicePackageCd(part.getServicePackageCd());
        result.setServicePackageId(part.getServicePackageId());
        result.setServicePackageNm(part.getServicePackageNm());
        result.setSvClaimFlag(StringUtils.equals(part.getServiceCategoryId(), ServiceCategory.CLAIM.getCodeDbid()) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);

        result.setProductId(part.getPartId());
        result.setProductCd(part.getPartCd());
        result.setProductNm(part.getPartNm());
        result.setAllocatedProductCd(part.getPartCd());
        result.setAllocatedProductNm(part.getPartNm());
        result.setAllocatedProductId(part.getPartId());
        result.setProductClassification(ProductClsType.PART.getCodeDbid());
        result.setBatteryFlag(part.getBatteryFlag());
        result.setBoCancelFlag(CommonConstants.CHAR_N);
        result.setOrderQty(part.getQty());
        result.setStandardPrice(part.getStandardPrice());
        result.setSeqNo(seqNo);
        result.setActualQty(part.getQty());
        result.setWaitingAllocateQty(part.getQty());
        result.setSpecialPrice(part.getSpecialPrice());
        result.setDiscountOffRate(part.getDiscount());
        result.setDiscountAmt(part.getDiscountAmt());
        result.setSellingPrice(part.getSellingPrice());
        result.setTaxRate(part.getTaxRate());
        result.setActualAmt(part.getAmount());
        result.setSellingPriceNotVat(part.getSellingPriceNotVat());
        result.setActualAmtNotVat(part.getActualAmtNotVat());
        result.setBatteryType(part.getBatteryType());

        return result;
    }

    private SalesOrderItemVO setSalesOrderItemVOForUpdate(SalesOrderItemVO result, PartDetailBO part) {

        result.setActualQty(part.getQty());
        result.setSpecialPrice(part.getSpecialPrice());
        result.setDiscountOffRate(part.getDiscount());
        result.setDiscountAmt(part.getDiscountAmt());
        result.setSellingPrice(part.getSellingPrice());
        result.setTaxRate(part.getTaxRate());
        result.setActualAmt(part.getAmount());
        result.setSellingPriceNotVat(part.getSellingPriceNotVat());
        result.setActualAmtNotVat(part.getActualAmtNotVat());
        result.setBatteryType(part.getBatteryType());

        return result;
    }

    private void prepareServiceOrderFaultList(SVM010201Form model, SVM010201Parameter para) {
        //处理新增和更新行
        para.setSituationListForSave(orderCmmMethod.prepareNewOrUpdateFaultList(model.getSituationList(), model.getSiteId(), para.getServiceOrder().getServiceOrderId()));
        //处理删除行
        para.setSituationListForDelete(model.getSituationList().getRemoveRecords().stream().map(SituationBO::getServiceOrderFaultId).toList());
    }

    private void prepareServiceOrderBatteryList(SVM010201Form model, SVM010201Parameter para) {

        if (model.getBatteryList().isEmpty()) {return;}

        //获取DB中的battery明细
        List<Long> serviceBatteryIds = model.getBatteryList().stream().map(BatteryBO::getServiceOrderBatteryId).toList();

        Map<Long, ServiceOrderBatteryVO> batteryInDBMap = serviceBatteryIds.isEmpty()
                                                            ? new HashMap<>()
                                                            : svm0102Service.findServiceOrderBatteryListByIds(serviceBatteryIds)
                                                                            .stream()
                                                                            .collect(Collectors.toMap(ServiceOrderBatteryVO::getServiceOrderBatteryId, value -> value));

        List<ServiceOrderBatteryVO> batteryList = new ArrayList<>();
        ServiceOrderBatteryVO serviceBattery;

        for (BatteryBO battery : model.getBatteryList()) {

            serviceBattery = Objects.isNull(battery.getServiceOrderBatteryId()) ? ServiceOrderBatteryVO.create(model.getSiteId(), para.getServiceOrder().getServiceOrderId()) : batteryInDBMap.get(battery.getServiceOrderBatteryId());

            batteryList.add(this.setValueForServiceOrderBatteryVO(serviceBattery, battery, model.getOrderInfo().getCmmSerializedProductId()));
        }

        para.setBatteryList(batteryList);
    }

    private ServiceOrderBatteryVO setValueForServiceOrderBatteryVO(ServiceOrderBatteryVO result, BatteryBO battery, Long cmmSerializedProId) {

        result.setBatteryType(battery.getBatteryType());
        result.setCmmSerializedProductId(cmmSerializedProId);
        result.setProductId(battery.getPartId());
        result.setProductCd(battery.getPartCd());
        result.setBatteryId(battery.getCurrentBatteryId());
        result.setBatteryNo(battery.getCurrentBatteryNo());
        result.setWarrantyStartDate(battery.getWarttryStartDate());
        result.setWarrantyTerm(battery.getWarrantyTerm());
        result.setNewProductId(battery.getNewPartId());
        result.setNewProductCd(battery.getNewPartCd());
        result.setNewBatteryNo(battery.getNewBatteryNo());
        result.setSellingPrice(battery.getSellingPrice());
        result.setOrderQty(StringUtils.isNotBlank(battery.getNewBatteryNo()) ? BigDecimal.ONE : BigDecimal.ZERO);

        return result;
    }

    private ServiceOrderJobVO setValueForServiceOrderJobVO(ServiceOrderJobVO result, JobDetailBO job) {

        result.setServiceCategoryId(job.getServiceCategoryId());
        result.setSettleTypeId(job.getSettleTypeId());
        result.setServiceDemandId(job.getServiceDemandId());
        result.setServiceDemandContent(job.getServiceDemandContent());
        result.setSymptomId(job.getSymptomId());
        result.setJobId(job.getJobId());
        result.setJobCd(job.getJobCd());
        result.setJobNm(job.getJobNm());
        result.setStdManhour(job.getManhour());
        result.setStandardPrice(job.getStandardPrice());
        result.setSpecialPrice(job.getSpecialPrice());
        result.setDiscountAmt(job.getDiscountAmt());
        result.setDiscount(job.getDiscount());
        result.setVatRate(result.getVatRate());
        result.setSellingPrice(job.getSellingPrice());
        result.setServicePackageId(job.getServicePackageId());
        result.setServicePackageCd(job.getServicePackageCd());
        result.setServicePackageNm(job.getServicePackageNm());

        return result;
    }

    private void prepareServiceOrderJobList(SVM010201Form model, SVM010201Parameter para) {

        List<ServiceOrderJobVO> saveList = new ArrayList<>();
        ServiceOrderJobVO serviceJob;

        //处理新增行
        for (JobDetailBO job : model.getJobList().getInsertRecords()) {

            serviceJob = ServiceOrderJobVO.create(model.getSiteId(), para.getServiceOrder().getServiceOrderId());

            saveList.add(this.setValueForServiceOrderJobVO(serviceJob, job));
        }
        //处理修改行
        Map<Long, ServiceOrderJobVO> jobInDBMap = model.getJobList().getUpdateRecords().isEmpty()
                                                ? new HashMap<>()
                                                : svm0102Service.findServiceOrderJobListByIds(model.getJobList().getUpdateRecords().stream().map(JobDetailBO::getServiceOrderJobId).toList())
                                                                .stream()
                                                                .collect(Collectors.toMap(ServiceOrderJobVO::getServiceOrderJobId, value -> value));

        for (JobDetailBO job : model.getJobList().getUpdateRecords()) {

            serviceJob = jobInDBMap.get(job.getServiceOrderJobId());

            if (Objects.isNull(serviceJob)) {continue;}

            saveList.add(this.setValueForServiceOrderJobVO(serviceJob, job));
        }
        //处理删除行
        para.setJobListForDelete(model.getJobList().getRemoveRecords().stream().map(JobDetailBO::getServiceOrderJobId).toList());
        para.setJobListForSave(saveList);
    }

    private void prepareServiceOrderForRegister(SVM010201Form model, SVM010201Parameter para, PJUserDetails uc) {

        para.setServiceOrder(this.setValueForServiceOrder(ServiceOrderVO.create(), para.getCmmSerializedProduct(), model, uc, CommonConstants.OPERATION_STATUS_NEW));
        model.getOrderInfo().setServiceOrderId(para.getServiceOrder().getServiceOrderId());
    }

    private void prepareServiceOrderForUpdate(SVM010201Form model, SVM010201Parameter para, PJUserDetails uc) {

        para.setServiceOrder(this.setValueForServiceOrder(para.getServiceOrder(), para.getCmmSerializedProduct(), model, uc, CommonConstants.OPERATION_STATUS_UPDATE));
    }

    private void prepareServiceOrderForSettle(SVM010201Form model, SVM010201Parameter para, PJUserDetails uc) {

        para.setServiceOrder(this.setValueForServiceOrder(para.getServiceOrder(), para.getCmmSerializedProduct(), model, uc, CommonConstants.OPERATION_STATUS_FINISH));
    }

    private void prepareServiceOrderForCancel(SVM010201Parameter para) {
        para.getServiceOrder().setOrderStatusId(ServiceOrderStatus.CANCELLED.getCodeDbid());
        para.getServiceOrder().setOrderStatusContent(ServiceOrderStatus.CANCELLED.getCodeData1());
    }

    private ServiceOrderVO setValueForServiceOrder(ServiceOrderVO result, CmmSerializedProductVO cmmSerializedProd, SVM010201Form model, PJUserDetails uc, String operationStatus) {

        if (StringUtils.equals(operationStatus, CommonConstants.OPERATION_STATUS_NEW)) {

            result.setSiteId(uc.getDealerCode());
            result.setFacilityId(model.getOrderInfo().getPointId());
            result.setFacilityCd(model.getOrderInfo().getPointCd());
            result.setFacilityNm(model.getOrderInfo().getPointNm());
            result.setOrderDate(model.getOrderInfo().getOrderDate());
            result.setOrderStatusId(ServiceOrderStatus.WAITFORSETTLE.getCodeDbid());
            result.setOrderStatusContent(ServiceOrderStatus.WAITFORSETTLE.getCodeData1());
            result.setServiceCategoryId(model.getOrderInfo().getServiceCategoryId());
            result.setServiceCategoryContent(constantsLogic.getConstantsByCodeDbId(ServiceCategory.class.getDeclaredFields(), model.getOrderInfo().getServiceCategoryId()).getCodeData1());
            result.setServiceDemandId(model.getOrderInfo().getServiceDemandId());
            result.setServiceDemandContent(model.getOrderInfo().getServiceDemand());
            result.setZeroKmFlag(CommonConstants.CHAR_N);
            result.setStartTime(LocalDateTime.parse(model.getOrderInfo().getStartTime(), DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMDHM)));
            result.setEntryPicId(uc.getPersonId());
            result.setEntryPicCd(uc.getPersonCode());
            result.setEntryPicNm(uc.getPersonName());
            result.setCmmSerializedProductId(model.getOrderInfo().getCmmSerializedProductId());
            result.setBrandId(Long.parseLong(BrandType.YAMAHA.getCodeDbid()));
            result.setBrandContent(BrandType.YAMAHA.getCodeData1());
            result.setFrameNo(model.getOrderInfo().getFrameNo());
            result.setModelId(model.getOrderInfo().getModelId());
            result.setModelCd(model.getOrderInfo().getModelCd());
            result.setModelNm(model.getOrderInfo().getModelNm());
            result.setEvFlag(model.getOrderInfo().getEvFlag());
            result.setModelTypeId(model.getOrderInfo().getModelTypeId());
            result.setSoldDate(model.getOrderInfo().getSoldDate());
            result.setCmmSpecialClaimId(model.getOrderInfo().getSpecialClaimId());
            result.setBulletinNo(model.getOrderInfo().getBulletinNo());

            if (!Objects.isNull(cmmSerializedProd)) {
                result.setEngineNo(cmmSerializedProd.getEngineNo());
                MstProductVO modelLv1 = orderCmmMethod.getProductById(cmmSerializedProd.getProductId());
                result.setColor(Objects.isNull(modelLv1) ? null : modelLv1.getColorNm());
            }
        }

        if (StringUtils.equals(operationStatus, CommonConstants.OPERATION_STATUS_FINISH)) {

            result.setSettleDate(LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)));
            result.setSettleTime(LocalDateTime.now());
            result.setOrderStatusId(ServiceOrderStatus.COMPLETED.getCodeDbid());
            result.setOrderStatusContent(ServiceOrderStatus.COMPLETED.getCodeData1());
            result.setCashierCd(uc.getPersonCode());
            result.setCashierId(uc.getPersonId());
            result.setCashierNm(uc.getPersonName());
        }

        result.setMileage(model.getOrderInfo().getMileage());
        result.setOperationStartTime(StringUtils.isBlank(model.getOrderInfo().getOperationStart()) ? null : LocalDateTime.parse(model.getOrderInfo().getOperationStart(), DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMDHM)));
        result.setOperationFinishTime(StringUtils.isBlank(model.getOrderInfo().getOperationFinish()) ? null : LocalDateTime.parse(model.getOrderInfo().getOperationFinish(), DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMDHM)));
        result.setMechanicId(model.getOrderInfo().getMechanicId());
        result.setMechanicCd(model.getOrderInfo().getMechanicCd());
        result.setMechanicNm(model.getOrderInfo().getMechanicNm());
        result.setWelcomeStaffId(model.getOrderInfo().getWelcomeStaffId());
        result.setWelcomeStaffCd(model.getOrderInfo().getWelcomeStaffCd());
        result.setWelcomeStaffNm(model.getOrderInfo().getWelcomeStaffNm());
        result.setPlateNo(model.getOrderInfo().getPlateNo());
        result.setUseTypeId(model.getOrderInfo().getUseType());
        result.setRelationTypeId(model.getOrderInfo().getOwnerType());
        result.setConsumerId(model.getOrderInfo().getConsumerId());
        result.setConsumerVipNo(Objects.isNull(model.getOrderInfo().getConsumerId()) ? null : orderCmmMethod.findCmmConsumerById(model.getOrderInfo().getConsumerId()).getVipNo());
        result.setLastNm(model.getOrderInfo().getLastName());
        result.setMiddleNm(model.getOrderInfo().getMiddleName());
        result.setFirstNm(model.getOrderInfo().getFirstName());
        result.setMobilePhone(model.getOrderInfo().getMobilephone());
        result.setEmail(model.getOrderInfo().getEmail());
        result.setConsumerFullNm(new StringBuilder().append(result.getLastNm()).append(CommonConstants.CHAR_SPACE).append(result.getMiddleNm()).append(CommonConstants.CHAR_SPACE).append(result.getFirstNm()).toString());
        result.setOrderForEmployeeFlag(model.getOrderInfo().getCreateForEmployeeFlag());
        result.setEmployeeCd(model.getOrderInfo().getEmployeeCd());
        result.setEmployeeRelationShipId(model.getOrderInfo().getRelationShipId());
        result.setTicketNo(model.getOrderInfo().getTicketNo());
        result.setServiceSubject(model.getOrderInfo().getServiceTitle());
        result.setMechanicComment(model.getOrderInfo().getMechanicComment());
        result.setPaymentMethodId(model.getOrderInfo().getPaymentMethodId());
        result.setDepositAmt(model.getOrderInfo().getDepositAmt());

        return result;
    }

    private SVM010201BO initForBasicInfo(SVM010201BO result, PJUserDetails uc) {

        result.setDoFlag(uc.getDoFlag());

        result.setSummaryList(orderCmmMethod.generateSummaryList(result.getJobList(), result.getPartList()));

        return result;
    }

    private SVM010201BO initForNewCreate(PJUserDetails uc) {

        SVM010201BO result = new SVM010201BO();

        result.setPointCd(uc.getDefaultPointCd());
        result.setPointId(uc.getDefaultPointId());
        result.setPointNm(uc.getDefaultPointNm());
        result.setOrderDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
        result.setOrderStatus(ServiceOrderStatus.NEW.getCodeData1());
        result.setOrderStatusId(ServiceOrderStatus.NEW.getCodeDbid());

        result.setEditor(uc.getPersonCode() + CommonConstants.CHAR_SPACE + uc.getPersonName());
        result.setStartTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMDHM)));

        return result;
    }

    private SVM010201BO initForUpdate(Long serviceOrderId, String orderNo, Long pointId, String siteId) {

        //根据条件查询serviceOrder
        ServiceOrderVO serviceOrder = orderCmmMethod.timelySearchServiceOrderByIdOrNo(serviceOrderId, orderNo, siteId);
        //验证service数据是否正确
        this.validateServiceOrderForInit(serviceOrder, orderNo, pointId);
        //生成BO并查询明细部分
        SVM010201BO result = this.generateServiceMainInfo(serviceOrder);
        svm0102Service.timelySearchServiceDetailByOrderId(result, siteId);

        return result;
    }

    private SVM010201BO generateServiceMainInfo(ServiceOrderVO serviceOrder) {

        SVM010201BO result = new SVM010201BO();

        result.setServiceOrderId(serviceOrder.getServiceOrderId());
        result.setPointId(serviceOrder.getFacilityId());
        result.setPointCd(serviceOrder.getFacilityCd());
        result.setPointNm(serviceOrder.getFacilityNm());
        result.setOrderNo(serviceOrder.getOrderNo());
        result.setOrderDate(serviceOrder.getOrderDate());
        result.setOrderStatus(serviceOrder.getOrderStatusContent());
        result.setOrderStatusId(serviceOrder.getOrderStatusId());
        result.setRelatedSalesOrderId(serviceOrder.getRelatedSalesOrderId());
        result.setBoFlag(CommonConstants.CHAR_N);//默认N，后面会根据part的BO情况重新设值

        result.setCmmSerializedProductId(serviceOrder.getCmmSerializedProductId());
        result.setPlateNo(serviceOrder.getPlateNo());
        result.setFrameNo(serviceOrder.getFrameNo());
        result.setSoldDate(serviceOrder.getSoldDate());
        result.setModelId(serviceOrder.getModelId());
        result.setModelCd(serviceOrder.getModelCd());
        result.setModelNm(serviceOrder.getModelNm());
        result.setModel(serviceOrder.getModelCd() + CommonConstants.CHAR_SPACE + serviceOrder.getModelNm());
        result.setModelTypeId(serviceOrder.getModelTypeId());
        result.setConsumerId(serviceOrder.getConsumerId());
        result.setEvFlag(serviceOrder.getEvFlag());
        result.setLastName(serviceOrder.getLastNm());
        result.setMiddleName(serviceOrder.getMiddleNm());
        result.setFirstName(serviceOrder.getFirstNm());
        result.setMobilephone(serviceOrder.getMobilePhone());
        result.setEmail(serviceOrder.getEmail());
        result.setPolicyResultFlag(orderCmmMethod.getConsumerPolicyInfo(serviceOrder.getSiteId(), serviceOrder.getLastNm(), serviceOrder.getMiddleNm(), serviceOrder.getFirstNm(), serviceOrder.getMobilePhone()));

        result.setUseType(serviceOrder.getUseTypeId());
        result.setOwnerType(serviceOrder.getRelationTypeId());
        result.setCreateForEmployeeFlag(serviceOrder.getOrderForEmployeeFlag());
        result.setEmployeeCd(serviceOrder.getEmployeeCd());
        result.setRelationShipId(serviceOrder.getEmployeeRelationShipId());
        result.setTicketNo(serviceOrder.getTicketNo());

        result.setMileage(serviceOrder.getMileage());
        result.setServiceCategoryId(serviceOrder.getServiceCategoryId());
        result.setServiceDemand(serviceOrder.getServiceDemandContent());
        result.setServiceDemandId(serviceOrder.getServiceDemandId());
        result.setServiceTitle(serviceOrder.getServiceSubject());
        result.setSpecialClaimId(serviceOrder.getCmmSpecialClaimId());
        result.setBulletinNo(serviceOrder.getBulletinNo());

        result.setMechanicId(serviceOrder.getMechanicId());
        result.setMechanicCd(serviceOrder.getMechanicCd());
        result.setMechanicNm(serviceOrder.getMechanicNm());

        result.setWelcomeStaffId(serviceOrder.getWelcomeStaffId());
        result.setWelcomeStaffCd(serviceOrder.getWelcomeStaffCd());
        result.setWelcomeStaffNm(serviceOrder.getWelcomeStaffNm());

        if (!Objects.isNull(serviceOrder.getEntryPicId())) {
            result.setEditor(Objects.requireNonNullElse(serviceOrder.getEntryPicCd(), CommonConstants.CHAR_BLANK) + CommonConstants.CHAR_SPACE + Objects.requireNonNullElse(serviceOrder.getEntryPicNm(), CommonConstants.CHAR_BLANK));
        }

        if (!Objects.isNull(serviceOrder.getCashierId())) {
            result.setCashier(Objects.requireNonNullElse(serviceOrder.getCashierCd(), CommonConstants.CHAR_BLANK) + CommonConstants.CHAR_SPACE + Objects.requireNonNullElse(serviceOrder.getCashierNm(), CommonConstants.CHAR_BLANK));
        }

        result.setStartTime(Objects.isNull(serviceOrder.getStartTime()) ? null : ComUtil.date2timeFormat(serviceOrder.getStartTime(), CommonConstants.DB_DATE_FORMAT_YMDHM));
        result.setOperationStart(Objects.isNull(serviceOrder.getOperationStartTime()) ? null : ComUtil.date2timeFormat(serviceOrder.getOperationStartTime(), CommonConstants.DB_DATE_FORMAT_YMDHM));
        result.setOperationFinish(Objects.isNull(serviceOrder.getOperationFinishTime()) ? null : ComUtil.date2timeFormat(serviceOrder.getOperationFinishTime(), CommonConstants.DB_DATE_FORMAT_YMDHM));

        result.setMechanicComment(serviceOrder.getMechanicComment());

        result.setDepositAmt(serviceOrder.getDepositAmt());
        result.setPaymentMethodId(serviceOrder.getPaymentMethodId());

        result.setUpdateCounter(serviceOrder.getUpdateCount().toString());

        return result;
    }

    private void validateServiceOrderForInit(ServiceOrderVO serviceOrder, String orderNo, Long pointId) {

        //当orderNo在DB查询不到数据时,当order不属于当前point时,报错
        if (Objects.isNull(serviceOrder) || serviceOrder.getFacilityId().compareTo(pointId) != 0) {
            throw new BusinessCodedException(ComUtil.t("M.E.00303", new String[] {ComUtil.t("label.serviceOrderNumber"), orderNo, ComUtil.t("title.serviceOrder_01")}));
        }

        //当serviceOrder不符合类型时（不是YAMAHA brand）
        if (!StringUtils.equals(serviceOrder.getBrandId().toString(), BrandType.YAMAHA.getCodeDbid())) {

            throw new BusinessCodedException(ComUtil.t("M.E.10225", new String[] {ComUtil.t("label.serviceOrderNumber"), serviceOrder.getOrderNo()}));
        }

        //当serviceOrder 属于Claim for battery
        if (StringUtils.equals(serviceOrder.getServiceCategoryId(), ServiceCategory.CLAIMBATTERY.getCodeDbid())) {

            throw new BusinessCodedException(ComUtil.t("M.E.10502", new String[] {ComUtil.t("label.serviceOrderNumber"), serviceOrder.getOrderNo()}));
        }

        //当serviceOrder 属于0km service
        if (StringUtils.equals(serviceOrder.getZeroKmFlag(), CommonConstants.CHAR_Y)) {

            throw new BusinessCodedException(ComUtil.t("M.E.10501", new String[] {ComUtil.t("label.serviceOrderNumber"), serviceOrder.getOrderNo()}));
        }
    }

    private void validateForRegister(SVM010201Form model, SVM010201Parameter para) {

        this.validateBasicInfo(model, para, CommonConstants.OPERATION_STATUS_NEW);
    }

    private void validateForUpdate(SVM010201Form model, SVM010201Parameter para) {
        //验证ServiceOrder存在性和状态,获得serviceOrderVO
        this.validateServiceOrderExistence(model, para);
        this.validateBasicInfo(model, para, CommonConstants.OPERATION_STATUS_UPDATE);
    }

    private void validateForSettle(SVM010201Form model, SVM010201Parameter para) {
        //验证ServiceOrder存在性和状态,获得serviceOrderVO
        this.validateServiceOrderExistence(model, para);
        this.validateBasicInfo(model, para, CommonConstants.OPERATION_STATUS_FINISH);
        //验证车牌号不重复（仅settle）
        this.validatePlateNoDup(model);
        //job至少有一条数据
        this.validateAtLeastOneJob(model, para);
    }

    private void validateAtLeastOneJob(SVM010201Form model, SVM010201Parameter para) {

        //job至少有一条数据
        if (para.getJobListCheckList().isEmpty()) {throw new BusinessCodedException(ComUtil.t("M.E.10242"));}

        //当FreeCoupon或Claim时，job至少有一条相同serviceCategory的数据
        if ((StringUtils.equals(model.getOrderInfo().getServiceCategoryId(), ServiceCategory.FREECOUPON.getCodeDbid())
                || StringUtils.equals(model.getOrderInfo().getServiceCategoryId(), ServiceCategory.CLAIM.getCodeDbid()))
                && para.getJobListCheckList().stream().map(JobDetailBO::getServiceCategoryId)
                .noneMatch(serviceCategoryId -> StringUtils.equals(serviceCategoryId, model.getOrderInfo().getServiceCategoryId()))) {

            throw new BusinessCodedException(ComUtil.t("M.E.10241"));
        }
    }

    private void validateBasicInfo(SVM010201Form model, SVM010201Parameter para, String operationStatus) {
        //验证字段必入力
        this.validateIsRequired(model, para.getDoFlag(), operationStatus);
        //验证车辆是否已卖出,获得CmmSerializedProductVO
        this.validateMc(model, para);
        //验证各Gird(必入力+重复性)，获得fullList
        para.setSituationListCheckList(orderCmmMethod.validateSituationDetail(model.getOrderInfo().getServiceOrderId(), model.getSituationList(), model.getOrderInfo().getSoldDate()));
        para.setJobListCheckList(orderCmmMethod.validateJobDetail(model.getOrderInfo().getServiceOrderId(), model.getJobList()));
        para.setPartListCheckList(orderCmmMethod.validatePartDetail(model.getOrderInfo().getServiceOrderId(), model.getPartList(), model.getOrderInfo().getEvFlag()));
        orderCmmMethod.validateBatteryDetail(model.getOrderInfo().getEvFlag(), model.getBatteryList());
        //验证字段特殊规则
        this.validateSoldDate(model, para);//需要前置查询出CmmSerializedProductVO
        orderCmmMethod.validateEmail(model.getOrderInfo().getEmail());
        orderCmmMethod.validateMobilePhone(model.getOrderInfo().getMobilephone());
        orderCmmMethod.validateOperationTime(model.getOrderInfo().getStartTime(), model.getOrderInfo().getOperationStart(), model.getOrderInfo().getOperationFinish(), operationStatus);
        orderCmmMethod.validateAuthorizationNo(model.getSituationList(), model.getSiteId(), model.getOrderInfo().getPointId(), model.getOrderInfo().getFrameNo(), model.getOrderInfo().getServiceOrderId());
        //特殊组合验证
        this.validateFreeServiceDemand(model, operationStatus);
        this.validateSpecialClaimDemand(model);
        this.validateMcVaildForClaim(model, para);//需要前置获得各grid的fullList
        this.validatePartBattery(model, para);//需要前置获得各grid的fullList
        this.validateNewBatteryNo(model);
        this.validateSymptomCdValid(para);//需要前置获得各grid的fullList
    }

    private void validateSymptomCdValid(SVM010201Parameter para) {

        Map<Long, String> symptomIds = para.getSituationListCheckList().stream()
                                                                 .filter(situation -> StringUtils.equals(situation.getWarrantyClaimFlag(), CommonConstants.CHAR_Y))
                                                                 .collect(Collectors.toMap(SituationBO::getSymptomId, SituationBO::getSymptomCd));

        Optional<JobDetailBO> invalidSymptomInJob = para.getJobListCheckList().stream()
                                                                            .filter(job -> !Objects.isNull(job.getSymptomId()) && !symptomIds.containsKey(job.getSymptomId()))
                                                                            .findFirst();

        if (invalidSymptomInJob.isPresent()) {

            throw new BusinessCodedException(ComUtil.t("M.E.10409", new String[] {invalidSymptomInJob.get().getJobCd()}));
        }

        Optional<PartDetailBO> invalidSymptomInPart = para.getPartListCheckList().stream()
                                                                    .filter(part -> !Objects.isNull(part.getSymptomId()) && !symptomIds.containsKey(part.getSymptomId()))
                                                                    .findFirst();

        if (invalidSymptomInPart.isPresent()) {

            throw new BusinessCodedException(ComUtil.t("M.E.10409", new String[] {PartNoUtil.format(invalidSymptomInPart.get().getPartCd())}));
        }
    }

    private void validateNewBatteryNo(SVM010201Form model) {

        //验证newBatteryNo在DB不存在（未使用过）
        List<String> batteryNoList = model.getBatteryList().stream()
                                                           .filter(battery -> StringUtils.isNotBlank(battery.getNewBatteryNo()))
                                                           .map(BatteryBO::getNewBatteryNo).toList();

        if (!batteryNoList.isEmpty()) {

            List<CmmBatteryVO> existBattery = svm0102Service.findCmmBatteryByBatteryNos(batteryNoList);

            if (!existBattery.isEmpty()) {

                throw new BusinessCodedException(ComUtil.t("M.E.00309", new String[] {ComUtil.t("label.batteryId"), existBattery.get(0).getBatteryNo(), ComUtil.t("label.tableCmmBatteryInfo")}));
            }
        }
    }

    private void validatePlateNoDup(SVM010201Form model) {

        CmmSerializedProductVO serialPro = svm0102Service.findCmmSerializedProductByFrameOrPlate(CommonConstants.CHAR_BLANK, model.getOrderInfo().getPlateNo());

        if (!Objects.isNull(serialPro) && !StringUtils.equals(serialPro.getFrameNo(), model.getOrderInfo().getFrameNo())) {

            throw new BusinessCodedException(ComUtil.t("M.E.00309", new String[] {ComUtil.t("label.numberPlate"), model.getOrderInfo().getPlateNo(), ComUtil.t("common.label.vehicleInfo")}));
        }
    }

    private void validateMc(SVM010201Form model, SVM010201Parameter para) {

        CmmSerializedProductVO cmmSerializedPro = orderCmmMethod.findCmmSerializedProductById(model.getOrderInfo().getCmmSerializedProductId());

        this.validateMcStatus(cmmSerializedPro.getSalesStatus());
        para.setCmmSerializedProduct(cmmSerializedPro);
    }

    private void validateMcStatus(String salesStatus) {
        if (!StringUtils.equals(salesStatus, MstCodeConstants.McSalesStatus.SALESTOUSER)) {
            throw new BusinessCodedException(ComUtil.t("M.E.10255"));
        }
    }

    private void validatePartBattery(SVM010201Form model, SVM010201Parameter para) {

        //parts中，电池类部品的行数 和 battery
        if (para.getPartListCheckList().stream().filter(part -> StringUtils.equals(part.getBatteryFlag(), CommonConstants.CHAR_Y)).count()
                != model.getBatteryList().stream().filter(battery -> StringUtils.isNotBlank(battery.getNewBatteryNo())).count()) {

            throw new BusinessCodedException(ComUtil.t("M.E.00203", new String[] {"battery quantity in parts detail", "battery quantity in battery detail"}));
        }
    }

    private void validateMcVaildForClaim(SVM010201Form model, SVM010201Parameter para) {

        //head+job+part明细中，都没有Claim标签，跳过此验证
        if (!this.isClaimServiceOrder(model, para)) {return;}

        CmmWarrantySerializedProductVO cmmWarrantySerializedProduct = svm0102Service.findCmmWarrantySerializedProductBySerializedId(model.getOrderInfo().getCmmSerializedProductId());

        //不存在warranty信息时，不能做Claim数据
        if (Objects.isNull(cmmWarrantySerializedProduct)) {

            throw new BusinessCodedException(ComUtil.t("M.E.10403", new String[] {ComUtil.t("common.label.vehicleInfo")}));
        }

        //如果有填AuthNo,不需要验证Claim是否需要AuthNo
        if (this.isAuthNoExistInSituationGrid(para)) {return;}

        BusinessCodedException needAuthNoException;

        //1.判断cmmWarrantySerializedProduct中的失效日
        if (this.isValidForWarrantySeriPro(cmmWarrantySerializedProduct)) {//车辆warranty有效

            //继续判断标准逻辑
            needAuthNoException = this.validateWarrantyPolicy(model.getOrderInfo().getSoldDate(), model.getOrderInfo().getMileage(), cmmWarrantySerializedProduct);
            //OK则通过
            if (Objects.isNull(needAuthNoException)) {return;}
            //NG则继续判断warrantyModelPart逻辑
            List<CmmWarrantyModelPartVO> warrantyModelPartList = svm0102Service.findWarrantyModelPartByModelCd(model.getOrderInfo().getModelCd());

            if (!warrantyModelPartList.isEmpty()) {//warrantyModelPart有设定，验证warrantyModelPart

                needAuthNoException = this.validatePartInWarrantyModelPart(model, para, warrantyModelPartList, needAuthNoException);
                //OK则无视标准逻辑判定结果，依然可以通过，NG则报错
                if (!Objects.isNull(needAuthNoException)) {throw needAuthNoException;}
            }
            else {//warrantyModelPart没有设定，直接报标准逻辑返回的错误信息

                throw needAuthNoException;
            }
        }
        else {//车辆warranty失效

            //直接判断warrantyModelPart
            List<CmmWarrantyModelPartVO> warrantyModelPartList = svm0102Service.findWarrantyModelPartByModelCd(model.getOrderInfo().getModelCd());

            if (!warrantyModelPartList.isEmpty()) {//warrantyModelPart有设定，无视标准逻辑，直接验证warrantyModelPart

                needAuthNoException = this.validatePartInWarrantyModelPart(model, para, warrantyModelPartList, new BusinessCodedException(ComUtil.t("M.E.10408", new String[] {LocalDate.parse(cmmWarrantySerializedProduct.getToDate(), DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)).format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DATE_FORMAT))})));

                if (!Objects.isNull(needAuthNoException)) {throw needAuthNoException;}
            }
            else {//warrantyModelPart没有设定，验证标准逻辑，OK则无视车辆warranty结果，依然可以通过，NG则报错

                needAuthNoException = this.validateWarrantyPolicy(model.getOrderInfo().getSoldDate(), model.getOrderInfo().getMileage(), cmmWarrantySerializedProduct);

                if (!Objects.isNull(needAuthNoException)) {throw needAuthNoException;}
            }
        }
    }

    private boolean isValidForWarrantySeriPro(CmmWarrantySerializedProductVO cmmWarrantySerializedProduct) {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD))
                .compareTo(cmmWarrantySerializedProduct.getToDate()) <= 0;
    }

    private BusinessCodedException validatePartInWarrantyModelPart(SVM010201Form model, SVM010201Parameter para, List<CmmWarrantyModelPartVO> warrantyModelPartList, BusinessCodedException defaultException){

        BusinessCodedException result;

        //明细中没有Claim的parts,不通过
        if (para.getPartListCheckList().stream().noneMatch(partBO -> StringUtils.equals(partBO.getServiceCategoryId(), ServiceCategory.CLAIM.getCodeDbid()))) {return defaultException;}

        List<CmmWarrantyModelPartVO> warrantyModelEmptyPart = warrantyModelPartList.stream()
                                                                                   .filter(modelPart -> StringUtils.isBlank(modelPart.getPartCd()))
                                                                                   .toList();

        if (warrantyModelEmptyPart.isEmpty()) {//model下的warrantyPart均具体指定

            List<CmmWarrantyModelPartVO> warrantyPartUsedList = new ArrayList<>();

            for (PartDetailBO partDetail : para.getPartListCheckList()) {

                //仅判断Claim的情况
                if (!StringUtils.equals(partDetail.getServiceCategoryId(), ServiceCategory.CLAIM.getCodeDbid())) {continue;}
                //所有Claim Part要求在WarrantyPartMap中存在
                List<CmmWarrantyModelPartVO> warrantyPartExistList = warrantyModelPartList.stream().filter(modelPart -> partDetail.getPartCd().startsWith(modelPart.getPartCd())).toList();
                //part在modelPart中不存在,不通过
                if (warrantyPartExistList.isEmpty()) {return defaultException;}

                warrantyPartUsedList = Stream.concat(warrantyPartUsedList.stream(), warrantyPartExistList.stream()).toList();
            }

            //获取空parts数据中的最小值
            BigDecimal minMileage = this.getMinWarrantyValue(warrantyPartUsedList, "WARRANTY_MILEAGE");
            BigDecimal minTerm = this.getMinWarrantyValue(warrantyPartUsedList, "WARRANTY_DAY");

            //term和mileage不满足条件,不通过
            result = this.validateWarrantyTerm(model.getOrderInfo().getSoldDate(), minTerm);
            if (!Objects.isNull(result)) {return result;}

            result = this.validateWarrantyMileage(model.getOrderInfo().getMileage(), minMileage);
            if (!Objects.isNull(result)) {return result;}
        }
        else {//model可以使用所有warrantyPart

            //获取空parts数据中的最小值
            BigDecimal minMileage = this.getMinWarrantyValue(warrantyModelEmptyPart, "WARRANTY_MILEAGE");
            BigDecimal minTerm = this.getMinWarrantyValue(warrantyModelEmptyPart, "WARRANTY_DAY");

            //term和mileage不满足条件,不通过
            result = this.validateWarrantyTerm(model.getOrderInfo().getSoldDate(), minTerm);
            if (!Objects.isNull(result)) {return result;}

            result = this.validateWarrantyMileage(model.getOrderInfo().getMileage(), minMileage);
            if (!Objects.isNull(result)) {return result;}
        }

        return result;
    }

    private BigDecimal getMinWarrantyValue(List<CmmWarrantyModelPartVO> warrantyModelEmptyPart, String warrantyType) {
        return warrantyModelEmptyPart.stream()
                                     .filter(p -> StringUtils.equals(p.getWarrantyType(), warrantyType))
                                     .map(p -> p.getWarrantyValue())
                                     .min(BigDecimal::compareTo)
                                     .orElse(null);
    }

    private BusinessCodedException validateWarrantyPolicy(String soldDate, BigDecimal mileage, CmmWarrantySerializedProductVO cmmWarrantySerializedProduct) {

        BusinessCodedException result = null;

        //非New的场合，直接使用默认policy
        if (StringUtils.equals(cmmWarrantySerializedProduct.getWarrantyProductClassification(), WarrantyPolicyType.NEW.getCodeDbid())) {

            result = this.validateWarrantyPolicyForNew(soldDate, mileage, cmmWarrantySerializedProduct);
        }
        else {

            result = this.validateWarrantyPolicyForNotNew(soldDate, mileage, cmmWarrantySerializedProduct.getWarrantyProductClassification());
        }

        return result;
    }

    private BusinessCodedException validateWarrantyPolicyForNew(String soldDate, BigDecimal mileage, CmmWarrantySerializedProductVO cmmWarrantySerializedProduct) {

        BusinessCodedException result = null;

        Integer mileageInt = mileage.intValue();

        String[] fsc = this.setFscResultOneToNine(cmmWarrantySerializedProduct.getSerializedProductId(), soldDate);
        Integer warrantyConditionPeriod = this.getDifferenceYearsToCurrentTime(ComUtil.str2date(cmmWarrantySerializedProduct.getFromDate()), LocalDate.now());
        int fscLength = fsc.length;

        boolean theFirstHaveNotYetDone = fscLength == 0 || !StringUtils.equals(fsc[0], CommonConstants.CHAR_Y);
        boolean warrantyConditionPeriodEqualsThreeYear = (warrantyConditionPeriod == 3);
        boolean warrantyConditionPeriodEqualsTwoOrThreeYear = (warrantyConditionPeriod == 2) || (warrantyConditionPeriod == 3);
        boolean fscResultMissInOneOrTwoOrThree = fscLength < 3
                                              || !StringUtils.equals(fsc[0], CommonConstants.CHAR_Y)
                                              || !StringUtils.equals(fsc[1], CommonConstants.CHAR_Y)
                                              || !StringUtils.equals(fsc[2], CommonConstants.CHAR_Y);

        boolean fscResultMissInFourOrFiveOrSix = fscLength < 6
                                              || !StringUtils.equals(fsc[3], CommonConstants.CHAR_Y)
                                              || !StringUtils.equals(fsc[4], CommonConstants.CHAR_Y)
                                              || !StringUtils.equals(fsc[5], CommonConstants.CHAR_Y);

        //超过3年
        if (warrantyConditionPeriod > 3){
            result = new BusinessCodedException(ComUtil.t("M.E.10276", new String[] {CommonConstants.CHAR_THREE}));
        } else if(warrantyConditionPeriodEqualsTwoOrThreeYear && fscResultMissInOneOrTwoOrThree) {
            result = new BusinessCodedException(ComUtil.t("M.E.10276", new String[] {CommonConstants.CHAR_ONE}));
        } else if(warrantyConditionPeriodEqualsThreeYear && fscResultMissInFourOrFiveOrSix){
            result = new BusinessCodedException(ComUtil.t("M.E.10276", new String[] {CommonConstants.CHAR_TWO}));
        } else if(theFirstHaveNotYetDone && mileageInt > 10000){
            result = new BusinessCodedException(ComUtil.t("M.E.10275", new String[] {"10,000"}));
        } else if (mileageInt > 30000) {
            result = new BusinessCodedException(ComUtil.t("M.E.10275", new String[] {"30,000"}));
        } else if (30000 >= mileageInt && mileageInt > 20000 && fscResultMissInFourOrFiveOrSix) {
            result = new BusinessCodedException(ComUtil.t("M.E.10275", new String[] {"20,000"}));
        } else if(30000 >= mileageInt && mileageInt > 10000 && fscResultMissInOneOrTwoOrThree){
            result = new BusinessCodedException(ComUtil.t("M.E.10275", new String[] {"10,000"}));
        }

        return result;
    }

    private Integer getDifferenceYearsToCurrentTime(LocalDate fromDate, LocalDate toDate) {

        long years = ChronoUnit.YEARS.between(fromDate, toDate);
        //年份向上取整
        return (int) (years + (fromDate.plusYears(years).isBefore(toDate) ? 1 : 0));
    }

    private String[] setFscResultOneToNine(Long cmmSerializedProId, String stuDate){

        List<CmmServiceDemandVO> serviceDemandList = svm0102Service.findDemandMstByServiceCategory(ServiceCategory.FREECOUPON.getCodeDbid())
                                                                        .stream()
                                                                        .sorted(Comparator.comparingInt(CmmServiceDemandVO::getBaseDateAfter))
                                                                        .toList();

        Map<Long, String> serviceDemandHistoryMap = svm0102Service.findDemandHistoryBySerialProId(cmmSerializedProId)
                                                                  .stream()
                                                                  .collect(Collectors.toMap(CmmServiceDemandDetailVO::getServiceDemandId, CmmServiceDemandDetailVO::getFscResultFlag));

        CmmServiceDemandVO demand;
        String[] result = new String[serviceDemandList.size()];

        for(int i = 0, j = serviceDemandList.size(); i < j; i++) {

            demand = serviceDemandList.get(i);

            result[i] = StringUtils.equals(serviceDemandHistoryMap.get(demand.getServiceDemandId()), CommonConstants.CHAR_Y)
                            ? CommonConstants.CHAR_Y
                            : getDemandExpireFlag(demand, stuDate);
        }

        return result;
    }

    private String getDemandExpireFlag(CmmServiceDemandVO serviceDemand, String stuDate) {

        LocalDate demandEndDate = ComUtil.str2date(stuDate).plusMonths((long)serviceDemand.getDuePeriod() + serviceDemand.getBaseDateAfter());

        return LocalDate.now().compareTo(demandEndDate) < 0 ? CommonConstants.CHAR_BLANK : CommonConstants.CHAR_N;
    }

    private BusinessCodedException validateWarrantyPolicyForNotNew(String soldDate, BigDecimal mileage, String warrantyType) {

        BusinessCodedException result = null;

        BigDecimal warrantyMilage = this.getWarrantyMileage(warrantyType);
        BigDecimal warrantyTerm = this.getWarrantyTerm(warrantyType);

        result = this.validateWarrantyMileage(mileage, warrantyMilage);

        if (!Objects.isNull(result)) {return result;}

        return this.validateWarrantyTerm(soldDate, warrantyTerm);
    }

    private BusinessCodedException validateWarrantyTerm(String soldDate, BigDecimal warrantyTerm) {

        BusinessCodedException result = null;

        if (Objects.isNull(warrantyTerm)) {return result;}

        LocalDate warrantyTermDate = LocalDate.parse(soldDate, DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)).plusMonths(warrantyTerm.intValue());

        if (warrantyTermDate.compareTo(LocalDate.now()) < 0) {

            result = new BusinessCodedException(ComUtil.t("M.E.10408", new String[] {warrantyTermDate.format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DATE_FORMAT))}));
        }

        return result;
    }

    private BusinessCodedException validateWarrantyMileage(BigDecimal mileage, BigDecimal warrantyMilage) {

        BusinessCodedException result = null;

        if (Objects.isNull(warrantyMilage)) {return result;}

        if (warrantyMilage.compareTo(mileage) < 0) {

            result = new BusinessCodedException(ComUtil.t("M.E.10275", new String[] {warrantyMilage.toString()}));
        }

        return result;
    }

    private BigDecimal getWarrantyMileage(String warrantyType) {

        BigDecimal result = null;

        if (StringUtils.equals(warrantyType, WarrantyPolicyType.BIGBIKE.getCodeDbid())) {
            result = WarrantyClaimDefaultPara.BIGBIKE_WARRANTY_MILEAGE;
        }
        else if (StringUtils.equals(warrantyType, WarrantyPolicyType.OLD.getCodeDbid())) {
            result = WarrantyClaimDefaultPara.OLD_WARRANTY_MILEAGE;
        }
        else if (StringUtils.equals(warrantyType, WarrantyPolicyType.NEW.getCodeDbid())) {
            result = WarrantyClaimDefaultPara.NEW_WARRANTY_MILEAGE;
        }

        return result;
    }

    private BigDecimal getWarrantyTerm(String warrantyType) {

        BigDecimal result = null;

        if (StringUtils.equals(warrantyType, WarrantyPolicyType.BIGBIKE.getCodeDbid())) {
            result = WarrantyClaimDefaultPara.BIGBIKE_WARRANTY_TERM;
        }
        else if (StringUtils.equals(warrantyType, WarrantyPolicyType.OLD.getCodeDbid())) {
            result = WarrantyClaimDefaultPara.OLD_WARRANTY_TERM;
        }
        else if (StringUtils.equals(warrantyType, WarrantyPolicyType.EV.getCodeDbid())) {
            result = WarrantyClaimDefaultPara.EV_WARRANTY_TERM;
        }
        else if (StringUtils.equals(warrantyType, WarrantyPolicyType.NEW.getCodeDbid())) {
            result = WarrantyClaimDefaultPara.NEW_WARRANTY_TERM;
        }

        return result;
    }

    private boolean isClaimServiceOrder(SVM010201Form model, SVM010201Parameter para) {
        return StringUtils.equals(model.getOrderInfo().getServiceCategoryId(), ServiceCategory.CLAIM.getCodeDbid())
                ||  Stream.concat(para.getJobListCheckList().stream().map(JobDetailBO :: getServiceCategoryId)
                                , para.getPartListCheckList().stream().map(PartDetailBO :: getServiceCategoryId))
                         .anyMatch(serviceCategoryId -> StringUtils.equals(serviceCategoryId, ServiceCategory.CLAIM.getCodeDbid()));
    }

    private boolean isAuthNoExistInSituationGrid(SVM010201Parameter para) {

        return para.getSituationListCheckList().stream().anyMatch(situationBO -> StringUtils.isNotBlank(situationBO.getAuthorizationNo()));
    }

    private void validateSoldDate(SVM010201Form model, SVM010201Parameter para) {

        //车辆本身已有销售日，不需要再次验证
        if (StringUtils.isNotBlank(para.getCmmSerializedProduct().getStuDate())) {return;}

        String nowDate = LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD));

        //soldDate不得晚于今天
        if (model.getOrderInfo().getSoldDate().compareTo(nowDate) > 0) {
            throw new BusinessCodedException(ComUtil.t("M.E.00207", new String[] {ComUtil.t("label.soldDate"), ComUtil.t("label.isToday")}));
        }

        //soldDate手动输入时(车辆stuDate为空)，不得早于ManufacturingDate
        if (StringUtils.isNotBlank(para.getCmmSerializedProduct().getManufacturingDate())
                && model.getOrderInfo().getSoldDate().compareTo(para.getCmmSerializedProduct().getManufacturingDate()) < 0) {

            throw new BusinessCodedException(ComUtil.t("M.E.00208", new String[] {ComUtil.t("label.soldDate"), ComUtil.t("label.manufacturingDate")}));
        }
    }

    private void validateIsRequired(SVM010201Form model, String doFlag, String operationStatus) {

        validateLogic.validateIsRequired(model.getOrderInfo().getPlateNo(), ComUtil.t("label.numberPlate"));
        validateLogic.validateIsRequired(model.getOrderInfo().getFrameNo(), ComUtil.t("label.frameNumber"));
        validateLogic.validateIsRequired(model.getOrderInfo().getModel(), ComUtil.t("label.model"));
        validateLogic.validateIsRequired(model.getOrderInfo().getSoldDate(), ComUtil.t("label.soldDate"));
        validateLogic.validateIsRequired(model.getOrderInfo().getOwnerType(), ComUtil.t("label.relationType"));
        validateLogic.validateIsRequired(model.getOrderInfo().getLastName(), ComUtil.t("label.consumerName"));
        validateLogic.validateIsRequired(model.getOrderInfo().getFirstName(), ComUtil.t("label.consumerName"));
        validateLogic.validateIsRequired(model.getOrderInfo().getMobilephone(), ComUtil.t("label.mobilephone"));

        validateLogic.validateIsRequired(model.getOrderInfo().getServiceCategoryId(), ComUtil.t("label.serviceCategory"));
        validateLogic.validateIsRequired(model.getOrderInfo().getMileage(), ComUtil.t("label.mileage"));
        validateLogic.validateNumberLtZero(model.getOrderInfo().getMileage(), ComUtil.t("label.mileage"));

        //freeCoupon时，demand必入力
        if (StringUtils.equals(model.getOrderInfo().getServiceCategoryId(), ServiceCategory.FREECOUPON.getCodeDbid())) {

            validateLogic.validateIsRequired(model.getOrderInfo().getServiceDemandId(), ComUtil.t("label.serviceDemand"));
        }

        //specialClaim时，demand必入力
        if (StringUtils.equals(model.getOrderInfo().getServiceCategoryId(), ServiceCategory.SPECIALCLAIM.getCodeDbid())) {

            validateLogic.validateIsRequired(model.getOrderInfo().getSpecialClaimId(), ComUtil.t("label.serviceDemand"));
        }

        //DO店的额外字段
        if (StringUtils.equals(doFlag, CommonConstants.CHAR_Y)) {

            validateLogic.validateIsRequired(model.getOrderInfo().getEmail(), ComUtil.t("label.email"));
            validateLogic.validateIsRequired(model.getOrderInfo().getModelTypeId(), ComUtil.t("label.modelType"));
            validateLogic.validateIsRequired(model.getOrderInfo().getWelcomeStaffId(), ComUtil.t("label.welcomeStaff"));
        }
        //Settle时验证
        if (StringUtils.equals(operationStatus, CommonConstants.OPERATION_STATUS_FINISH)) {

            validateLogic.validateIsRequired(model.getOrderInfo().getUseType(), ComUtil.t("label.useType"));
            validateLogic.validateIsRequired(model.getOrderInfo().getOperationStart(), ComUtil.t("label.operationStartTime"));
            validateLogic.validateIsRequired(model.getOrderInfo().getOperationFinish(), ComUtil.t("label.operationFinishTime"));
            validateLogic.validateIsRequired(model.getOrderInfo().getMechanicId(), ComUtil.t("label.mechanic"));
            validateLogic.validateIsRequired(model.getOrderInfo().getPolicyResultFlag(), ComUtil.t("label.privacyPolicyResult"));

            if (StringUtils.equals(doFlag, CommonConstants.CHAR_Y)) {

                validateLogic.validateIsRequired(model.getOrderInfo().getPaymentMethodId(), ComUtil.t("label.paymentMethod"));
            }
        }
    }

    private void validateServiceOrderExistence(SVM010201Form model, SVM010201Parameter para) {

        ServiceOrderVO serviceOrder = orderCmmMethod.orderExistence(model.getOrderInfo().getServiceOrderId(), model.getOrderInfo().getOrderNo(), model.getOrderInfo().getUpdateCounter());

        para.setServiceOrder(serviceOrder);
    }

    private void validateFreeCouponCategorySame(SVM010201Form model) {
        //如果明细（job+part）选择了freeCoupon,但head中没有选择freeCoupon，则报错
        if (Stream.concat(model.getJobList().getNewUpdateRecords().stream().map(JobDetailBO :: getServiceCategoryId)
                        , model.getPartList().getNewUpdateRecords().stream().map(PartDetailBO :: getServiceCategoryId))
                  .anyMatch(serviceCategoryId -> StringUtils.equals(serviceCategoryId, ServiceCategory.FREECOUPON.getCodeDbid()))
            && !StringUtils.equals(model.getOrderInfo().getServiceCategoryId(), ServiceCategory.FREECOUPON.getCodeDbid())) {

            throw new BusinessCodedException(ComUtil.t("M.E.10239"));
        }
    }

    private void validateSpecialClaimDemand(SVM010201Form model) {
        //仅当specialClaim时需要验证
        if (!StringUtils.equals(model.getOrderInfo().getServiceCategoryId(), ServiceCategory.SPECIALCLAIM.getCodeDbid())) {return;}

        CmmSpecialClaimSerialProVO cmmSpecialClaimSerialPro = orderCmmMethod.findCmmSpecialClaimSerialProBySpcialClaimIdAndMotorId(model.getOrderInfo().getSpecialClaimId(), model.getOrderInfo().getCmmSerializedProductId());

        //如果master表中没有该车辆的数据，说明该车不属于所选SpecialClaim的对象
        if (Objects.isNull(cmmSpecialClaimSerialPro)) {

            throw new BusinessCodedException(ComUtil.t("M.E.10287"));
        }
        //如果claimFlg=Y,说明该车已做过所选SpecialClaim
        else if (StringUtils.equals(cmmSpecialClaimSerialPro.getClaimFlag(), CommonConstants.CHAR_Y)) {

            throw new BusinessCodedException(ComUtil.t("M.E.10288", new String[] {cmmSpecialClaimSerialPro.getDealerCd()}));
        }
    }

    private void validateFreeServiceDemand(SVM010201Form model, String operationStatus) {

        //1.如果有明细（job+part）选择了freeCoupon,head必须也选择freeCoupon
        this.validateFreeCouponCategorySame(model);

        //以下逻辑仅在Free Coupon下验证
        if (!StringUtils.equals(model.getOrderInfo().getServiceCategoryId(), ServiceCategory.FREECOUPON.getCodeDbid())) {return;}

        //2.验证detail demand(job + parts)，必须和head中保持一致
        this.validateFreeCouponDemandSame(model);

        CmmServiceDemandVO serviceDemand = svm0102Service.getCmmServiceDemandById(model.getOrderInfo().getServiceDemandId());

        Integer maxBaseDate = svm0102Service.getMaxBaseDateByMcId(model.getOrderInfo().getCmmSerializedProductId());

        //3.选择的demand起始日必须>履历中的最大值，即必须选择没有使用过的demand
        //防止同一车辆重复生成freeCoupon， 从serviceHistory中判断
        if (!Objects.isNull(maxBaseDate) && Integer.compare(serviceDemand.getBaseDateAfter(), maxBaseDate) <= 0) {
            throw new BusinessCodedException(ComUtil.t("M.E.10183", new String[] {model.getOrderInfo().getServiceDemand()}));
        }

        //以下逻辑仅在创建时验证（创建后，车辆+serviceCategory+demand均不可再修改）
        if (!StringUtils.equals(operationStatus, CommonConstants.OPERATION_STATUS_NEW)) {return;}

        //4.车辆属于大型车时，只能选couponLevelOne，前置已判定modelCd非空
        if (!StringUtils.equals(serviceDemand.getDescription(), ServiceDemand.COUPON_LEVEL_ONE)
                && svm0102Service.findBigBikeExistByModleCd(StringUtils.substring(model.getOrderInfo().getModelCd(), 0, 6))  ){

            throw new BusinessCodedException(ComUtil.t("M.E.00346", new String[] {model.getOrderInfo().getServiceDemand(), ServiceDemand.COUPON_LEVEL_ONE}));
        }

        //5.demand的范围符合要求（仅验证日期范围）， 前置已判定soldDate非空
        //effectiveDate = soldDate + 起始月
        //expiryDate = soldDate + 起始月 + 持续月
        LocalDate effectiveDate = LocalDate.parse(model.getOrderInfo().getSoldDate(), DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)).plusMonths(serviceDemand.getBaseDateAfter().longValue());
        LocalDate expiryDate = LocalDate.parse(model.getOrderInfo().getSoldDate(), DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)).plusMonths(serviceDemand.getBaseDateAfter().longValue() + serviceDemand.getDuePeriod().longValue());
        LocalDate nowDate = LocalDate.now();

        if (nowDate.compareTo(effectiveDate) < 0 || nowDate.compareTo(expiryDate) >= 0) {

            throw new BusinessCodedException(ComUtil.t("M.E.10188", new String[] {serviceDemand.getDescription()}));
        }
    }

    private void validateFreeCouponDemandSame(SVM010201Form model) {
        //明细中，当freeCoupon，当demand为空，或 demand与head所选不一致时，报错
        if (model.getJobList().getNewUpdateRecords().stream()
              .filter(job -> StringUtils.equals(job.getServiceCategoryId(), ServiceCategory.FREECOUPON.getCodeDbid()))
              .anyMatch(job -> Objects.isNull(job.getServiceDemandId()) || Long.compare(job.getServiceDemandId(), model.getOrderInfo().getServiceDemandId()) != 0 || !StringUtils.equals(job.getServiceDemandContent(), model.getOrderInfo().getServiceDemand()))){

            throw new BusinessCodedException(ComUtil.t("M.E.10340"));
        }
        if (model.getPartList().getNewUpdateRecords().stream()
                .filter(part -> StringUtils.equals(part.getServiceCategoryId(), ServiceCategory.FREECOUPON.getCodeDbid()))
                .anyMatch(part -> Objects.isNull(part.getServiceDemandId()) || Long.compare(part.getServiceDemandId(), model.getOrderInfo().getServiceDemandId()) != 0 || !StringUtils.equals(part.getServiceDemandContent(), model.getOrderInfo().getServiceDemand()))){

            throw new BusinessCodedException(ComUtil.t("M.E.10340"));
        }
    }

    private void getMotorAndConsumerDetail(SVM010201BO result, String siteId, CmmSerializedProductVO cmmSerializedProduct, String doFlag) {

        result.setPlateNo(cmmSerializedProduct.getPlateNo());
        result.setFrameNo(cmmSerializedProduct.getFrameNo());
        result.setSoldDate(validateLogic.isValidDate(cmmSerializedProduct.getStuDate(), CommonConstants.DB_DATE_FORMAT_YMD) ? cmmSerializedProduct.getStuDate() : CommonConstants.CHAR_BLANK);
        result.setModelId(cmmSerializedProduct.getProductId());

        if (!Objects.isNull(cmmSerializedProduct.getProductId())) {

            MstProductVO model = orderCmmMethod.getProductById(cmmSerializedProduct.getProductId());

            if (!Objects.isNull(model)) {

                result.setModelCd(model.getProductCd());
                result.setModelNm(model.getSalesDescription());
                result.setModel(result.getModelCd() + CommonConstants.CHAR_SPACE + result.getModelNm());
            }

            if (StringUtils.equals(doFlag, CommonConstants.CHAR_Y)) {

                result.setModelTypeId(svm0102Service.getModelCategoryIdByLvOneModelId(cmmSerializedProduct.getProductId()));
            }
        }

        result.setEvFlag(cmmSerializedProduct.getEvFlag());
        result.setCmmSerializedProductId(cmmSerializedProduct.getSerializedProductId());
        result.setUseType(this.getUseTypeByMotorId(cmmSerializedProduct.getSerializedProductId()));

        //获取owner信息
        CmmConsumerBO ownerConsumer = svm0102Service.getOwnerRelationBySerialProId(siteId, cmmSerializedProduct.getSerializedProductId());

        if (Objects.isNull(ownerConsumer)) {return;}

        result.setConsumerId(ownerConsumer.getConsumerId());
        result.setFirstName(ownerConsumer.getFirstNm());
        result.setMiddleName(ownerConsumer.getMiddleNm());
        result.setLastName(ownerConsumer.getLastNm());
        result.setMobilephone(ownerConsumer.getMobilePhone());
        result.setEmail(ownerConsumer.getEmail());
        result.setOwnerType(ConsumerSerialProRelationType.OWNER.getCodeDbid());
        result.setPolicyResultFlag(ownerConsumer.getPrivacyPolicyResult());
    }

    public DownloadResponseView printServiceJobCard(Long serviceOrderId, String siteId) {
        List<SVM0102PrintBO> dataList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

        //HeaderData
        SVM0102PrintBO printBO = svm0102Service.getServiceJobCardHeaderData(serviceOrderId);

        if (!Nulls.isNull(printBO)) {

            printBO.setDate(DateUtils.getCurrentDateString(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS));

            //ServiceDate格式化
            if (StringUtils.isNotBlank(printBO.getServiceDate())) {
                printBO.setServiceDate(LocalDate.parse(printBO.getServiceDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //SoldDate格式化
            if (StringUtils.isNotBlank(printBO.getSoldDate())) {
                printBO.setSoldDate(LocalDate.parse(printBO.getSoldDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //ServiceHistoryData
            List<SVM0102PrintServiceHistoryBO> serviceHistoryList = svm0102Service.getServiceHistoryPrintList(serviceOrderId, siteId);

            //OrderDate格式化
            if (serviceHistoryList.isEmpty()) {

                serviceHistoryList.add(new SVM0102PrintServiceHistoryBO());

            } else {

                serviceHistoryList.forEach(historyBO -> {
                    if (StringUtils.isNotBlank(historyBO.getOrderDate())) {
                        historyBO.setOrderDate(LocalDate.parse(historyBO.getOrderDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
                    }
                });

            }
            printBO.setServiceHistoryList(serviceHistoryList);

            //ServiceJobData
            List<SVM0102PrintServiceJobBO> serviceJobList = svm0102Service.getServiceJobPrintList(serviceOrderId);
            if (serviceJobList.isEmpty()) {
                SVM0102PrintServiceJobBO serviceJobBO = new SVM0102PrintServiceJobBO();
                serviceJobBO.setStdManhour(CommonConstants.BIGDECIMAL_ZERO);
                serviceJobList.add(serviceJobBO);
            }
            printBO.setServiceJobList(serviceJobList);

            //ServicePartData
            List<SVM0102PrintServicePartBO> servicePartList = svm0102Service.getServicePartPrintList(serviceOrderId);

            //PartCd格式化
            if (servicePartList.isEmpty()) {

                SVM0102PrintServicePartBO servicePartBO = new SVM0102PrintServicePartBO();
                servicePartBO.setQty(CommonConstants.BIGDECIMAL_ZERO);
                servicePartList.add(servicePartBO);

            } else {

                servicePartList.forEach(partBO -> {
                    if (StringUtils.isNotBlank(partBO.getPartCd())) {
                        partBO.setPartCd(PartNoUtil.format(partBO.getPartCd()));
                    }
                });
            }
            printBO.setServicePartList(servicePartList);

            dataList.add(printBO);
        }

        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_SERVICEJOBCARD_REPORT,
                                                   dataList,
                                                   CommonConstants.CHAR_BLANK);

        return new DownloadResponseView(rp);
    }

    public DownloadResponseView printServiceJobCardForDO(Long serviceOrderId, String siteId) {

        List<SVM0102PrintBO> dataList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

        //HeaderData
        SVM0102PrintBO printBO = svm0102Service.getServiceJobCardForDoHeaderData(serviceOrderId);

        if (!Nulls.isNull(printBO)) {
            printBO.setLogo(FileConstants.LOGOPATH_YAMAHA_BLUE);
            printBO.setDate(DateUtils.getCurrentDateString(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS));

            //ServiceDate格式化
            if (StringUtils.isNotBlank(printBO.getServiceDate())) {
                printBO.setServiceDate(LocalDate.parse(printBO.getServiceDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //SoldDate格式化
            if (StringUtils.isNotBlank(printBO.getSoldDate())) {
                printBO.setSoldDate(LocalDate.parse(printBO.getSoldDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //ServiceHistoryData
            List<SVM0102PrintServiceHistoryBO> serviceHistoryList = svm0102Service.getServiceHistoryPrintList(serviceOrderId, siteId);

            //OrderDate格式化
            if (serviceHistoryList.isEmpty()) {

                serviceHistoryList.add(new SVM0102PrintServiceHistoryBO());

            } else {

                serviceHistoryList.forEach(historyBO -> {
                    if (StringUtils.isNotBlank(historyBO.getOrderDate())) {
                        historyBO.setOrderDate(LocalDate.parse(historyBO.getOrderDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
                    }
                });
            }
            printBO.setServiceHistoryList(serviceHistoryList);

            //ServiceJobData
            List<SVM0102PrintServiceJobBO> serviceJobList = svm0102Service.getServiceJobPrintList(serviceOrderId);

            //StdManhour * 60
            serviceJobList.forEach(jobBO -> {
                if (!Nulls.isNull(jobBO.getStdManhour())) {
                    jobBO.setStdManhour(NumberUtil.multiply(jobBO.getStdManhour(), new BigDecimal("60")).setScale(0, java.math.RoundingMode.HALF_UP));
                }
            });

            if (serviceJobList.isEmpty()) {
                SVM0102PrintServiceJobBO serviceJobBO = new SVM0102PrintServiceJobBO();
                serviceJobBO.setStdManhour(CommonConstants.BIGDECIMAL_ZERO);
                serviceJobList.add(serviceJobBO);
            }
            printBO.setServiceJobList(serviceJobList);

            //ServicePartData
            List<SVM0102PrintServicePartBO> servicePartList = svm0102Service.getServicePartPrintList(serviceOrderId);

            //PartCd格式化
            if (servicePartList.isEmpty()) {

                SVM0102PrintServicePartBO servicePartBO = new SVM0102PrintServicePartBO();
                servicePartBO.setQty(CommonConstants.BIGDECIMAL_ZERO);
                servicePartList.add(servicePartBO);

            } else {

                servicePartList.forEach(partBO -> {
                    if (StringUtils.isNotBlank(partBO.getPartCd())) {
                        partBO.setPartCd(PartNoUtil.format(partBO.getPartCd()));
                    }
                });

            }
            printBO.setServicePartList(servicePartList);

            dataList.add(printBO);
        }

        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_SERVICEJOBCARDFORDO_REPORT,
                                                   dataList,
                                                   CommonConstants.CHAR_BLANK);

        return new DownloadResponseView(rp);
    }

    public DownloadResponseView printServicePayment(Long serviceOrderId, String userName) {

        List<SVM0102PrintBO> dataList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

        //HeaderData
        SVM0102PrintBO printBO = svm0102Service.getServicePaymentHederData(serviceOrderId);

        if (!Nulls.isNull(printBO)) {

            printBO.setLogo(FileConstants.LOGOPATH_YAMAHA_BLUE);
            printBO.setDate(DateUtils.getCurrentDateString(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS));
            printBO.setCustomerComment(StringUtils.isNotBlank(printBO.getCustomerComment()) ? printBO.getCustomerComment() : CommonConstants.CHAR_BLANK);

            //ServiceDate格式化
            if (StringUtils.isNotBlank(printBO.getServiceDate())) {
                printBO.setServiceDate(LocalDate.parse(printBO.getServiceDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //SoldDate格式化
            if (StringUtils.isNotBlank(printBO.getSoldDate())) {
                printBO.setSoldDate(LocalDate.parse(printBO.getSoldDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //userName
            if (StringUtils.isNotBlank(userName)) {
                JSONObject json = JSON.parseObject(userName);
                String userNm = json.getString("username");
                printBO.setReceptionPic(StringUtils.isNotBlank(userNm) ? userNm : CommonConstants.CHAR_BLANK);
            }

            //ServiceJobDataEdit
            List<SVM0102PrintServiceJobBO> serviceJobList = svm0102Service.getServicePaymentJobPrintList(serviceOrderId);

            if (serviceJobList.isEmpty()) {

                SVM0102PrintServiceJobBO serviceJobBO = new SVM0102PrintServiceJobBO();
                serviceJobBO.setAmount(CommonConstants.BIGDECIMAL_ZERO);
                printBO.setJobAmount(CommonConstants.BIGDECIMAL_ZERO);
                serviceJobList.add(serviceJobBO);

            } else {

                //ServiceCategoryId -> CodeData1
                Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(ServiceCategory.CODE_ID);

                for (SVM0102PrintServiceJobBO jobBo : serviceJobList) {

                    jobBo.setServiceCategory(codeMap.get(jobBo.getServiceCategoryId()));

                }

                BigDecimal jobAmount = serviceJobList.stream()
                        .filter(job -> StringUtils.equals(job.getSettleTypeId(), SettleType.CUSTOMER.getCodeDbid()))
                        .map(SVM0102PrintServiceJobBO::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                printBO.setJobAmount(jobAmount.setScale(0, java.math.RoundingMode.HALF_UP));

            }
            printBO.setServiceJobList(serviceJobList);

            //ServicePartDataEdit
            List<SVM0102PrintServicePartBO> servicePartList = svm0102Service.getServicePaymentPartPrintList(serviceOrderId);

            if (servicePartList.isEmpty()) {

                SVM0102PrintServicePartBO servicePartBO = new SVM0102PrintServicePartBO();
                servicePartBO.setSellingPrice(CommonConstants.BIGDECIMAL_ZERO);
                servicePartBO.setQty(CommonConstants.BIGDECIMAL_ZERO);
                servicePartBO.setTotalPrice(CommonConstants.BIGDECIMAL_ZERO);
                printBO.setPartAmount(CommonConstants.BIGDECIMAL_ZERO);
                servicePartList.add(servicePartBO);

            } else {

                //PartCd格式化
                servicePartList.forEach(partBO -> {
                    if (StringUtils.isNotBlank(partBO.getPartCd())) {
                        partBO.setPartCd(PartNoUtil.format(partBO.getPartCd()));
                    }
                    partBO.setTotalPrice(NumberUtil.multiply(partBO.getSellingPrice(), partBO.getQty()).setScale(0, java.math.RoundingMode.HALF_UP));
                });

                BigDecimal partAmount = servicePartList.stream()
                        .filter(part -> StringUtils.equals(part.getSettleTypeId(), SettleType.CUSTOMER.getCodeDbid()))
                        .map(SVM0102PrintServicePartBO::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                printBO.setPartAmount(partAmount.setScale(0, java.math.RoundingMode.HALF_UP));
            }
            printBO.setServicePartList(servicePartList);

            dataList.add(printBO);
        }

        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_SERVICEPAYMENT_REPORT,
                                                   dataList,
                                                   CommonConstants.CHAR_BLANK);

        return new DownloadResponseView(rp);
    }

    public DownloadResponseView printServicePaymentForDO(Long serviceOrderId) {

        List<SVM0102PrintBO> dataList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

        SVM0102PrintBO printBO = svm0102Service.getServicePaymentForDoHederData(serviceOrderId);

        if (!Nulls.isNull(printBO)) {

            printBO.setLogo(FileConstants.LOGOPATH_YAMAHA_BLUE);
            printBO.setDate(DateUtils.getCurrentDateString(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS));
            printBO.setCustomerComment(StringUtils.isNotBlank(printBO.getCustomerComment()) ? printBO.getCustomerComment() : CommonConstants.CHAR_BLANK);

            //ServiceDate格式化
            if (StringUtils.isNotBlank(printBO.getServiceDate())) {
                printBO.setServiceDate(LocalDate.parse(printBO.getServiceDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //SoldDate格式化
            if (StringUtils.isNotBlank(printBO.getSoldDate())) {
                printBO.setSoldDate(LocalDate.parse(printBO.getSoldDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //RelationShipId -> codeData1
            Map<String, String> relationShipCodeMap = helperFacade.getMstCodeInfoMap(Relationship.CODE_ID);
            printBO.setRelationShip(relationShipCodeMap.get(printBO.getRelationShipId()));

            //ServiceJobDataEdit
            List<SVM0102PrintServiceJobBO> serviceJobList = svm0102Service.getServicePaymentJobForDoPrintList(serviceOrderId);

            if (serviceJobList.isEmpty()) {

                SVM0102PrintServiceJobBO serviceJobBO = new SVM0102PrintServiceJobBO();
                serviceJobBO.setAmount(CommonConstants.BIGDECIMAL_ZERO);
                printBO.setJobAmount(CommonConstants.BIGDECIMAL_ZERO);
                serviceJobList.add(serviceJobBO);

            } else {

                //StdManhour * 60
                serviceJobList.forEach(jobBO -> {
                    if (!Nulls.isNull(jobBO.getStdManhour())) {
                        jobBO.setStdManhour(NumberUtil.multiply(jobBO.getStdManhour(), new BigDecimal("60")));
                    }
                });

                //ServiceCategoryId -> CodeData1
                Map<String, String> serviceCategoryCodeMap = helperFacade.getMstCodeInfoMap(ServiceCategory.CODE_ID);
                for (SVM0102PrintServiceJobBO jobBo : serviceJobList) {

                    jobBo.setServiceCategory(serviceCategoryCodeMap.get(jobBo.getServiceCategoryId()));

                }

                //计算settleTypeId=Pj.SettleType.CUSTOMER的Amount合计
                BigDecimal jobAmount = serviceJobList.stream()
                        .filter(job -> StringUtils.equals(job.getSettleTypeId(), SettleType.CUSTOMER.getCodeDbid()))
                        .map(SVM0102PrintServiceJobBO::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                printBO.setJobAmount(jobAmount.setScale(0, java.math.RoundingMode.HALF_UP));
            }
            printBO.setServiceJobList(serviceJobList);

            //ServicePartDataEdit
            List<SVM0102PrintServicePartBO> servicePartList = svm0102Service.getServicePaymentPartForDoPrintList(serviceOrderId);

            if (servicePartList.isEmpty()) {

                SVM0102PrintServicePartBO servicePartBO = new SVM0102PrintServicePartBO();
                servicePartBO.setAmount(CommonConstants.BIGDECIMAL_ZERO);
                printBO.setPartAmount(CommonConstants.BIGDECIMAL_ZERO);
                servicePartList.add(servicePartBO);

            } else {

                //PartCd格式化
                servicePartList.forEach(partBO -> {
                    if (StringUtils.isNotBlank(partBO.getPartCd())) {
                        partBO.setPartCd(PartNoUtil.format(partBO.getPartCd()));
                    }
                });

                //计算settleTypeId=Pj.SettleType.CUSTOMER的Amount合计
                BigDecimal partAmount = servicePartList.stream()
                        .filter(part -> StringUtils.equals(part.getSettleTypeId(), SettleType.CUSTOMER.getCodeDbid()))
                        .map(SVM0102PrintServicePartBO::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                printBO.setPartAmount(partAmount.setScale(0, java.math.RoundingMode.HALF_UP));
            }

            printBO.setServicePartList(servicePartList);

            dataList.add(printBO);
        }

        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_SERVICEPAYMENTFORDO_REPORT,
                                                   dataList,
                                                   CommonConstants.CHAR_BLANK);

        return new DownloadResponseView(rp);
    }

    public String privacyPolicyResultsFileUpload(SVM010201Form form, MultipartFile[] files) {

        if (files != null && files.length > 0 && !StringUtils.isEmpty(form.getOrderInfo().getMobilephone())){
            return svm0102Service.privacyPolicyResultsFileUpload(form, files);
        } else {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10401"));
        }
    }
}
