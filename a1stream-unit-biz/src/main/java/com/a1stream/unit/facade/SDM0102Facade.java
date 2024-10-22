package com.a1stream.unit.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.DeliveryStatus;
import com.a1stream.common.constants.MstCodeConstants.JudgementStatus;
import com.a1stream.common.constants.MstCodeConstants.McSalesStatus;
import com.a1stream.common.constants.MstCodeConstants.SalesOrderStatus;
import com.a1stream.common.constants.PJConstants.CostType;
import com.a1stream.common.constants.PJConstants.InOutType;
import com.a1stream.common.constants.PJConstants.InterfaceStatus;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.ReceiptSlipStatus;
import com.a1stream.common.constants.PJConstants.SalesOrderPriorityType;
import com.a1stream.common.constants.PJConstants.SdStockStatus;
import com.a1stream.common.constants.PJConstants.SerialProQualityStatus;
import com.a1stream.common.constants.PJConstants.SerialproductStockStatus;
import com.a1stream.common.manager.GenerateNoManager;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.common.manager.InvoiceManager;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.unit.SDM010202ModelBO;
import com.a1stream.domain.bo.unit.SDM010202Param;
import com.a1stream.domain.bo.unit.SDM010202ScanBO;
import com.a1stream.domain.form.unit.SDM010202Form;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmMstOrganizationVO;
import com.a1stream.domain.vo.CmmPromotionOrderVO;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.CmmUnitPromotionItemVO;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.DeliverySerializedItemVO;
import com.a1stream.domain.vo.InventoryTransactionVO;
import com.a1stream.domain.vo.InvoiceItemVO;
import com.a1stream.domain.vo.InvoiceSerializedItemVO;
import com.a1stream.domain.vo.InvoiceVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductCostVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.QueueEinvoiceVO;
import com.a1stream.domain.vo.ReceiptSerializedItemVO;
import com.a1stream.domain.vo.ReceiptSlipItemVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.SerializedProductTranVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.unit.service.SDM0102Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

/**
* 功能描述: Fast Shipping Report
*
* mid2303
* 2024年8月19日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/08/19   Ruan Hansheng   New
*  1.1    2024/10/08   SC              Modify
*/
@Component
public class SDM0102Facade {

    @Resource
    private SDM0102Service sdm0102Service;

    @Resource
    private GenerateNoManager generateNoMgr;

    @Resource
    private InvoiceManager invoiceMgr;

    @Resource
    private InventoryManager inventoryMgr;

    private static final String TRANSFEROUT = InventoryTransactionType.TRANSFEROUT.getCodeDbid();
    private static final String SALESTOCKOUT = InventoryTransactionType.SALESTOCKOUT.getCodeDbid();
    private static final String ONHAND_QTY = SdStockStatus.ONHAND_QTY.getCodeDbid();
    private static final String GOODS = ProductClsType.GOODS.getCodeDbid();
    private static final Set<String> validStockSts = new HashSet<>(Arrays.asList(SerialproductStockStatus.ONHAND, SerialproductStockStatus.ALLOCATED ));

    public SDM010202Form getScanDetails(SDM010202Form form, PJUserDetails uc) {

        Map<String, SerializedProductVO> serialProductMap = sdm0102Service.getSerialProductMap(form.getFromPointId(), new HashSet<>(Arrays.asList(form.getFrameNo())));

        List<SDM010202ScanBO> scanList = form.getScanList();
        List<SDM010202ModelBO> modelList = form.getModelList();

        Set<Long> productIds = new HashSet<>();
        Set<String> frameNos = scanList.stream().map(SDM010202ScanBO::getFrameNo).collect(Collectors.toSet());

        Map<Long, SDM010202ModelBO> modelDtlMap = modelList.stream().collect(Collectors.toMap(SDM010202ModelBO::getProductId, Function.identity()));
        productIds.addAll(modelDtlMap.keySet());
        // 不存在时
        if (!serialProductMap.containsKey(form.getFrameNo())) {
            throw new BusinessCodedException(ComUtil.t("M.E.00303", new String[] { ComUtil.t("label.frameNumber"), form.getFrameNo(), ComUtil.t("label.tableProduct") }));
        }

        SerializedProductVO serialProd = serialProductMap.get(form.getFrameNo());

        // 验证车辆的品质状态是否为S039NORMAL，且库存状态是否为S033ALLOCATED 和S033ONHAND
        if (!SerialProQualityStatus.NORMAL.equals(serialProd.getQualityStatus())
                || !validStockSts.contains(serialProd.getStockStatus())) {
            throw new BusinessCodedException(ComUtil.t("M.E.10168", new String[] { form.getFrameNo() }));
        }

        Long serialProdId = serialProd.getProductId();

        boolean isFrameNoExist = frameNos.contains(serialProd.getFrameNo());
        boolean isProductIdExist = productIds.contains(serialProdId);

        // 若scanDetailList中不存在该frameNo则添加
        if (!isFrameNoExist) {
            scanList.add(buildScanBO(serialProd));
        }

        if (!isProductIdExist || !isFrameNoExist) {
            productIds.add(serialProd.getProductId());
            Map<Long, ProductStockStatusVO> productStockStsMap = sdm0102Service.getProductStockStsMap(uc.getDealerCode(), form.getFromPointId(), productIds, ONHAND_QTY);
            ProductStockStatusVO productStockSts = productStockStsMap.get(serialProdId);

            SDM010202ModelBO modelDetail;
            // 若modelDetailList中不存在该productId则添加
            if (!isProductIdExist) {

                Map<Long, MstProductVO> mstProductMap = sdm0102Service.getMstProductMap(new HashSet<>(Arrays.asList(serialProdId)));
                MstProductVO mstProduct = mstProductMap.keySet().isEmpty()? new MstProductVO() : mstProductMap.values().iterator().next();

                modelDetail = buildModelBO(productStockSts, mstProduct);
                modelDetail.setShippedQty(BigDecimal.ONE);

                modelDtlMap.put(serialProdId, modelDetail);
            } else if (!isFrameNoExist){
                modelDetail = modelDtlMap.get(serialProdId);

                modelDetail.setCurrentStock(null == productStockSts ? BigDecimal.ZERO : productStockSts.getQuantity());
                modelDetail.setShippedQty(modelDetail.getShippedQty().add(BigDecimal.ONE));

                modelDtlMap.put(serialProdId, modelDetail);
            }
        }

        form.setScanList(scanList);
        form.setModelList(modelDtlMap.values().stream().collect(Collectors.toList()));

        return form;
    }

