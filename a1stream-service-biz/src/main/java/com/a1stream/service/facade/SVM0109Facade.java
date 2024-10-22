package com.a1stream.service.facade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.common.constants.MstCodeConstants.SalesOrderStatus;
import com.a1stream.common.constants.PJConstants.BatteryType;
import com.a1stream.common.constants.PJConstants.BrandType;
import com.a1stream.common.constants.PJConstants.DropShipType;
import com.a1stream.common.constants.PJConstants.OrgRelationType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.SalesOrderPriorityType;
import com.a1stream.common.constants.PJConstants.SdStockStatus;
import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.common.constants.PJConstants.ServiceOrderStatus;
import com.a1stream.common.constants.PJConstants.WarrantyPolicyType;
import com.a1stream.common.facade.ConsumerFacade;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.common.logic.ValidateLogic;
import com.a1stream.common.model.BaseConsumerForm;
import com.a1stream.common.model.BaseTableData;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.common.CmmConsumerBO;
import com.a1stream.domain.bo.service.SVM010901BO;
import com.a1stream.domain.bo.service.SVM0109PrintBO;
import com.a1stream.domain.bo.service.SituationBO;
import com.a1stream.domain.form.service.SVM010901Form;
import com.a1stream.domain.parameter.service.ConsumerPolicyParameter;
import com.a1stream.domain.parameter.service.SVM010901Parameter;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmRegistrationDocumentVO;
import com.a1stream.domain.vo.CmmWarrantyBatteryVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.ServiceOrderBatteryVO;
import com.a1stream.domain.vo.ServiceOrderFaultVO;
import com.a1stream.domain.vo.ServiceOrderVO;
import com.a1stream.service.service.SVM0109Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.report.exporter.PdfReportExporter;
import com.ymsl.solid.web.download.DownloadResponseView;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

@Component
public class SVM0109Facade {

    @Resource
    private SVM0109Service svm0109Service;

    @Resource
    private OrderCmmMethod orderCmmMethod;

    @Resource
    private ConsumerFacade consumerFacade;

    @Resource
    private ValidateLogic validLogic;

    @Resource
    private HelperFacade constantsFac;

    @Resource
    private PdfReportExporter pdfExporter;

    private static String COMM_SITE = CommonConstants.CHAR_DEFAULT_SITE_ID;

    public SVM010901BO getOrderDetail(Long pointId, Long orderId, String orderNo, PJUserDetails uc) {

        SVM010901BO result;
        if (orderId == null && StringUtils.isBlank(orderNo)) {
            result = initForNew(uc);
        } else {
            result = initForUpdate(pointId, orderId, orderNo, uc);
        }

        return result;
    }

    public Long saveOrderInfo(SVM010901Form form, PJUserDetails uc) {

        String action = form.getAction();
        SVM010901BO orderBO = form.getOrderInfo();
        BaseTableData<SituationBO> situations = form.getSituations();
        if (orderBO.getServiceOrderId() == null) {
            action = CommonConstants.OPERATION_STATUS_NEW;
        }
        switch(action) {
            case CommonConstants.OPERATION_STATUS_NEW:

                registOrder(orderBO, situations, uc);
                break;

            case CommonConstants.OPERATION_STATUS_UPDATE:

                updateOrder(orderBO, situations, uc);
                break;
            case CommonConstants.OPERATION_STATUS_FINISH:

                settleOrder(orderBO, situations, uc);
                break;

            case CommonConstants.OPERATION_STATUS_CANCEL:

                cancelOrder(orderBO, uc);
                break;
        }

        return orderBO.getServiceOrderId();
    }

    public SVM010901BO downloadBattery(SVM010901BO orderBO, PJUserDetails uc) {

        String batteryNo = orderBO.getBatteryNo();
        String siteId = uc.getDealerCode();

        CmmBatteryVO cmmBattery = orderCmmMethod.cmmBatteryExistence(batteryNo, siteId);
        orderBO.setCmmBatteryId(cmmBattery.getBatteryId());
        BatteryVO batteryInfo = orderCmmMethod.findBatteryByNo(batteryNo, siteId);
        MstProductVO product;
        BatteryVO insertBattery = null;
        if (batteryInfo == null) {
            // build new battery to DB
            insertBattery = BatteryVO.copyFromCmm(cmmBattery, siteId, null);

            product = orderCmmMethod.findProductByCd(cmmBattery.getBatteryCd(), COMM_SITE);

            orderBO.setSoldDate(cmmBattery.getSaleDate());
            orderBO.setBatteryId(insertBattery.getBatteryId());
        } else {
            orderBO.setBatteryId(batteryInfo.getBatteryId());
            CmmRegistrationDocumentVO regDocForBattery
            = orderCmmMethod.findRegDocForBattery(orderBO.getBatteryId(), siteId);


            product = orderCmmMethod.findProductByCd(batteryInfo.getBatteryCd(), COMM_SITE);

            if (regDocForBattery != null) {
                orderBO.setSoldDate(regDocForBattery.getRegistrationDate());
                CmmConsumerBO consumer = orderCmmMethod.findConsumerPrivateDetailByConsumerId(regDocForBattery.getConsumerId(), siteId);
                if (consumer != null) {

                    ConsumerPolicyParameter policyParam
                        = new ConsumerPolicyParameter(consumer.getConsumerId(), uc.getDealerCode()
                                , consumer.getLastNm(), consumer.getMiddleNm(), consumer.getFirstNm()
                                , consumer.getMobilePhone(), consumer.getEmail());
                    setConsumerValue2BO(policyParam, orderBO);
                }
            } else {
                orderBO.setSoldDate(batteryInfo.getSaleDate());
            }
        }

        if (product != null) {
            orderBO.setPartsId(product.getProductId());
            orderBO.setPartsCd(product.getProductCd());
            orderBO.setPartsNm(product.getSalesDescription());
            orderBO.setParts(ComUtil.concatFull(orderBO.getPartsCd(), orderBO.getPartsNm()));
        }

        CmmWarrantyBatteryVO batteryWarranty = orderCmmMethod.findCmmWarrantyBatteryById(orderBO.getCmmBatteryId());
        if (batteryWarranty != null
                && StringUtils.equals(batteryWarranty.getWarrantyProductClassification(), WarrantyPolicyType.BATTERY.getCodeDbid())) {

            orderBO.setWarrantyTerm(ComUtil.t("M.E.10308"));
            orderBO.setNewWarrantyTerm(ComUtil.t("M.E.10308"));
        }
        // 追加BatteryInfo数据
        if (insertBattery != null) {
            svm0109Service.insertBatteryInfo(insertBattery);
        }

        return orderBO;
    }

