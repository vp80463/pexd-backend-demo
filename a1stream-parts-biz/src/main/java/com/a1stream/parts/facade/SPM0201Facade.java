package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.DropShipType;
import com.a1stream.common.constants.PJConstants.OrgRelationType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.SalesOrderPriorityType;
import com.a1stream.common.model.BaseConsumerForm;
import com.a1stream.common.model.PartsInfoBO;
import com.a1stream.common.service.ConsumerService;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.SPM020103BO;
import com.a1stream.domain.bo.parts.SPM020103PrintBO;
import com.a1stream.domain.bo.parts.SPM020103PrintDetailBO;
import com.a1stream.domain.form.parts.SPM020103Form;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmRegistrationDocumentVO;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.InvoiceItemVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstOrganizationVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.parts.service.SPM0201Service;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.report.exporter.PdfReportExporter;
import com.ymsl.solid.web.download.DownloadResponseView;

import jakarta.annotation.Resource;
import jodd.util.StringUtil;

@Component
public class SPM0201Facade {
    @Resource
    SPM0201Service spm0201Service;
    @Resource
    ConsumerService consumerService;
    @Resource
    public void setPdfReportExporter(PdfReportExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
    }
    private PdfReportExporter pdfExporter;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

    private static final String ZERO_PERCENT_STRING = "0.00%";

    public SPM020103Form init020103Screen(Long salesOrderId, PJUserDetails uc) {
        SPM020103Form form = new SPM020103Form();

        MstFacilityVO ucFacility = spm0201Service.getFacilityVoByFacilityId(uc.getDefaultPointId());
        form.setSpPurchaseFlag(ucFacility.getSpPurchaseFlag());
        form.setMultiAddressFlag(ucFacility.getMultiAddressFlag());
        form.setDoFlag(ucFacility.getDoFlag());
        form.setFacilityId(uc.getDefaultPointId());
        // 新建salesOrder时只返回mstFacility信息
        if (ObjectUtils.isEmpty(salesOrderId)) {
            return form;
        }

        SalesOrderVO order = spm0201Service.timelyFindBysalesOrderId(salesOrderId);
        form.setUpdateCount(order.getUpdateCount());
        List<SalesOrderItemVO> itemList = spm0201Service.timelyFindItemBysalesOrderId(salesOrderId);

        form.setDeliveryPlanDate(order.getDeliveryPlanDate());
        form.setSalesOrderId(salesOrderId);
        form.setOrderNo(order.getOrderNo());
        form.setPointId(order.getFacilityId());
        form.setOrderStatus(order.getOrderStatus());
        MstFacilityVO pointVo = spm0201Service.getFacilityVoByFacilityId(order.getFacilityId());
        form.setPoint(pointVo.getFacilityCd()+" "+pointVo.getFacilityNm());
        form.setTaxRate(new BigDecimal(spm0201Service.getTaxRate().getParameterValue()));
        if (!ObjectUtils.isEmpty(order.getDiscountOffRate())) {
            form.setDiscountPercentage(order.getDiscountOffRate().stripTrailingZeros().toString());
        }
        form.setDepositAmt(order.getDepositAmt());
        form.setMemo(order.getComment());
        form.setShipSign(order.getShipCompleteFlag());
        form.setEoACCSign(order.getAccEoFlag());
        form.setFirstNm(order.getConsumerNmFirst());
        form.setMiddleNm(order.getConsumerNmMiddle());
        form.setLastNm(order.getConsumerNmLast());
        form.setConsumerId(order.getCmmConsumerId());
        form.setMobilePhone(order.getMobilePhone());
        form.setPrivacyResult(null);
        form.setCustaxCd(order.getCustomerTaxCode());
        form.setAddress(order.getAddress());
        form.setEmail(order.getEmail());
        form.setPaymentMethod(order.getPaymentMethodType());
        form.setCreateEmployeeSign(order.getOrderForEmployeeFlag());
        form.setEmployeeCd(order.getEmployeeCd());
        form.setPrivacyResult(spm0201Service.timelyFindConsumerPrivacyPolicyResultVO(uc.getDealerCode()
                                                                                    , form.getLastNm()
                                                                                    ,form.getMiddleNm()
                                                                                    , form.getFirstNm()
                                                                                    , form.getMobilePhone()));
        form.setDeliveryAddress(order.getFacilityAddress());
        form.setDeliveryAddressId(order.getFacilityAddressId());
        List<SPM020103BO> boList = new ArrayList<>();
        Set<Long> proIds = itemList.stream().map(SalesOrderItemVO::getAllocatedProductId).collect(Collectors.toSet());
        Map<Long, BigDecimal> proIdWithOnhandQtyMap = spm0201Service.getOnHandQtyMap(order.getSiteId(), order.getFacilityId(), proIds);

        List<MstProductVO> productVOs =spm0201Service.getProductList(proIds);
        Map<Long,MstProductVO> proIdWithproVoMap = productVOs.stream().collect(Collectors.toMap(MstProductVO::getProductId, c->c));
        SPM020103BO bo;
        for (SalesOrderItemVO item : itemList) {
            // 已取消的不显示
            if (PJConstants.OrderCancelReasonTypeSub.KEY_MANUALCANCEL.equals(item.getCancelReasonType())) {
                continue;
            }
            bo = new SPM020103BO();
            bo.setOrderItemId(item.getSalesOrderItemId());
            bo.setPartsId(item.getProductId());
            bo.setPartsNo(item.getProductCd());
            bo.setPartsNm(item.getProductNm());
            bo.setSupersedingPartsId(item.getAllocatedProductId());
            bo.setSupersedingPartsCd(item.getAllocatedProductCd());
            bo.setOnHandQty(proIdWithOnhandQtyMap.get(item.getAllocatedProductId())==null?BigDecimal.ZERO
                                                                                         :proIdWithOnhandQtyMap.get(item.getAllocatedProductId()));
            bo.setOrderQty(item.getOrderQty());
            bo.setStdPrice(item.getStandardPrice());
            bo.setSellingPrice(item.getSellingPrice());
            bo.setOrderAmt(item.getActualAmt());
            bo.setDiscountAmtVAT(item.getDiscountAmt());
            bo.setTaxRate(item.getTaxRate());
            bo.setDiscount(item.getDiscountOffRate());
            bo.setSpecialPriceVAT(item.getSpecialPrice());
            bo.setBatteryID(item.getBatteryId());
            bo.setBatteryFlag(item.getBatteryFlag());
            bo.setLargeGroupNm(proIdWithproVoMap.get(item.getAllocatedProductId()).getAllNm().split("|")[0]);
            bo.setBoCancelSign(item.getBoCancelFlag());
            bo.setBeforeBoCancelSign(item.getBoCancelFlag());
            bo.setCancelledQty(item.getCancelQty());
            bo.setAllocatedQty(item.getAllocatedQty());
            bo.setBoQty(item.getBoQty());
            bo.setPickingQty(item.getInstructionQty());
            bo.setShippedQty(item.getShipmentQty());
            bo.setUpdateCount(item.getUpdateCount());

            boList.add(bo);
        }
        form.getAllTableDataList().addAll(boList);

        // printPartsPickingListByOrderReport需要deliveryOrderId
        List<DeliveryOrderItemVO> deliveryOrderItemVOList = spm0201Service.getDeliveryOrderItemVOList(salesOrderId);
        if (CollectionUtils.isNotEmpty(deliveryOrderItemVOList)) {
            form.setDeliveryOrderId(deliveryOrderItemVOList.get(0).getDeliveryOrderId());
        }

        // printPartsSalesInvoiceReportForDO需要invoiceIdList
        List<InvoiceItemVO> invoiceItemVOList = spm0201Service.getInvoiceItemVOList(order.getOrderNo());
        if (CollectionUtils.isNotEmpty(invoiceItemVOList)) {
            form.setInvoiceIdList(invoiceItemVOList.stream().map(InvoiceItemVO::getInvoiceId).collect(Collectors.toList()));
        }

        return form;
    }