    public SDM010202Form checkFile(SDM010202Form form, PJUserDetails uc) {

        List<SDM010202ScanBO> importList = form.getImportList();
        if (CollectionUtils.isEmpty(importList)) { return form; }

        Set<String> frameNoSet = importList.stream().map(SDM010202ScanBO::getFrameNo).collect(Collectors.toSet());
        Map<String, SerializedProductVO> serialProductMap = sdm0102Service.getSerialProductMap(form.getOtherProperty(), frameNoSet);

        // 验证导入数据
        frameNoSet = new HashSet<>();
        int index = 0;
        for (SDM010202ScanBO importData : importList) {

            index++;
            List<String> error          = new ArrayList<>();
            List<Object[]> errorParam   = new ArrayList<>();
            StringBuilder errorMsg = new StringBuilder();

            // 车架号不存在
            if (!serialProductMap.containsKey(importData.getFrameNo())) {
                error.add("M.E.00303");
                errorParam.add(new Object[] { ComUtil.t("label.frameNumber"), importData.getFrameNo(), ComUtil.t("label.tableProduct") });
                errorMsg.append(ComUtil.t("M.E.00303", new String[] { ComUtil.t("label.frameNumber"), importData.getFrameNo(), ComUtil.t("label.tableProduct") }));
            } else {
                // 验证车辆的品质状态是否为S039NORMAL，且库存状态是否为S033ALLOCATED 和S033ONHAND
                SerializedProductVO serialProd = serialProductMap.get(importData.getFrameNo());
                if (!SerialProQualityStatus.NORMAL.equals(serialProd.getQualityStatus())
                        || !validStockSts.contains(serialProd.getStockStatus())) {
                    error.add("M.E.10168");
                    errorParam.add(new Object[] { importData.getFrameNo() });
                    errorMsg.append(ComUtil.t("M.E.10168", new String[] { importData.getFrameNo() }));
                }
            }

            // 车架号已扫描
            if (frameNoSet.contains(importData.getFrameNo())) {
                error.add("M.E.00304");
                errorParam.add(new Object[] { "resultModel.scanResult[" + index + "].frameNumber", ComUtil.t("label.frameNumber") });
                errorMsg.append(ComUtil.t("M.E.00304", new String[] {  "resultModel.scanResult[" + index + "].frameNumber", ComUtil.t("label.frameNumber") }));
            }

            frameNoSet.add(importData.getFrameNo());

            importData.setError(error);
            importData.setErrorParam(errorParam);
            importData.setErrorMessage(errorMsg.toString());
        }

        // 将导入数据赋值到画面
        List<SDM010202ScanBO> scanList = new ArrayList<>();
        List<SDM010202ModelBO> modelList = new ArrayList<>();
        Map<Long, BigDecimal> prodIdAndQtyMap = new HashMap<>();

        for (SerializedProductVO serialProd : serialProductMap.values()) {

            scanList.add(buildScanBO(serialProd));
            prodIdAndQtyMap.put(serialProd.getProductId(), prodIdAndQtyMap.getOrDefault(serialProd.getProductId(), BigDecimal.ZERO).add(BigDecimal.ONE));
        }

        Set<Long> productIds = prodIdAndQtyMap.keySet();
        Map<Long, MstProductVO> mstProductMap = sdm0102Service.getMstProductMap(productIds);
        Map<Long, ProductStockStatusVO> productStockStsMap = sdm0102Service.getProductStockStsMap(uc.getDealerCode(), form.getOtherProperty(), productIds, ONHAND_QTY);

        for (MstProductVO mstProduct : mstProductMap.values()) {

            ProductStockStatusVO productStockSts = productStockStsMap.get(mstProduct.getProductId());

            SDM010202ModelBO modelDetail = new SDM010202ModelBO();

            modelDetail = buildModelBO(productStockSts, mstProduct);
            modelDetail.setShippedQty(prodIdAndQtyMap.getOrDefault(mstProduct.getProductId(), BigDecimal.ZERO));

            modelList.add(modelDetail);
        }

        form.setScanList(scanList);
        form.setModelList(modelList);

        return form;
    }

    public SDM010202Form reportTransfer(SDM010202Form form, PJUserDetails uc) {

        validReportData(form);
        setParam2Form(form, uc);
        SDM010202Param param = prepareReportData(form, uc);
        sdm0102Service.maintainData(param, form.getShippingTypeId(), StringUtils.equals(CommonConstants.CHAR_YT00_SITE_ID, form.getDealerCd()));

        return form;
    }