    private void registOrder(SVM010901BO orderBO, BaseTableData<SituationBO> situations, PJUserDetails uc) {

        SVM010901Parameter params = new SVM010901Parameter();

        params.setPointId(orderBO.getPointId());
        params.setSiteId(uc.getDealerCode());
        params.setAction(CommonConstants.OPERATION_STATUS_NEW);

        // 校验
        validBeforeSaveOrder(orderBO, situations, uc, params);

        /**
         * 单据数据组装
         */
        ServiceOrderVO serviceOrderVO = ServiceOrderVO.create();
        Long serviceOrderId = serviceOrderVO.getServiceOrderId();
        SalesOrderVO salesOrderVO = SalesOrderVO.create();
        ServiceOrderBatteryVO serviceBatteryVO = ServiceOrderBatteryVO.create(uc.getDealerCode(), serviceOrderId);

        prepareOrderData(orderBO, situations, serviceOrderVO, salesOrderVO, serviceBatteryVO, params, uc);

        // 更新到表
        svm0109Service.maintainOrderData(params);

        orderBO.setServiceOrderId(serviceOrderId);
    }

    private void updateOrder(SVM010901BO orderBO, BaseTableData<SituationBO> situations, PJUserDetails uc) {

        ServiceOrderVO serviceOrderVO = validServiceOrderExist(orderBO);

        SVM010901Parameter params = new SVM010901Parameter();

        params.setPointId(orderBO.getPointId());
        params.setSiteId(uc.getDealerCode());
        params.setAction(CommonConstants.OPERATION_STATUS_UPDATE);

        // 校验
        validBeforeSaveOrder(orderBO, situations, uc, params);

        /**
         * 单据数据组装
         */
        Long serviceOrderId = serviceOrderVO.getServiceOrderId();
        SalesOrderVO salesOrderVO = orderCmmMethod.findSalesOrderById(serviceOrderVO.getRelatedSalesOrderId());
        ServiceOrderBatteryVO serviceBatteryVO = orderCmmMethod.timelySearchOrderBattery(serviceOrderId);

        prepareOrderData(orderBO, situations, serviceOrderVO, salesOrderVO, serviceBatteryVO, params, uc);

        // 更新到表
        svm0109Service.maintainOrderData(params);
    }

    private void settleOrder(SVM010901BO orderBO, BaseTableData<SituationBO> situations, PJUserDetails uc) {

        ServiceOrderVO serviceOrderVO = validServiceOrderExist(orderBO);

        SVM010901Parameter params = new SVM010901Parameter();

        params.setPointId(orderBO.getPointId());
        params.setSiteId(uc.getDealerCode());
        params.setAction(CommonConstants.OPERATION_STATUS_FINISH);

        // 校验
        validBeforeSaveOrder(orderBO, situations, uc, params);

        /**
         * 单据数据组装
         */
        Long serviceOrderId = serviceOrderVO.getServiceOrderId();
        SalesOrderVO salesOrderVO = orderCmmMethod.findSalesOrderById(serviceOrderVO.getRelatedSalesOrderId());
        ServiceOrderBatteryVO serviceBatteryVO = orderCmmMethod.timelySearchOrderBattery(serviceOrderId);

        prepareOrderData(orderBO, situations, serviceOrderVO, salesOrderVO, serviceBatteryVO, params, uc);

        // 更新到表
        svm0109Service.settleOderData(params, uc);
    }

    private void cancelOrder(SVM010901BO orderBO, PJUserDetails uc) {

        ServiceOrderVO serviceOrderVO = validServiceOrderExist(orderBO);

        SVM010901Parameter params = new SVM010901Parameter();

        params.setPointId(orderBO.getPointId());
        params.setSiteId(uc.getDealerCode());
        params.setAction(CommonConstants.OPERATION_STATUS_CANCEL);

        serviceOrderVO.setOrderStatusId(ServiceOrderStatus.CANCELLED.getCodeDbid());
        serviceOrderVO.setOrderStatusContent(ServiceOrderStatus.CANCELLED.getCodeData1());

        SalesOrderVO salesOrderVO = orderCmmMethod.findSalesOrderById(serviceOrderVO.getRelatedSalesOrderId());
        salesOrderVO.setOrderStatus(SalesOrderStatus.CANCELLED);

        SalesOrderItemVO salesOrderItemVO = orderCmmMethod.timelySearchSoItemNo0ActQty(salesOrderVO.getSalesOrderId());
        List<SalesOrderItemVO> salesOrderItemList = new ArrayList<>();
        salesOrderItemList.add(salesOrderItemVO);

        Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = orderCmmMethod.prepareServiceOrderPartListForCancel(salesOrderItemList, params.getSiteId(), params.getPointId());
        params.setSalesOrderItemVO(salesOrderItemList);
        params.setStockStatusVOChangeMap(stockStatusVOChangeMap);

        // 更新到表
        svm0109Service.cancelOrderData(params);
    }