    public void saveOrder(SPM020103Form screenModel,PJUserDetails uc) {

        BaseConsumerForm consumerForm = BeanMapUtils.mapTo(screenModel, BaseConsumerForm.class);
        if(screenModel.getSalesOrderId() == null) {
            this.newSaveOrder(screenModel
                            , uc
                            , consumerForm);
        }else {
            this.updateSaveOrder(screenModel
                               , uc
                               , consumerForm);
        }
    }

    public void newSaveOrder(SPM020103Form screenModel
                            ,PJUserDetails uc
                            ,BaseConsumerForm baseConsumerForm  ) {

        SalesOrderVO salesOrderVO;
        List<SalesOrderItemVO> itemVOs = new ArrayList<>();
        if(screenModel.getSalesOrderId() == null) {
            salesOrderVO =SalesOrderVO.create();
            screenModel.setSalesOrderId(salesOrderVO.getSalesOrderId());
            salesOrderVO.setSiteId(screenModel.getSiteId());
            salesOrderVO.setOrderDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER ));
            salesOrderVO.setAllocateDueDate(salesOrderVO.getOrderDate());
            salesOrderVO.setDeliveryPlanDate(screenModel.getDeliveryPlanDate());
            salesOrderVO.setFacilityId(screenModel.getPointId());
            salesOrderVO.setOrderToType(OrgRelationType.CONSUMER.getCodeDbid());
            salesOrderVO.setProductClassification(ProductClsType.PART.getCodeDbid());
            salesOrderVO.setOrderPriorityType(SalesOrderPriorityType.SORO.getCodeDbid());
            salesOrderVO.setOrderSourceType(ProductClsType.PART.getCodeDbid());
            salesOrderVO.setEntryFacilityId(screenModel.getPointId());
            salesOrderVO.setDropShipType(DropShipType.NOTDROPSHIP);
            salesOrderVO.setBoCancelFlag(CommonConstants.CHAR_N);
            salesOrderVO.setDemandExceptionFlag(CommonConstants.CHAR_N);
            salesOrderVO.setAccEoFlag(screenModel.getEoACCSign());
            salesOrderVO.setTaxRate(screenModel.getTaxRate());
            salesOrderVO.setBoFlag(CommonConstants.CHAR_N);
            salesOrderVO.setCustomerIfFlag(CommonConstants.CHAR_N);
            salesOrderVO.setCustomerTaxCode(screenModel.getCustaxCd());
            salesOrderVO.setDiscountOffRate((new BigDecimal(StringUtil.isEmpty(screenModel.getDiscountPercentage())?"0":screenModel.getDiscountPercentage() )));
            salesOrderVO.setDepositAmt(screenModel.getDepositAmt());
            salesOrderVO.setEntryPicId(uc.getPersonId());
            salesOrderVO.setEntryPicNm(uc.getPersonName());
            salesOrderVO.setSalesPicId(uc.getPersonId());
            salesOrderVO.setSalesPicNm(uc.getPersonName());
            salesOrderVO.setComment(screenModel.getMemo());
            salesOrderVO.setCmmConsumerId(screenModel.getConsumerId());
            salesOrderVO.setMobilePhone(screenModel.getMobilePhone());
            salesOrderVO.setConsumerNmFirst(screenModel.getFirstNm());
            salesOrderVO.setConsumerNmMiddle(screenModel.getMiddleNm());
            salesOrderVO.setConsumerNmLast(screenModel.getLastNm());
            salesOrderVO.setConsumerNmFull(screenModel.getLastNm()+" "+screenModel.getMiddleNm()+" "+screenModel.getFirstNm());
            salesOrderVO.setCustomerTaxCode(screenModel.getCustaxCd());
            salesOrderVO.setEmail(screenModel.getEmail());
            salesOrderVO.setPaymentMethodType(screenModel.getPaymentMethod());
            salesOrderVO.setEmployeeCd(screenModel.getEmployeeCd());
            salesOrderVO.setEmployeeRelationShip(screenModel.getRelationship());
            salesOrderVO.setTicketNo(screenModel.getTicketNo());
            salesOrderVO.setShipCompleteFlag(screenModel.getShipSign());
            salesOrderVO.setOrderForEmployeeFlag(screenModel.getCreateEmployeeSign());
            salesOrderVO.setAddress(screenModel.getAddress());
            salesOrderVO.setFacilityAddressId(screenModel.getDeliveryAddressId());
            salesOrderVO.setFacilityAddress(screenModel.getDeliveryAddress());