    private SDM010202Param prepareReportData(SDM010202Form form, PJUserDetails uc) {

        String shippingType = form.getShippingTypeId();

        List<SDM010202ScanBO> scanList = form.getScanList();
        List<SDM010202ModelBO> modelList = form.getModelList();

        Set<Long> productIds = modelList.stream().map(SDM010202ModelBO::getProductId).collect(Collectors.toSet());
        Set<Long> cmmSerialProdIds = scanList.stream().map(SDM010202ScanBO::getCmmSerializedProductId).collect(Collectors.toSet());
        Set<String> frameNos = scanList.stream().map(SDM010202ScanBO::getFrameNo).collect(Collectors.toSet());

        /**
         * 参数准备
         */
        CmmMstOrganizationVO mstOrgInfo = sdm0102Service.getMstOrgInfoByCd(form.getSiteId());
        MstFacilityVO fromPoint = sdm0102Service.getMstFacility(form.getFromPointId());
        Map<Long, MstProductVO> mstProductMap = sdm0102Service.getMstProductMap(productIds);
        Map<Long, ProductCostVO> productCostMap = sdm0102Service.getProductCostList(productIds, CostType.RECEIVE_COST, form.getSiteId());
        Map<Long, ProductStockStatusVO> productStockStatusVOMap = sdm0102Service.getProductStockStsMap(form.getSiteId(), form.getFromPointId(), productIds, ONHAND_QTY);
        Map<Long, CmmUnitPromotionItemVO> cmmUnitPromotItemMap = null;
        Map<String, SerializedProductVO> serialProductMap = sdm0102Service.getSerialProductMap(form.getFromPointId(), frameNos);
        Set<Long> serialProdIds = serialProductMap.values().stream().map(SerializedProductVO::getSerializedProductId).collect(Collectors.toSet());
        String siteNm = null;
        if (StringUtils.equals(CommonConstants.CHAR_YT00_SITE_ID, form.getDealerCd())) {
            cmmUnitPromotItemMap = sdm0102Service.getCmmUnitPromotionItemMap(cmmSerialProdIds);
            CmmSiteMasterVO cmmSiteMst = sdm0102Service.getCmmSiteMaster(form.getSiteId());
            if (cmmSiteMst != null) siteNm = cmmSiteMst.getSiteNm();
        }

        Map<Long, Long> prodIdAndDoItemId = new HashMap<>();
        Map<Long, Long> prodIdAndInvoiceItemId = new HashMap<>();
        Map<Long, Long> prodIdAndReceiptSlipItemId = new HashMap<>();

        // 更新对象
        DeliveryOrderVO deliveryOrder;
        List<DeliveryOrderItemVO> doItemList = new ArrayList<>();
        Map<String, Map<Long, ProductStockStatusVO>> stockStatusChangeMap = new HashMap<>();
        SalesOrderVO salesOrder = null;
        List<SalesOrderItemVO> soItemList = new ArrayList<>();
        List<DeliverySerializedItemVO> deliverySeraialItemList = new ArrayList<>();
        List<InventoryTransactionVO> invTransList = new ArrayList<>();
        List<SerializedProductVO> serialProductList = serialProductMap.values().stream().collect(Collectors.toList());
        List<BatteryVO> batteryList;
        List<SerializedProductTranVO> serialProdTranList = new ArrayList<>();
        InvoiceVO invoice;
        List<InvoiceItemVO> invoiceItemList = new ArrayList<>();
        List<InvoiceSerializedItemVO> invoiceSerialItemList = new ArrayList<>();
        QueueEinvoiceVO queueEInvoice = null;
        ReceiptSlipVO receiptSlip = null; Long receiptSlipId = null;
        List<ReceiptSlipItemVO> receiptSlipItemList = new ArrayList<>();
        List<ReceiptSerializedItemVO> receiptSerialItemList = new ArrayList<>();
        List<CmmPromotionOrderVO> cmmPromotOrderList = new ArrayList<>();

        deliveryOrder = buildDeliveryOrder(form, mstOrgInfo);
        Long deliveryOrderId = deliveryOrder.getDeliveryOrderId();
        form.setDeliveryOrderId(deliveryOrderId);

        BigDecimal totalAmt = BigDecimal.ZERO;
        BigDecimal totalQty = BigDecimal.ZERO;
        if (StringUtils.equals(shippingType, TRANSFEROUT)) {

            Integer seqNo_soItem = 0;
            for (SDM010202ModelBO modelDetail : modelList) {

                seqNo_soItem++;
                DeliveryOrderItemVO doItem = buildDeliveryOrderItem(modelDetail, productCostMap, mstProductMap, deliveryOrderId, form.getSiteId(), seqNo_soItem);
                doItemList.add(doItem);

                totalAmt = totalAmt.add(doItem.getAmt());
                totalQty = totalQty.add(doItem.getDeliveryQty());
                prodIdAndDoItemId.put(modelDetail.getProductId(), doItem.getDeliveryOrderItemId());

                genStockStsChangeMap(TRANSFEROUT, stockStatusChangeMap, form, modelDetail.getProductId(), modelDetail.getShippedQty());
            }

            deliveryOrder.setTotalAmt(totalAmt);
            deliveryOrder.setTotalQty(totalQty);
            // 收到接收调拨的车辆
            receiptSlip = buildReceiptSlip(form, deliveryOrder);
            receiptSlipId = receiptSlip.getReceiptSlipId();
        } else if (StringUtils.equals(shippingType, SALESTOCKOUT)) {

            salesOrder = buildSalesOrder(form);

            Integer seqNo_soItem = 0;
            Integer seqNo_doItem = 0;
            for (SDM010202ModelBO modelDetail : modelList) {

                seqNo_soItem++;
                SalesOrderItemVO soItem = buildSalesOrderItem(modelDetail, salesOrder.getSalesOrderId(), form.getSiteId(), seqNo_soItem);
                soItemList.add(soItem);

                seqNo_doItem++;
                DeliveryOrderItemVO doItem = buildDeliveryOrderItem(modelDetail, productCostMap, mstProductMap, deliveryOrderId, form.getSiteId(), seqNo_doItem);
                doItem.setOrderItemId(soItem.getSalesOrderItemId());
                doItem.setSalesOrderId(salesOrder.getSalesOrderId());
                doItem.setSalesOrderNo(salesOrder.getOrderNo());
                doItemList.add(doItem);

                totalAmt = totalAmt.add(doItem.getAmt());
                totalQty = totalQty.add(doItem.getDeliveryQty());
                prodIdAndDoItemId.put(modelDetail.getProductId(), doItem.getDeliveryOrderItemId());

                genStockStsChangeMap(SALESTOCKOUT, stockStatusChangeMap, form, modelDetail.getProductId(), modelDetail.getShippedQty());
            }

            deliveryOrder.setTotalAmt(totalAmt);
            deliveryOrder.setTotalQty(totalQty);
        }

        invoice = invoiceMgr.createInvoice(deliveryOrder);
        Long invoiceId = invoice.getInvoiceId();

        Integer seqNo_ivItem = 0;
        for (DeliveryOrderItemVO doItem : doItemList) {

            seqNo_ivItem++;
            InvoiceItemVO invoiceItem = buildInvoiceItem(doItem, salesOrder, invoiceId, form.getSiteId(), seqNo_ivItem);
            invoiceItemList.add(invoiceItem);
            prodIdAndInvoiceItemId.put(doItem.getProductId(), invoiceItem.getInvoiceItemId());

            if (StringUtils.equals(TRANSFEROUT, shippingType)) {
                MstProductVO mstProduct = mstProductMap.get(doItem.getProductId());
                ReceiptSlipItemVO receiptSlipItem = buildReceiptSlipItem(doItem, receiptSlipId, form.getSiteId(), mstProduct);
                receiptSlipItemList.add(receiptSlipItem);
                prodIdAndReceiptSlipItemId.put(doItem.getProductId(), receiptSlipItem.getReceiptSlipItemId());
            }

            InventoryTransactionVO invTrans = inventoryMgr.generateInventoryTransactionVO(form.getSiteId()
                                                                                          , InOutType.OUT
                                                                                          , deliveryOrder.getFromFacilityId()
                                                                                          , deliveryOrder.getFromFacilityId()
                                                                                          , deliveryOrder.getToFacilityId()
                                                                                          , doItem.getProductId()
                                                                                          , doItem.getProductCd()
                                                                                          , doItem.getProductNm()
                                                                                          , deliveryOrder.getInventoryTransactionType()
                                                                                          , doItem.getDeliveryQty()
                                                                                          , productStockStatusVOMap.get(doItem.getProductId()).getQuantity()
                                                                                          , doItem.getProductCost()
                                                                                          , deliveryOrder.getDeliveryOrderId()
                                                                                          , deliveryOrder.getDeliveryOrderNo()
                                                                                          , deliveryOrder.getFromOrganizationId()
                                                                                          , deliveryOrder.getToOrganizationId()
                                                                                          , null
                                                                                          , productCostMap.get(doItem.getProductId())
                                                                                          , null
                                                                                          , form.getPersonId()
                                                                                          , form.getPersonNm()
                                                                                          , GOODS);
            invTransList.add(invTrans);
        }

        for (SDM010202ScanBO scanDetail : scanList) {

            DeliverySerializedItemVO deliverySerialItem = buildDeliverySerializedItem(scanDetail, productCostMap, prodIdAndDoItemId, deliveryOrderId, form.getSiteId());
            deliverySeraialItemList.add(deliverySerialItem);

            InvoiceSerializedItemVO invoiceSerialItem = buildInvoiceSerialItem(scanDetail, prodIdAndInvoiceItemId, invoiceId, form.getSiteId());
            invoiceSerialItemList.add(invoiceSerialItem);

            if (StringUtils.equals(TRANSFEROUT, shippingType)) {

                ReceiptSerializedItemVO receiptSerialItem = buildReceiptSerialItem(scanDetail, productCostMap, prodIdAndReceiptSlipItemId, receiptSlipId, form.getSiteId());
                receiptSerialItemList.add(receiptSerialItem);
            } else if (StringUtils.equals(SALESTOCKOUT, shippingType)) {

                // sendManifestToDealerByESB
                if (StringUtils.equals(CommonConstants.CHAR_YT00_SITE_ID, form.getDealerCd())) {
                    Long cmmSerialProdId = scanDetail.getCmmSerializedProductId();
                    if(cmmUnitPromotItemMap != null && cmmUnitPromotItemMap.containsKey(cmmSerialProdId)) {

                        Long promotionListId = cmmUnitPromotItemMap.get(cmmSerialProdId).getPromotionListId();
                        MstProductVO mstProduct = mstProductMap.get(scanDetail.getProductId());
                        CmmPromotionOrderVO cmmPromotOrder = buildCmmPromotOrder(form, scanDetail, salesOrder, deliveryOrder, invoice, mstProduct, promotionListId, siteNm);
                        cmmPromotOrderList.add(cmmPromotOrder);
                    }
                }
            }
        }

        for (SerializedProductVO serialProd : serialProductList) {
            setValue2SerialProdVO(form, serialProd, mstProductMap, deliveryOrder.getToFacilityId());

            SerializedProductTranVO serialProdTran = buildSerialProdTran(form, deliveryOrder, serialProd, mstProductMap, productCostMap);
            serialProdTranList.add(serialProdTran);
        }

        batteryList = sdm0102Service.getBatteryList(form.getSiteId(), serialProdIds);
        for (BatteryVO batteryVO : batteryList) {
            batteryVO.setBatteryStatus(SerialproductStockStatus.ONTRANSFEROUT);
        }

        if (StringUtils.equals(fromPoint.getDoFlag(), CommonConstants.CHAR_Y)) {
            queueEInvoice = buildQueueEInvoice(form, salesOrder, invoice);
        }

        // 设置更新对象到Param
        SDM010202Param param = new SDM010202Param(deliveryOrder, doItemList, stockStatusChangeMap, salesOrder, soItemList
                , deliverySeraialItemList, invTransList, serialProductList, batteryList, serialProdTranList
                , invoice, invoiceItemList, invoiceSerialItemList, queueEInvoice, receiptSlip
                , receiptSlipItemList, receiptSerialItemList, cmmPromotOrderList);

        return param;
    }