    /**
     * 单据数据组装
     */
    private void prepareOrderData(SVM010901BO orderBO, BaseTableData<SituationBO> situations,
            ServiceOrderVO serviceOrderVO, SalesOrderVO salesOrderVO, ServiceOrderBatteryVO serviceBatteryVO,
            SVM010901Parameter params, PJUserDetails uc) {

        String siteId = params.getSiteId();
        String action = params.getAction();
        Long serviceOrderId = serviceOrderVO.getServiceOrderId();

        if (StringUtils.equals(action, CommonConstants.OPERATION_STATUS_FINISH)) {
            prepareBatteryChange(orderBO, params);
            prepareConsumerBasicInfoForSettle(orderBO, params);
        }
        buildServiceOrderVO(serviceOrderVO, orderBO, uc, action, params);
        buildSalesOrder(salesOrderVO, orderBO, uc, action, params);
        buildCmmBattery(orderBO, params.getSiteId(), params);
        buildServiceOrderBattery(serviceBatteryVO, orderBO, params);
        //电池状况
        List<Long> removeFaultIds = situations.getRemoveRecords().stream().map(SituationBO::getServiceOrderFaultId).toList();
        List<ServiceOrderFaultVO> saveFaultList = orderCmmMethod.prepareNewOrUpdateFaultList(situations, siteId, serviceOrderId);
        params.setSaveFaultList(saveFaultList);
        params.setRemoveFaultIds(removeFaultIds);

        // 配件
        SalesOrderItemVO newSalesOrderItemVO = null;
        if (StringUtils.equals(action, CommonConstants.OPERATION_STATUS_NEW)) {
            newSalesOrderItemVO = SalesOrderItemVO.create(siteId, salesOrderVO.getSalesOrderId());
            List<SalesOrderItemVO> salesOrderItems = buildSalesOrderItem(newSalesOrderItemVO, 0, orderBO);
            params.setSalesOrderItemVO(salesOrderItems);
        } else {
            SalesOrderItemVO salesOrderItemVO = orderCmmMethod.timelySearchSoItemNo0ActQty(salesOrderVO.getSalesOrderId());
            // new parts changed
            Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = new HashMap<>();

            Long partsId = salesOrderItemVO.getProductId();
            List<SalesOrderItemVO> salesOrderItemList = new ArrayList<>();
            if(partsId != null && !partsId.equals(orderBO.getNewPartsId())) {
                //处理新增行：新增新配件
                int seqNo = salesOrderItemVO != null? salesOrderItemVO.getSeqNo() + 1 : CommonConstants.INTEGER_ZERO;
                newSalesOrderItemVO = SalesOrderItemVO.create(siteId, salesOrderVO.getSalesOrderId());
                List<SalesOrderItemVO> salesOrderItems = buildSalesOrderItem(newSalesOrderItemVO, seqNo, orderBO);
                salesOrderItemList.addAll(salesOrderItems);

                //处理删除行：删除原配件
                if (salesOrderItemVO != null) {
                    orderCmmMethod.doPartDetailDelete(siteId, orderBO.getPointId(), stockStatusVOChangeMap, salesOrderItemVO);
                    salesOrderItemList.add(salesOrderItemVO);
                }
            }

            params.setSalesOrderItemVO(salesOrderItemList);
            params.setStockStatusVOChangeMap(stockStatusVOChangeMap);
        }



        // 隐私政策上传
        ConsumerPolicyParameter policyParam = new ConsumerPolicyParameter(siteId
                                                                        , orderBO.getPointCd()
                                                                        , orderBO.getLastName()
                                                                        , orderBO.getMiddleName()
                                                                        , orderBO.getFirstName()
                                                                        , orderBO.getMobilephone()
                                                                        , orderBO.getPolicyResultFlag()
                                                                        , orderBO.getPolicyFileName());
        params.setPolicyResultVO(consumerFacade.prepareConsumerPrivacyPolicyResult(policyParam));

        //外键赋值
        setForeignKey(serviceOrderVO, params, serviceOrderId, salesOrderVO, newSalesOrderItemVO);
    }

    private void prepareBatteryChange(SVM010901BO model, SVM010901Parameter params) {

        // 修改Common旧电池
        CmmBatteryVO oldCmmBattery = this.prepareOldCmmBattery(model, params);
        // 追加Common新电池
        CmmBatteryVO newCmmBatteryVO = this.prepareNewCmmBattery(oldCmmBattery, model, params);

        // 修改Local旧电池
        this.prepareOldLocBattery(params, model);
        // 追加Local新电池
        BatteryVO newBatteryVO = BatteryVO.copyFromCmm(newCmmBatteryVO, params.getSiteId(), null);
        params.getBatteryList().add(newBatteryVO);
        model.setNewBatteryId(newCmmBatteryVO.getBatteryId());
        // 新增 cmm_registration_document_for_battery
        prepareCmmRegistDocumentForBattery(model, params, newBatteryVO.getBatteryId());

        // TODO  sendToYNSPIRE
    }


    private void prepareConsumerBasicInfoForSettle(SVM010901BO model, SVM010901Parameter params){

        BaseConsumerForm consumerModel = new BaseConsumerForm();

        consumerModel.setSiteId(params.getSiteId());
        consumerModel.setLastNm(model.getLastName());
        consumerModel.setMiddleNm(model.getMiddleName());
        consumerModel.setFirstNm(model.getFirstName());
        consumerModel.setMobilePhone(model.getMobilephone());
        consumerModel.setEmail(model.getEmail());
        consumerModel.setConsumerId(model.getConsumerId());

        params.setConsumerBaseInfo(consumerModel);
    }

    private void prepareCmmRegistDocumentForBattery(SVM010901BO model, SVM010901Parameter params, Long batteryId) {

        CmmRegistrationDocumentVO result = new CmmRegistrationDocumentVO();

        String nowDate = ComUtil.nowLocalDate();
        String nowTime = ComUtil.nowTime();

        result.setSiteId(params.getSiteId());
        result.setRegistrationDate(nowDate);
        result.setRegistrationTime(nowTime);
        result.setFacilityId(params.getPointId());
        result.setConsumerId(model.getConsumerId());
        result.setServiceOrderId(model.getServiceOrderId());
        result.setBatteryId(batteryId);

        params.setCmmRegistDocument(result);
    }

    private CmmBatteryVO prepareOldCmmBattery(SVM010901BO model, SVM010901Parameter params) {

        CmmBatteryVO result = orderCmmMethod.findCmmBatteryByNo(model.getBatteryNo());
        result.setToDate(ComUtil.date2str(LocalDate.now().minusDays(1)));

        params.getCmmBatteryList().add(result);

        return result;
    }

    private void prepareOldLocBattery(SVM010901Parameter params, SVM010901BO model) {

        BatteryVO result = orderCmmMethod.findBatteryByNo(model.getBatteryNo(), params.getSiteId());

        if (!Objects.isNull(result)) {

            result.setToDate(ComUtil.date2str(LocalDate.now().minusDays(1)));

            params.getBatteryList().add(result);
        }
    }

