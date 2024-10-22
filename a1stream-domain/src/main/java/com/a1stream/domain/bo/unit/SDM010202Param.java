package com.a1stream.domain.bo.unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmPromotionOrderVO;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.DeliverySerializedItemVO;
import com.a1stream.domain.vo.InventoryTransactionVO;
import com.a1stream.domain.vo.InvoiceItemVO;
import com.a1stream.domain.vo.InvoiceSerializedItemVO;
import com.a1stream.domain.vo.InvoiceVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.QueueEinvoiceVO;
import com.a1stream.domain.vo.ReceiptSerializedItemVO;
import com.a1stream.domain.vo.ReceiptSlipItemVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.SerializedProductTranVO;
import com.a1stream.domain.vo.SerializedProductVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM010202Param {

    // 更新对象
    private DeliveryOrderVO deliveryOrder;
    private List<DeliveryOrderItemVO> doItemList = new ArrayList<>();
    private Map<String, Map<Long, ProductStockStatusVO>> stockStatusChangeMap = new HashMap<>();
    private SalesOrderVO salesOrder = null;
    private List<SalesOrderItemVO> soItemList = new ArrayList<>();
    private List<DeliverySerializedItemVO> deliverySeraialItemList = new ArrayList<>();
    private List<InventoryTransactionVO> invTransList = new ArrayList<>();
    private List<SerializedProductVO> serialProductList = null;
    private List<BatteryVO> batteryList;
    private List<SerializedProductTranVO> serialProdTranList = new ArrayList<>();
    private InvoiceVO invoice;
    private List<InvoiceItemVO> invoiceItemList = new ArrayList<>();
    private List<InvoiceSerializedItemVO> invoiceSerialItemList = new ArrayList<>();
    private QueueEinvoiceVO queueEInvoice = null;
    private ReceiptSlipVO receiptSlip = null;
    private List<ReceiptSlipItemVO> receiptSlipItemList = new ArrayList<>();
    private List<ReceiptSerializedItemVO> receiptSerialItemList = new ArrayList<>();
    private List<CmmPromotionOrderVO> cmmPromotOrderList = new ArrayList<>();

    public SDM010202Param() {}

    /**
     * 共18张表
     */
    public SDM010202Param(DeliveryOrderVO deliveryOrder, List<DeliveryOrderItemVO> doItemList, Map<String, Map<Long, ProductStockStatusVO>> stockStatusChangeMap
            , SalesOrderVO salesOrder, List<SalesOrderItemVO> soItemList, List<DeliverySerializedItemVO> deliverySeraialItemList
            , List<InventoryTransactionVO> invTransList, List<SerializedProductVO> serialProductList
            , List<BatteryVO> batteryList, List<SerializedProductTranVO> serialProdTranList, InvoiceVO invoice
            , List<InvoiceItemVO> invoiceItemList, List<InvoiceSerializedItemVO> invoiceSerialItemList
            , QueueEinvoiceVO queueEInvoice, ReceiptSlipVO receiptSlip, List<ReceiptSlipItemVO> receiptSlipItemList
            , List<ReceiptSerializedItemVO> receiptSerialItemList, List<CmmPromotionOrderVO> cmmPromotOrderList) {

        this.deliveryOrder = deliveryOrder;
        this.doItemList = doItemList;
        this.stockStatusChangeMap = stockStatusChangeMap;
        this.salesOrder = salesOrder;
        this.soItemList = soItemList;
        this.deliverySeraialItemList = deliverySeraialItemList;
        this.invTransList = invTransList;
        this.serialProductList = serialProductList;
        this.batteryList = batteryList;
        this.serialProdTranList = serialProdTranList;
        this.invoice = invoice;
        this.invoiceItemList = invoiceItemList;
        this.invoiceSerialItemList = invoiceSerialItemList;
        this.queueEInvoice = queueEInvoice;
        this.receiptSlip = receiptSlip;
        this.receiptSlipItemList = receiptSlipItemList;
        this.receiptSerialItemList = receiptSerialItemList;
        this.cmmPromotOrderList = cmmPromotOrderList;
    }
}