    private CmmPromotionOrderVO buildCmmPromotOrder(SDM010202Form form, SDM010202ScanBO scanDetail
            , SalesOrderVO so, DeliveryOrderVO deliveryOrder, InvoiceVO invoice
            , MstProductVO mstProduct, Long promotListId, String siteNm) {

        CmmPromotionOrderVO cmmPromotOrder = new CmmPromotionOrderVO();

        cmmPromotOrder.setPromotionListId(promotListId);
        cmmPromotOrder.setSiteId(form.getSiteId());
        cmmPromotOrder.setSiteNm(siteNm);
        cmmPromotOrder.setFacilityId(form.getFromPointId());
        cmmPromotOrder.setFacilityCd(form.getFromPointCd());
        cmmPromotOrder.setFacilityNm(form.getFromPointNm());
        if (so != null) {
            cmmPromotOrder.setLocalOrderId(so.getSalesOrderId());
            cmmPromotOrder.setLocalOrderNo(so.getOrderNo());
        }
        cmmPromotOrder.setCmmProductId(mstProduct.getProductId());
        cmmPromotOrder.setProductCd(mstProduct.getProductCd());
        cmmPromotOrder.setProductNm(mstProduct.getSalesDescription());
        cmmPromotOrder.setLocalSerialProductId(scanDetail.getSerializedProductId());
        cmmPromotOrder.setCmmSerialProductId(scanDetail.getCmmSerializedProductId());
        cmmPromotOrder.setCmmCustomerId(form.getDealerId());
        cmmPromotOrder.setCustomerNm(form.getDealerNm());
        cmmPromotOrder.setFrameNo(scanDetail.getFrameNo());
        cmmPromotOrder.setOrderDate(form.getSysDate());
        cmmPromotOrder.setLocalInvoiceId(invoice.getInvoiceId());
        cmmPromotOrder.setLocalInvoiceNo(invoice.getInvoiceNo());
        cmmPromotOrder.setLocalDeliveryOrderId(deliveryOrder.getDeliveryOrderId());
        cmmPromotOrder.setLocalDeliveryOrderNo(deliveryOrder.getDeliveryOrderNo());
        cmmPromotOrder.setSalesMethod("FastShiping");
        cmmPromotOrder.setSalesPic(form.getPersonId());
        cmmPromotOrder.setJugementStatus(JudgementStatus.WAITINGUPLOAD);
        cmmPromotOrder.setCanEnjoyPromotion(CommonConstants.CHAR_N);

        return cmmPromotOrder;
    }