    private CmmBatteryVO prepareNewCmmBattery(CmmBatteryVO oldCmmBattery, SVM010901BO orderBO, SVM010901Parameter params) {

        CmmBatteryVO result = CmmBatteryVO.create();

        String now = ComUtil.nowLocalDate();

        result.setBatteryNo(orderBO.getNewBatteryNo());
        result.setBatteryCd(orderBO.getNewPartsCd());
        result.setBatteryStatus(SdStockStatus.SHIPPED.getCodeDbid());
        result.setServiceCalculateDate(oldCmmBattery.getServiceCalculateDate());
        result.setSellingPrice(orderBO.getRetailPrice());
        result.setSaleDate(now);
        result.setOriginalFlag(CommonConstants.CHAR_ONE);
        result.setPositionSign(BatteryType.TYPE1.getCodeDbid());
        result.setServiceCalculateDate(now);
        result.setFromDate(now);
        result.setToDate(CommonConstants.MAX_DATE);
        result.setProductId(orderBO.getNewPartsId());
        result.setSiteId(CommonConstants.CHAR_BLANK);

        params.getCmmBatteryList().add(result);

        return result;
    }

    private void setForeignKey(ServiceOrderVO serviceOrderVO, SVM010901Parameter params, Long serviceOrderId,
            SalesOrderVO salesOrderVO, SalesOrderItemVO salesOrderItemVO) {
        //外键赋值
        serviceOrderVO.setRelatedSalesOrderId(salesOrderVO.getSalesOrderId());
        salesOrderVO.setRelatedSvOrderId(serviceOrderId);

        if (salesOrderItemVO != null) {
            List<SituationBO> situationBOList = params.getSituationCheckedList();
            SituationBO situation = situationBOList.get(0);
            salesOrderItemVO.setSymptomId(situation.getSymptomId());
            salesOrderItemVO.setServiceOrderFaultId(situation.getServiceOrderFaultId());
        }
    }

    private void buildCmmBattery(SVM010901BO model, String siteId, SVM010901Parameter params) {

        CmmBatteryVO cmmBatteryVO = orderCmmMethod.findCmmBatteryByNo(model.getBatteryNo());
        cmmBatteryVO.setServiceCalculateDate(model.getSoldDate());

        params.getCmmBatteryList().add(cmmBatteryVO);
    }

    private void buildServiceOrderBattery(ServiceOrderBatteryVO result, SVM010901BO model, SVM010901Parameter params) {

        result.setBatteryType(BatteryType.TYPE1.getCodeDbid());
        result.setProductId(model.getPartsId());
        result.setProductCd(model.getPartsCd());
        result.setProductNm(model.getPartsNm());
        result.setBatteryId(model.getCmmBatteryId());
        result.setBatteryNo(model.getBatteryNo());
        result.setWarrantyTerm(model.getWarrantyTerm());
        result.setWarrantyStartDate(model.getWarrantyStartDate());
        result.setNewProductId(model.getNewPartsId());
        result.setNewProductCd(model.getNewPartsCd());
        result.setNewProductNm(model.getNewPartsNm());
        result.setNewBatteryNo(model.getNewBatteryNo());
        result.setNewBatteryId(model.getNewBatteryId());
        result.setSellingPrice(model.getRetailPrice());
        result.setOrderQty(StringUtils.isNotBlank(model.getNewBatteryNo()) ? BigDecimal.ONE : BigDecimal.ZERO);

        params.setServiceBatteryVO(result);
    }

    private List<SalesOrderItemVO> buildSalesOrderItem(SalesOrderItemVO result, int seqNo, SVM010901BO model) {

        List<SalesOrderItemVO> salesOrderItemList = new ArrayList<>();

        result.setProductId(model.getNewPartsId());
        result.setProductCd(model.getNewPartsCd());
        result.setProductNm(model.getNewPartsNm());
        result.setAllocatedProductId(model.getNewPartsId());
        result.setAllocatedProductCd(model.getNewPartsCd());
        result.setAllocatedProductNm(model.getNewPartsNm());
        result.setProductClassification(ProductClsType.PART.getCodeDbid());
        result.setStandardPrice(model.getRetailPrice());
        result.setSellingPrice(model.getRetailPrice());
        result.setServiceCategoryId(model.getServiceCategoryId());
        result.setSeqNo(seqNo);
        result.setOrderQty(BigDecimal.ONE);
        result.setActualQty(BigDecimal.ONE);
        result.setBatteryFlag(CommonConstants.CHAR_Y);
        // KEY_PARTSSOBATTERYTYPEFORSV \ KEY_PARTSSOCALCULATESELLINGPRICE
        result.setSvClaimFlag(CommonConstants.CHAR_N);
        result.setBoCancelFlag(CommonConstants.CHAR_N);
        result.setWaitingAllocateQty(BigDecimal.ONE);
        result.setSellingPriceNotVat(model.getSellingPriceNotVat());
        result.setActualAmtNotVat(model.getActualAmtNotVat());
        result.setBatteryType(BatteryType.TYPE1.getCodeDbid());

        salesOrderItemList.add(result);

        return salesOrderItemList;
    }

