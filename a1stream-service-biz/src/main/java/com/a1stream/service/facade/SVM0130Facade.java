package com.a1stream.service.facade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.MstCodeConstants.SalesOrderStatus;
import com.a1stream.common.constants.PJConstants.BatteryOriginalSource;
import com.a1stream.common.constants.PJConstants.BrandType;
import com.a1stream.common.constants.PJConstants.DropShipType;
import com.a1stream.common.constants.PJConstants.OrgRelationType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.SalesOrderPriorityType;
import com.a1stream.common.constants.PJConstants.SdStockStatus;
import com.a1stream.common.constants.PJConstants.SerialProQualityStatus;
import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.common.constants.PJConstants.ServiceOrderStatus;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.common.logic.ConstantsLogic;
import com.a1stream.common.logic.InventoryLogic;
import com.a1stream.common.logic.ValidateLogic;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.service.BatteryBO;
import com.a1stream.domain.bo.service.JobDetailBO;
import com.a1stream.domain.bo.service.PartDetailBO;
import com.a1stream.domain.bo.service.SVM013001BO;
import com.a1stream.domain.bo.service.SVM0130PrintBO;
import com.a1stream.domain.bo.service.SVM0130PrintServiceJobBO;
import com.a1stream.domain.bo.service.SVM0130PrintServicePartBO;
import com.a1stream.domain.bo.service.SituationBO;
import com.a1stream.domain.bo.service.SpecialClaimBO;
import com.a1stream.domain.form.service.SVM013001Form;
import com.a1stream.domain.parameter.service.SVM013001Parameter;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmSpecialClaimSerialProVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.domain.vo.ServiceOrderBatteryVO;
import com.a1stream.domain.vo.ServiceOrderFaultVO;
import com.a1stream.domain.vo.ServiceOrderJobVO;
import com.a1stream.domain.vo.ServiceOrderVO;
import com.a1stream.service.service.SVM0130Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.report.exporter.PdfReportExporter;
import com.ymsl.solid.web.download.DownloadResponseView;

import jakarta.annotation.Resource;

@Component
public class SVM0130Facade {

    @Resource
    private SVM0130Service svm0130Service;

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
    private ValidateLogic validateLogic;
    @Resource
    private InventoryLogic inventoryLogic;
    @Resource
    private ConstantsLogic constantsLogic;

    public SVM013001BO getServiceDetailByIdOrNo(Long serviceOrderId, String orderNo, Long pointId, String frameNo, PJUserDetails uc) {
        //orderId和orderNo均无值时，仅初始化; 有值时，获取service明细
        SVM013001BO result = Objects.isNull(serviceOrderId) && StringUtils.isBlank(orderNo) ? this.initForNewCreate(uc) : this.initForUpdate(serviceOrderId, orderNo, pointId, uc.getDealerCode());
        //基础数据设置
        this.initForBasicInfo(result, uc);

        //当frameNo有值时，代表从MC master画面跳转过来，需要带出MC信息
        if (Objects.isNull(serviceOrderId) && StringUtils.isNotBlank(frameNo)) {
            result.setFrameNo(frameNo);
            result = this.getMotorDetailByPlateOrFrame(result);
        }

        return result;
    }

    public SVM013001BO getMotorDetailByPlateOrFrame(SVM013001BO model){

        String frameNo = model.getFrameNo();

        CmmSerializedProductVO cmmSerializedProduct = orderCmmMethod.findCmmSerializedProductByFrameOrPlate(frameNo, CommonConstants.CHAR_BLANK);

        //车辆不存在时，报错
        if (Objects.isNull(cmmSerializedProduct)) {throw new BusinessCodedException(ComUtil.t("M.E.00303", new String[] {ComUtil.t("label.frameNumber"), frameNo , ComUtil.t("common.label.vehicleInfo")}));}
        //车辆需要在库,并且质量状态是normal
        this.validateMcStatus(cmmSerializedProduct, CommonConstants.OPERATION_STATUS_NEW);

        //构建result motorInfo + batteryList
        SVM013001BO result = BeanMapUtils.mapTo(model, SVM013001BO.class);

        //获取车辆信息
        this.getMotorAndConsumerDetail(result, cmmSerializedProduct);

        //获取电池明细
        if (StringUtils.equals(result.getEvFlag(), CommonConstants.CHAR_Y)) {

            result.setBatteryList(orderCmmMethod.findServiceBatteryByMotorId(cmmSerializedProduct.getSerializedProductId()));
        }

        return result;
    }

    public void newOrModifyServiceOrder(SVM013001Form model, PJUserDetails uc) {

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

    public DownloadResponseView print0KmJobCard(Long serviceOrderId) {

        List<SVM0130PrintBO> dataList = new ArrayList<>();

        //HeaderData
        SVM0130PrintBO printBO = svm0130Service.get0KmJobCardHeaderData(serviceOrderId);

        if (!Nulls.isNull(printBO)) {

            printBO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS)));