    private ReceiptSerializedItemVO buildReceiptSerialItem(SDM010202ScanBO scanDetail, Map<Long, ProductCostVO> productCostMap
            , Map<Long, Long> prodIdAndReceiptSlipItemId, Long receiptSlipId, String siteId) {

        ReceiptSerializedItemVO receiptSerialItem = new ReceiptSerializedItemVO();

        receiptSerialItem.setSiteId(siteId);
        receiptSerialItem.setReceiptSlipItemId(prodIdAndReceiptSlipItemId.get(scanDetail.getProductId()));
        receiptSerialItem.setSerializedProductId(scanDetail.getSerializedProductId());
        receiptSerialItem.setReceiptSlipId(receiptSlipId);
        receiptSerialItem.setCompleteFlag(CommonConstants.CHAR_N);
        if (productCostMap.containsKey(scanDetail.getProductId())) {
            ProductCostVO cost = productCostMap.get(scanDetail.getProductId());
            receiptSerialItem.setInCost(cost.getCost());
        }

        return receiptSerialItem;
    }

    private ReceiptSlipItemVO buildReceiptSlipItem(DeliveryOrderItemVO doItem, Long receiptSlipId, String siteId, MstProductVO mstProduct) {

        ReceiptSlipItemVO receiptSlipItem = ReceiptSlipItemVO.create();

        receiptSlipItem.setSiteId(siteId);
        receiptSlipItem.setReceiptSlipId(receiptSlipId);
        receiptSlipItem.setProductId(doItem.getProductId());
        receiptSlipItem.setProductCd(doItem.getProductCd());
        receiptSlipItem.setProductNm(doItem.getProductNm());
        receiptSlipItem.setReceiptQty(doItem.getDeliveryQty());
        receiptSlipItem.setFrozenQty(doItem.getDeliveryQty());
        receiptSlipItem.setReceiptPrice(doItem.getSellingPrice());
        receiptSlipItem.setProductClassification(GOODS);
        receiptSlipItem.setColorNm(mstProduct.getColorNm());

        return receiptSlipItem;
    }

    private ReceiptSlipVO buildReceiptSlip(SDM010202Form form, DeliveryOrderVO deliveryOrderVO) {

        String slipNo = generateNoMgr.generateSlipNo(form.getSiteId(), form.getToPointId());

        ReceiptSlipVO receiptSlip = ReceiptSlipVO.create();

        receiptSlip.setSiteId(form.getSiteId());
        receiptSlip.setSlipNo(slipNo);
        receiptSlip.setSupplierDeliveryDate(form.getSysDate());
        receiptSlip.setReceiptSlipTotalAmt(deliveryOrderVO.getTotalAmt());
        receiptSlip.setReceivedFacilityId(deliveryOrderVO.getToFacilityId());
        receiptSlip.setReceivedOrganizationId(deliveryOrderVO.getToOrganizationId());
        receiptSlip.setFromOrganizationId(deliveryOrderVO.getFromOrganizationId());
        receiptSlip.setFromFacilityId(deliveryOrderVO.getFromFacilityId());
        receiptSlip.setReceiptSlipStatus(ReceiptSlipStatus.ONTRANSIT.getCodeDbid());
        receiptSlip.setInventoryTransactionType(InventoryTransactionType.TRANSFERIN.getCodeDbid());
        receiptSlip.setCommercialInvoiceNo(deliveryOrderVO.getDeliveryOrderNo());
        receiptSlip.setProductClassification(GOODS);

        return receiptSlip;
    }

    private QueueEinvoiceVO buildQueueEInvoice(SDM010202Form form, SalesOrderVO so, InvoiceVO invoice) {

        QueueEinvoiceVO queueEInvoice = new QueueEinvoiceVO();

        queueEInvoice.setSiteId(form.getSiteId());
        if (so != null) {
            queueEInvoice.setRelatedOrderId(so.getSalesOrderId());
            queueEInvoice.setRelatedOrderNo(so.getOrderNo());
        }
        if (invoice != null) {
            queueEInvoice.setRelatedInvoiceId(invoice.getInvoiceId());
            queueEInvoice.setRelatedInvoiceNo(invoice.getInvoiceNo());
        }
        queueEInvoice.setInterfCode(GOODS);
        queueEInvoice.setInvoiceDate(form.getSysDate());
        queueEInvoice.setSendTimes(CommonConstants.INTEGER_ZERO);
        queueEInvoice.setStatus(InterfaceStatus.WAITINGSEND.getCodeDbid());

        return queueEInvoice;
    }