    private void buildSalesOrder(SalesOrderVO result, SVM010901BO model
                                , PJUserDetails uc, String action
                                , SVM010901Parameter params) {

        if (StringUtils.equals(action, CommonConstants.OPERATION_STATUS_NEW)) {

            result.setSiteId(uc.getDealerCode());
            result.setOrderDate(model.getOrderDate());
            result.setOrderStatus(SalesOrderStatus.SP_WAITINGALLOCATE);
            result.setAllocateDueDate(result.getOrderDate());
            result.setFacilityId(model.getPointId());
            result.setProductClassification(ProductClsType.PART.getCodeDbid());
            result.setOrderPriorityType(SalesOrderPriorityType.SOEO.getCodeDbid());
            result.setOrderSourceType(ProductClsType.SERVICE.getCodeDbid());
            result.setEntryFacilityId(result.getFacilityId());
            result.setEntryPicId(uc.getPersonId());
            result.setEntryPicNm(uc.getPersonName());

            result.setOrderToType(OrgRelationType.CONSUMER.getCodeDbid());
            result.setDropShipType(DropShipType.NOTDROPSHIP);
            result.setBoCancelFlag(CommonConstants.CHAR_N);
            result.setDemandExceptionFlag(CommonConstants.CHAR_N);
            result.setAccEoFlag(CommonConstants.CHAR_N);
            result.setBoFlag(CommonConstants.CHAR_Y);
            result.setCustomerIfFlag(CommonConstants.CHAR_N);
            result.setShipCompleteFlag(CommonConstants.CHAR_Y);
        }

        result.setSalesPicId(uc.getPersonId());
        result.setSalesPicNm(uc.getPersonName());

        result.setCmmConsumerId(model.getConsumerId());
        result.setMobilePhone(model.getMobilephone());
        result.setConsumerNmFirst(model.getFirstName());
        result.setConsumerNmMiddle(model.getMiddleName());
        result.setConsumerNmLast(model.getLastName());
        String fullNm = orderCmmMethod.getConsumerFullNm(model.getLastName(), model.getMiddleName(), model.getFirstName());
        result.setConsumerNmFull(fullNm);
        result.setEmail(model.getEmail());

        params.setSalesOrderVO(result);
    }

    private void buildServiceOrderVO(ServiceOrderVO result, SVM010901BO orderBO
                                    , PJUserDetails uc, String action
                                    , SVM010901Parameter params) {

        Map<String, String> codeMap = constantsFac.getMstCodeInfoMap(ServiceCategory.CODE_ID);

        if (StringUtils.equals(action, CommonConstants.OPERATION_STATUS_NEW)) {

            result.setSiteId(uc.getDealerCode());
            result.setFacilityId(orderBO.getPointId());
            result.setFacilityCd(orderBO.getPointCd());
            result.setFacilityNm(orderBO.getPointNm());
            result.setOrderDate(orderBO.getOrderDate());
            result.setOrderStatusId(ServiceOrderStatus.WAITFORSETTLE.getCodeDbid());
            result.setOrderStatusContent(ServiceOrderStatus.WAITFORSETTLE.getCodeData1());
            result.setServiceCategoryId(orderBO.getServiceCategoryId());
            result.setServiceCategoryContent(codeMap.get(orderBO.getServiceCategoryId()));
            result.setZeroKmFlag(CommonConstants.CHAR_N);
            result.setStartTime(ComUtil.str2DateTime(orderBO.getStartTime()));
            result.setEntryPicId(uc.getPersonId());
            result.setEntryPicCd(uc.getPersonCode());
            result.setEntryPicNm(uc.getPersonName());


            result.setBrandId(Long.parseLong(BrandType.YAMAHA.getCodeDbid()));
            result.setBrandContent(BrandType.YAMAHA.getCodeData1());
            result.setSoldDate(orderBO.getSoldDate());
        }

        if (StringUtils.equals(action, CommonConstants.OPERATION_STATUS_FINISH)) {

            result.setSettleDate(ComUtil.nowLocalDate());
            result.setSettleTime(LocalDateTime.now());
            result.setOrderStatusId(ServiceOrderStatus.COMPLETED.getCodeDbid());
            result.setOrderStatusContent(ServiceOrderStatus.COMPLETED.getCodeData1());

            result.setCashierId(uc.getPersonId());
            result.setCashierCd(uc.getPersonCode());
            result.setCashierNm(uc.getPersonName());

            // AR
            result.setPartsAmt(orderBO.getRetailPrice());
        }

//        result.setServiceSubject(orderBO.getServiceTitle());
        result.setOperationStartTime(StringUtils.isBlank(orderBO.getOperationStart())? null : ComUtil.str2DateTime(orderBO.getOperationStart()));
        result.setOperationFinishTime(StringUtils.isBlank(orderBO.getOperationFinish())? null : ComUtil.str2DateTime(orderBO.getOperationFinish()));

//        result.setMechanicId(orderBO.getMechanicId());
//        result.setMechanicCd(orderBO.getMechanicCd());
//        result.setMechanicNm(orderBO.getMechanicNm());

        result.setWelcomeStaffId(orderBO.getWelcomeStaffId());
        result.setWelcomeStaffCd(orderBO.getWelcomeStaffCd());
        result.setWelcomeStaffNm(orderBO.getWelcomeStaffNm());

        result.setConsumerId(orderBO.getConsumerId());
        result.setConsumerVipNo(Objects.isNull(orderBO.getConsumerId()) ? null : orderCmmMethod.findCmmConsumerById(orderBO.getConsumerId()).getVipNo());
        result.setLastNm(orderBO.getLastName());
        result.setMiddleNm(orderBO.getMiddleName());
        result.setFirstNm(orderBO.getFirstName());
        result.setMobilePhone(orderBO.getMobilephone());
        result.setEmail(orderBO.getEmail());
        String fullNm = orderCmmMethod.getConsumerFullNm(orderBO.getLastName(), orderBO.getMiddleName(), orderBO.getFirstName());
        result.setConsumerFullNm(fullNm);

//        result.setPaymentMethodId(orderBO.getPaymentMethodId());
//        result.setDepositAmt(orderBO.getDepositAmt());

        params.setServiceOrderVO(result);
    }

    private ServiceOrderVO validServiceOrderExist(SVM010901BO orderBO) {

        ServiceOrderVO serviceOrderVO = orderCmmMethod.orderExistence(orderBO.getServiceOrderId()
                                                                    , orderBO.getOrderNo()
                                                                    , orderBO.getUpdateCounter());
        return serviceOrderVO;
    }

