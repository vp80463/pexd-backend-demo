package com.a1stream.common.manager;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.BatteryType;
import com.a1stream.common.constants.PJConstants.SerialProQualityStatus;
import com.a1stream.common.exception.PJCustomException;
import com.a1stream.domain.entity.Battery;
import com.a1stream.domain.entity.CmmBattery;
import com.a1stream.domain.entity.CmmSerializedProduct;
import com.a1stream.domain.entity.DeliveryOrder;
import com.a1stream.domain.entity.SalesOrder;
import com.a1stream.domain.entity.SerializedProduct;
import com.a1stream.domain.entity.SerializedProductTran;
import com.a1stream.domain.repository.BatteryRepository;
import com.a1stream.domain.repository.CmmBatteryRepository;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.DeliveryOrderRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.a1stream.domain.repository.SerializedProductTranRepository;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.DateUtils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dong zhen
 */
@Service
@Slf4j
public class SerializedProductManager {

    @Resource
    private SalesOrderRepository salesOrderRepository;

    @Resource
    private CmmSerializedProductRepository cmmSerializedProductRepository;

    @Resource
    private SerializedProductRepository serializedProductRepository;

    @Resource
    private BatteryRepository batteryRepository;

    @Resource
    private CmmBatteryRepository cmmBatteryRepository;

    @Resource
    private DeliveryOrderRepository deliveryOrderRepository;

    @Resource
    private SerializedProductTranRepository serializedProductTranRepository;

    /**
     * 处理单元零售发货完成流程
     * 该方法整合了零售发货流程中的三个关键步骤：
     * 1. 更新MC（商品管理）系统中的库存信息
     * 2. 更新电池溯源系统中的信息
     * 3. 创建交易记录
     *
     * @param deliveryOrderItemVO 配送订单项视图对象，包含配送订单的详细信息
     * @param serializedProductVO 序列化产品视图对象，包含产品的溯源信息
     * @param personNm 操作人员的名字，用于记录操作责任
     * @throws PJCustomException 处理单元零售发货完成流程出现错误会弹出自定义错误提示.
     */
    public void doUnitRetailShippingCompletion(DeliveryOrderItemVO deliveryOrderItemVO, SerializedProductVO serializedProductVO, String personNm) {

        try {
            this.validateInput(deliveryOrderItemVO, serializedProductVO, personNm);

            this.doUnitRetailUpdateMc(deliveryOrderItemVO, serializedProductVO);
            log.info("更新MC成功");

            this.doUnitRetailUpdateBattery(serializedProductVO);
            log.info("更新电池溯源成功");

            this.doUnitRetailCreateTran(deliveryOrderItemVO, serializedProductVO, personNm);
            log.info("创建交易记录成功");
        } catch (Exception e) {
            log.error("处理单元零售发货错误: {}", e.getMessage());
            throw new PJCustomException("doUnitRetailShippingCompletion error");
        }
    }

    private void validateInput(DeliveryOrderItemVO deliveryOrderItemVO, SerializedProductVO serializedProductVO, String personNm) {
        if (deliveryOrderItemVO == null || serializedProductVO == null || personNm == null || personNm.trim().isEmpty()) {
            log.error("输入参数不能为空");
            throw new PJCustomException("The input parameter cannot be empty");
        }
    }

    private void doUnitRetailUpdateMc(DeliveryOrderItemVO deliveryOrderItemVO, SerializedProductVO serializedProductVO) {

        SalesOrder salesOrder = salesOrderRepository.findBySalesOrderId(deliveryOrderItemVO.getSalesOrderId());

        serializedProductVO.setStuSiteId(deliveryOrderItemVO.getSiteId());
        serializedProductVO.setStuDate(salesOrder.getOrderDate());
        serializedProductVO.setStuPrice(deliveryOrderItemVO.getSellingPrice());
        serializedProductVO.setStockStatus(MstCodeConstants.SerialproductStockStatus.SHIPPED);
        serializedProductVO.setSalesStatus(MstCodeConstants.McSalesStatus.SALESTOUSER);
        serializedProductVO.setPdiFlag(CommonConstants.CHAR_Y);
        serializedProductRepository.save(BeanMapUtils.mapTo(serializedProductVO, SerializedProduct.class));

        CmmSerializedProduct cmmSerializedProduct = cmmSerializedProductRepository.findBySerializedProductId(serializedProductVO.getCmmSerializedProductId());

        cmmSerializedProduct.setSiteId(CommonConstants.CHAR_SPACE);
        cmmSerializedProduct.setStuSiteId(deliveryOrderItemVO.getSiteId());
        cmmSerializedProduct.setStuDate(salesOrder.getOrderDate());
        cmmSerializedProduct.setStuPrice(deliveryOrderItemVO.getSellingPrice());
        cmmSerializedProduct.setStockStatus(MstCodeConstants.SerialproductStockStatus.SHIPPED);
        cmmSerializedProduct.setSalesStatus(MstCodeConstants.McSalesStatus.SALESTOUSER);
        cmmSerializedProduct.setPdiFlag(CommonConstants.CHAR_Y);
        cmmSerializedProduct.setQualityStatus(SerialProQualityStatus.NORMAL);
        cmmSerializedProduct.setRefuseCall(CommonConstants.CHAR_N);

        cmmSerializedProductRepository.save(cmmSerializedProduct);
    }

