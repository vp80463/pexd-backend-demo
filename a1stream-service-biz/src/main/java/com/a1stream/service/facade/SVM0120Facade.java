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
import com.a1stream.common.constants.PJConstants.BrandType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.common.constants.PJConstants.ServiceOrderStatus;
import com.a1stream.common.constants.PJConstants.SettleType;
import com.a1stream.common.facade.ConsumerFacade;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.common.logic.ConstantsLogic;
import com.a1stream.common.logic.ValidateLogic;
import com.a1stream.common.model.BaseConsumerForm;
import com.a1stream.common.model.ConstantsBO;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.service.SVM012001BO;
import com.a1stream.domain.bo.service.SVM012001JobBO;
import com.a1stream.domain.bo.service.SVM012001PartBO;
import com.a1stream.domain.bo.service.SVM0120PrintBO;
import com.a1stream.domain.bo.service.SVM0120PrintServiceJobBO;
import com.a1stream.domain.bo.service.SVM0120PrintServicePartBO;
import com.a1stream.domain.bo.service.SummaryBO;
import com.a1stream.domain.form.service.SVM012001Form;
import com.a1stream.domain.parameter.service.ConsumerPolicyParameter;
import com.a1stream.domain.parameter.service.SVM012001Parameter;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.ServiceOrderItemOtherBrandVO;
import com.a1stream.domain.vo.ServiceOrderVO;
import com.a1stream.service.service.SVM0120Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.report.exporter.PdfReportExporter;
import com.ymsl.solid.web.download.DownloadResponseView;

import jakarta.annotation.Resource;

@Component
public class SVM0120Facade {

    @Resource
    private ConstantsLogic constantsLogic;
    @Resource
    private ValidateLogic validateLogic;
    @Resource
    private SVM0120Service svm0120Service;
    @Resource
    private HelperFacade helperFacade;
    @Resource
    private OrderCmmMethod orderCmmMethod;
    @Resource
    private ConsumerFacade consumerFacade;
    @Resource
    public void setPdfReportExporter(PdfReportExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
    }
    private PdfReportExporter pdfExporter;

    public SVM012001BO getServiceDetailByIdOrNo(Long serviceOrderId, String orderNo, Long pointId, PJUserDetails uc) {
        //orderId和orderNo均无值时，仅初始化; 有值时，获取service明细
        SVM012001BO result = Objects.isNull(serviceOrderId) && StringUtils.isBlank(orderNo) ? this.initForNewCreate(uc) : this.initForUpdate(serviceOrderId, orderNo, pointId, uc.getDealerCode());
        //基础数据设置
        this.initForBasicInfo(result, uc);

        return result;
    }

    public void newOrModifyServiceOrder(SVM012001Form model, PJUserDetails uc) {

        if (Objects.isNull(model.getOrderInfo().getServiceOrderId())) {

            this.registerOperationForServiceOrder(model, uc);
        }
        else {

            this.updateOperationForServiceOrder(model, uc);
        }
    }

    public void settleOperationForServiceOrder(SVM012001Form model, PJUserDetails uc) {

        SVM012001Parameter para = new SVM012001Parameter();

        //设置当前通用参数
        para.setDoFlag(uc.getDoFlag());
        //前置验证
        this.validateForSettle(model, para);
        //修改相关VO
        this.prepareServiceOrderForSettle(model, para, uc);
        this.prepareServiceOrderDetailList(model, para);
        this.prepareConsumerPrivacyPolicyResult(model, para);
        this.prepareConsumerBasicInfoForSettle(model, para);

        svm0120Service.settleServiceOrder(para);
    }

    public void cancelOperationForServiceOrder(SVM012001Form model, PJUserDetails uc) {

        SVM012001Parameter para = new SVM012001Parameter();

        this.validateServiceOrderExistence(model, para);
        //修改相关VO
        this.prepareServiceOrderForCancel(para);

        svm0120Service.cancelServiceOrder(para);
    }

    public DownloadResponseView printBlankJobCard(Long serviceOrderId) {

        List<SVM0120PrintBO> dataList = new ArrayList<>();

        SVM0120PrintBO printBO = svm0120Service.getOtherBrandBlankJobCardData(serviceOrderId);

        if (Nulls.isNull(printBO)) {

            return null;

        } else {

            //ServiceDate格式化
            if (StringUtils.isNotBlank(printBO.getServiceDate())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);
                printBO.setServiceDate(LocalDate.parse(printBO.getServiceDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

        }

        dataList.add(printBO);
        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_STREAMBLANKJOBCARDOTHERBRAND_REPORT,
                                                   dataList,
                                                   CommonConstants.CHAR_BLANK);

        return new DownloadResponseView(rp);
    }

    public DownloadResponseView printJobCard(Long serviceOrderId) {

        List<SVM0120PrintBO> dataList = new ArrayList<>();

        //HeaderData
        SVM0120PrintBO printBO = svm0120Service.getOtherBrandJobCardHeaderData(serviceOrderId);

        if (!Nulls.isNull(printBO)) {

            printBO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS)));