    private void validBeforeSaveOrder(SVM010901BO orderBO
                                    , BaseTableData<SituationBO> situations
                                    , PJUserDetails uc
                                    , SVM010901Parameter params) {

        String action = params.getAction();

        // 电池ID
        validBatteryInput(orderBO, situations, uc.getDealerCode(), action);
        // 操作时间
        orderCmmMethod.validateOperationTime(orderBO.getStartTime(), orderBO.getOperationStart(), orderBO.getOperationFinish(), action);
        // 姓名
        validLogic.validateIsRequired(orderBO.getLastName(), ComUtil.t("label.consumerName"));
        validLogic.validateIsRequired(orderBO.getFirstName(), ComUtil.t("label.consumerName"));
        // 手机号
        validLogic.validateIsRequired(orderBO.getMobilephone(), ComUtil.t("label.mobilephone"));
        orderCmmMethod.validateMobilePhone(orderBO.getMobilephone());
        // 服务类型
        validLogic.validateIsRequired(orderBO.getServiceCategoryId(), ComUtil.t("label.serviceCategory"));
        // 销售日期
        if (StringUtils.isNotBlank(orderBO.getSoldDate())) {
            if (orderBO.getSoldDate().compareTo(ComUtil.nowLocalDate()) > 0) {
                throw new BusinessCodedException(ComUtil.t("M.E.00207", new String[] {ComUtil.t("label.soldDate"), ComUtil.t("label.sysDate")}));
            }
        }
        // DO店的额外字段
        if (StringUtils.equals(uc.getDoFlag(), CommonConstants.CHAR_Y)) {
            validLogic.validateIsRequired(orderBO.getEmail(), ComUtil.t("label.email"));
            validLogic.validateEmail(orderBO.getEmail());
            validLogic.validateIsRequired(orderBO.getWelcomeStaffId(), ComUtil.t("label.welcomeStaff"));
        }
        // 新配件
        if (!StringUtils.equals(orderBO.getBatteryFlag(), CommonConstants.CHAR_Y)) {
            throw new BusinessCodedException(ComUtil.t("M.E.10327"));
        }
        validLogic.validateIsRequired(orderBO.getNewPartsCd(), ComUtil.t("label.newPart"));
        validLogic.validateEntityNotExist(orderBO.getNewPartsCd(), orderBO.getNewPartsId(), ComUtil.t("label.newPart"));

        params.setSituationCheckedList(situations.getNewUpdateRecords());
    }

    private SVM010901BO initForNew(PJUserDetails uc) {

        SVM010901BO result = new SVM010901BO();

        result.setPointId(uc.getDefaultPointId());
        result.setPointCd(uc.getDefaultPointCd());
        result.setPointNm(uc.getDefaultPointNm());
        result.setOrderDate(ComUtil.nowLocalDate());
        result.setOrderStatus(ServiceOrderStatus.NEW.getCodeData1());
        result.setOrderStatusId(ServiceOrderStatus.NEW.getCodeDbid());

        result.setEditor(ComUtil.concatFull(uc.getPersonCode(), uc.getPersonName()));
        result.setStartTime(ComUtil.nowDateTime());
        result.setDoFlag(uc.getDoFlag());

        result.setSituations(new ArrayList<SituationBO>());
        return result;
    }

    private SVM010901BO initForUpdate(Long pointId,Long orderId, String orderNo, PJUserDetails uc) {

        // 服务单数据
        ServiceOrderVO serviceOrder = orderCmmMethod.timelySearchBatteryClaimOrder(pointId, orderId, orderNo, uc.getDealerCode());
        //当orderNo在DB查询不到数据时,报错
        if (Objects.isNull(serviceOrder)) {
            throw new BusinessCodedException(ComUtil.t("M.E.00303", new String[] {ComUtil.t("label.serviceOrderNumber"), orderNo, ComUtil.t("title.serviceOrder_01")}));
        }
        Long serviceOrderId = serviceOrder.getServiceOrderId();
        // 索赔电池数据
        ServiceOrderBatteryVO orderBattery = orderCmmMethod.timelySearchOrderBattery(serviceOrderId);
        // 电池状态明细
        List<SituationBO> orderFaults = orderCmmMethod.timelySearchOrderFaults(serviceOrderId);
        // 新配件的预定、缺货数量
        SalesOrderItemVO soItem = orderCmmMethod.timelySearchSoItemNo0ActQty(serviceOrder.getRelatedSalesOrderId());

        // 赋值到画面BO
        SVM010901BO result = this.generateOrderBO(serviceOrder, orderBattery, orderFaults, soItem, uc);

        return result;
    }

    private SVM010901BO generateOrderBO(ServiceOrderVO serviceOrder
                                        , ServiceOrderBatteryVO orderBattery
                                        , List<SituationBO> orderFaults
                                        , SalesOrderItemVO soItem
                                        , PJUserDetails uc) {

        SVM010901BO result = BeanMapUtils.mapTo(serviceOrder, SVM010901BO.class);

        // 服务单数据
        result.setPointId(serviceOrder.getFacilityId());
        result.setPointCd(serviceOrder.getFacilityCd());
        result.setPointNm(serviceOrder.getFacilityNm());
        result.setOrderStatus(serviceOrder.getOrderStatusContent());

        ConsumerPolicyParameter policyParam
            = new ConsumerPolicyParameter(serviceOrder.getConsumerId(), uc.getDealerCode()
                    , serviceOrder.getLastNm(), serviceOrder.getMiddleNm(), serviceOrder.getFirstNm()
                    , serviceOrder.getMobilePhone(), serviceOrder.getEmail());
        setConsumerValue2BO(policyParam, result);

//        result.setServiceTitle(serviceOrder.getServiceSubject());

        if (!Objects.isNull(serviceOrder.getEntryPicId())) {
            String entryPicCd = Objects.requireNonNullElse(serviceOrder.getEntryPicCd(), CommonConstants.CHAR_BLANK);
            String entryPicNm = Objects.requireNonNullElse(serviceOrder.getEntryPicNm(), CommonConstants.CHAR_BLANK);
            result.setEditor(ComUtil.concatFull(entryPicCd, entryPicNm));
        }

        if (!Objects.isNull(serviceOrder.getCashierId())) {
            String cashierCd = Objects.requireNonNullElse(serviceOrder.getCashierCd(), CommonConstants.CHAR_BLANK);
            String cashierNm = Objects.requireNonNullElse(serviceOrder.getCashierNm(), CommonConstants.CHAR_BLANK);
            result.setCashier(ComUtil.concatFull(cashierCd, cashierNm));
        }

        result.setStartTime(ComUtil.formatDateTime(serviceOrder.getStartTime()));
        result.setOperationStart(ComUtil.formatDateTime(serviceOrder.getOperationStartTime()));
        result.setOperationFinish(ComUtil.formatDateTime(serviceOrder.getOperationFinishTime()));

        result.setDoFlag(uc.getDoFlag());
        result.setUpdateCounter(serviceOrder.getUpdateCount().toString());

        // 索赔电池数据
        if (orderBattery != null) {
            result.setBatteryNo(orderBattery.getBatteryNo());
            result.setCmmBatteryId(orderBattery.getBatteryId());
            result.setPartsId(orderBattery.getProductId());
            result.setPartsCd(orderBattery.getProductCd());
            result.setPartsNm(orderBattery.getProductNm());
            result.setParts(ComUtil.concatFull(result.getPartsCd(), result.getPartsNm()));
            result.setWarrantyTerm(orderBattery.getWarrantyTerm());

            result.setNewPartsId(orderBattery.getNewProductId());
            result.setNewPartsCd(orderBattery.getNewProductCd());
            result.setNewPartsNm(orderBattery.getNewProductNm());
//            result.setNewParts(ComUtil.concatFull(result.getNewPartsCd(), result.getNewPartsNm()));
            result.setNewBatteryNo(orderBattery.getNewBatteryNo());
            result.setNewBatteryId(orderBattery.getNewBatteryId());
            result.setRetailPrice(orderBattery.getSellingPrice());
            result.setNewWarrantyTerm(orderBattery.getWarrantyTerm());
        }
        // 预定数量、缺货数量
        if (soItem != null) {
            result.setAllocatedQty(soItem.getAllocatedQty());
            result.setBoQty(soItem.getBoQty());
            result.setSalesOrderItemId(soItem.getSalesOrderItemId());
        }
        // 电池状态明细
        result.setSituations(orderFaults);

        return result;
    }