    private InvoiceSerializedItemVO buildInvoiceSerialItem(SDM010202ScanBO scanDetail, Map<Long, Long> prodIdAndInvoiceItemId, Long invoiceId, String siteId) {

        InvoiceSerializedItemVO invoiceSerialItem = new InvoiceSerializedItemVO();

        invoiceSerialItem.setSiteId(siteId);
        invoiceSerialItem.setInvoiceId(invoiceId);
        invoiceSerialItem.setInvoiceItemId(prodIdAndInvoiceItemId.get(scanDetail.getProductId()));
        invoiceSerialItem.setSerializedProductId(scanDetail.getSerializedProductId());

        return invoiceSerialItem;
    }

    private InvoiceItemVO buildInvoiceItem(DeliveryOrderItemVO doItem, SalesOrderVO so, Long invoiceId, String siteId, Integer seqNo) {

        InvoiceItemVO invoiceItem = InvoiceItemVO.create();

        invoiceItem.setSiteId(siteId);
        invoiceItem.setSeqNo(seqNo);
        invoiceItem.setInvoiceId(invoiceId);
        invoiceItem.setProductId(doItem.getProductId());
        invoiceItem.setProductCd(doItem.getProductCd());
        invoiceItem.setProductNm(doItem.getProductNm());
        invoiceItem.setQty(doItem.getDeliveryQty());
        invoiceItem.setSellingPrice(doItem.getSellingPrice());
        invoiceItem.setAmt(doItem.getAmt());
        invoiceItem.setCost(doItem.getProductCost());
        invoiceItem.setStandardPrice(doItem.getStandardPrice());
        invoiceItem.setRelatedSoItemId(doItem.getOrderItemId());
        invoiceItem.setSalesOrderId(doItem.getSalesOrderId());
        invoiceItem.setSalesOrderNo(doItem.getSalesOrderNo());
        invoiceItem.setOrderedProductId(doItem.getProductId());
        invoiceItem.setOrderedProductCd(doItem.getProductCd());
        invoiceItem.setOrderedProductNm(doItem.getProductNm());
        invoiceItem.setTaxRate(doItem.getTaxRate());
        if (so != null) {
            invoiceItem.setDiscountOffRate(so.getDiscountOffRate());
            invoiceItem.setSalesOrderNo(so.getOrderNo());
            invoiceItem.setOrderDate(so.getOrderDate());
        }
        invoiceItem.setOrderSourceType(GOODS);
        invoiceItem.setProductClassification(GOODS);

        return invoiceItem;
    }

    private SerializedProductTranVO buildSerialProdTran(SDM010202Form form
            , DeliveryOrderVO deliveryOrder, SerializedProductVO serialProd
            , Map<Long, MstProductVO> mstProductMap, Map<Long, ProductCostVO> productCostMap) {

        Long productId = serialProd.getProductId();

        SerializedProductTranVO serialProdTran = new SerializedProductTranVO();

        serialProdTran.setSiteId(form.getSiteId());
        serialProdTran.setSerializedProductId(serialProd.getSerializedProductId());
        serialProdTran.setTransactionDate(form.getSysDate());
        serialProdTran.setTransactionTime(form.getSysTime());
        serialProdTran.setRelatedSlipNo(deliveryOrder.getDeliveryOrderNo());
        serialProdTran.setReporterNm(form.getPersonNm());
        serialProdTran.setTransactionTypeId(deliveryOrder.getInventoryTransactionType());
        serialProdTran.setFromStatus(SerialproductStockStatus.ONSHIPPING);
        serialProdTran.setToStatus(StringUtils.equals(TRANSFEROUT, form.getShippingTypeId()) ? SerialproductStockStatus.ONTRANSFERIN : SerialproductStockStatus.SHIPPED);
        serialProdTran.setProductId(productId);
        serialProdTran.setFromPartyId(deliveryOrder.getFromOrganizationId());
        serialProdTran.setToPartyId(deliveryOrder.getToOrganizationId());
        serialProdTran.setToConsumerId(deliveryOrder.getToConsumerId());
        serialProdTran.setTargetFacilityId(deliveryOrder.getFromFacilityId());
        serialProdTran.setFromFacilityId(deliveryOrder.getFromFacilityId());
        serialProdTran.setToFacilityId(deliveryOrder.getToFacilityId());
        serialProdTran.setInCost(BigDecimal.ZERO);
        if (productCostMap.containsKey(productId)) {
            serialProdTran.setOutCost(productCostMap.get(productId).getCost());
        }
        if (mstProductMap.containsKey(productId)) {
            serialProdTran.setOutPrice(mstProductMap.get(productId).getStdRetailPrice());
        }

        return serialProdTran;
    }

    private void setValue2SerialProdVO(SDM010202Form form, SerializedProductVO serialProd, Map<Long, MstProductVO> mstProductMap, Long toFacilityId) {

        serialProd.setSiteId(form.getSiteId());
        serialProd.setStuDate(form.getSysDate());
        serialProd.setFacilityId(toFacilityId);
        serialProd.setSalesStatus(McSalesStatus.SALESTOUSER);
        serialProd.setStockStatus(StringUtils.equals(TRANSFEROUT, form.getShippingTypeId()) ? SerialproductStockStatus.ONTRANSFEROUT : SerialproductStockStatus.SHIPPED);
        if (mstProductMap.containsKey(serialProd.getProductId())) {
            MstProductVO product = mstProductMap.get(serialProd.getProductId());
            serialProd.setStuPrice(product.getStdRetailPrice());
        }
    }