            //ServiceDate格式化
            if (StringUtils.isNotBlank(printBO.getServiceDate())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);
                printBO.setServiceDate(LocalDate.parse(printBO.getServiceDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //ServiceJobDataEdit
            List<SVM0120PrintServiceJobBO> serviceJobList = svm0120Service.getOtherBrandJobCardJobList(serviceOrderId);

            if (serviceJobList.isEmpty()) {

                SVM0120PrintServiceJobBO serviceJobBO = new SVM0120PrintServiceJobBO();
                serviceJobBO.setStdmenhour(BigDecimal.ZERO);
                serviceJobList.add(serviceJobBO);

            } else {

                for (SVM0120PrintServiceJobBO jobBo : serviceJobList) {

                    if (StringUtils.isNotBlank(jobBo.getItemCd())) {

                        jobBo.setServiceJob(jobBo.getItemCd() +
                                CommonConstants.CHAR_SPACE +
                                CommonConstants.CHAR_VERTICAL_BAR +
                                CommonConstants.CHAR_SPACE +
                                jobBo.getItemContent());

                    } else {

                        jobBo.setServiceJob(jobBo.getItemContent());

                    }
                }
            }
            printBO.setServiceJobList(serviceJobList);

            //ServicePartDataEdit
            List<SVM0120PrintServicePartBO> servicePartList = svm0120Service.getOtherBrandJobCardPartList(serviceOrderId);
            if (servicePartList.isEmpty()) {

                SVM0120PrintServicePartBO servicePartBO = new SVM0120PrintServicePartBO();
                servicePartBO.setQty(BigDecimal.ZERO);
                servicePartList.add(servicePartBO);

            }

            printBO.setServicePartList(servicePartList);
        }

        dataList.add(printBO);

        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_STREAMJOBCARDOTHERBRAND_REPORT,
                                                   dataList,
                                                   CommonConstants.CHAR_BLANK);

