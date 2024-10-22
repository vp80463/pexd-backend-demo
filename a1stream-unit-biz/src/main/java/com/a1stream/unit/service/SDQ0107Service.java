package com.a1stream.unit.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.common.bo.SdManifestItemBO;
import com.a1stream.common.constants.PJConstants.SdStockStatus;
import com.a1stream.common.manager.GenerateNoManager;
import com.a1stream.common.manager.ReceiptSlipManager;
import com.a1stream.domain.bo.unit.SDQ010701BO;
import com.a1stream.domain.bo.unit.SDQ010702DetailBO;
import com.a1stream.domain.bo.unit.SDQ010702HeaderBO;
import com.a1stream.domain.entity.ProductStockStatus;
import com.a1stream.domain.entity.ReceiptManifest;
import com.a1stream.domain.entity.ReceiptManifestSerializedItem;
import com.a1stream.domain.entity.ReceiptSerializedItem;
import com.a1stream.domain.entity.ReceiptSlip;
import com.a1stream.domain.entity.ReceiptSlipItem;
import com.a1stream.domain.entity.SerializedProduct;
import com.a1stream.domain.form.unit.SDQ010701Form;
import com.a1stream.domain.repository.MstOrganizationRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.ReceiptManifestItemRepository;
import com.a1stream.domain.repository.ReceiptManifestRepository;
import com.a1stream.domain.repository.ReceiptManifestSerializedItemRepository;
import com.a1stream.domain.repository.ReceiptSerializedItemRepository;
import com.a1stream.domain.repository.ReceiptSlipItemRepository;
import com.a1stream.domain.repository.ReceiptSlipRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.a1stream.domain.vo.MstOrganizationVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.ReceiptManifestItemVO;
import com.a1stream.domain.vo.ReceiptManifestSerializedItemVO;
import com.a1stream.domain.vo.ReceiptManifestVO;
import com.a1stream.domain.vo.ReceiptSerializedItemVO;
import com.a1stream.domain.vo.ReceiptSlipItemVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
 * @author mid2259
 */
@Service
public class SDQ0107Service {

    @Resource
    private ReceiptManifestRepository receiptManifestRepository;

    @Resource
    private ReceiptSlipManager receiptSlipManager;

    @Resource
    private SerializedProductRepository serializedProductRepository;

    @Resource
    private ReceiptManifestItemRepository receiptManifestItemRepository;

    @Resource
    private MstOrganizationRepository mstOrganizationRepository;

    @Resource
    private GenerateNoManager generateNoManager;

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private ReceiptSerializedItemRepository receiptSerializedItemRepository;

    @Resource
    private ProductStockStatusRepository productStockStatusRepository;

    @Resource
    private ReceiptSlipRepository receiptSlipRepository;

    @Resource
    private ReceiptSlipItemRepository receiptSlipItemRepository;

    @Resource
    private ReceiptManifestSerializedItemRepository receiptManifestSerializedItemRepo;

    public List<ReceiptManifestSerializedItemVO> findByReceiptManifestItemIdIn(Set<Long> receiptManifestItemId){

        return BeanMapUtils.mapListTo(receiptManifestSerializedItemRepo.findByReceiptManifestItemIdIn(receiptManifestItemId), ReceiptManifestSerializedItemVO.class);
    }

    public List<SDQ010701BO> getReceiptManifestListForSD(SDQ010701Form model) {
        return receiptManifestRepository.getReceiptManifestListForSD(model);
    }

    public void doManifestImportsForSD(List<SdManifestItemBO> sdManifestItemBOList,String siteId){
        receiptSlipManager.importDataForSD(sdManifestItemBOList,siteId);
    }

    //sdq010702
    public SDQ010702HeaderBO getReceiptManifestMaintenanceHeader(Long receiptManifestId) {

        return receiptManifestRepository.getRecManMaintHeader(receiptManifestId);
    }

