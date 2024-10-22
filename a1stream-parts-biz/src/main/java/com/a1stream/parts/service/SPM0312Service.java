package com.a1stream.parts.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.domain.entity.InventoryTransaction;
import com.a1stream.domain.form.parts.SPM031201Form;
import com.a1stream.domain.repository.InventoryTransactionRepository;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.ProductCostRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.vo.InventoryTransactionVO;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductCostVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.solid.base.util.BeanMapUtils;
import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@Service
public class SPM0312Service {

    @Resource
    ProductStockStatusRepository productStockStatusRepository;

    @Resource
    LocationRepository locationRepository;

    @Resource
    ProductInventoryRepository productInventoryRepository;

    @Resource
    ProductCostRepository productCostRepository;

    @Resource
    InventoryTransactionRepository inventoryTransactionRepository;

    @Resource
    InventoryManager inventoryManager;

    @Resource
    MstProductRepository mstProductRepository;

    public void doFrozenStockRelease( SPM031201Form model
                                    , ProductInventoryVO fromInventoryVO
                                    , ProductInventoryVO toInventoryVO
                                    , InventoryTransactionVO inventoryTransactionVO) {
        //Location Storage
        if (StringUtils.equals(model.getReleaseType(), PJConstants.ReleaseType.LOCATIONSTORAGE)) {

            Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = new HashMap<>();

            //调用共通方法保存productstockstatus
            inventoryManager.generateStockStatusVOMap(model.getSiteId(), model.getPointId(), model.getPartsId(), model.getReleaseQty().negate(), PJConstants.SpStockStatus.ONFROZEN_QTY.getCodeDbid(), stockStatusVOChangeMap);
            inventoryManager.generateStockStatusVOMap(model.getSiteId(), model.getPointId(), model.getPartsId(), model.getReleaseQty(), PJConstants.SpStockStatus.ONHAND_QTY.getCodeDbid(), stockStatusVOChangeMap);
            inventoryManager.updateProductStockStatusByMap(stockStatusVOChangeMap);

            //减库存
            inventoryManager.doUpdateProductInventoryQtyByCondition(model.getSiteId()
                    , model.getPointId()
                    , model.getPartsId()
                    , model.getFromLocationId()
                    , model.getReleaseQty()
                    , fromInventoryVO
                    , CommonConstants.CHAR_MINUS);

            inventoryManager.doUpdateProductInventoryQtyByCondition(model.getSiteId()
                    , model.getPointId()
                    , model.getPartsId()
                    , model.getToLocationId()
                    , model.getReleaseQty()
                    , toInventoryVO
                    , CommonConstants.CHAR_PLUS);

            inventoryManager.doLocationStockMovement(model.getSiteId(),model.getPartsId(), model.getPointId(),  model.getFromLocationId(), model.getToLocationId(), model.getReleaseQty(), model.getPersonId());

        }
        //Scrapping
        if (StringUtils.equals(model.getReleaseType(), PJConstants.ReleaseType.SCRAPPING)) {


            Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = new HashMap<>();
            inventoryManager.generateStockStatusVOMap(model.getSiteId(), model.getPointId(), model.getPartsId(), model.getReleaseQty().negate(), PJConstants.SpStockStatus.ONFROZEN_QTY.getCodeDbid(), stockStatusVOChangeMap);
            inventoryManager.updateProductStockStatusByMap(stockStatusVOChangeMap);

            inventoryManager.doUpdateProductInventoryQtyByCondition(model.getSiteId()
                    , model.getPointId()
                    , model.getPartsId()
                    , model.getFromLocationId()
                    , model.getReleaseQty()
                    , fromInventoryVO
                    , CommonConstants.CHAR_MINUS);

            inventoryTransactionRepository.save(BeanMapUtils.mapTo(inventoryTransactionVO, InventoryTransaction.class));
        }
    }

    public ProductStockStatusVO findProductStockStatus(String siteId,Long facilityId,Long productId,String productStockStatusType) {

        return BeanMapUtils.mapTo(productStockStatusRepository.findProductStockStatus(siteId,facilityId,productId,productStockStatusType), ProductStockStatusVO.class);

    }

    public LocationVO findLocationCdByLocationId(Long locationId,String siteId) {

        return BeanMapUtils.mapTo(locationRepository.findByLocationIdAndSiteId(locationId,siteId), LocationVO.class);
    }

    public ProductInventoryVO findProductInventoryByFlag(Long facilityId, Long productId, String siteId, String flag) {

        return BeanMapUtils.mapTo(productInventoryRepository.findFirstByFacilityIdAndProductIdAndSiteIdAndPrimaryFlag(facilityId,productId,siteId,flag), ProductInventoryVO.class);
    }

    public ProductInventoryVO findProductInventory(Long facilityId, Long productId, String siteId, Long locationId) {

        return BeanMapUtils.mapTo(productInventoryRepository.findFirstByFacilityIdAndProductIdAndSiteIdAndLocationId(facilityId,productId,siteId,locationId), ProductInventoryVO.class);
    }

    public ProductInventoryVO findProductInventoryByLocationId(Long facilityId, Long productId, String siteId, Long locationId) {

        return BeanMapUtils.mapTo(productInventoryRepository.findByFacilityIdAndProductIdAndSiteIdAndLocationId(facilityId,productId,siteId,locationId), ProductInventoryVO.class);
    }

    public ProductCostVO findProductCostByProductId(Long productId, String costType, String siteId) {

        return BeanMapUtils.mapTo(productCostRepository.findByProductIdAndCostTypeAndSiteId(productId,costType,siteId), ProductCostVO.class);
    }

    public MstProductVO findPorudctByProductNo(String productNo,List<String> siteId) {

        return BeanMapUtils.mapTo(mstProductRepository.findFirstByProductCdAndSiteIdIn(productNo,siteId), MstProductVO.class);

    }

    public List<ProductStockStatusVO>  findProductStockStatusIn(String siteId , Long facilityId, Long productId, List<String> productStockStatusType){

        return BeanMapUtils.mapListTo(productStockStatusRepository.findProductStockStatusIn(siteId,facilityId,productId,productStockStatusType), ProductStockStatusVO.class);
    }
}