    private void setConsumerValue2BO(ConsumerPolicyParameter policyParam, SVM010901BO result) {

        String policyFlag = orderCmmMethod.getConsumerPolicyInfo(policyParam.getSiteId()
                , policyParam.getLastNm()
                , policyParam.getMiddleNm(), policyParam.getFirstNm(), policyParam.getMobilephone());

        result.setLastName(policyParam.getLastNm());
        result.setMiddleName(policyParam.getMiddleNm());
        result.setFirstName(policyParam.getFirstNm());
        result.setMobilephone(policyParam.getMobilephone());
        result.setConsumerId(policyParam.getConsumerId());
        result.setEmail(policyParam.getEmail());

        result.setPolicyResultFlag(policyFlag);
    }

    private void validBatteryInput(SVM010901BO orderBO, BaseTableData<SituationBO> situations, String siteId, String action) {

        List<SituationBO> resultFaultList = situations.getNewUpdateRecords();
        // 电池
        validLogic.validateIsRequired(orderBO.getBatteryNo(), ComUtil.t("label.batteryNo"));

        if (StringUtils.equals(action, CommonConstants.OPERATION_STATUS_FINISH)) {
            if (StringUtils.isBlank(orderBO.getNewBatteryNo()) && StringUtils.isNotBlank(orderBO.getNewPartsCd())) {
                validLogic.validateIsRequired(orderBO.getNewBatteryNo(), ComUtil.t("label.batteryNo"));
            }
            if (StringUtils.equals(orderBO.getNewBatteryNo(), orderBO.getBatteryNo())) {
                throw new BusinessCodedException(ComUtil.t("M.E.10316", new String[] { ComUtil.t("label.batteryId"), ComUtil.t("label.newBatteryId") }));
            }
        }

        BatteryVO batteryInfo = orderCmmMethod.findBatteryByNo(orderBO.getBatteryNo(), siteId);
        if(batteryInfo == null || !StringUtils.equals(batteryInfo.getOriginalFlag(), CommonConstants.CHAR_ONE)) {
            // is not Spare Part battery error
            throw new BusinessCodedException(ComUtil.t("M.E.10327"));
        }
        String calcuteDate = batteryInfo.getServiceCalculateDate();

        if(StringUtils.isBlank(calcuteDate) && !Objects.isNull(orderBO.getNewPartsId())) {
            // cannot be empty
            throw new BusinessCodedException(ComUtil.t("M.E.10317", new String[] { ComUtil.t("label.warttryStartDate") }));
        }
        // 电池状况明细
        if (resultFaultList.isEmpty() || Objects.isNull(resultFaultList.get(0).getSymptomId())) {
            throw new BusinessCodedException(ComUtil.t("M.E.10190", new String[] { ComUtil.t("label.symptom") }));
        }
        orderCmmMethod.validateSituationDetail(orderBO.getServiceOrderId(), situations, orderBO.getSoldDate());

        if(StringUtils.isNotBlank(calcuteDate) && !Objects.isNull(orderBO.getNewPartsId())) {
            CmmWarrantyBatteryVO batteryWarranty = orderCmmMethod.findCmmWarrantyBatteryById(orderBO.getCmmBatteryId());

            String effectiveDate = CommonConstants.CHAR_BLANK;
            String expiredDate = CommonConstants.CHAR_BLANK;
            if (batteryWarranty != null) {
                effectiveDate = batteryInfo.getFromDate();
                expiredDate = batteryInfo.getToDate();
                orderBO.setWarrantyStartDate(effectiveDate);
            }
            if (calcuteDate.compareTo(effectiveDate) < 0
                    || calcuteDate.compareTo(expiredDate) > 0) {
                // Authority number required.
                orderCmmMethod.validateAuthorizationNoExistence(resultFaultList, orderBO.getWarrantyTerm());
            }
            // Policy type is new warranty
            String soldDate = orderBO.getSoldDate();
            if (batteryWarranty != null) {
                if (StringUtils.equals(batteryWarranty.getWarrantyProductClassification(), WarrantyPolicyType.BATTERY.getCodeDbid())) {
                    if ( soldDate.compareTo(effectiveDate) > 0
                            || soldDate.compareTo(expiredDate) < 0) {
                        // Authority number required.
                        orderCmmMethod.validateAuthorizationNoExistence(resultFaultList, orderBO.getWarrantyTerm());
                    }
                }
            }
        }
        // 事前申请番号
        orderCmmMethod.validateAuthorizationNo(situations, siteId, orderBO.getPointId(), orderBO.getBatteryNo(), orderBO.getServiceOrderId());
    }