            MstOrganizationVO mstOrganizationVO = spm0201Service.getMstOrganizationVO(screenModel.getSiteId(), OrgRelationType.CONSUMER.getCodeDbid());
            salesOrderVO.setCustomerId(mstOrganizationVO.getOrganizationId());
            salesOrderVO.setCustomerNm(mstOrganizationVO.getOrganizationNm());
            salesOrderVO.setCustomerCd(mstOrganizationVO.getOrganizationCd());

            SalesOrderItemVO itemVO;
            int seqNo=0;
            BigDecimal totalQty= BigDecimal.ZERO;
            BigDecimal totalAmt= BigDecimal.ZERO;
            BigDecimal totalAmtNotVat= BigDecimal.ZERO;

            Set<Long> proIdSet = screenModel.getAllTableDataList().stream().map(SPM020103BO::getPartsId).collect(Collectors.toSet());
            Set<Long> allocProIdSet = screenModel.getAllTableDataList().stream().map(SPM020103BO::getSupersedingPartsId).collect(Collectors.toSet());
            proIdSet.addAll(allocProIdSet);
            Map<Long, MstProductVO> proIdWtihProductMap = spm0201Service.getProductList(proIdSet)
                                                                        .stream()
                                                                        .collect(Collectors.toMap(MstProductVO::getProductId, Function.identity()));