    private DeliverySerializedItemVO buildDeliverySerializedItem(SDM010202ScanBO scanDetail, Map<Long, ProductCostVO> productCostMap, Map<Long, Long> prodIdAndDoItemId, Long deliveryOrderId, String siteId) {

        DeliverySerializedItemVO deliverySerialItem = DeliverySerializedItemVO.create();

        deliverySerialItem.setSiteId(siteId);
        deliverySerialItem.setSerializedProductId(scanDetail.getSerializedProductId());
        deliverySerialItem.setDeliveryOrderItemId(prodIdAndDoItemId.get(scanDetail.getProductId()));
        deliverySerialItem.setDeliveryOrderId(deliveryOrderId);
        if (productCostMap.containsKey(scanDetail.getProductId())) {
            ProductCostVO cost = productCostMap.get(scanDetail.getProductId());
            deliverySerialItem.setOutCost(cost.getCost());
        }

        return deliverySerialItem;
    }

    private SalesOrderItemVO buildSalesOrderItem(SDM010202ModelBO modelDetail, Long salesOrderId, String siteId, Integer seqNo) {

        SalesOrderItemVO salesOrderItemVO = SalesOrderItemVO.create(siteId, salesOrderId);

        salesOrderItemVO.setSeqNo(seqNo);
        salesOrderItemVO.setProductClassification(GOODS);
        salesOrderItemVO.setProductId(modelDetail.getProductId());
        salesOrderItemVO.setProductCd(modelDetail.getProductCd());
        salesOrderItemVO.setProductNm(modelDetail.getProductNm());
        salesOrderItemVO.setOrderQty(modelDetail.getShippedQty());
        salesOrderItemVO.setActualQty(modelDetail.getShippedQty());
        salesOrderItemVO.setOrderPrioritySeq(CommonConstants.INTEGER_FIVE);
        salesOrderItemVO.setWaitingAllocateQty(modelDetail.getShippedQty());

        return salesOrderItemVO;
    }

    private void genStockStsChangeMap(String shippingType, Map<String, Map<Long, ProductStockStatusVO>> stockStatusChangeMap
                                    , SDM010202Form form
                                    , Long productId, BigDecimal shippedQty) {

        // S084ONHANDQTY - qty
        inventoryMgr.generateStockStatusVOMapForSD(form.getSiteId()
                                                 , form.getFromPointId()
                                                 , productId
                                                 , shippedQty.negate()
                                                 , ONHAND_QTY
                                                 , stockStatusChangeMap);

        String targetStockStatus = "";
        if (StringUtils.equals(shippingType, TRANSFEROUT)) {
            // S084SHIPPED + qty
            targetStockStatus = SdStockStatus.SHIPPED.getCodeDbid();
        } else if (StringUtils.equals(shippingType, SALESTOCKOUT)) {
            // S084SHIPPINGREQUEST + qty
            targetStockStatus = SdStockStatus.SHIPPING_REQUEST.getCodeDbid(); // TODO
        }

        inventoryMgr.generateStockStatusVOMapForSD(form.getSiteId()
                                                 , form.getFromPointId()
                                                 , productId
                                                 , shippedQty
                                                 , targetStockStatus
                                                 , stockStatusChangeMap);
    }

    private DeliveryOrderItemVO buildDeliveryOrderItem(SDM010202ModelBO modelDetail
            , Map<Long, ProductCostVO> productCostMap
            , Map<Long, MstProductVO> mstProductMap
            , Long deliveryOrderId, String siteId, Integer seqNo) {

        Long productId = modelDetail.getProductId();

        DeliveryOrderItemVO doItemVO = DeliveryOrderItemVO.create();

        doItemVO.setSeqNo(seqNo);
        doItemVO.setSiteId(siteId);
        doItemVO.setDeliveryOrderId(deliveryOrderId);
        doItemVO.setProductClassification(GOODS);
        doItemVO.setProductId(productId);
        doItemVO.setProductCd(modelDetail.getProductCd());
        doItemVO.setProductNm(modelDetail.getProductNm());
        doItemVO.setDeliveryQty(modelDetail.getShippedQty());
        doItemVO.setOriginalDeliveryQty(modelDetail.getShippedQty());
        if (productCostMap.containsKey(productId)) {
            ProductCostVO cost = productCostMap.get(modelDetail.getProductId());
            doItemVO.setProductCost(cost.getCost());
        }
        if (mstProductMap.containsKey(productId)) {
            MstProductVO product = mstProductMap.get(modelDetail.getProductId());
            BigDecimal stdRetailPrice = null == product.getStdRetailPrice() ? BigDecimal.ZERO : product.getStdRetailPrice();
            doItemVO.setSellingPrice(stdRetailPrice);
            doItemVO.setStandardPrice(stdRetailPrice);
            doItemVO.setAmt(stdRetailPrice.multiply(modelDetail.getShippedQty()));
        }

        return doItemVO;
    }

    private SalesOrderVO buildSalesOrder(SDM010202Form form) {

        SalesOrderVO salesOrderVO = SalesOrderVO.create();

        String orderNo = generateNoMgr.generateFastShippingReportSalesOrderNo(form.getSiteId(), form.getFromPointId());

        salesOrderVO.setSiteId(form.getSiteId());
        salesOrderVO.setOrderNo(orderNo);
        salesOrderVO.setOrderDate(form.getSysDate());
        salesOrderVO.setEntryFacilityId(form.getFromPointId());
        salesOrderVO.setProductClassification(GOODS);
        salesOrderVO.setOrderPriorityType(SalesOrderPriorityType.SORO.getCodeDbid());
        salesOrderVO.setOrderSourceType(GOODS);
        salesOrderVO.setCustomerId(form.getDealerId());
        salesOrderVO.setCustomerCd(form.getDealerCd());
        salesOrderVO.setCustomerNm(form.getDealerNm());
        salesOrderVO.setConsigneeId(form.getToPointId());
        salesOrderVO.setConsigneePerson(form.getToPointNm());
        salesOrderVO.setEntryPicId(form.getPersonId());
        salesOrderVO.setEntryPicNm(form.getPersonNm());
        salesOrderVO.setSalesPicId(form.getPersonId());
        salesOrderVO.setSalesPicNm(form.getPersonNm());
        salesOrderVO.setOrderStatus(SalesOrderStatus.SHIPPED);

        return salesOrderVO;
    }