    public DownloadResponseView printServiceJobCard(Long serviceOrderId, String locationNo, String userName) {

        List<SVM0109PrintBO> dataList = new ArrayList<>();

        //HeaderDataEdit
        SVM0109PrintBO printBO = svm0109Service.getJobCardData(serviceOrderId);

        if (!Nulls.isNull(printBO)) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

            printBO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS)));

            //userName
            if (StringUtils.isNotBlank(userName)) {

                JSONObject json = JSON.parseObject(userName);
                String userNm = json.getString("username");
                printBO.setUserName(StringUtils.isNotBlank(userNm) ? userNm : CommonConstants.CHAR_BLANK);
            }

            //ServiceDate格式化
            if (StringUtils.isNotBlank(printBO.getServiceDate())) {
                printBO.setServiceDate(LocalDate.parse(printBO.getServiceDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //soldDate
            if (StringUtils.isNotBlank(printBO.getSoldDate())) {
                printBO.setSoldDate(LocalDate.parse(printBO.getSoldDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //location
            if (StringUtils.isNotBlank(locationNo)) {

                printBO.setLocationNo(locationNo);
            }

            //detailDataEdit
            this.detailDataEdit(printBO, serviceOrderId);

            dataList.add(printBO);
        }
        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_STREAMJOBCARD_CLAIMFORBATTERY,
                                                   dataList,
                                                   CommonConstants.CHAR_BLANK);

        return new DownloadResponseView(rp);
    }

    public DownloadResponseView printServiceJobCardForDO(Long serviceOrderId, String locationNo) {

        List<SVM0109PrintBO> dataList = new ArrayList<>();

        //HeaderDataEdit
        SVM0109PrintBO printBO = svm0109Service.getJobCardForDoData(serviceOrderId);

        if (!Nulls.isNull(printBO)) {

            printBO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS)));
            printBO.setLogo(FileConstants.LOGOPATH_YAMAHA_BLUE);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

            //ServiceDate格式化
            if (StringUtils.isNotBlank(printBO.getServiceDate())) {
                printBO.setServiceDate(LocalDate.parse(printBO.getServiceDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //soldDate
            if (StringUtils.isNotBlank(printBO.getSoldDate())) {

                printBO.setSoldDate(LocalDate.parse(printBO.getSoldDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //location
            if (StringUtils.isNotBlank(locationNo)) {

                printBO.setLocationNo(locationNo);
            }

            //detailDataEdit
            this.detailDataEdit(printBO, serviceOrderId);

            dataList.add(printBO);
        }
        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_STREAMJOBCARDFORDO_CLAIMFORBATTERY,
                                                   dataList,
                                                   CommonConstants.CHAR_BLANK);

        return new DownloadResponseView(rp);
    }

    public DownloadResponseView printServicePayment(Long serviceOrderId, String userName) {

        List<SVM0109PrintBO> dataList = new ArrayList<>();

        //HeaderDataEdit
        SVM0109PrintBO printBO = svm0109Service.getServicePaymentData(serviceOrderId);

        if (!Nulls.isNull(printBO)) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

            printBO.setLogo(FileConstants.LOGOPATH_YAMAHA_BLUE);
            printBO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS)));

            //userName
            if (StringUtils.isNotBlank(userName)) {

                JSONObject json = JSON.parseObject(userName);
                String userNm = json.getString("username");
                printBO.setUserName(StringUtils.isNotBlank(userNm) ? userNm : CommonConstants.CHAR_BLANK);
            }

            //ServiceDate格式化
            if (StringUtils.isNotBlank(printBO.getServiceDate())) {
                printBO.setServiceDate(LocalDate.parse(printBO.getServiceDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //finishedDate
            if (StringUtils.isNotBlank(printBO.getFinishedDate())) {
                printBO.setFinishedDate(LocalDate.parse(printBO.getFinishedDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //DetailDataEdit
            this.detailDataEdit(printBO, serviceOrderId);

            dataList.add(printBO);
        }
        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_SERVICEPAYMENT_CLAIMFORBATTERY,
                                                   dataList,
                                                   CommonConstants.CHAR_BLANK);

        return new DownloadResponseView(rp);
    }

    public DownloadResponseView printServicePaymentForDO(Long serviceOrderId) {

        List<SVM0109PrintBO> dataList = new ArrayList<>();

        //HeaderDataEdit
        SVM0109PrintBO printBO = svm0109Service.getServicePaymentForDoData(serviceOrderId);

        if (!Nulls.isNull(printBO)) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

            printBO.setLogo(FileConstants.LOGOPATH_YAMAHA_BLUE);
            printBO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS)));

            //ServiceDate格式化
            if (StringUtils.isNotBlank(printBO.getServiceDate())) {
                printBO.setServiceDate(LocalDate.parse(printBO.getServiceDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //finishedDate
            if (StringUtils.isNotBlank(printBO.getFinishedDate())) {
                printBO.setFinishedDate(LocalDate.parse(printBO.getFinishedDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            //detailDataEdit
            this.detailDataEdit(printBO, serviceOrderId);

            dataList.add(printBO);
        }
        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_SERVICEPAYMENTFORDO_CLAIMFORBATTERY,
                                                    dataList,
                                                    CommonConstants.CHAR_BLANK);

        return new DownloadResponseView(rp);
    }

    private void detailDataEdit(SVM0109PrintBO printBO, Long serviceOrderId) {

        ServiceOrderBatteryVO detailData = svm0109Service.getClaimForBatteryReportDetailData(serviceOrderId);

        if (!Nulls.isNull(detailData)) {

            printBO.setPartsNo(PartNoUtil.format(detailData.getNewProductCd()));
            printBO.setPartsNm(detailData.getNewProductNm());
            printBO.setBattery(detailData.getNewBatteryNo());
            printBO.setQty(BigDecimal.ONE);
        }
    }
}