            for(SPM020103BO bo:screenModel.getTableDataList().getInsertRecords()) {

                itemVO = new SalesOrderItemVO();
                itemVO.setSalesOrderId(salesOrderVO.getSalesOrderId());
                itemVO.setSiteId(salesOrderVO.getSiteId());
                itemVO.setProductId(bo.getPartsId());
                itemVO.setProductCd(proIdWtihProductMap.get(bo.getPartsId()).getProductCd());
                itemVO.setProductNm(proIdWtihProductMap.get(bo.getPartsId()).getLocalDescription());
                itemVO.setAllocatedProductCd(proIdWtihProductMap.get(bo.getPartsId()).getProductCd());
                itemVO.setAllocatedProductNm(proIdWtihProductMap.get(bo.getPartsId()).getLocalDescription());
                itemVO.setAllocatedProductId(bo.getPartsId());
                itemVO.setOrderQty(bo.getOrderQty());
                itemVO.setActualQty(bo.getOrderQty());
                itemVO.setWaitingAllocateQty(bo.getOrderQty());
                itemVO.setStandardPrice(bo.getStdPrice());
                itemVO.setSellingPrice(bo.getSellingPrice());
                itemVO.setBatteryFlag(bo.getBatteryFlag());
                itemVO.setTaxRate(bo.getTaxRate());
                itemVO.setBatteryId(bo.getBatteryID());
                itemVO.setSeqNo(seqNo++);
                itemVO.setBoCancelFlag(bo.getBoCancelSign());
                itemVO.setDiscountOffRate(bo.getDiscount());
                itemVO.setActualAmt(bo.getOrderAmt());
                itemVO.setDiscountAmt(bo.getDiscountAmtVAT());
                itemVO.setProductClassification(ProductClsType.PART.getCodeDbid());
                itemVO.setSellingPriceNotVat(bo.getSellingPriceNotVat());
                itemVO.setActualAmtNotVat(bo.getActualAmtNotVat());
                itemVO.setSpecialPrice(bo.getSpecialPriceVAT());
                itemVO.setOrderPrioritySeq(salesOrderVO.getOrderPriorityType().equals(SalesOrderPriorityType.SOEO.getCodeDbid()) ? CommonConstants.INTEGER_ONE : CommonConstants.INTEGER_FIVE);
                itemVOs.add(itemVO);
                totalQty = totalQty.add(bo.getOrderQty());
                totalAmt = totalAmt.add(bo.getOrderAmt());
                totalAmtNotVat = totalAmtNotVat.add(bo.getActualAmtNotVat());
            }
            salesOrderVO.setTotalActualQty(totalQty);
            salesOrderVO.setTotalActualAmt(totalAmt);
            salesOrderVO.setTotalQty(totalQty);
            salesOrderVO.setTotalAmt(totalAmt);