            //ServiceDate格式化
            if (StringUtils.isNotBlank(printBO.getServiceDate())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);
                printBO.setServiceDate(LocalDate.parse(printBO.getServiceDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //ServiceJobDataEdit
            List<SVM0130PrintServiceJobBO> serviceJobList = svm0130Service.get0KmJobCardJobList(serviceOrderId);

            if (serviceJobList.isEmpty()) {

                SVM0130PrintServiceJobBO serviceJobBO = new SVM0130PrintServiceJobBO();
                serviceJobBO.setStdmenhour(BigDecimal.ZERO);
                serviceJobList.add(serviceJobBO);

            }
            printBO.setServiceJobList(serviceJobList);

            //ServicePartDataEdit
            List<SVM0130PrintServicePartBO> servicePartList = svm0130Service.get0KmJobCardPartList(serviceOrderId);

            if (servicePartList.isEmpty()) {

                SVM0130PrintServicePartBO servicePartBO = new SVM0130PrintServicePartBO();
                servicePartBO.setQty(BigDecimal.ZERO);
                servicePartList.add(servicePartBO);

            } else {

                //PartCd格式化
                servicePartList.stream().forEach(item -> {
                    if (StringUtils.isNotBlank(item.getPartCd())) {
                        item.setPartCd(PartNoUtil.format(item.getPartCd()));
                    }
                });

            }
            printBO.setServicePartList(servicePartList);
        }

        dataList.add(printBO);

        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_STREAM0KMJOBCARD_REPORT,
                                                   dataList,
                                                   CommonConstants.CHAR_BLANK);

