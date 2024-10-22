package com.a1stream.parts.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.domain.bo.parts.SPM030501BO;
import com.a1stream.domain.entity.ProductInventory;
import com.a1stream.domain.form.parts.SPM030501Form;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:Parts Location Stock Movement
*
* mid2287
* 2024年6月6日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/06   Wang Nan      New
*/
@Service
public class SPM0305Service {

    @Resource
    private ProductInventoryRepository productInventoryRepository;

    @Resource
    private LocationRepository locationRepository;

    @Resource
    private InventoryManager inventoryManager;

    public List<SPM030501BO> getPartsLocationList(SPM030501Form form, String siteId) {
        return productInventoryRepository.getPartsLocationList(form, siteId);
    }

    public List<LocationVO> findLocationVOList(String siteId, Long facilityId, List<String> locationCds) {
        return BeanMapUtils.mapListTo(locationRepository.findBySiteIdAndFacilityIdAndLocationCdIn(siteId, facilityId, locationCds), LocationVO.class);
    }

    public ProductInventoryVO findProductInventoryVO(Long facilityId, Long productId, String siteId, Long locationId) {
        return BeanMapUtils.mapTo(productInventoryRepository.findFirstByFacilityIdAndProductIdAndSiteIdAndLocationId(facilityId, productId, siteId, locationId), ProductInventoryVO.class);
    }

    public List<ProductInventoryVO> findProductInventoryVOList(String siteId, Long productId ,Long facilityId ,List<Long> locationIds){
        return BeanMapUtils.mapListTo(productInventoryRepository.findBySiteIdAndProductIdAndFacilityIdAndLocationIdIn(siteId, productId, facilityId, locationIds), ProductInventoryVO.class);
    }

    public void update(String siteId,
                       Long facilityId,
                       Long productId,
                       Long picId,
                       List<SPM030501BO> list) {

        List<ProductInventoryVO> delList = new ArrayList<>();

        for (SPM030501BO bo : list) {
            Long fromLocationId = bo.getFromLocationId();
            Long toLocationId = bo.getToLocationId();
            BigDecimal movementQty = bo.getMovementQty();

            ProductInventoryVO fromInventoryVO = this.findProductInventoryVO(facilityId, productId, siteId, fromLocationId);
            ProductInventoryVO toInventoryVO = this.findProductInventoryVO(facilityId, productId, siteId, toLocationId);

            //减库存(若qty - Movement Qty = 0且 primary_flag = N 时删除)

            BigDecimal changeQty = NumberUtil.subtract(fromInventoryVO.getQuantity(), movementQty);
            if (NumberUtil.equals(changeQty, BigDecimal.ZERO) && StringUtils.equals(CommonConstants.CHAR_N, fromInventoryVO.getPrimaryFlag())) {

                delList.add(fromInventoryVO);

            } else {
                inventoryManager.doUpdateProductInventoryQtyByCondition(siteId,
                                                                        facilityId,
                                                                        productId,
                                                                        fromLocationId,
                                                                        movementQty,
                                                                        fromInventoryVO,
                                                                        CommonConstants.CHAR_MINUS);
            }

            //加库存
            inventoryManager.doUpdateProductInventoryQtyByCondition(siteId,
                                                                    facilityId,
                                                                    productId,
                                                                    toLocationId,
                                                                    movementQty,
                                                                    toInventoryVO,
                                                                    CommonConstants.CHAR_PLUS);

            ProductInventoryVO toInventoryVOTemp = this.findProductInventoryVO(facilityId,
                                                                             productId,
                                                                             siteId,
                                                                             toLocationId);

            //设置主库位
            if (StringUtils.equals(CommonConstants.CHAR_Y, bo.getSetAsMainLocation())) {
                inventoryManager.updateMainLocationOfProdInventory(toLocationId,
                                                                   siteId,
                                                                   productId,
                                                                   facilityId,
                                                                   BigDecimal.ZERO,
                                                                   toInventoryVOTemp);
            }

            //stock_location_transaction
            inventoryManager.doLocationStockMovement(siteId,
                                                     productId,
                                                     facilityId,
                                                     fromLocationId,
                                                     toLocationId,
                                                     movementQty,
                                                     picId);
        }

        productInventoryRepository.deleteAllInBatch(BeanMapUtils.mapListTo(delList, ProductInventory.class));

    }
}