    private DeliveryOrderVO buildDeliveryOrder(SDM010202Form form, CmmMstOrganizationVO mstOrgInfo) {

        DeliveryOrderVO deliveryOrderVO = DeliveryOrderVO.create();

        String delieryNo = generateNoMgr.generateDeliveryNo(form.getSiteId(), form.getFromPointId());

        deliveryOrderVO.setSiteId(form.getSiteId());
        deliveryOrderVO.setFromFacilityId(form.getFromPointId());
        deliveryOrderVO.setToFacilityId(form.getToPointId());
        deliveryOrderVO.setFromOrganizationId(mstOrgInfo.getOrganizationId());
        deliveryOrderVO.setActivityFlag(CommonConstants.CHAR_Y);
        deliveryOrderVO.setProductClassification(GOODS);
        deliveryOrderVO.setDeliveryOrderNo(delieryNo);
        deliveryOrderVO.setDeliveryStatus(DeliveryStatus.SHIPPING_COMPLETION);
        deliveryOrderVO.setEntryPicId(form.getPersonId());
        deliveryOrderVO.setEntryPicNm(form.getPersonNm());
        deliveryOrderVO.setDeliveryOrderDate(form.getSysDate());
        deliveryOrderVO.setFinishDate(form.getSysDate());
        deliveryOrderVO.setOrderSourceType(GOODS);

        if (StringUtils.equals(form.getShippingTypeId(), TRANSFEROUT)) {

            deliveryOrderVO.setInventoryTransactionType(TRANSFEROUT);
        } else if (StringUtils.equals(form.getShippingTypeId(), SALESTOCKOUT)) {

            deliveryOrderVO.setToOrganizationId(form.getDealerId());
            deliveryOrderVO.setInventoryTransactionType(SALESTOCKOUT);
        }

        return deliveryOrderVO;
    }

    private void validReportData(SDM010202Form form) {

        List<SDM010202ScanBO> scanList = form.getScanList();
        List<SDM010202ModelBO> modelList = form.getModelList();

        // 目标据点不能与原据点相同
        if (form.getFromPointId().compareTo(form.getToPointId()) == 0) {
            throw new BusinessCodedException(ComUtil.t("M.E.00214", new String[] { ComUtil.t("label.toPoint"), form.getFromPointDesc(), ComUtil.t("label.fromPoint"), form.getToPointDesc() }));
        }

        int index = 0;
        Set<String> frameNoSet = new HashSet<>();
        for (SDM010202ScanBO scanDetail : scanList) {

            index++;
            // 验证当前车辆表中的facility id 是否和from point id 一致
            if (scanDetail.getFacilityId().compareTo(form.getFromPointId()) != 0) {
                throw new BusinessCodedException(ComUtil.t("M.E.10204", new String[] { scanDetail.getFrameNo(), form.getFromPointDesc() }));
            }
            // 验证车辆的品质状态是否为S039NORMAL， 且库存状态是否为S033ALLOCATED 和S033ONHAND
            if (!SerialProQualityStatus.NORMAL.equals(scanDetail.getQualityStatus())
                    || !validStockSts.contains(scanDetail.getStockStatus())) {
                throw new BusinessCodedException(ComUtil.t("M.E.10168", new String[] { scanDetail.getFrameNo() }));
            }
            // 重复行
            if (frameNoSet.contains(scanDetail.getFrameNo())) {
                throw new BusinessCodedException(ComUtil.t("M.E.00304", new String[] { "resultModel.scanResult[" + index + "].frameNumber", ComUtil.t("label.frameNumber") }));
            }

            frameNoSet.add(scanDetail.getFrameNo());
        }

        // model detail中的ShipmentQuantity  > getAllocatedQuantity
        for (SDM010202ModelBO modelDetail : modelList) {

            if (modelDetail.getCurrentStock().compareTo(modelDetail.getShippedQty()) < 0) {
                throw new BusinessCodedException(ComUtil.t("M.E.00209", new String[] { ComUtil.t("label.shipmentQuantity"), ComUtil.t("label.allocatedQuantity") }));
            }
        }
    }

    private void setParam2Form(SDM010202Form form, PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        form.setPersonNm(uc.getPersonName());
        form.setSysDate(ComUtil.nowDate());
        form.setSysTime(ComUtil.nowTime());
    }

    /**
     * @param serialProd
     * @return
     */
    private SDM010202ScanBO buildScanBO(SerializedProductVO serialProd) {

        SDM010202ScanBO scanDetail = new SDM010202ScanBO();

        scanDetail.setFrameNo(serialProd.getFrameNo());
        scanDetail.setEngineNo(serialProd.getEngineNo());
        scanDetail.setQualityStatus(serialProd.getQualityStatus());
        scanDetail.setStockStatus(serialProd.getStockStatus());
        scanDetail.setFacilityId(serialProd.getFacilityId());
        scanDetail.setProductId(serialProd.getProductId());
        scanDetail.setSerializedProductId(serialProd.getSerializedProductId());
        scanDetail.setCmmSerializedProductId(serialProd.getCmmSerializedProductId());

        return scanDetail;
    }

    /**
     * @param productStockSts
     * @param mstProduct
     * @return
     */
    private SDM010202ModelBO buildModelBO(ProductStockStatusVO productStockSts, MstProductVO mstProduct) {

        SDM010202ModelBO modelDetail = new SDM010202ModelBO();

        modelDetail.setProductId(mstProduct.getProductId());
        modelDetail.setProductCd(mstProduct.getProductCd());
        modelDetail.setProductNm(mstProduct.getSalesDescription());
        modelDetail.setModelCd(mstProduct.getProductCd());
        modelDetail.setModelNm(mstProduct.getSalesDescription());
        modelDetail.setColor(mstProduct.getColorNm());
        modelDetail.setCurrentStock(null == productStockSts ? BigDecimal.ZERO : productStockSts.getQuantity());

        return modelDetail;
    }
}