        return new DownloadResponseView(rp);
    }

    public DownloadResponseView print0KmServicePayment(Long serviceOrderId) {

        List<SVM0130PrintBO> dataList = new ArrayList<>();

        //HeaderData
        SVM0130PrintBO printBO = svm0130Service.get0KmServicePaymentHeaderData(serviceOrderId);

        if (!Nulls.isNull(printBO)) {

            printBO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS)));
            printBO.setLogo(FileConstants.LOGOPATH_YAMAHA_BLUE);
            printBO.setCustomerComment(StringUtils.isNotBlank(printBO.getCustomerComment()) ? printBO.getCustomerComment() : CommonConstants.CHAR_BLANK);

            //ServiceDate格式化
            if (StringUtils.isNotBlank(printBO.getServiceDate())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);
                printBO.setServiceDate(LocalDate.parse(printBO.getServiceDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //ServiceJobDataEdit
            List<SVM0130PrintServiceJobBO> serviceJobList = svm0130Service.get0KmServicePaymentJobList(serviceOrderId);

            if (serviceJobList.isEmpty()) {

                SVM0130PrintServiceJobBO serviceJobBO = new SVM0130PrintServiceJobBO();
                serviceJobBO.setAmount(BigDecimal.ZERO);
                serviceJobList.add(serviceJobBO);

            } else {

                Map<String, String> serviceCategoryCodeMap = helperFacade.getMstCodeInfoMap(ServiceCategory.CODE_ID);
                BigDecimal jobAmount = BigDecimal.ZERO;

                for (SVM0130PrintServiceJobBO jobBo : serviceJobList) {

                    //ServiceCategoryId -> CodeData1
                    jobBo.setServiceCategory(serviceCategoryCodeMap.get(jobBo.getServiceCategoryId()));
                    //计算JobAmount总和
                    jobAmount = jobAmount.add(jobBo.getAmount().setScale(0, RoundingMode.HALF_UP));

                }
                printBO.setJobAmount(jobAmount);
            }
            printBO.setServiceJobList(serviceJobList);

            //ServicePartDataEdit
            List<SVM0130PrintServicePartBO> servicePartList = svm0130Service.get0KmServicePaymentPartList(serviceOrderId);

            if (servicePartList.isEmpty()) {

                SVM0130PrintServicePartBO servicePartBO = new SVM0130PrintServicePartBO();
                servicePartBO.setSellingPrice(BigDecimal.ZERO);
                servicePartBO.setQty(BigDecimal.ZERO);
                servicePartList.add(servicePartBO);

            } else {

                BigDecimal partAmount = BigDecimal.ZERO;

                for (SVM0130PrintServicePartBO partBO : servicePartList) {

                    //PartCd格式化
                    partBO.setPartCd(PartNoUtil.format(partBO.getPartCd()));

                    //计算PartAmount总和
                    partAmount = partAmount.add(partBO.getPartAmount().setScale(0, RoundingMode.HALF_UP));
                }

                printBO.setPartAmount(partAmount);

            }
            printBO.setServicePartList(servicePartList);
        }

        dataList.add(printBO);
        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_SERVICE0KMPAYMENT_REPORT,
                                                   dataList,
                                                   CommonConstants.CHAR_BLANK);

        return new DownloadResponseView(rp);
    }

    private SVM013001BO initForNewCreate(PJUserDetails uc) {

        SVM013001BO result = new SVM013001BO();

        result.setPointCd(uc.getDefaultPointCd());
        result.setPointId(uc.getDefaultPointId());
        result.setPointNm(uc.getDefaultPointNm());
        result.setOrderDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
        result.setOrderStatus(ServiceOrderStatus.NEW.getCodeData1());
        result.setOrderStatusId(ServiceOrderStatus.NEW.getCodeDbid());

        result.setStartTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMDHM)));

        return result;
    }

    private SVM013001BO initForUpdate(Long serviceOrderId, String orderNo, Long pointId, String siteId) {

        //根据条件查询serviceOrder
        ServiceOrderVO serviceOrder = orderCmmMethod.timelySearchServiceOrderByIdOrNo(serviceOrderId, orderNo, siteId);
        //验证service数据是否正确
        this.validateServiceOrderForInit(serviceOrder, orderNo, pointId);
        //生成BO并查询明细部分
        SVM013001BO result = this.generateServiceMainInfo(serviceOrder);
        svm0130Service.timelySearchServiceDetailByOrderId(result, siteId);

        return result;
    }

    private void validateServiceOrderForInit(ServiceOrderVO serviceOrder, String orderNo, Long pointId) {

        //当orderNo在DB查询不到数据时,当order不属于当前point时,报错
        if (Objects.isNull(serviceOrder) || serviceOrder.getFacilityId().compareTo(pointId) != 0) {
            throw new BusinessCodedException(ComUtil.t("M.E.00303", new String[] {ComUtil.t("label.serviceOrderNumber"), orderNo, ComUtil.t("title.serviceOrder_01")}));
        }

        //当serviceOrder 属于非雅马哈
        if (!StringUtils.equals(serviceOrder.getBrandId().toString(), BrandType.YAMAHA.getCodeDbid())) {

            throw new BusinessCodedException(ComUtil.t("M.E.10225", new String[] {ComUtil.t("label.serviceOrderNumber"), serviceOrder.getOrderNo()}));
        }

        //当serviceOrder 属于Claim for battery
        if (StringUtils.equals(serviceOrder.getServiceCategoryId(), ServiceCategory.CLAIMBATTERY.getCodeDbid())) {

            throw new BusinessCodedException(ComUtil.t("M.E.10502", new String[] {ComUtil.t("label.serviceOrderNumber"), serviceOrder.getOrderNo()}));
        }

        //当serviceOrder 属于普通服务单
        if (!StringUtils.equals(serviceOrder.getZeroKmFlag(), CommonConstants.CHAR_Y)) {

            throw new BusinessCodedException(ComUtil.t("M.E.10226", new String[] {ComUtil.t("label.serviceOrderNumber"), serviceOrder.getOrderNo()}));
        }
    }


    private SVM013001BO generateServiceMainInfo(ServiceOrderVO serviceOrder) {

        SVM013001BO result = new SVM013001BO();

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
        result.setFrameNo(serviceOrder.getFrameNo());
        result.setModelId(serviceOrder.getModelId());
        result.setModelCd(serviceOrder.getModelCd());
        result.setModelNm(serviceOrder.getModelNm());
        result.setModel(serviceOrder.getModelCd() + CommonConstants.CHAR_SPACE + serviceOrder.getModelNm());
        result.setEvFlag(serviceOrder.getEvFlag());

        result.setServiceCategoryId(serviceOrder.getServiceCategoryId());
        result.setServiceDemand(serviceOrder.getServiceDemandContent());
        result.setServiceTitle(serviceOrder.getServiceSubject());
        result.setSpecialClaimId(serviceOrder.getCmmSpecialClaimId());

        result.setMechanicId(serviceOrder.getMechanicId());
        result.setMechanicCd(serviceOrder.getMechanicCd());
        result.setMechanicNm(serviceOrder.getMechanicNm());

        if (!Objects.isNull(serviceOrder.getCashierId())) {
            result.setCashier(Objects.requireNonNullElse(serviceOrder.getCashierCd(), CommonConstants.CHAR_BLANK) + CommonConstants.CHAR_SPACE + Objects.requireNonNullElse(serviceOrder.getCashierNm(), CommonConstants.CHAR_BLANK));
        }

        result.setStartTime(Objects.isNull(serviceOrder.getStartTime()) ? null : ComUtil.date2timeFormat(serviceOrder.getStartTime(), CommonConstants.DB_DATE_FORMAT_YMDHM));
        result.setOperationStart(Objects.isNull(serviceOrder.getOperationStartTime()) ? null : ComUtil.date2timeFormat(serviceOrder.getOperationStartTime(), CommonConstants.DB_DATE_FORMAT_YMDHM));
        result.setOperationFinish(Objects.isNull(serviceOrder.getOperationFinishTime()) ? null : ComUtil.date2timeFormat(serviceOrder.getOperationFinishTime(), CommonConstants.DB_DATE_FORMAT_YMDHM));

        result.setMechanicComment(serviceOrder.getMechanicComment());

        result.setPaymentAmt(serviceOrder.getPaymentAmt());

        result.setUpdateCounter(serviceOrder.getUpdateCount().toString());

        return result;
    }

    private SVM013001BO initForBasicInfo(SVM013001BO result, PJUserDetails uc) {

        result.setDoFlag(uc.getDoFlag());

        result.setSummaryList(orderCmmMethod.generateSummaryList(result.getJobList(), result.getPartList()));

        return result;
    }

    private void validateForRegister(SVM013001Form model, SVM013001Parameter para) {

        this.validateBasicInfo(model, para, CommonConstants.OPERATION_STATUS_NEW);
    }

    private void registerOperationForServiceOrder(SVM013001Form model, PJUserDetails uc) {

        SVM013001Parameter para = new SVM013001Parameter();
        //设置当前通用参数
        para.setDoFlag(uc.getDoFlag());
        //前置验证
        this.validateForRegister(model, para);
        //创建相关VO
        this.prepareServiceOrderForRegister(model, para, uc);
        this.prepareSalesOrderForRegister(model, para, uc);//无论有没有parts，均创建salesOrder,在settle和cancel时，会根据salesOrder明细情况删除
        this.prepareServiceOrderFaultList(model, para);
        this.prepareServiceOrderJobList(model, para);
        this.prepareServiceOrderPartListForRegister(model, para);
        this.prepareServiceOrderBatteryList(model, para);
        //register特有逻辑：锁死Special Claim Master数据， 更新车辆质量状态为Fault
        this.prepareSpecialClaimSerialProForRegister(para);
        this.prepareSerializedProForRegister(para);
        //外键赋值
        this.setForeignKey(para);

        svm0130Service.registerServiceOrder(para);
    }

    private void updateOperationForServiceOrder(SVM013001Form model, PJUserDetails uc) {

        SVM013001Parameter para = new SVM013001Parameter();
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
        //外键赋值
        this.setForeignKey(para);
        //part明细的处理逻辑设计库存实时加减，放在service中
        svm0130Service.updateServiceOrder(para);
    }

    public void cancelOperationForServiceOrder(SVM013001Form model) {

        SVM013001Parameter para = new SVM013001Parameter();

        this.validateServiceOrderExistence(model, para);
        //修改相关VO
        this.prepareServiceOrderForCancel(para);
        this.prepareSalesOrderForCancel(para);
        this.prepareSpecialClaimSerialProForCancel(para);
        this.prepareServiceOrderPartListForCancel(para);
        //更新车辆质量状态为Normal
        this.prepareSerializedProForCancel(model, para);

        svm0130Service.cancelServiceOrder(para);
    }

    public void settleOperationForServiceOrder(SVM013001Form model, PJUserDetails uc) {

        SVM013001Parameter para = new SVM013001Parameter();

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
        //更新车辆质量状态为Normal
        this.prepareSerializedProForSettle(model, para);
        this.prepareBattery(model, para);

        //外键赋值
        this.setForeignKey(para);

        svm0130Service.settleServiceOrder(para, uc);
    }

    private void prepareBattery(SVM013001Form model, SVM013001Parameter para) {

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

    private void prepareLocMotor(SVM013001Parameter para, String siteId) {

        SerializedProductVO serializedPro = svm0130Service.findSerializedProductByCmmId(para.getCmmSerializedProduct().getSerializedProductId(), siteId);

        if (Objects.isNull(serializedPro)) {

            serializedPro = SerializedProductVO.copyFromCmm(para.getCmmSerializedProduct(), siteId);
        }

        para.setSerializedProduct(serializedPro);
    }

    private void prepareNewLocBatteryMst(SVM013001Parameter para, CmmBatteryVO newCmmBattery, Long serializedProductId, String siteId) {

        para.getBatteryMstList().add(BatteryVO.copyFromCmm(newCmmBattery, siteId, serializedProductId));
    }

    private CmmBatteryVO prepareNewCmmBatteryMst(SVM013001Parameter para, BatteryBO batteryDetail, CmmBatteryVO oldBattery, String serviceCategoryId, Long cmmSerializedProductId) {

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

    private CmmBatteryVO prepareOldCmmBatteryMst(SVM013001Parameter para, BatteryBO batteryDetail) {

        CmmBatteryVO result = svm0130Service.findCmmBatteryById(batteryDetail.getCurrentBatteryId());
        result.setToDate(ComUtil.date2str(LocalDate.now().minusDays(1)));
        para.getCmmBatteryMstList().add(result);

        return result;
    }

    private void prepareOldLocBatteryMst(SVM013001Parameter para, BatteryBO batteryDetail, String siteId) {

        BatteryVO result = svm0130Service.findBatteryByCmmId(siteId, batteryDetail.getCurrentBatteryId());

        if (!Objects.isNull(result)) {

            result.setToDate(ComUtil.date2str(LocalDate.now().minusDays(1)));
            para.getBatteryMstList().add(result);
        }
    }

    private void prepareSpecialClaimSerialProForRegister(SVM013001Parameter para) {
        //SpecialClaim Service一旦创建，需要锁死同一回收批次下的master数据
        if (!StringUtils.equals(para.getServiceOrder().getServiceCategoryId(), ServiceCategory.SPECIALCLAIM.getCodeDbid())) {return;}

        List<CmmSpecialClaimSerialProVO> specialClaimSerialProList = svm0130Service.findSpecialClaimSerialProListByBulletinNo(para.getServiceOrder().getBulletinNo(), para.getServiceOrder().getCmmSerializedProductId());

        for (CmmSpecialClaimSerialProVO specialClaimSerialPro : specialClaimSerialProList) {

            specialClaimSerialPro.setDealerCd(para.getServiceOrder().getSiteId());
            specialClaimSerialPro.setFacilityCd(para.getServiceOrder().getFacilityCd());
            specialClaimSerialPro.setApplyDate(LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)));
            specialClaimSerialPro.setClaimFlag(CommonConstants.CHAR_Y);
        }

        para.setCmmSpecialClaimSerialProList(specialClaimSerialProList);
    }

    private void prepareSerializedProForRegister(SVM013001Parameter para) {

        CmmSerializedProductVO cmmSerializedPro = para.getCmmSerializedProduct();

        cmmSerializedPro.setQualityStatus(SerialProQualityStatus.FAULT);
        para.setCmmSerializedProduct(cmmSerializedPro);
    }

    private void prepareSerializedProForCancel(SVM013001Form model, SVM013001Parameter para) {

        para.setCmmSerializedProduct(this.setSerializedProQuality(model.getOrderInfo().getCmmSerializedProductId(), SerialProQualityStatus.NORMAL));
    }

    private void prepareSerializedProForSettle(SVM013001Form model, SVM013001Parameter para) {

        para.setCmmSerializedProduct(this.setSerializedProQuality(model.getOrderInfo().getCmmSerializedProductId(), SerialProQualityStatus.NORMAL));
    }

    private CmmSerializedProductVO setSerializedProQuality(Long cmmSerializedProductId, String qualityStatus) {

        CmmSerializedProductVO result = svm0130Service.findCmmSerializedProductById(cmmSerializedProductId);

        result.setQualityStatus(qualityStatus);

        return result;
    }

    private void prepareSpecialClaimSerialProForCancel(SVM013001Parameter para) {
        //SpecialClaim Service取消时，需要放开回收批次的master数据
        if (!StringUtils.equals(para.getServiceOrder().getServiceCategoryId(), ServiceCategory.SPECIALCLAIM.getCodeDbid())) {return;}

        List<CmmSpecialClaimSerialProVO> specialClaimSerialProList = svm0130Service.findSpecialClaimSerialProListByBulletinNo(para.getServiceOrder().getBulletinNo(), para.getServiceOrder().getCmmSerializedProductId());

        for (CmmSpecialClaimSerialProVO specialClaimSerialPro : specialClaimSerialProList) {

            specialClaimSerialPro.setDealerCd(null);
            specialClaimSerialPro.setFacilityCd(null);
            specialClaimSerialPro.setApplyDate(null);
            specialClaimSerialPro.setClaimFlag(CommonConstants.CHAR_N);
        }

        para.setCmmSpecialClaimSerialProList(specialClaimSerialProList);
    }

    private void setForeignKey(SVM013001Parameter para) {

        para.getServiceOrder().setRelatedSalesOrderId(para.getSalesOrder().getSalesOrderId());
        para.getSalesOrder().setRelatedSvOrderId(para.getServiceOrder().getServiceOrderId());

        //获取Situation中symptomCd和faultId的map
        this.setFaultIdForJobAndPart(para);
    }

    private void setFaultIdForJobAndPart(SVM013001Parameter para) {

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

    private void prepareSalesOrderForRegister(SVM013001Form model, SVM013001Parameter para, PJUserDetails uc) {
        para.setSalesOrder(this.setValueForSalesOrder(SalesOrderVO.create() , model, uc, CommonConstants.OPERATION_STATUS_NEW));
    }

    private void prepareSalesOrderForUpdate(SVM013001Form model, SVM013001Parameter para, PJUserDetails uc) {
        para.setSalesOrder(this.setValueForSalesOrder(orderCmmMethod.findSalesOrderById(para.getServiceOrder().getRelatedSalesOrderId()), model, uc, CommonConstants.OPERATION_STATUS_UPDATE));
    }

    private void prepareSalesOrderForCancel(SVM013001Parameter para) {
        SalesOrderVO salesOrder = orderCmmMethod.findSalesOrderById(para.getServiceOrder().getRelatedSalesOrderId());
        salesOrder.setOrderStatus(SalesOrderStatus.CANCELLED);
        para.setSalesOrder(salesOrder);
    }

    private SalesOrderVO setValueForSalesOrder(SalesOrderVO result, SVM013001Form model, PJUserDetails uc, String operationStatus) {

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

        return result;
    }

    private void prepareServiceOrderPartListForRegister(SVM013001Form model, SVM013001Parameter para) {

        List<SalesOrderItemVO> saveList = new ArrayList<>();

        Integer seqNo = CommonConstants.INTEGER_ZERO;

        //处理新增行
        for (PartDetailBO part : model.getPartList().getInsertRecords()) {
            saveList.add(this.setSalesOrderItemVOForRegister(SalesOrderItemVO.create(model.getSiteId(), para.getSalesOrder().getSalesOrderId()), part, seqNo));
            seqNo++;
        }

        para.setPartListForSave(saveList);
    }

    private void prepareServiceOrderPartListForUpdate(SVM013001Form model, SVM013001Parameter para) {

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
                                                     : svm0130Service.timelyFindServiceOrderPartListByIds(Stream.concat(model.getPartList().getUpdateRecords().stream().map(PartDetailBO::getSalesOrderItemId)
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

    private void prepareServiceOrderPartListForCancel(SVM013001Parameter para){

        List<SalesOrderItemVO> salesOrderItemList = svm0130Service.timelyFindServiceOrderPartListBySalesOrderId(para.getSalesOrder().getSalesOrderId())
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

        return result;
    }

    private void prepareServiceOrderFaultList(SVM013001Form model, SVM013001Parameter para) {
        //处理新增和更新行
        para.setSituationListForSave(orderCmmMethod.prepareNewOrUpdateFaultList(model.getSituationList(), model.getSiteId(), para.getServiceOrder().getServiceOrderId()));
        //处理删除行
        para.setSituationListForDelete(model.getSituationList().getRemoveRecords().stream().map(SituationBO::getServiceOrderFaultId).toList());
    }

    private void prepareServiceOrderBatteryList(SVM013001Form model, SVM013001Parameter para) {

        if (model.getBatteryList().isEmpty()) {return;}

        //获取DB中的battery明细
        List<Long> serviceBatteryIds = model.getBatteryList().stream().map(BatteryBO::getServiceOrderBatteryId).toList();

        Map<Long, ServiceOrderBatteryVO> batteryInDBMap = serviceBatteryIds.isEmpty()
                                                            ? new HashMap<>()
                                                            : svm0130Service.findServiceOrderBatteryListByIds(serviceBatteryIds)
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

    private void prepareServiceOrderJobList(SVM013001Form model, SVM013001Parameter para) {

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
                                                : svm0130Service.findServiceOrderJobListByIds(model.getJobList().getUpdateRecords().stream().map(JobDetailBO::getServiceOrderJobId).toList())
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

    private void prepareServiceOrderForRegister(SVM013001Form model, SVM013001Parameter para, PJUserDetails uc) {

        para.setServiceOrder(this.setValueForServiceOrder(ServiceOrderVO.create(), para.getCmmSerializedProduct(), model, uc, CommonConstants.OPERATION_STATUS_NEW));
        model.getOrderInfo().setServiceOrderId(para.getServiceOrder().getServiceOrderId());
    }

    private void prepareServiceOrderForUpdate(SVM013001Form model, SVM013001Parameter para, PJUserDetails uc) {

        para.setServiceOrder(this.setValueForServiceOrder(para.getServiceOrder(), para.getCmmSerializedProduct(), model, uc, CommonConstants.OPERATION_STATUS_UPDATE));
    }

    private void prepareServiceOrderForSettle(SVM013001Form model, SVM013001Parameter para, PJUserDetails uc) {

        para.setServiceOrder(this.setValueForServiceOrder(para.getServiceOrder(), para.getCmmSerializedProduct(), model, uc, CommonConstants.OPERATION_STATUS_FINISH));
    }

    private void prepareServiceOrderForCancel(SVM013001Parameter para) {
        para.getServiceOrder().setOrderStatusId(ServiceOrderStatus.CANCELLED.getCodeDbid());
        para.getServiceOrder().setOrderStatusContent(ServiceOrderStatus.CANCELLED.getCodeData1());
    }

    private ServiceOrderVO setValueForServiceOrder(ServiceOrderVO result, CmmSerializedProductVO cmmSerializedProd, SVM013001Form model, PJUserDetails uc, String operationStatus) {

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
            result.setServiceDemandContent(model.getOrderInfo().getServiceDemand());
            result.setZeroKmFlag(CommonConstants.CHAR_Y);
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

        result.setMileage(BigDecimal.ZERO);
        result.setOperationStartTime(StringUtils.isBlank(model.getOrderInfo().getOperationStart()) ? null : LocalDateTime.parse(model.getOrderInfo().getOperationStart(), DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMDHM)));
        result.setOperationFinishTime(StringUtils.isBlank(model.getOrderInfo().getOperationFinish()) ? null : LocalDateTime.parse(model.getOrderInfo().getOperationFinish(), DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMDHM)));
        result.setMechanicId(model.getOrderInfo().getMechanicId());
        result.setMechanicCd(model.getOrderInfo().getMechanicCd());
        result.setMechanicNm(model.getOrderInfo().getMechanicNm());
        result.setPaymentAmt(model.getOrderInfo().getPaymentAmt());

        return result;
    }

    private void validateForUpdate(SVM013001Form model, SVM013001Parameter para) {
        //验证ServiceOrder存在性和状态,获得serviceOrderVO
        this.validateServiceOrderExistence(model, para);
        this.validateBasicInfo(model, para, CommonConstants.OPERATION_STATUS_UPDATE);
    }

    private void validateForSettle(SVM013001Form model, SVM013001Parameter para) {
        //验证ServiceOrder存在性和状态,获得serviceOrderVO
        this.validateServiceOrderExistence(model, para);
        this.validateBasicInfo(model, para, CommonConstants.OPERATION_STATUS_FINISH);
        //job至少有一条数据
        this.validateAtLeastOneJob(model, para);
    }

    private void validateAtLeastOneJob(SVM013001Form model, SVM013001Parameter para) {

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

    private void validateBasicInfo(SVM013001Form model, SVM013001Parameter para, String operationStatus) {
        //验证字段必入力
        this.validateIsRequired(model, operationStatus);
        //验证车辆是否已卖出,获得CmmSerializedProductVO
        this.validateMc(model, para, operationStatus);
        //验证各Gird(必入力+重复性)，获得fullList
        para.setSituationListCheckList(orderCmmMethod.validateSituationDetail(model.getOrderInfo().getServiceOrderId(), model.getSituationList(), CommonConstants.CHAR_BLANK));
        para.setJobListCheckList(orderCmmMethod.validateJobDetail(model.getOrderInfo().getServiceOrderId(), model.getJobList()));
        para.setPartListCheckList(orderCmmMethod.validatePartDetail(model.getOrderInfo().getServiceOrderId(), model.getPartList(), model.getOrderInfo().getEvFlag()));
        orderCmmMethod.validateBatteryDetail(model.getOrderInfo().getEvFlag(), model.getBatteryList());
        //验证字段特殊规则
        orderCmmMethod.validateOperationTime(model.getOrderInfo().getStartTime(), model.getOrderInfo().getOperationStart(), model.getOrderInfo().getOperationFinish(), operationStatus);
        orderCmmMethod.validateAuthorizationNo(model.getSituationList(), model.getSiteId(), model.getOrderInfo().getPointId(), model.getOrderInfo().getFrameNo(), model.getOrderInfo().getServiceOrderId());
        //特殊组合验证
        this.validateSpecialClaimDemand(model);
        this.validatePartBattery(model, para);//需要前置获得各grid的fullList
        this.validateNewBatteryNo(model);
        this.validateSymptomCdValid(para);//需要前置获得各grid的fullList
    }

    private void validateSymptomCdValid(SVM013001Parameter para) {

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

    private void validateNewBatteryNo(SVM013001Form model) {

        //验证newBatteryNo在DB不存在（未使用过）
        List<String> batteryNoList = model.getBatteryList().stream()
                                                           .filter(battery -> StringUtils.isNotBlank(battery.getNewBatteryNo()))
                                                           .map(BatteryBO::getNewBatteryNo).toList();

        if (!batteryNoList.isEmpty()) {

            List<CmmBatteryVO> existBattery = svm0130Service.findCmmBatteryByBatteryNos(batteryNoList);

            if (!existBattery.isEmpty()) {

                throw new BusinessCodedException(ComUtil.t("M.E.00309", new String[] {ComUtil.t("label.batteryId"), existBattery.get(0).getBatteryNo(), ComUtil.t("label.tableCmmBatteryInfo")}));
            }
        }
    }

    private void validateMc(SVM013001Form model, SVM013001Parameter para, String operationStatus) {

        CmmSerializedProductVO cmmSerializedPro = svm0130Service.findCmmSerializedProductById(model.getOrderInfo().getCmmSerializedProductId());

        this.validateMcStatus(cmmSerializedPro, operationStatus);
        para.setCmmSerializedProduct(cmmSerializedPro);
    }

    private void validateMcStatus(CmmSerializedProductVO cmmSerializedPro, String operationStatus) {

        if (!StringUtils.equals(cmmSerializedPro.getSalesStatus(), MstCodeConstants.McSalesStatus.STOCK)) {
            throw new BusinessCodedException(ComUtil.t("M.E.10131"));
        }

        //新建订单时，车辆质量状态必须为normal， 订单过程中，车辆状态必须为Fault， 订单结束/取消后，车辆恢复成normal
        if ((StringUtils.equals(operationStatus, CommonConstants.OPERATION_STATUS_NEW)
                && !StringUtils.equals(cmmSerializedPro.getQualityStatus(), SerialProQualityStatus.NORMAL))
                ||
            (!StringUtils.equals(operationStatus, CommonConstants.OPERATION_STATUS_NEW)
                    && !StringUtils.equals(cmmSerializedPro.getQualityStatus(), SerialProQualityStatus.FAULT))) {
            throw new BusinessCodedException(ComUtil.t("M.E.10131"));
        }
    }

    private void validatePartBattery(SVM013001Form model, SVM013001Parameter para) {

        //parts中，电池类部品的行数 和 battery
        if (para.getPartListCheckList().stream().filter(part -> StringUtils.equals(part.getBatteryFlag(), CommonConstants.CHAR_Y)).count()
                != model.getBatteryList().stream().filter(battery -> StringUtils.isNotBlank(battery.getNewBatteryNo())).count()) {

            throw new BusinessCodedException(ComUtil.t("M.E.00203", new String[] {"battery quantity in parts detail", "battery quantity in battery detail"}));
        }
    }

    private void validateIsRequired(SVM013001Form model, String operationStatus) {

        validateLogic.validateIsRequired(model.getOrderInfo().getFrameNo(), ComUtil.t("label.frameNumber"));
        validateLogic.validateIsRequired(model.getOrderInfo().getModel(), ComUtil.t("label.model"));

        validateLogic.validateIsRequired(model.getOrderInfo().getServiceCategoryId(), ComUtil.t("label.serviceCategory"));


        //specialClaim时，demand必入力
        if (StringUtils.equals(model.getOrderInfo().getServiceCategoryId(), ServiceCategory.SPECIALCLAIM.getCodeDbid())) {

            validateLogic.validateIsRequired(model.getOrderInfo().getSpecialClaimId(), ComUtil.t("label.serviceDemand"));
        }

        //Settle时验证
        if (StringUtils.equals(operationStatus, CommonConstants.OPERATION_STATUS_FINISH)) {

            validateLogic.validateIsRequired(model.getOrderInfo().getOperationStart(), ComUtil.t("label.operationStartTime"));
            validateLogic.validateIsRequired(model.getOrderInfo().getOperationFinish(), ComUtil.t("label.operationFinishTime"));
            validateLogic.validateIsRequired(model.getOrderInfo().getMechanicId(), ComUtil.t("label.mechanic"));
        }
    }

    private void validateServiceOrderExistence(SVM013001Form model, SVM013001Parameter para) {

        ServiceOrderVO serviceOrder = orderCmmMethod.orderExistence(model.getOrderInfo().getServiceOrderId(), model.getOrderInfo().getOrderNo(), model.getOrderInfo().getUpdateCounter());

        para.setServiceOrder(serviceOrder);
    }

    private void validateSpecialClaimDemand(SVM013001Form model) {
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

    private void getMotorAndConsumerDetail(SVM013001BO result, CmmSerializedProductVO cmmSerializedProduct) {

        result.setFrameNo(cmmSerializedProduct.getFrameNo());
        result.setModelId(cmmSerializedProduct.getProductId());

        if (!Objects.isNull(cmmSerializedProduct.getProductId())) {

            MstProductVO model = orderCmmMethod.getProductById(cmmSerializedProduct.getProductId());

            if (!Objects.isNull(model)) {

                result.setModelCd(model.getProductCd());
                result.setModelNm(model.getSalesDescription());
                result.setModel(result.getModelCd() + CommonConstants.CHAR_SPACE + result.getModelNm());
            }
        }

        result.setEvFlag(cmmSerializedProduct.getEvFlag());
        result.setCmmSerializedProductId(cmmSerializedProduct.getSerializedProductId());
    }
}