    private void doUnitRetailUpdateBattery(SerializedProductVO serializedProductVO) {

        // 若为电车同步更新电池信息
        if (CommonConstants.CHAR_Y.equals(serializedProductVO.getEvFlag())) {
            Battery battery1 = batteryRepository.findBySerializedProductIdAndPositionSign(serializedProductVO.getSerializedProductId(), BatteryType.TYPE1.getCodeDbid());
            if (null != battery1) {
                battery1.setBatteryStatus(MstCodeConstants.SerialproductStockStatus.SHIPPED);
                battery1.setSaleDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
                battery1.setServiceCalculateDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
                batteryRepository.save(battery1);
                CmmBattery cmmBattery1 = cmmBatteryRepository.findByBatteryId(battery1.getCmmBatteryInfoId());
                if (null != cmmBattery1) {
                    cmmBattery1.setBatteryStatus(MstCodeConstants.SerialproductStockStatus.SHIPPED);
                    cmmBattery1.setSaleDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
                    cmmBatteryRepository.save(cmmBattery1);
                }
            }

            Battery battery2 = batteryRepository.findBySerializedProductIdAndPositionSign(serializedProductVO.getSerializedProductId(), BatteryType.TYPE2.getCodeDbid());
            if (null != battery2) {
                battery2.setBatteryStatus(MstCodeConstants.SerialproductStockStatus.SHIPPED);
                battery2.setSaleDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
                battery2.setServiceCalculateDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
                batteryRepository.save(battery2);
                CmmBattery cmmBattery2 = cmmBatteryRepository.findByBatteryId(battery2.getCmmBatteryInfoId());
                if (null != cmmBattery2) {
                    cmmBattery2.setBatteryStatus(MstCodeConstants.SerialproductStockStatus.SHIPPED);
                    cmmBattery2.setSaleDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
                    cmmBatteryRepository.save(cmmBattery2);
                }
            }
        }
    }

    private void doUnitRetailCreateTran(DeliveryOrderItemVO deliveryOrderItemVO, SerializedProductVO serializedProductVO, String personNm) {

        DeliveryOrder deliveryOrder = deliveryOrderRepository.findByDeliveryOrderId(deliveryOrderItemVO.getDeliveryOrderId());

        SerializedProductTran serializedProductTran = new SerializedProductTran();
        serializedProductTran.setSiteId(deliveryOrder.getSiteId());
        serializedProductTran.setSerializedProductId(serializedProductVO.getSerializedProductId());
        serializedProductTran.setTransactionDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
        serializedProductTran.setTransactionTime(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_H_M));
        serializedProductTran.setRelatedSlipNo(deliveryOrder.getDeliveryOrderNo());
        serializedProductTran.setReporterNm(personNm);
        serializedProductTran.setTransactionTypeId(deliveryOrder.getInventoryTransactionType());
        serializedProductTran.setFromStatus(PJConstants.SerialproductStockStatus.ONSHIPPING);
        serializedProductTran.setToStatus(MstCodeConstants.SerialproductStockStatus.SHIPPED);
        serializedProductTran.setProductId(deliveryOrderItemVO.getProductId());
        serializedProductTran.setFromPartyId(deliveryOrder.getFromOrganizationId());
        serializedProductTran.setToConsumerId(deliveryOrder.getCmmConsumerId());
        serializedProductTran.setTargetFacilityId(deliveryOrder.getFromFacilityId());
        serializedProductTran.setFromFacilityId(deliveryOrder.getFromFacilityId());
        serializedProductTran.setOutPrice(deliveryOrderItemVO.getSellingPrice());
        serializedProductTranRepository.save(serializedProductTran);
    }
}