        return new DownloadResponseView(rp);
    }

    public DownloadResponseView printServicePayment(Long serviceOrderId, String userName) {

        List<SVM0120PrintBO> dataList = new ArrayList<>();

        //HeaderData
        SVM0120PrintBO printBO = svm0120Service.getOtherBrandPaymentHeaderData(serviceOrderId);

        //userName
        if (StringUtils.isNotBlank(userName)) {
            JSONObject json = JSON.parseObject(userName);
            String userNm = json.getString("username");
            printBO.setUserName(StringUtils.isNotBlank(userNm) ? userNm : CommonConstants.CHAR_BLANK);
        }

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
            List<SVM0120PrintServiceJobBO> serviceJobList = svm0120Service.getOtherBrandPaymentJobList(serviceOrderId);

            if (serviceJobList.isEmpty()) {

                SVM0120PrintServiceJobBO serviceJobBO = new SVM0120PrintServiceJobBO();
                serviceJobBO.setAmount(BigDecimal.ZERO);
                serviceJobList.add(serviceJobBO);

            } else {

                //ServiceCategoryId -> CodeData1
                Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(ServiceCategory.CODE_ID);

                for (SVM0120PrintServiceJobBO jobBo : serviceJobList) {

                    jobBo.setServiceCategory(codeMap.get(jobBo.getServiceCategory()));

                    if (StringUtils.isNotBlank(jobBo.getItemCd())) {

                        jobBo.setJob(jobBo.getItemCd() +
                                     CommonConstants.CHAR_SPACE +
                                     CommonConstants.CHAR_VERTICAL_BAR +
                                     CommonConstants.CHAR_SPACE +
                                     jobBo.getItemContent());

                    } else {

                        jobBo.setJob(jobBo.getItemContent());

                    }
                }

                //计算JobAmount总和
                BigDecimal jobAmount = BigDecimal.ZERO;
                for (SVM0120PrintServiceJobBO job : serviceJobList) {
                    jobAmount = jobAmount.add(job.getAmount().setScale(0, RoundingMode.HALF_UP));
                }

                printBO.setJobAmount(jobAmount);

            }
            printBO.setServiceJobList(serviceJobList);

            //ServicePartDataEdit
            List<SVM0120PrintServicePartBO> servicePartList = svm0120Service.getOtherBrandPaymentPartList(serviceOrderId);
            if (servicePartList.isEmpty()) {

                SVM0120PrintServicePartBO servicePartBO = new SVM0120PrintServicePartBO();
                servicePartBO.setQty(BigDecimal.ZERO);
                servicePartList.add(servicePartBO);

            } else {

                for (SVM0120PrintServicePartBO part : servicePartList) {

                    part.setTotalPrice(NumberUtil.multiply(part.getSellingPrice(), part.getQty()));

                }

                //计算PartAmount总和
                BigDecimal partAmount = BigDecimal.ZERO;
                for (SVM0120PrintServicePartBO job : servicePartList) {
                    partAmount = partAmount.add(job.getTotalPrice().setScale(0, RoundingMode.HALF_UP));
                }
                printBO.setPartAmount(partAmount);

            }
            printBO.setServicePartList(servicePartList);
        }
        dataList.add(printBO);

        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_STREAMSERVICEPAYMENTOTHERBRAND_REPORT,
                                                   dataList,
                                                   CommonConstants.CHAR_BLANK);

        return new DownloadResponseView(rp);
    }

    private void prepareServiceOrderForCancel(SVM012001Parameter para) {
        para.getServiceOrder().setOrderStatusId(ServiceOrderStatus.CANCELLED.getCodeDbid());
        para.getServiceOrder().setOrderStatusContent(ServiceOrderStatus.CANCELLED.getCodeData1());
    }

    private void validateForSettle(SVM012001Form model, SVM012001Parameter para) {
        //验证ServiceOrder存在性和状态,获得serviceOrderVO
        this.validateServiceOrderExistence(model, para);
        this.validateBasicInfo(model, para, CommonConstants.OPERATION_STATUS_FINISH);
        //job至少有一条数据
        this.validateAtLeastOneJob(para);
    }

    private void validateAtLeastOneJob(SVM012001Parameter para) {
        //job至少有一条数据
        if (para.getJobListCheckList().isEmpty()) {throw new BusinessCodedException(ComUtil.t("M.E.10242"));}
    }

    private void registerOperationForServiceOrder(SVM012001Form model, PJUserDetails uc) {

        SVM012001Parameter para = new SVM012001Parameter();
        //设置当前通用参数
        para.setDoFlag(uc.getDoFlag());
        //前置验证
        this.validateForRegister(model, para);
        //创建相关VO
        this.prepareServiceOrderForRegister(model, para, uc);
        this.prepareServiceOrderDetailList(model, para);
        this.prepareConsumerPrivacyPolicyResult(model, para);

        svm0120Service.registerServiceOrder(para);
    }

    private void updateOperationForServiceOrder(SVM012001Form model, PJUserDetails uc) {

        SVM012001Parameter para = new SVM012001Parameter();
        //设置当前通用参数
        para.setDoFlag(uc.getDoFlag());
        //前置验证
        this.validateForUpdate(model, para);
        //修改相关VO
        this.prepareServiceOrderForUpdate(model, para, uc);
        this.prepareServiceOrderDetailList(model, para);
        this.prepareConsumerPrivacyPolicyResult(model, para);

        svm0120Service.updateServiceOrder(para);
    }

    private void prepareServiceOrderForUpdate(SVM012001Form model, SVM012001Parameter para, PJUserDetails uc) {

        para.setServiceOrder(this.setValueForServiceOrder(para.getServiceOrder(), model, uc, CommonConstants.OPERATION_STATUS_UPDATE));
    }

    private void prepareServiceOrderForSettle(SVM012001Form model, SVM012001Parameter para, PJUserDetails uc) {

        para.setServiceOrder(this.setValueForServiceOrder(para.getServiceOrder(), model, uc, CommonConstants.OPERATION_STATUS_FINISH));
    }

    private void validateForUpdate(SVM012001Form model, SVM012001Parameter para) {
        //验证ServiceOrder存在性和状态,获得serviceOrderVO
        this.validateServiceOrderExistence(model, para);
        this.validateBasicInfo(model, para, CommonConstants.OPERATION_STATUS_UPDATE);
    }

    private void validateServiceOrderExistence(SVM012001Form model, SVM012001Parameter para) {

        ServiceOrderVO serviceOrder = orderCmmMethod.orderExistence(model.getOrderInfo().getServiceOrderId(), model.getOrderInfo().getOrderNo(), model.getOrderInfo().getUpdateCounter());

        para.setServiceOrder(serviceOrder);
    }

    private void prepareConsumerBasicInfoForSettle(SVM012001Form model, SVM012001Parameter para){

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

    private void prepareConsumerPrivacyPolicyResult(SVM012001Form model, SVM012001Parameter para) {

        para.setConsumerPrivacyPolicyResult(consumerFacade.prepareConsumerPrivacyPolicyResult(new ConsumerPolicyParameter(model.getSiteId()
                                                                                                                        , model.getOrderInfo().getPointCd()
                                                                                                                        , model.getOrderInfo().getLastName()
                                                                                                                        , model.getOrderInfo().getMiddleName()
                                                                                                                        , model.getOrderInfo().getFirstName()
                                                                                                                        , model.getOrderInfo().getMobilephone()
                                                                                                                        , model.getOrderInfo().getPolicyResultFlag()
                                                                                                                        , model.getOrderInfo().getPolicyFileName())));
    }

    private void prepareServiceOrderDetailList(SVM012001Form model, SVM012001Parameter para) {

        List<ServiceOrderItemOtherBrandVO> saveList = new ArrayList<>();
        ServiceOrderItemOtherBrandVO serviceDetail;

        //处理新增行
        for (SVM012001JobBO job : model.getJobList().getInsertRecords()) {

            serviceDetail = ServiceOrderItemOtherBrandVO.create(model.getSiteId(), para.getServiceOrder().getServiceOrderId());

            saveList.add(this.setValueForServiceOrderJobDetail(serviceDetail, job));
        }

        for (SVM012001PartBO part : model.getPartList().getInsertRecords()) {

            serviceDetail = ServiceOrderItemOtherBrandVO.create(model.getSiteId(), para.getServiceOrder().getServiceOrderId());

            saveList.add(this.setValueForServiceOrderPartDetail(serviceDetail, part));
        }

        //处理修改行
        Map<Long, ServiceOrderItemOtherBrandVO> detailInDBMap = model.getJobList().getUpdateRecords().isEmpty() && model.getPartList().getUpdateRecords().isEmpty()
                                                             ? new HashMap<>()
                                                             : svm0120Service.findServiceDetailListByIds(Stream.concat(model.getJobList().getUpdateRecords().stream().map(SVM012001JobBO::getServiceOrderItemOtherBrandId)
                                                                                                                     , model.getPartList().getUpdateRecords().stream().map(SVM012001PartBO::getServiceOrderItemOtherBrandId))
                                                                                                        .toList())
                                                                             .stream()
                                                                             .collect(Collectors.toMap(ServiceOrderItemOtherBrandVO::getServiceOrderItemOtherBrandId, value -> value));

        for (SVM012001JobBO job : model.getJobList().getUpdateRecords()) {

            serviceDetail = detailInDBMap.get(job.getServiceOrderItemOtherBrandId());

            if (Objects.isNull(serviceDetail)) {continue;}

            saveList.add(this.setValueForServiceOrderJobDetail(serviceDetail, job));
        }

        for (SVM012001PartBO part : model.getPartList().getUpdateRecords()) {

            serviceDetail = detailInDBMap.get(part.getServiceOrderItemOtherBrandId());

            if (Objects.isNull(serviceDetail)) {continue;}

            saveList.add(this.setValueForServiceOrderPartDetail(serviceDetail, part));
        }

        //处理删除行
        para.setServiceDetailForDelete(Stream.concat(model.getJobList().getRemoveRecords().stream().map(SVM012001JobBO::getServiceOrderItemOtherBrandId)
                                                   , model.getPartList().getRemoveRecords().stream().map(SVM012001PartBO::getServiceOrderItemOtherBrandId))
                                      .toList());
        para.setServiceDetailForSave(saveList);
    }

    private ServiceOrderItemOtherBrandVO setValueForServiceOrderPartDetail(ServiceOrderItemOtherBrandVO result, SVM012001PartBO part) {

        result.setServiceCategory(part.getServiceCategoryId());
        result.setSettleType(part.getSettleTypeId());
        result.setItemContent(part.getPartNm());
        result.setStandardPrice(part.getStandardPrice());
        result.setTaxRate(part.getTaxRate());
        result.setSellingPrice(part.getSellingPrice());
        result.setOrderQty(part.getQty());
        result.setProductClassification(ProductClsType.PART.getCodeDbid());

        return result;
    }

    private ServiceOrderItemOtherBrandVO setValueForServiceOrderJobDetail(ServiceOrderItemOtherBrandVO result, SVM012001JobBO job) {

        result.setServiceCategory(job.getServiceCategoryId());
        result.setSettleType(job.getSettleTypeId());
        result.setItemId(job.getJobId());
        result.setItemCd(job.getJobCd());
        result.setItemContent(job.getJobNm());
        result.setStdManhour(job.getManhour());
        result.setStandardPrice(job.getStandardPrice());
        result.setSpecialPrice(job.getSpecialPrice());
        result.setDiscount(job.getDiscount());
        result.setTaxRate(job.getTaxRate());
        result.setSellingPrice(job.getSellingPrice());
        result.setProductClassification(ProductClsType.SERVICE.getCodeDbid());

        return result;
    }

    private void validateForRegister(SVM012001Form model, SVM012001Parameter para) {

        this.validateBasicInfo(model, para, CommonConstants.OPERATION_STATUS_NEW);
    }

    private void validateBasicInfo(SVM012001Form model, SVM012001Parameter para, String operationStatus) {
        //验证字段必入力
        this.validateIsRequired(model, para.getDoFlag(), operationStatus);
        //验证各Gird(必入力+重复性)，获得fullList
        this.validateJobDetail(model, para, operationStatus);
        this.validatePartDetail(model, para, operationStatus);
        //验证字段特殊规则
        orderCmmMethod.validateEmail(model.getOrderInfo().getEmail());
        orderCmmMethod.validateMobilePhone(model.getOrderInfo().getMobilephone());
        orderCmmMethod.validateOperationTime(model.getOrderInfo().getStartTime(), model.getOrderInfo().getOperationStart(), model.getOrderInfo().getOperationFinish(), operationStatus);
        //Plate+FrameNo如果在车辆master表中存在，需要到normal service做单
        this.validatePlateOrFrameExistInMc(model.getOrderInfo().getPlateNo(), model.getOrderInfo().getFrameNo());
    }

    private void validatePlateOrFrameExistInMc(String plateNo, String frameNo) {

        //车辆在master表中存在时，需要到normal service做单
        CmmSerializedProductVO cmmSerializedProduct = svm0120Service.findCmmSerializedProductByFrameOrPlate(frameNo, plateNo);

        if(!Objects.isNull(cmmSerializedProduct)) {

            throw new BusinessCodedException(ComUtil.t("M.E.10238", new String[] {StringUtils.isBlank(frameNo) ? ComUtil.t("label.numberPlate") : ComUtil.t("label.frameNumber"), StringUtils.isBlank(frameNo) ? plateNo : frameNo}));
        }
    }

    private void prepareServiceOrderForRegister(SVM012001Form model, SVM012001Parameter para, PJUserDetails uc) {

        para.setServiceOrder(this.setValueForServiceOrder(ServiceOrderVO.create(), model, uc, CommonConstants.OPERATION_STATUS_NEW));
        model.getOrderInfo().setServiceOrderId(para.getServiceOrder().getServiceOrderId());
    }

    private ServiceOrderVO setValueForServiceOrder(ServiceOrderVO result, SVM012001Form model, PJUserDetails uc, String operationStatus) {

        if (StringUtils.equals(operationStatus, CommonConstants.OPERATION_STATUS_NEW)) {

            result.setSiteId(uc.getDealerCode());
            result.setFacilityId(model.getOrderInfo().getPointId());
            result.setFacilityCd(model.getOrderInfo().getPointCd());
            result.setFacilityNm(model.getOrderInfo().getPointNm());
            result.setOrderDate(model.getOrderInfo().getOrderDate());
            result.setOrderStatusId(ServiceOrderStatus.WAITFORSETTLE.getCodeDbid());
            result.setOrderStatusContent(ServiceOrderStatus.WAITFORSETTLE.getCodeData1());
            result.setZeroKmFlag(CommonConstants.CHAR_N);
            result.setStartTime(LocalDateTime.parse(model.getOrderInfo().getStartTime(), DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMDHM)));
            result.setEntryPicId(uc.getPersonId());
            result.setEntryPicCd(uc.getPersonCode());
            result.setEntryPicNm(uc.getPersonName());
            result.setBrandId(Long.valueOf(model.getOrderInfo().getBrandId()));
            result.setBrandContent(constantsLogic.getConstantsByCodeDbId(BrandType.class.getDeclaredFields(), model.getOrderInfo().getBrandId()).getCodeData1());
            result.setModelNm(model.getOrderInfo().getModel());
            result.setModelTypeId(model.getOrderInfo().getModelTypeId());
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
        result.setPlateNo(model.getOrderInfo().getPlateNo());
        result.setFrameNo(model.getOrderInfo().getFrameNo());
        result.setConsumerId(model.getOrderInfo().getConsumerId());
        result.setConsumerVipNo(Objects.isNull(model.getOrderInfo().getConsumerId()) ? null : orderCmmMethod.findCmmConsumerById(model.getOrderInfo().getConsumerId()).getVipNo());
        result.setLastNm(model.getOrderInfo().getLastName());
        result.setMiddleNm(model.getOrderInfo().getMiddleName());
        result.setFirstNm(model.getOrderInfo().getFirstName());
        result.setMobilePhone(model.getOrderInfo().getMobilephone());
        result.setEmail(model.getOrderInfo().getEmail());
        result.setConsumerFullNm(new StringBuilder().append(result.getLastNm()).append(CommonConstants.CHAR_SPACE).append(result.getMiddleNm()).append(CommonConstants.CHAR_SPACE).append(result.getFirstNm()).toString());
        result.setServiceSubject(model.getOrderInfo().getServiceTitle());
        result.setMechanicComment(model.getOrderInfo().getMechanicComment());
        result.setPaymentAmt(model.getOrderInfo().getDepositAmt());
        result.setPaymentMethodId(model.getOrderInfo().getPaymentMethodId());

        return result;
    }

    private void validatePartDetail(SVM012001Form model, SVM012001Parameter para, String operationStatus) {

        //存在和必入力验证
        for (SVM012001PartBO part : model.getPartList().getNewUpdateRecords()) {

            //当是Repair时，sellingPrice要求>0
            if (StringUtils.equals(part.getServiceCategoryId(), ServiceCategory.REPAIR.getCodeDbid())
                    && part.getSellingPrice().compareTo(BigDecimal.ZERO) < 1) {

                throw new BusinessCodedException(ComUtil.t("M.E.00200", new String[] {ComUtil.t("label.sellingPrice"), CommonConstants.CHAR_ZERO}));
            }
        }

        //重复数据验证
        this.validateDuplicatePartDetail(model, para, operationStatus);
    }

    private void validateDuplicatePartDetail(SVM012001Form model, SVM012001Parameter para, String operationStatus) {

        //获取全数据
        para.setPartListCheckList(this.generatePartCheckList(model, operationStatus));

        if (model.getPartList().getNewUpdateRecords().isEmpty()) {return;}

        //非电池时，PartCd不得重复
        Optional<String> duplicatesPart = para.getPartListCheckList().stream()
                                                                     .map(SVM012001PartBO::getPartNm)
                                                                     .collect(Collectors.groupingBy(part -> part, Collectors.counting()))
                                                                     .entrySet().stream()
                                                                     .filter(entry -> entry.getValue() > 1)
                                                                     .map(Map.Entry::getKey).findFirst();

        if (duplicatesPart.isPresent()) {

            throw new BusinessCodedException(ComUtil.t("M.E.00304", new String[] {ComUtil.t("label.parts"), PartNoUtil.format(duplicatesPart.get())}));
        }
    }

    private void validateJobDetail(SVM012001Form model, SVM012001Parameter para, String operationStatus) {

        //存在和必入力验证
        for (SVM012001JobBO job : model.getJobList().getNewUpdateRecords()) {

            //当DO店选择YAMAHAS brand时，job的数据来源于job master，要验证VL存在性
            if (this.isDoYamahas(para.getDoFlag(), model.getOrderInfo().getBrandId())) {
                validateLogic.validateEntityNotExist(job.getJobCd(), job.getJobId(), ComUtil.t("label.job"));
            }

            //当是Repair时，sellingPrice要求>0
            if (StringUtils.equals(job.getServiceCategoryId(), ServiceCategory.REPAIR.getCodeDbid())
                    && job.getSellingPrice().compareTo(BigDecimal.ZERO) < 1) {

                throw new BusinessCodedException(ComUtil.t("M.E.00200", new String[] {ComUtil.t("label.sellingPrice"), CommonConstants.CHAR_ZERO}));
            }
        }

        //重复数据验证
        this.validateDuplicateJobDetail(model, para, operationStatus);
    }

    private void validateDuplicateJobDetail(SVM012001Form model, SVM012001Parameter para, String operationStatus) {

        //获取全数据
        para.setJobListCheckList(this.generateJobCheckList(model, operationStatus));

        if (model.getJobList().getNewUpdateRecords().isEmpty()) {return;}

        //Job不得重复,当jobCd为空时，仅判断jobName，否则以jobCode为准
        Optional<String> duplicatesJob = para.getJobListCheckList().stream()
                                                                   .map(job -> StringUtils.isBlank(job.getJobCd()) ? job.getJobNm() : job.getJobCd())
                                                                   .collect(Collectors.groupingBy(jobInfo -> jobInfo, Collectors.counting()))
                                                                   .entrySet().stream()
                                                                   .filter(entry -> entry.getValue() > 1)
                                                                   .map(Map.Entry::getKey).findFirst();

        if (duplicatesJob.isPresent()) {

            throw new BusinessCodedException(ComUtil.t("M.E.00304", new String[] {ComUtil.t("label.job"), duplicatesJob.get()}));
        }
    }

    private List<SVM012001PartBO> generatePartCheckList(SVM012001Form model, String operationStatus){
        //新增时，只需check插入行；更新时，需整合所有数据。
        return StringUtils.equals(operationStatus, CommonConstants.OPERATION_STATUS_NEW)
                ? model.getPartList().getInsertRecords()
                : this.generatePartCheckListFromDB(model);
    }

    private List<SVM012001JobBO> generateJobCheckList(SVM012001Form model, String operationStatus){
        //新增时，只需check插入行；更新时，需整合所有数据。
        return StringUtils.equals(operationStatus, CommonConstants.OPERATION_STATUS_NEW)
                ? model.getJobList().getInsertRecords()
                : this.generateJobCheckListFromDB(model);
    }

    private List<SVM012001PartBO> generatePartCheckListFromDB(SVM012001Form model){

        List<ServiceOrderItemOtherBrandVO> serviceDetailList = svm0120Service.timelySearchServiceDetailByOrderId(model.getOrderInfo().getServiceOrderId());

        //获取DB当前的List
        Map<Long, SVM012001PartBO> result = this.generatePartDetailList(serviceDetailList.stream().filter(serviceDetail -> StringUtils.equals(serviceDetail.getProductClassification(), ProductClsType.PART.getCodeDbid())).toList())
                                                          .stream()
                                                          .collect(Collectors.toMap(SVM012001PartBO::getServiceOrderItemOtherBrandId, value -> value));
        //result中移除removeList数据
        model.getPartList().getRemoveRecords().stream().map(SVM012001PartBO::getServiceOrderItemOtherBrandId).forEach(result :: remove);
        //result中替换updateList数据
        model.getPartList().getUpdateRecords().stream().collect(Collectors.toMap(SVM012001PartBO::getServiceOrderItemOtherBrandId, value -> value))
                            .forEach((key, value) -> {
                                if (!result.containsKey(key) || !StringUtils.equals(result.get(key).getUpdateCounter(), value.getUpdateCounter())) {
                                    throw new BusinessCodedException(ComUtil.t("M.E.10449"));
                                }
                                else {
                                    result.put(key, value);
                                }
                            });
        //result中拼接insertList数据
        return Stream.concat(result.values().stream(), model.getPartList().getInsertRecords().stream()).toList();
    }

    private List<SVM012001JobBO> generateJobCheckListFromDB(SVM012001Form model){

        List<ServiceOrderItemOtherBrandVO> serviceDetailList = svm0120Service.timelySearchServiceDetailByOrderId(model.getOrderInfo().getServiceOrderId());

        //获取DB当前的List
        Map<Long, SVM012001JobBO> result = this.generateJobDetailList(serviceDetailList.stream().filter(serviceDetail -> StringUtils.equals(serviceDetail.getProductClassification(), ProductClsType.SERVICE.getCodeDbid())).toList())
                                                         .stream()
                                                         .collect(Collectors.toMap(SVM012001JobBO::getServiceOrderItemOtherBrandId, value -> value));
        //result中移除removeList数据
        model.getJobList().getRemoveRecords().stream().map(SVM012001JobBO::getServiceOrderItemOtherBrandId).forEach(result :: remove);
        //result中替换updateList数据
        model.getJobList().getUpdateRecords().stream().collect(Collectors.toMap(SVM012001JobBO::getServiceOrderItemOtherBrandId, value -> value))
                            .forEach((key, value) -> {
                                if (!result.containsKey(key) || !StringUtils.equals(result.get(key).getUpdateCounter(), value.getUpdateCounter())) {
                                    throw new BusinessCodedException(ComUtil.t("M.E.10449"));
                                }
                                else {
                                    result.put(key, value);
                                }
                            });
        //result中拼接insertList数据
        return Stream.concat(result.values().stream(), model.getJobList().getInsertRecords().stream()).toList();
    }

    private void validateIsRequired(SVM012001Form model, String doFlag, String operationStatus) {

        validateLogic.validateIsRequired(model.getOrderInfo().getLastName(), ComUtil.t("label.consumerName"));
        validateLogic.validateIsRequired(model.getOrderInfo().getFirstName(), ComUtil.t("label.consumerName"));
        validateLogic.validateIsRequired(model.getOrderInfo().getMobilephone(), ComUtil.t("label.mobilephone"));

        validateLogic.validateIsRequired(model.getOrderInfo().getPlateNo(), ComUtil.t("label.numberPlate"));

        validateLogic.validateIsRequired(model.getOrderInfo().getBrandId(), ComUtil.t("label.brand"));

        validateLogic.validateIsRequired(model.getOrderInfo().getMileage(), ComUtil.t("label.mileage"));
        validateLogic.validateNumberLtZero(model.getOrderInfo().getMileage(), ComUtil.t("label.mileage"));

        //DO店的额外字段
        if (this.isDoYamahas(doFlag, model.getOrderInfo().getBrandId())) {

            validateLogic.validateIsRequired(model.getOrderInfo().getEmail(), ComUtil.t("label.email"));
            validateLogic.validateIsRequired(model.getOrderInfo().getModelTypeId(), ComUtil.t("label.modelType"));
        }
        //Settle时验证
        if (StringUtils.equals(operationStatus, CommonConstants.OPERATION_STATUS_FINISH)) {

            validateLogic.validateIsRequired(model.getOrderInfo().getOperationStart(), ComUtil.t("label.operationStartTime"));
            validateLogic.validateIsRequired(model.getOrderInfo().getOperationFinish(), ComUtil.t("label.operationFinishTime"));
            validateLogic.validateIsRequired(model.getOrderInfo().getMechanicId(), ComUtil.t("label.mechanic"));
            validateLogic.validateIsRequired(model.getOrderInfo().getPolicyResultFlag(), ComUtil.t("label.privacyPolicyResult"));

            if (this.isDoYamahas(doFlag, model.getOrderInfo().getBrandId())) {

                validateLogic.validateIsRequired(model.getOrderInfo().getPaymentMethodId(), ComUtil.t("label.paymentMethod"));
            }
        }
    }

    private boolean isDoYamahas(String doFlag, String brandId) {

        return StringUtils.equals(doFlag, CommonConstants.CHAR_Y) && StringUtils.equals(brandId, BrandType.YAMAHAS.getCodeDbid());
    }

    private SVM012001BO initForBasicInfo(SVM012001BO result, PJUserDetails uc) {

        result.setDoFlag(uc.getDoFlag());

        result.setSummaryList(this.generateSummaryList(result));

        return result;
    }

    private List<SummaryBO> generateSummaryList(SVM012001BO serviceInfo){

        Map<String, BigDecimal> jobSummary = serviceInfo.getJobList().stream().collect(Collectors.groupingBy(SVM012001JobBO::getSettleTypeId, Collectors.mapping(SVM012001JobBO::getSellingPrice, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
        Map<String, BigDecimal> partSummary = serviceInfo.getPartList().stream().collect(Collectors.groupingBy(SVM012001PartBO::getSettleTypeId, Collectors.mapping(SVM012001PartBO::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        List<SummaryBO> result = new ArrayList<>();

        SummaryBO summaryBO;

        for (ConstantsBO settleType : constantsLogic.getConstantsData(SettleType.class.getDeclaredFields())) {

            //仅保留Customer和Factory
            if (!StringUtils.equals(settleType.getCodeDbid(), SettleType.CUSTOMER.getCodeDbid())
                    && !StringUtils.equals(settleType.getCodeDbid(), SettleType.SHOP.getCodeDbid())
                    && !StringUtils.equals(settleType.getCodeDbid(), SettleType.TOTAL.getCodeDbid())) {continue;}

            summaryBO = new SummaryBO();

            summaryBO.setSettleTypeId(settleType.getCodeDbid());
            summaryBO.setSettleType(settleType.getCodeData1());
            summaryBO.setJobDetail(jobSummary.containsKey(settleType.getCodeDbid()) ? jobSummary.get(settleType.getCodeDbid()) : BigDecimal.ZERO);
            summaryBO.setPartDetail(partSummary.containsKey(settleType.getCodeDbid()) ? partSummary.get(settleType.getCodeDbid()) : BigDecimal.ZERO);
            summaryBO.setTotal(summaryBO.getJobDetail().add(summaryBO.getPartDetail()));

            result.add(summaryBO);
        }

        return result;
    }

    private SVM012001BO initForNewCreate(PJUserDetails uc) {

        SVM012001BO result = new SVM012001BO();

        result.setPointCd(uc.getDefaultPointCd());
        result.setPointId(uc.getDefaultPointId());
        result.setPointNm(uc.getDefaultPointNm());
        result.setOrderDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
        result.setOrderStatus(ServiceOrderStatus.NEW.getCodeData1());
        result.setOrderStatusId(ServiceOrderStatus.NEW.getCodeDbid());

        result.setStartTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMDHM)));

        return result;
    }

    private SVM012001BO initForUpdate(Long serviceOrderId, String orderNo, Long pointId, String siteId) {

        //根据条件查询serviceOrder
        ServiceOrderVO serviceOrder = orderCmmMethod.timelySearchServiceOrderByIdOrNo(serviceOrderId, orderNo, siteId);
        //验证service数据是否正确
        this.validateServiceOrderForInit(serviceOrder, orderNo, pointId);
        //生成BO并查询明细部分
        SVM012001BO result = this.generateServiceMainInfo(serviceOrder);
        this.timelySearchServiceDetailByOrderId(result, result.getServiceOrderId());

        return result;
    }

    private void timelySearchServiceDetailByOrderId(SVM012001BO result, Long serviceOrderId) {

        List<ServiceOrderItemOtherBrandVO> serviceDetailList = svm0120Service.timelySearchServiceDetailByOrderId(serviceOrderId);

        result.setJobList(this.generateJobDetailList(serviceDetailList.stream().filter(serviceDetail -> StringUtils.equals(serviceDetail.getProductClassification(), ProductClsType.SERVICE.getCodeDbid())).toList()));
        result.setPartList(this.generatePartDetailList(serviceDetailList.stream().filter(serviceDetail -> StringUtils.equals(serviceDetail.getProductClassification(), ProductClsType.PART.getCodeDbid())).toList()));
    }

    private List<SVM012001PartBO> generatePartDetailList(List<ServiceOrderItemOtherBrandVO> partDetailList){

        List<SVM012001PartBO> result = new ArrayList<>();

        SVM012001PartBO partModel;

        for (ServiceOrderItemOtherBrandVO part : partDetailList) {

            partModel = new SVM012001PartBO();

            partModel.setServiceCategoryId(part.getServiceCategory());
            partModel.setSettleTypeId(part.getSettleType());
            partModel.setPartNm(part.getItemContent());
            partModel.setStandardPrice(part.getStandardPrice());
            partModel.setSellingPrice(part.getSellingPrice());
            partModel.setQty(part.getOrderQty());
            partModel.setAmount(part.getOrderQty().multiply(part.getSellingPrice()));
            partModel.setTaxRate(part.getTaxRate());
            partModel.setUpdateCounter(part.getUpdateCount().toString());
            partModel.setServiceOrderItemOtherBrandId(part.getServiceOrderItemOtherBrandId());

            result.add(partModel);
        }

        return result;
    }

    private List<SVM012001JobBO> generateJobDetailList(List<ServiceOrderItemOtherBrandVO> jobDetailList){

        List<SVM012001JobBO> result = new ArrayList<>();

        SVM012001JobBO jobModel;

        for (ServiceOrderItemOtherBrandVO job : jobDetailList) {

            jobModel = new SVM012001JobBO();

            jobModel.setServiceCategoryId(job.getServiceCategory());
            jobModel.setSettleTypeId(job.getSettleType());
            jobModel.setJobId(job.getItemId());
            jobModel.setJobCd(job.getItemCd());
            jobModel.setJobNm(job.getItemContent());
            jobModel.setManhour(job.getStdManhour());
            jobModel.setStandardPrice(job.getStandardPrice());
            jobModel.setDiscount(job.getDiscount());
            jobModel.setSpecialPrice(job.getSpecialPrice());
            jobModel.setSellingPrice(job.getSellingPrice());
            jobModel.setUpdateCounter(job.getUpdateCount().toString());
            jobModel.setTaxRate(job.getTaxRate());
            jobModel.setServiceOrderItemOtherBrandId(job.getServiceOrderItemOtherBrandId());

            result.add(jobModel);
        }

        return result;
    }

    private SVM012001BO generateServiceMainInfo(ServiceOrderVO serviceOrder) {

        SVM012001BO result = new SVM012001BO();

        result.setServiceOrderId(serviceOrder.getServiceOrderId());
        result.setPointId(serviceOrder.getFacilityId());
        result.setPointCd(serviceOrder.getFacilityCd());
        result.setPointNm(serviceOrder.getFacilityNm());
        result.setOrderNo(serviceOrder.getOrderNo());
        result.setOrderDate(serviceOrder.getOrderDate());
        result.setOrderStatus(serviceOrder.getOrderStatusContent());
        result.setOrderStatusId(serviceOrder.getOrderStatusId());

        result.setPlateNo(serviceOrder.getPlateNo());
        result.setFrameNo(serviceOrder.getFrameNo());
        result.setModel(serviceOrder.getModelNm());
        result.setModelTypeId(serviceOrder.getModelTypeId());
        result.setBrandId(serviceOrder.getBrandId().toString());
        result.setConsumerId(serviceOrder.getConsumerId());
        result.setLastName(serviceOrder.getLastNm());
        result.setMiddleName(serviceOrder.getMiddleNm());
        result.setFirstName(serviceOrder.getFirstNm());
        result.setMobilephone(serviceOrder.getMobilePhone());
        result.setEmail(serviceOrder.getEmail());
        result.setPolicyResultFlag(orderCmmMethod.getConsumerPolicyInfo(serviceOrder.getSiteId(), serviceOrder.getLastNm(), serviceOrder.getMiddleNm(), serviceOrder.getFirstNm(), serviceOrder.getMobilePhone()));

        result.setMileage(serviceOrder.getMileage());
        result.setServiceTitle(serviceOrder.getServiceSubject());

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

        result.setDepositAmt(serviceOrder.getPaymentAmt());
        result.setPaymentMethodId(serviceOrder.getPaymentMethodId());

        result.setUpdateCounter(serviceOrder.getUpdateCount().toString());

        return result;
    }

    private void validateServiceOrderForInit(ServiceOrderVO serviceOrder, String orderNo, Long pointId) {

        //当orderNo在DB查询不到数据时,当order不属于当前point时,报错
        if (Objects.isNull(serviceOrder) || serviceOrder.getFacilityId().compareTo(pointId) != 0) {
            throw new BusinessCodedException(ComUtil.t("M.E.00303", new String[] {ComUtil.t("label.serviceOrderNumber"), orderNo, ComUtil.t("title.serviceOrder_01")}));
        }

        //当serviceOrder 属于Claim for battery
        if (StringUtils.equals(serviceOrder.getServiceCategoryId(), ServiceCategory.CLAIMBATTERY.getCodeDbid())) {

            throw new BusinessCodedException(ComUtil.t("M.E.10502", new String[] {ComUtil.t("label.serviceOrderNumber"), serviceOrder.getOrderNo()}));
        }

        //当serviceOrder 属于0km service
        if (StringUtils.equals(serviceOrder.getZeroKmFlag(), CommonConstants.CHAR_Y)) {

            throw new BusinessCodedException(ComUtil.t("M.E.10501", new String[] {ComUtil.t("label.serviceOrderNumber"), serviceOrder.getOrderNo()}));
        }

        //当serviceOrder 属于YAMAHA brand时
        if (StringUtils.equals(serviceOrder.getBrandId().toString(), BrandType.YAMAHA.getCodeDbid())) {

            throw new BusinessCodedException(ComUtil.t("M.E.10226", new String[] {ComUtil.t("label.serviceOrderNumber"), serviceOrder.getOrderNo()}));
        }
    }
}