            spm0201Service.newSaveOrder(salesOrderVO,itemVOs,baseConsumerForm);
        }
    }
    public void updateSaveOrder(SPM020103Form screenModel
                               ,PJUserDetails uc
                               ,BaseConsumerForm  baseConsumerForm) {

        SalesOrderVO salesOrderVO = spm0201Service.timelyFindBysalesOrderId(screenModel.getSalesOrderId());
        if (null == salesOrderVO || !screenModel.getUpdateCount().equals(salesOrderVO.getUpdateCount())) {
            throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[] {ComUtil.t("label.salesOrderNumber"), screenModel.getOrderNo(), ComUtil.t("title.fastSalesEntry_03")}));
        }
        List<SalesOrderItemVO> itemVOs = spm0201Service.timelyFindItemBysalesOrderId(screenModel.getSalesOrderId());
        Map<Long, SalesOrderItemVO> itemVOMap = itemVOs.stream().collect(Collectors.toMap(SalesOrderItemVO::getSalesOrderItemId, Function.identity()));
        for (SPM020103BO bo : screenModel.getTableDataList().getRemoveRecords()) {
            SalesOrderItemVO salesOrderItemVO = itemVOMap.get(bo.getOrderItemId());
            if (!bo.getUpdateCount().equals(salesOrderItemVO.getUpdateCount())) {
                throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[] {ComUtil.t("label.salesOrderNumber"), screenModel.getOrderNo(), ComUtil.t("title.fastSalesEntry_03")}));
            }
        }
        for (SPM020103BO bo : screenModel.getTableDataList().getUpdateRecords()) {
            SalesOrderItemVO salesOrderItemVO = itemVOMap.get(bo.getOrderItemId());
            if (!bo.getUpdateCount().equals(salesOrderItemVO.getUpdateCount())) {
                throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[] {ComUtil.t("label.salesOrderNumber"), screenModel.getOrderNo(), ComUtil.t("title.fastSalesEntry_03")}));
            }
        }
        salesOrderVO.setSiteId(screenModel.getSiteId());
        salesOrderVO.setOrderDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER ));
        salesOrderVO.setAllocateDueDate(salesOrderVO.getOrderDate());
        salesOrderVO.setDeliveryPlanDate(screenModel.getDeliveryPlanDate());
        salesOrderVO.setFacilityId(screenModel.getPointId());
        salesOrderVO.setOrderSourceType(ProductClsType.PART.getCodeDbid());
        salesOrderVO.setEntryFacilityId(screenModel.getPointId());
        salesOrderVO.setAccEoFlag(screenModel.getEoACCSign());
        salesOrderVO.setCustomerTaxCode(screenModel.getCustaxCd());
        BigDecimal discountPercentage = screenModel.getDiscountPercentage() != null ? new BigDecimal(screenModel.getDiscountPercentage()) : BigDecimal.ZERO;
        salesOrderVO.setDiscountOffRate(discountPercentage);

        salesOrderVO.setComment(screenModel.getMemo());
        salesOrderVO.setCmmConsumerId(baseConsumerForm.getConsumerId());
        salesOrderVO.setMobilePhone(screenModel.getMobilePhone());
        salesOrderVO.setConsumerNmFirst(screenModel.getFirstNm());
        salesOrderVO.setConsumerNmMiddle(screenModel.getMiddleNm());
        salesOrderVO.setConsumerNmLast(screenModel.getLastNm());
        salesOrderVO.setConsumerNmFull(screenModel.getLastNm()+" "+screenModel.getMiddleNm()+" "+screenModel.getFirstNm());
        salesOrderVO.setCustomerTaxCode(screenModel.getCustaxCd());
        salesOrderVO.setEmail(screenModel.getEmail());
        salesOrderVO.setPaymentMethodType(screenModel.getPaymentMethod());
        salesOrderVO.setEmployeeCd(screenModel.getEmployeeCd());
        salesOrderVO.setEmployeeRelationShip(screenModel.getRelationship());
        salesOrderVO.setTicketNo(screenModel.getTicketNo());
        salesOrderVO.setShipCompleteFlag(screenModel.getShipSign());
        salesOrderVO.setOrderForEmployeeFlag(screenModel.getCreateEmployeeSign());
        salesOrderVO.setAddress(screenModel.getAddress());
        salesOrderVO.setBoFlag(screenModel.getBoFlag());

        spm0201Service.updateSaveOrder(salesOrderVO
                                      ,screenModel.getTableDataList().getRemoveRecords()
                                      ,screenModel.getTableDataList().getInsertRecords()
                                      ,screenModel.getTableDataList().getUpdateRecords()
                                      ,screenModel.getAllTableDataList()
                                      ,baseConsumerForm);
    }

    public void pickingInstruct(SPM020103Form screenModel,PJUserDetails uc) {

        SalesOrderVO salesOrderVO = spm0201Service.timelyFindBysalesOrderId(screenModel.getSalesOrderId());
        if (null == salesOrderVO || !screenModel.getUpdateCount().equals(salesOrderVO.getUpdateCount())) {
            throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[] {ComUtil.t("label.salesOrderNumber"), screenModel.getOrderNo(), ComUtil.t("title.fastSalesEntry_03")}));
        }
        salesOrderVO.setCustomerTaxCode(screenModel.getCustaxCd());
        salesOrderVO.setDiscountOffRate((new BigDecimal(screenModel.getDiscountPercentage())));

        salesOrderVO.setComment(screenModel.getMemo());
        salesOrderVO.setCmmConsumerId(screenModel.getConsumerId());
        salesOrderVO.setMobilePhone(screenModel.getMobilePhone());
        salesOrderVO.setConsumerNmFirst(screenModel.getFirstNm());
        salesOrderVO.setConsumerNmMiddle(screenModel.getMiddleNm());
        salesOrderVO.setConsumerNmLast(screenModel.getLastNm());
        salesOrderVO.setConsumerNmFull(screenModel.getLastNm()+" "+screenModel.getMiddleNm()+" "+screenModel.getFirstNm());
        salesOrderVO.setCustomerTaxCode(screenModel.getCustaxCd());
        salesOrderVO.setEmail(screenModel.getEmail());
        salesOrderVO.setPaymentMethodType(screenModel.getPaymentMethod());
        salesOrderVO.setEmployeeCd(screenModel.getEmployeeCd());
        salesOrderVO.setEmployeeRelationShip(screenModel.getRelationship());
        salesOrderVO.setTicketNo(screenModel.getTicketNo());
        salesOrderVO.setShipCompleteFlag(screenModel.getShipSign());
        salesOrderVO.setOrderForEmployeeFlag(screenModel.getCreateEmployeeSign());
        salesOrderVO.setAddress(screenModel.getAddress());
        BaseConsumerForm consumerForm = BeanMapUtils.mapTo(screenModel, BaseConsumerForm.class);
        spm0201Service.pickingInstruction(salesOrderVO,consumerForm);
    }

    public void doShipment(Long salesOrderId, SPM020103Form screenModel) {

        //新增电池信息
        List<SPM020103BO> allTableDataList = screenModel.getAllTableDataList();
        List<CmmBatteryVO> cmmBatteryVOs = new ArrayList<>();
        List<BatteryVO> batteryVOs = new ArrayList<>();
        List<CmmRegistrationDocumentVO> cmmRegistrationDocumentVOs = new ArrayList<>();
        LocalDateTime localDate = LocalDateTime.now(); // 获取当前日期的LocalDate对象
        DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD); // 创建日期格式化
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_H_M_S);

        String formattedDate = localDate.format(dateformatter);
        String formattedTime = localDate.format(timeFormatter);

        for (SPM020103BO bo : allTableDataList) {

            if(!ObjectUtils.isEmpty(bo.getBatteryID())){
                CmmBatteryVO cmmBattertVO =  CmmBatteryVO.create();
                BatteryVO batteryVO =  BatteryVO.create();
                CmmRegistrationDocumentVO cmmRegistrationDocumentVO =  CmmRegistrationDocumentVO.create();

                cmmBattertVO.setBatteryNo(Long.toString(bo.getBatteryID()));
                cmmBattertVO.setBatteryCd(bo.getPartsNo());
                cmmBattertVO.setSiteId(screenModel.getSiteId());
                cmmBattertVO.setBatteryStatus(PJConstants.SdStockStatus.SHIPPED.getCodeDbid());
                cmmBattertVO.setSellingPrice(bo.getSellingPrice());
                cmmBattertVO.setSaleDate(formattedDate);
                cmmBattertVO.setOriginalFlag(CommonConstants.CHAR_N);
                cmmBattertVO.setProductId(bo.getPartsId());

                batteryVO.setBatteryNo(Long.toString(bo.getBatteryID()));
                batteryVO.setBatteryCd(bo.getPartsNo());
                batteryVO.setSiteId(screenModel.getSiteId());
                batteryVO.setBatteryStatus(PJConstants.SdStockStatus.SHIPPED.getCodeDbid());
                batteryVO.setSellingPrice(bo.getSellingPrice());
                batteryVO.setSaleDate(formattedDate);
                batteryVO.setOriginalFlag(CommonConstants.CHAR_N);
                batteryVO.setProductId(bo.getPartsId());
                batteryVO.setCmmBatteryInfoId(cmmBattertVO.getBatteryId());

                cmmRegistrationDocumentVO.setRegistrationDate(formattedDate);
                cmmRegistrationDocumentVO.setRegistrationTime(formattedTime);
                cmmRegistrationDocumentVO.setSiteId(screenModel.getSiteId());
                cmmRegistrationDocumentVO.setFacilityId(screenModel.getPointId());
                cmmRegistrationDocumentVO.setConsumerId(screenModel.getConsumerId());
                cmmRegistrationDocumentVO.setSalesOrderId(screenModel.getSalesOrderId());
                cmmRegistrationDocumentVO.setBatteryId(batteryVO.getBatteryId());
                cmmRegistrationDocumentVO.setUseType(PJConstants.RegistrationDocumentFeatrueCategory.USETYPE001.getCodeData1());
                cmmRegistrationDocumentVO.setOwnerType(PJConstants.RegistrationDocumentFeatrueCategory.OWNERTYPE001.getCodeData1());

                cmmBatteryVOs.add(cmmBattertVO);
                batteryVOs.add(batteryVO);
                cmmRegistrationDocumentVOs.add(cmmRegistrationDocumentVO);
            }
        }

        spm0201Service.doShipment(salesOrderId, screenModel,cmmBatteryVOs,batteryVOs,cmmRegistrationDocumentVOs);
    }

    public void doCancel(Long salesOrderId, SPM020103Form screenModel) {
        spm0201Service.doCancel(salesOrderId, screenModel);
    }


    public SPM020103Form checkFile(SPM020103Form form, PJUserDetails uc) {

        // 上传的数据
        List<SPM020103BO> importList = form.getImportList();
        if (CollectionUtils.isEmpty(importList)) {
            return form;
        }

        for (SPM020103BO importItem : importList) {
            importItem.setPartsNo(PartNoUtil.formaForDB(importItem.getPartsNo()));
        }

        List<String> partsNoList = importList.stream().map(SPM020103BO::getPartsNo).toList();
        List<PartsInfoBO> partsInfoBOList = spm0201Service.getPartsInfoList(partsNoList, uc);
        Map<String, PartsInfoBO> partsInfoMap = partsInfoBOList.stream().collect(Collectors.toMap(PartsInfoBO::getPartsNo, Function.identity()));
        for (SPM020103BO bo : importList) {
            List<String> error          = new ArrayList<>();
            List<Object[]> errorParam   = new ArrayList<>();
            List<String> warning        = new ArrayList<>();
            List<Object[]> warningParam = new ArrayList<>();

            // partsNo是否存在
            if (!partsInfoMap.containsKey(bo.getPartsNo())) {
                error.add("M.E.00303");
                errorParam.add(new Object[]{"label.Parts", bo.getPartsNo(), "label.tableProduct"});
            }

            // partsNo是否重复
            if (partsNoList.indexOf(bo.getPartsNo()) != partsNoList.lastIndexOf(bo.getPartsNo())) {
                error.add("M.E.00301");
                errorParam.add(new Object[]{"label.Parts No"});
            }

            // orderQty是否小于等于0
            if (bo.getOrderQty().compareTo(BigDecimal.ZERO) <= 0) {
                error.add("M.E.00200");
                errorParam.add(new Object[]{"label.Order Qty", CommonConstants.CHAR_ZERO});
            }

            // 填充其余信息
            PartsInfoBO partsInfoBO = partsInfoMap.get(bo.getPartsNo());
            bo.setPartsNo(partsInfoBO.getPartsNo());
            bo.setPartsId(partsInfoBO.getPartsId());
            bo.setPartsNm(partsInfoBO.getPartsNm());
            bo.setSupersedingPartsCd(partsInfoBO.getSupersedingPartsCd());
            bo.setLargeGroupNm(partsInfoBO.getLargeGroup());
            bo.setOnHandQty(null == partsInfoBO.getOnHandQty() ? BigDecimal.ZERO : partsInfoBO.getOnHandQty());
            bo.setSalesLotQty(partsInfoBO.getSalLotSize());
            bo.setBatteryFlag(partsInfoBO.getBatteryFlag());
            bo.setStdPrice(partsInfoBO.getStdRetailPrice());
            bo.setTaxRate(partsInfoBO.getTaxRate());
            bo.setBoCancelSign(CommonConstants.CHAR_N);
            bo.setLocationId(partsInfoBO.getMainLocationId());
            bo.setLocationCd(partsInfoBO.getMainLocationCd());

            bo.setError(error);
            bo.setErrorParam(errorParam);
            bo.setWarning(warning);
            bo.setWarningParam(warningParam);

            // 计算orderQty应该调整到的倍数
            BigDecimal orderQty = bo.getOrderQty();
            BigDecimal salLotSize = partsInfoBO.getSalLotSize();
            BigDecimal divided = orderQty.divide(salLotSize, 0, RoundingMode.CEILING);
            BigDecimal adjustedOrderQty = divided.multiply(salLotSize);
            bo.setOrderQty(adjustedOrderQty);
        }

        return form;
    }

    public SPM020103Form checkConsumer(SPM020103Form form, PJUserDetails uc) {

        List<SPM020103BO> filteredList = form.getAllTableDataList().stream().filter(sp -> CommonConstants.CHAR_Y.equals(sp.getBatteryFlag())).collect(Collectors.toList());
        if(!filteredList.isEmpty() && ObjectUtils.isEmpty(form.getConsumerId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10157", new String[]{ CodedMessageUtils.getMessage("label.consumer") }));
        }
        return form;
    }

    public SPM020103Form checkBattery(SPM020103Form form, PJUserDetails uc) {

        List<Long> batteryIdList = form.getBatteryIdList();
        List<CmmBatteryVO> cmmBatteryVOList = spm0201Service.getCmmBatteryVOList(batteryIdList);
        // 如果传入的batteryId在CmmBattery中则抛出异常
        if (CollectionUtils.isNotEmpty(cmmBatteryVOList)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00309", new String[]{ CodedMessageUtils.getMessage("label.batteryId")
                                                                                                   , cmmBatteryVOList.get(0).getBatteryId().toString()
                                                                                                   , CodedMessageUtils.getMessage("label.tableCmmBatteryInfo") }));
        }

        return form;
    }

    public DownloadResponseView printFastSalesOrderReport(Long salesOrderId) {

        List<SPM020103PrintBO> dataList = new ArrayList<>();

        //HeaderData
        SPM020103PrintBO data = spm0201Service.getFastSalesOrderReportData(salesOrderId);
        //DetailData
        List<SPM020103PrintDetailBO> details = spm0201Service.getFastSalesOrderReportDetailData(salesOrderId);

        for (SPM020103PrintDetailBO detail : details) {
            detail.setParts(PartNoUtil.format(detail.getParts()));
            detail.setPartsNo(PartNoUtil.format(detail.getPartsNo()));
        }
        if (!Nulls.isNull(data, details)) {
            data.setDate(DateUtils.getCurrentDateString(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS));
            data.setOrderDate(LocalDate.parse(data.getOrderDate(), DATE_FORMATTER).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            data.setDetailList(details);
            dataList.add(data);
        }
        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_SPM0201_03_FASTSALESORDERREPORT, dataList, StringUtils.EMPTY);
        return new DownloadResponseView(rp);
    }

    public DownloadResponseView printFastSalesOrderReportForDO(Long salesOrderId,PJUserDetails uc) {

        List<SPM020103PrintBO> dataList = new ArrayList<>();

        //HeaderData
        SPM020103PrintBO data = spm0201Service.getFastSalesOrderReportData(salesOrderId);

        //DetailData
        List<SPM020103PrintDetailBO> details = spm0201Service.getFastSalesOrderReportDetailData(salesOrderId);

        String sysTaxRate = spm0201Service.getTaxRate().getParameterValue();
        String sysTaxPeriod = uc.getTaxPeriod();

        for (SPM020103PrintDetailBO detail : details) {
            detail.setParts(PartNoUtil.format(detail.getParts()));
            detail.setPartsNo(PartNoUtil.format(detail.getPartsNo()));
            detail.setSl(NumberUtil.add(detail.getAllocatedQty(), detail.getBoQty()));
            detail.setCurrencyVat(NumberUtil.multiply(detail.getSellingPrice(), detail.getSl().setScale(0, RoundingMode.HALF_UP)));
            detail.setDiscountOffRate(StringUtils.equals(ZERO_PERCENT_STRING, detail.getDiscountOffRate()) ? StringUtils.EMPTY : detail.getDiscountOffRate());

            if (StringUtils.isNotBlank(detail.getOrderDate()) && StringUtils.isNotBlank(sysTaxPeriod)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

                LocalDate orderDate = LocalDate.parse(detail.getOrderDate(), formatter);
                LocalDate taxPeriod = LocalDate.parse(sysTaxPeriod, formatter);

                if (orderDate.isAfter(taxPeriod) || orderDate.isEqual(taxPeriod)) {
                    if (NumberUtil.larger(detail.getProductTax(), BigDecimal.ZERO)) {
                        detail.setTaxRate(detail.getProductTax().toString() + CommonConstants.CHAR_PERCENT);
                    } else {
                        detail.setTaxRate(sysTaxRate + CommonConstants.CHAR_PERCENT);
                    }
                }
            }
        }

        if (!Nulls.isNull(data, details)) {
            data.setPointAddress(StringUtils.equals(CommonConstants.CHAR_Y, data.getMultiAddressFlag()) ? data.getMultiAddressFlag() : null);
            data.setLogo(FileConstants.LOGOPATH_YAMAHA_BLUE);
            data.setDate(DateUtils.getCurrentDateString(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS));
            data.setOrderDate(LocalDate.parse(data.getOrderDate(), DATE_FORMATTER).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            data.setDetailList(details);
            dataList.add(data);
        }

        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_SPM0201_03_FASTSALESORDERREPORTFORDO, dataList, StringUtils.EMPTY);
        return new DownloadResponseView(rp);
    }

    public DownloadResponseView printPartsSalesInvoiceReportForDO(List<Long> invoiceIds, PJUserDetails uc) {
        //data
        List<SPM020103PrintBO> dataList = spm0201Service.getPartsSalesInvoiceForDOList(invoiceIds);

        if(dataList.isEmpty()) {
            return null;
        }

        String sysTaxRate = spm0201Service.getTaxRate().getParameterValue();
        String sysTaxPeriod = uc.getTaxPeriod();

        //dataEdit
        for (SPM020103PrintBO data : dataList) {
            data.setLogo(FileConstants.LOGOPATH_YAMAHA_BLUE);
            data.setDate(DateUtils.getCurrentDateString(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS));
            data.setPartsNo(PartNoUtil.format(data.getPartsNo()));
            data.setDiscountOffRate(StringUtils.equals(ZERO_PERCENT_STRING, data.getDiscountOffRate()) ? StringUtils.EMPTY : data.getDiscountOffRate());
            //taxRate
            if (StringUtils.isNotBlank(data.getOrderDate()) && StringUtils.isNotBlank(sysTaxPeriod)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

                LocalDate orderDate = LocalDate.parse(data.getOrderDate(), formatter);
                LocalDate taxPeriod = LocalDate.parse(sysTaxPeriod, formatter);

                if (orderDate.isAfter(taxPeriod) || orderDate.isEqual(taxPeriod)) {
                    if (NumberUtil.larger(data.getProductTax(), BigDecimal.ZERO)) {
                        data.setTaxRate(data.getProductTax().toString() + CommonConstants.CHAR_PERCENT);
                    } else {
                        data.setTaxRate(sysTaxRate + CommonConstants.CHAR_PERCENT);
                    }
                }
            }
        }

        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_SPM0201_03_PARTSSALESINVOICEFORDO, dataList, StringUtils.EMPTY);
        return new DownloadResponseView(rp);
    }

}