    public List<SDQ010702DetailBO> getReceiptManifestMaintenanceDetail(Long receiptManifestId) {

        return serializedProductRepository.getRecManMaintDetail(receiptManifestId);
    }

    public ReceiptManifestVO findReceiptManifestVO(Long receiptManifestId) {

        return BeanMapUtils.mapTo(receiptManifestRepository.findByReceiptManifestId(receiptManifestId), ReceiptManifestVO.class);
    }

    public MstOrganizationVO findMstOrganizationVO(String siteId) {
        //site_id与organization_cd值相同
        return BeanMapUtils.mapTo(mstOrganizationRepository.findBySiteIdAndOrganizationCd(siteId, siteId), MstOrganizationVO.class);
    }

    public void newSlipNo(ReceiptSlipVO newReceiptSlipVO, String siteId) {

        newReceiptSlipVO.setSlipNo(generateNoManager.
                generateSlipNo(siteId, newReceiptSlipVO.getReceiptSlipId()));
    }

    public List<ReceiptManifestItemVO> findReceiptManifestItem(Long receiptManifestId) {

        return BeanMapUtils.mapListTo(receiptManifestItemRepository.findByReceiptManifestId(receiptManifestId), ReceiptManifestItemVO.class);
    }

    public List<ReceiptSerializedItemVO> findReceiptSerializedItem(List<Long> receiptManifestItemIds) {

        return BeanMapUtils.mapListTo(receiptSerializedItemRepository.findByReceiptSlipItemIdIn(receiptManifestItemIds), ReceiptSerializedItemVO.class);
    }

    public List<SerializedProductVO> findSerializedProductVO(String siteId) {

        return BeanMapUtils.mapListTo(serializedProductRepository.findBySiteId(siteId), SerializedProductVO.class);
    }

    public List<ProductStockStatusVO> findProductStockStatusVO(Long facilityId, Set<Long> pointIds) {

        return BeanMapUtils.mapListTo(productStockStatusRepository.findByFacilityIdAndProductIdInAndProductStockStatusType(facilityId, pointIds, SdStockStatus.ONTRANSIT_QTY.getCodeDbid()), ProductStockStatusVO.class);
    }

    public List<MstProductVO> getProductByIds(Set<Long> receiptManifestId) {

        return BeanMapUtils.mapListTo(mstProductRepository.findByProductIdIn(receiptManifestId), MstProductVO.class);
    }

    public void doIssue(ReceiptSlipVO receiptSlipVO
                      , List<ReceiptSlipItemVO> receiptSlipItemAddVOs
                      , List<ProductStockStatusVO> productStockStatusUpdateVOs
                      , List<SerializedProductVO> receiptSlipItemUpdateVOs
                      , ReceiptManifestVO receiptManifestVO
                      , List<ReceiptManifestSerializedItemVO> receiptManifestSerializedItems
                      , List<ReceiptSerializedItemVO> receiptSerializedItemAddVOs) {

        receiptSlipRepository.save(BeanMapUtils.mapTo(receiptSlipVO, ReceiptSlip.class));
        receiptSlipItemRepository.saveInBatch(BeanMapUtils.mapListTo(receiptSlipItemAddVOs, ReceiptSlipItem.class));
        productStockStatusRepository.saveInBatch(BeanMapUtils.mapListTo(productStockStatusUpdateVOs, ProductStockStatus.class));
        serializedProductRepository.saveInBatch(BeanMapUtils.mapListTo(receiptSlipItemUpdateVOs, SerializedProduct.class));
        receiptManifestRepository.save(BeanMapUtils.mapTo(receiptManifestVO, ReceiptManifest.class));
        receiptManifestSerializedItemRepo.saveInBatch(BeanMapUtils.mapListTo(receiptManifestSerializedItems, ReceiptManifestSerializedItem.class));
        receiptSerializedItemRepository.saveInBatch(BeanMapUtils.mapListTo(receiptSerializedItemAddVOs, ReceiptSerializedItem.class));
    }
}
