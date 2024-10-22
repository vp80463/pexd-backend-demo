package com.a1stream.unit.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.bo.CreateRegistrationDocumentBO;
import com.a1stream.common.bo.SDCommonCheckBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.constants.MstCodeConstants.JudgementStatus;
import com.a1stream.common.constants.PJConstants.ConsumerSerialProRelationType;
import com.a1stream.common.constants.PJConstants.CostType;
import com.a1stream.common.constants.PJConstants.InOutType;
import com.a1stream.common.constants.PJConstants.MCSalesType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.SalesOrderPriorityType;
import com.a1stream.common.constants.PJConstants.SalesOrderStatus;
import com.a1stream.common.constants.PJConstants.SdStockStatus;
import com.a1stream.common.manager.CmmSerializedProductManager;
import com.a1stream.common.manager.ConsumerManager;
import com.a1stream.common.manager.DeliveryOrderManager;
import com.a1stream.common.manager.GenerateNoManager;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.common.manager.InvoiceManager;
import com.a1stream.common.manager.RegistrationDocumentManager;
import com.a1stream.common.manager.RemindManager;
import com.a1stream.common.manager.SDCommonCheckManager;
import com.a1stream.common.manager.SalesOrderManager;
import com.a1stream.common.manager.SerializedProductManager;
import com.a1stream.common.model.BaseConsumerForm;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.batch.SvVinCodeTelIFBO;
import com.a1stream.domain.bo.unit.SDM030701BO;
import com.a1stream.domain.entity.CmmConsumerSerialProRelation;
import com.a1stream.domain.entity.CmmPromotionOrder;
import com.a1stream.domain.entity.DeliveryOrder;
import com.a1stream.domain.entity.DeliveryOrderItem;
import com.a1stream.domain.entity.InventoryTransaction;
import com.a1stream.domain.entity.OrderSerializedItem;
import com.a1stream.domain.entity.QueueData;
import com.a1stream.domain.entity.RemindSchedule;
import com.a1stream.domain.entity.SalesOrder;
import com.a1stream.domain.entity.SalesOrderItem;
import com.a1stream.domain.form.unit.SDM030701Form;
import com.a1stream.domain.repository.BatteryRepository;
import com.a1stream.domain.repository.CmmBatteryRepository;
import com.a1stream.domain.repository.CmmConsumerRepository;
import com.a1stream.domain.repository.CmmConsumerSerialProRelationRepository;
import com.a1stream.domain.repository.CmmPromotionOrderRepository;
import com.a1stream.domain.repository.CmmRegistrationDocumentRepository;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.repository.CmmSpecialCompanyTaxRepository;
import com.a1stream.domain.repository.CmmUnitPromotionItemRepository;
import com.a1stream.domain.repository.CmmUnitPromotionListRepository;
import com.a1stream.domain.repository.ConsumerPrivacyPolicyResultRepository;
import com.a1stream.domain.repository.ConsumerPrivateDetailRepository;
import com.a1stream.domain.repository.DeliveryOrderItemRepository;
import com.a1stream.domain.repository.DeliveryOrderRepository;
import com.a1stream.domain.repository.InventoryTransactionRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.OrderSerializedItemRepository;
import com.a1stream.domain.repository.ProductCostRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.ProductTaxRepository;
import com.a1stream.domain.repository.QueueDataRepository;
import com.a1stream.domain.repository.RemindScheduleRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmConsumerSerialProRelationVO;
import com.a1stream.domain.vo.CmmConsumerVO;
import com.a1stream.domain.vo.CmmPromotionOrderVO;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.CmmSpecialCompanyTaxVO;
import com.a1stream.domain.vo.CmmUnitPromotionItemVO;
import com.a1stream.domain.vo.CmmUnitPromotionListVO;
import com.a1stream.domain.vo.ConsumerPrivacyPolicyResultVO;
import com.a1stream.domain.vo.ConsumerPrivateDetailVO;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.InventoryTransactionVO;
import com.a1stream.domain.vo.InvoiceVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.OrderSerializedItemVO;
import com.a1stream.domain.vo.ProductCostVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.ProductTaxVO;
import com.a1stream.domain.vo.QueueDataVO;
import com.a1stream.domain.vo.RemindScheduleVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.ymsl.plugins.userauth.util.JsonUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:  Retail Order Entry For DO
*
* mid2303
* 2024年10月14日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/10/14   Ruan Hansheng   New
*/
@Service
public class SDM0307Service {

    @Resource
    private SalesOrderRepository salesOrderRepository;

    @Resource
    private SalesOrderItemRepository salesOrderItemRepository;

    @Resource
    private BatteryRepository batteryRepository;

    @Resource
    private CmmConsumerRepository cmmConsumerRepository;

    @Resource
    private ConsumerPrivacyPolicyResultRepository consumerPrivacyPolicyResultRepository;

    @Resource
    private ConsumerPrivateDetailRepository consumerPrivateDetailRepository;

    @Resource
    private SerializedProductRepository serializedProductRepository;

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    @Resource
    private ProductStockStatusRepository productStockStatusRepository;

    @Resource
    private ConsumerManager consumerManager;

    @Resource
    private GenerateNoManager generateNoManager;

    @Resource
    private OrderSerializedItemRepository orderSerializedItemRepository;

    @Resource
    private CmmRegistrationDocumentRepository cmmRegistrationDocumentRepository;

    @Resource
    private SDCommonCheckManager sdCommonCheckManager;

    @Resource
    private InventoryManager inventoryManager;

    @Resource
    private SalesOrderManager salesOrderManager;

    @Resource
    private CmmSerializedProductManager cmmSerializedProductManager;

    @Resource
    private DeliveryOrderManager deliveryOrderManager;

    @Resource
    private DeliveryOrderItemRepository deliveryOrderItemRepository;

    @Resource
    private ProductCostRepository productCostRepository;

    @Resource
    private InventoryTransactionRepository inventoryTransactionRepository;

    @Resource
    private SerializedProductManager serializedProductManager;

    @Resource
    private InvoiceManager invoiceManager;

    @Resource
    private CmmSerializedProductRepository cmmSerializedProductRepository;

    @Resource
    private CmmBatteryRepository cmmBatteryRepository;

    @Resource
    private RemindManager remindManager;

    @Resource
    private RemindScheduleRepository remindScheduleRepository;

    @Resource
    private CmmConsumerSerialProRelationRepository cmmConsumerSerialProRelationRepository;

    @Resource
    private QueueDataRepository queueDataRepository;

    @Resource
    private CmmUnitPromotionItemRepository cmmUnitPromotionItemRepository;

    @Resource
    private CmmPromotionOrderRepository cmmPromotionOrderRepository;

    @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepository;

    @Resource
    private CmmUnitPromotionListRepository cmmUnitPromotionListRepository;

    @Resource
    private ProductTaxRepository productTaxRepository;

    @Resource
    private CmmSpecialCompanyTaxRepository cmmSpecialCompanyTaxRepository;

    @Resource
    private RegistrationDocumentManager registrationDocumentManager;

    @Resource
    private DeliveryOrderRepository deliveryOrderRepository;

    public SalesOrderVO getSalesOrderVO(Long salesOrderId) {

        return BeanMapUtils.mapTo(salesOrderRepository.findBySalesOrderId(salesOrderId), SalesOrderVO.class);
    }
    
    public List<SalesOrderItemVO> getSalesOrderItemVOList(Long salesOrderId) {

        return BeanMapUtils.mapListTo(salesOrderItemRepository.findBySalesOrderId(salesOrderId), SalesOrderItemVO.class);
    }

    public BatteryVO getBatteryVO(Long serializedProductId, String positoinSign) {

        return BeanMapUtils.mapTo(batteryRepository.findBySerializedProductIdAndPositionSign(serializedProductId, positoinSign), BatteryVO.class);
    }

    public CmmConsumerVO getCmmConsumerVO(Long consumerId) {

        return BeanMapUtils.mapTo(cmmConsumerRepository.findByConsumerId(consumerId), CmmConsumerVO.class);
    }

    public ConsumerPrivacyPolicyResultVO getConsumerPrivacyPolicyResultVO(Long consumerId) {

        return BeanMapUtils.mapTo(consumerPrivacyPolicyResultRepository.findByConsumerId(consumerId), ConsumerPrivacyPolicyResultVO.class);
    }

    public ConsumerPrivateDetailVO getConsumerPrivateDetailVO(String consumerRetrieve) {

        return BeanMapUtils.mapTo(consumerPrivateDetailRepository.findByConsumerRetrieve(consumerRetrieve), ConsumerPrivateDetailVO.class);
    }

    public SerializedProductVO getSerializedProductVO(String siteId, String frameNo) {

        return BeanMapUtils.mapTo(serializedProductRepository.findBySiteIdAndFrameNo(siteId, frameNo), SerializedProductVO.class);
    }

    public MstProductVO getMstProductVO(Long productId) {

        return BeanMapUtils.mapTo(mstProductRepository.findByProductId(productId), MstProductVO.class);
    }

    public MstFacilityVO getMstFacilityVO(Long facilityId) {

        return BeanMapUtils.mapTo(mstFacilityRepository.findByFacilityId(facilityId), MstFacilityVO.class);
    }

    public BatteryVO getBatteryVO(Long batteryId) {
        
        return BeanMapUtils.mapTo(batteryRepository.findByBatteryId(batteryId), BatteryVO.class);
    }

    public ProductStockStatusVO getProductStockStatusVO(String siteId, Long facilityId, Long productId, String productStockStatusType) {

        return BeanMapUtils.mapTo(productStockStatusRepository.findProductStockStatus(siteId, facilityId, productId, productStockStatusType), ProductStockStatusVO.class);
    }

    public void checkSerializedProductCorrelation(SDCommonCheckBO checkBO) {

        sdCommonCheckManager.checkSerializedProductCorrelation(checkBO);
    }

    public void checkBatteryStatus(String batteryNo1, String batteryNo2, Long batteryId1, Long batteryId2) {
    
        sdCommonCheckManager.checkBatteryStatus(batteryNo1, batteryNo2, batteryId1, batteryId2);
    }

    public ProductTaxVO getProductTaxVO(Long productId) {

        return BeanMapUtils.mapTo(productTaxRepository.findByProductIdAndProductClassification(productId, ProductClsType.GOODS.getCodeDbid()), ProductTaxVO.class);
    }

    public SDM030701BO save(SDM030701Form form) {

        SDM030701BO formData = form.getFormData();
        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);

        BaseConsumerForm ownerForm = new BaseConsumerForm();
        ownerForm.setSiteId(form.getSiteId());
        ownerForm.setConsumerId(formData.getUserConsumerId());
        ownerForm.setLastNm(formData.getOwnerLastNm());
        ownerForm.setMiddleNm(formData.getOwnerMiddleNm());
        ownerForm.setFirstNm(formData.getOwnerFirstNm());
        ownerForm.setMobilePhone(formData.getOwnerMobile());
        ownerForm.setSns(formData.getOwnerSns());
        ownerForm.setGender(formData.getOwnerGender());
        ownerForm.setBirthYear(formData.getOwnerBirthYear());
        ownerForm.setBirthDate(formData.getOwnerBirthDate());
        ownerForm.setPrivacyResult(formData.getPolicyResultFlag());
        ownerForm.setProvince(formData.getOwnerProvince());
        ownerForm.setDistrict(formData.getOwnerDistrict());
        ownerForm.setAddress(formData.getOwnerAddress());
        ownerForm.setEmail(formData.getOwnerEmail());
        ownerForm.setTaxCode(formData.getOwnerCusTaxCode());

        consumerManager.saveOrUpdateConsumer(ownerForm);
        consumerManager.saveOrUpdateConsumerPrivacyPolicyResult(ownerForm);
        formData.setOwnerConsumerId(ownerForm.getConsumerId());

        if (StringUtils.isNotBlankText(formData.getUserLastNm())) {
            BaseConsumerForm userForm = new BaseConsumerForm();
            userForm.setSiteId(form.getSiteId());
            userForm.setConsumerId(formData.getUserConsumerId());
            userForm.setLastNm(formData.getUserLastNm());
            userForm.setMiddleNm(formData.getUserMiddleNm());
            userForm.setFirstNm(formData.getUserFirstNm());
            userForm.setMobilePhone(formData.getUserMobile());
            userForm.setGender(formData.getUserGender());
            userForm.setBirthYear(formData.getUserBirthYear());
            userForm.setBirthDate(formData.getUserBirthDate());
            userForm.setProvince(formData.getUserProvince());
            userForm.setDistrict(formData.getUserDistrict());
    
            consumerManager.saveOrUpdateConsumer(userForm);
            formData.setUserConsumerId(userForm.getConsumerId());
        }

        SalesOrderVO salesOrderVO;
        SalesOrderItemVO salesOrderItemVO;
        if (ObjectUtils.isEmpty(formData.getSalesOrderId())) {
            salesOrderVO = SalesOrderVO.create();
            salesOrderItemVO = SalesOrderItemVO.create(form.getSiteId(), salesOrderVO.getSalesOrderId());
        } else {
            salesOrderVO = BeanMapUtils.mapTo(salesOrderRepository.findBySalesOrderId(formData.getSalesOrderId()), SalesOrderVO.class);
            List<SalesOrderItemVO> salesOrderItemVOList = BeanMapUtils.mapListTo(salesOrderItemRepository.findBySalesOrderId(formData.getSalesOrderId()), SalesOrderItemVO.class);
            salesOrderItemVO = salesOrderItemVOList.get(0);
        }

        salesOrderVO.setSiteId(form.getSiteId());
        salesOrderVO.setOrderNo(generateNoManager.generateNonSerializedItemSalesOrderNo(form.getSiteId(), formData.getPointId()));
        salesOrderVO.setOrderDate(sysDate);
        salesOrderVO.setShipDate(sysDate);
        salesOrderVO.setOrderStatus(SalesOrderStatus.WAITING_SHIPPING.getCodeDbid());
        salesOrderVO.setProductClassification(ProductClsType.GOODS.getCodeDbid());
        salesOrderVO.setOrderPriorityType(SalesOrderPriorityType.SORO.getCodeDbid());
        salesOrderVO.setOrderSourceType(ProductClsType.GOODS.getCodeDbid());
        salesOrderVO.setEntryFacilityId(formData.getPointId());
        salesOrderVO.setFacilityId(formData.getPointId());
        salesOrderVO.setOrderType(MCSalesType.SPECIALSALESORDER);
        salesOrderVO.setCmmConsumerId(formData.getOwnerConsumerId());
        salesOrderVO.setUserConsumerId(formData.getUserConsumerId());
        salesOrderVO.setConsumerNmLast(formData.getOwnerLastNm());
        salesOrderVO.setConsumerNmMiddle(formData.getOwnerMiddleNm());
        salesOrderVO.setConsumerNmFirst(formData.getOwnerFirstNm());
        salesOrderVO.setEmail(formData.getOwnerEmail());
        salesOrderVO.setMobilePhone(formData.getOwnerMobile());
        salesOrderVO.setAddress(formData.getOwnerAddress());
        salesOrderVO.setEntryPicId(form.getPersonId());
        salesOrderVO.setEntryPicNm(form.getPersonNm());
        salesOrderVO.setSalesPicId(form.getPersonId());
        salesOrderVO.setSalesPicNm(form.getPersonNm());
        salesOrderVO.setEvOrderFlag(formData.getEvFlag());
        salesOrderVO.setModelCd(formData.getModelCd());
        salesOrderVO.setModelNm(formData.getModelNm());
        salesOrderVO.setFrameNo(formData.getFrameNo());
        salesOrderVO.setEngineNo(formData.getEngineNo());
        salesOrderVO.setColorNm(formData.getColorNm());
        salesOrderVO.setSerializedProductId(formData.getSerializedProductId());
        salesOrderVO.setModelType(formData.getModelType());
        salesOrderVO.setDisplacement(formData.getDisplacement());
        salesOrderVO.setCustomerTaxCode(formData.getOwnerCusTaxCode());
        salesOrderVO.setInvoicePrintFlag(CommonConstants.CHAR_Y);
        salesOrderVO.setTotalQty(BigDecimal.ONE);
        salesOrderVO.setTotalAmt(formData.getActualAmt());
        salesOrderVO.setTotalActualQty(BigDecimal.ONE);
        salesOrderVO.setTotalActualAmt(formData.getActualAmt());

        ProductTaxVO productTaxVO = this.getProductTaxVO(formData.getProductId());
        BigDecimal taxRate = new BigDecimal("1.1");
        if (null != productTaxVO) {
            taxRate = new BigDecimal("1.08");
        }
        BigDecimal totalActualAmtNotVat = formData.getActualAmt().divide(taxRate);
        salesOrderVO.setTotalActualAmtNotVat(totalActualAmtNotVat);
        salesOrderVO.setGiftDescription(formData.getGiftDescription());
        salesOrderVO.setSpecialReduceFlag(formData.getSpecialReduceFlag());
        salesOrderVO.setPaymentMethodType(formData.getPaymentMethod());
        salesOrderRepository.save(BeanMapUtils.mapTo(salesOrderVO, SalesOrder.class));

        salesOrderItemVO.setProductId(formData.getProductId());
        salesOrderItemVO.setProductCd(formData.getModelCd());
        salesOrderItemVO.setProductNm(formData.getModelNm());
        salesOrderItemVO.setProductClassification(ProductClsType.GOODS.getCodeDbid());
        salesOrderItemVO.setOrderQty(BigDecimal.ONE);
        salesOrderItemVO.setActualQty(BigDecimal.ONE);
        salesOrderItemVO.setOrderPrioritySeq(CommonConstants.INTEGER_FIVE);
        salesOrderItemVO.setDiscountAmt(formData.getDiscountAmt());
        salesOrderItemVO.setSellingPrice(formData.getSellingPrice());
        BigDecimal sellingPriceNotVat = formData.getSellingPrice().divide(taxRate);
        salesOrderItemVO.setSellingPriceNotVat(sellingPriceNotVat);
        salesOrderItemVO.setActualAmt(salesOrderItemVO.getSellingPrice().subtract(salesOrderItemVO.getDiscountAmt()));
        BigDecimal actualAmtNotVat = salesOrderItemVO.getActualAmt().divide(taxRate);
        salesOrderItemVO.setActualAmtNotVat(actualAmtNotVat);
        salesOrderItemVO.setTaxRate(taxRate);
        salesOrderItemRepository.save(BeanMapUtils.mapTo(salesOrderItemVO, SalesOrderItem.class));

        if (!ObjectUtils.isEmpty(formData.getSalesOrderId())) {
            OrderSerializedItemVO orderSerializedItemVO = new OrderSerializedItemVO();
            orderSerializedItemVO.setSiteId(form.getSiteId());
            orderSerializedItemVO.setSerializedProductId(salesOrderVO.getSerializedProductId());
            orderSerializedItemVO.setOrderItemId(salesOrderItemVO.getSalesOrderItemId());
            orderSerializedItemVO.setSalesOrderId(salesOrderVO.getSalesOrderId());
            orderSerializedItemRepository.save(BeanMapUtils.mapTo(orderSerializedItemVO, OrderSerializedItem.class));    
        }
        formData.setSalesOrderId(salesOrderVO.getSalesOrderId());
        return formData;
    }

    private CmmConsumerSerialProRelationVO registerConsumerMotorRelation(Long serializedProId, Long consumerId, String relationType) {

        CmmConsumerSerialProRelationVO result = CmmConsumerSerialProRelationVO.create();

        result.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);
        result.setSerializedProductId(serializedProId);
        result.setConsumerId(consumerId); 
        result.setConsumerSerializedProductRelationTypeId(relationType);
        result.setOwnerFlag(StringUtils.equals(relationType, ConsumerSerialProRelationType.OWNER.getCodeDbid()) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
        result.setCreatedBy(CommonConstants.CHAR_IFS);
        result.setLastUpdatedBy(CommonConstants.CHAR_IFS);
        result.setUpdateProgram(CommonConstants.CHAR_IFS);

        return result;
    }

    private SvVinCodeTelIFBO prepareVinCodeTelIFBOByOwnerChange(Long cmmConsumerMotorRelationId, SalesOrderVO salesOrderVO) {

        SvVinCodeTelIFBO result = new SvVinCodeTelIFBO();

        result.setQueueId(cmmConsumerMotorRelationId.toString());
        result.setVinHin(salesOrderVO.getFrameNo());
        result.setTelNo(salesOrderVO.getMobilePhone());
        result.setTelLastDigits(result.getTelNo().length() >= 4 ? result.getTelNo().substring(result.getTelNo().length() - 4) : result.getTelNo());
        result.setOwnerChangedFlg(CommonConstants.CHAR_ONE);
        result.setOwnerChangedDate(ComUtil.nowDate());
        result.setSalesDate(salesOrderVO.getOrderDate());
        result.setUpdateAuthor(salesOrderVO.getLastUpdatedBy());
        result.setUpdateDate(LocalDateTime.now().toString());
        result.setCreateAuthor(salesOrderVO.getCreatedBy());
        result.setCreateDate(LocalDateTime.now().toString());
        result.setUpdateProgram(salesOrderVO.getUpdateProgram());
        result.setUpdateCounter(CommonConstants.CHAR_ZERO);

        return result;
    }

    public String getConsumerFullNm(String lastNm, String middleNm, String firstNm) {

        return new StringBuilder()
                  .append(lastNm)
                  .append(CommonConstants.CHAR_SPACE)
                  .append(middleNm)
                  .append(CommonConstants.CHAR_SPACE)
                  .append(firstNm)
                  .toString();
    }

    public SDM030701BO delivery(SDM030701Form form) {

        SDM030701BO formData = form.getFormData();
        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);
        SalesOrderVO salesOrderVO = BeanMapUtils.mapTo(salesOrderRepository.findBySalesOrderId(formData.getSalesOrderId()), SalesOrderVO.class);
        String fullNm = this.getConsumerFullNm(salesOrderVO.getConsumerNmLast(), salesOrderVO.getConsumerNmMiddle(), salesOrderVO.getConsumerNmFirst());
        CmmSpecialCompanyTaxVO cmmSpecialCompanyTaxVO = BeanMapUtils.mapTo(cmmSpecialCompanyTaxRepository.findBySpecialCompanyTaxCdAndSpecialCompanyTaxNmAndAddress(salesOrderVO.getCustomerTaxCode(), fullNm, salesOrderVO.getAddress()), CmmSpecialCompanyTaxVO.class);
        if (null != cmmSpecialCompanyTaxVO) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("DO is NOT allowed to sell MC to this customer. Please contact Sales Planning Manager for more information."));
        }
        List<SalesOrderItemVO> salesOrderItemVOList = BeanMapUtils.mapListTo(salesOrderItemRepository.findBySalesOrderId(formData.getSalesOrderId()), SalesOrderItemVO.class);
        SalesOrderItemVO salesOrderItemVO = salesOrderItemVOList.get(0);

        BaseConsumerForm ownerForm = new BaseConsumerForm();
        ownerForm.setSiteId(form.getSiteId());
        ownerForm.setConsumerId(formData.getUserConsumerId());
        ownerForm.setLastNm(formData.getOwnerLastNm());
        ownerForm.setMiddleNm(formData.getOwnerMiddleNm());
        ownerForm.setFirstNm(formData.getOwnerFirstNm());
        ownerForm.setMobilePhone(formData.getOwnerMobile());
        ownerForm.setSns(formData.getOwnerSns());
        ownerForm.setGender(formData.getOwnerGender());
        ownerForm.setBirthYear(formData.getOwnerBirthYear());
        ownerForm.setBirthDate(formData.getOwnerBirthDate());
        ownerForm.setPrivacyResult(formData.getPolicyResultFlag());
        ownerForm.setProvince(formData.getOwnerProvince());
        ownerForm.setDistrict(formData.getOwnerDistrict());
        ownerForm.setAddress(formData.getOwnerAddress());
        ownerForm.setEmail(formData.getOwnerEmail());
        ownerForm.setTaxCode(formData.getOwnerCusTaxCode());

        consumerManager.saveOrUpdateConsumer(ownerForm);
        consumerManager.saveOrUpdateConsumerPrivacyPolicyResult(ownerForm);
        formData.setOwnerConsumerId(ownerForm.getConsumerId());

        if (StringUtils.isNotBlankText(formData.getUserLastNm())) {
            BaseConsumerForm userForm = new BaseConsumerForm();
            userForm.setSiteId(form.getSiteId());
            userForm.setConsumerId(formData.getUserConsumerId());
            userForm.setLastNm(formData.getUserLastNm());
            userForm.setMiddleNm(formData.getUserMiddleNm());
            userForm.setFirstNm(formData.getUserFirstNm());
            userForm.setMobilePhone(formData.getUserMobile());
            userForm.setGender(formData.getUserGender());
            userForm.setBirthYear(formData.getUserBirthYear());
            userForm.setBirthDate(formData.getUserBirthDate());
            userForm.setProvince(formData.getUserProvince());
            userForm.setDistrict(formData.getUserDistrict());
    
            consumerManager.saveOrUpdateConsumer(userForm);
            formData.setUserConsumerId(userForm.getConsumerId());
        }
        
        SerializedProductVO serializedProductVO = this.getSerializedProductVO(form.getSiteId(), salesOrderVO.getFrameNo());

        Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = new HashMap<>();
        // S084ONHANDQTY - 1
        inventoryManager.generateStockStatusVOMapForSD(form.getSiteId()
                                                     , formData.getPointId()
                                                     , formData.getProductId()
                                                     , BigDecimal.ONE.negate()
                                                     , SdStockStatus.ONHAND_QTY.getCodeDbid()
                                                     , stockStatusVOChangeMap);
    
        // S084ALLOCATEDQTY + 1
        inventoryManager.generateStockStatusVOMapForSD(form.getSiteId()
                                                     , formData.getPointId()
                                                     , formData.getProductId()
                                                     , BigDecimal.ONE
                                                     , SdStockStatus.ALLOCATED_QTY.getCodeDbid()
                                                     , stockStatusVOChangeMap);

        inventoryManager.updateProductStockStatusByMapForSD(stockStatusVOChangeMap);

        salesOrderManager.doUnitRetailShippingCompletion(salesOrderVO, salesOrderItemVO, BigDecimal.ONE);
        salesOrderRepository.save(BeanMapUtils.mapTo(salesOrderVO, SalesOrder.class));
        salesOrderItemRepository.save(BeanMapUtils.mapTo(salesOrderItemVO, SalesOrderItem.class));

        CreateRegistrationDocumentBO model = new CreateRegistrationDocumentBO();
        model.setPointId(formData.getPointId());
        model.setCustomerId(formData.getOwnerConsumerId());
        model.setSerializedProductId(serializedProductVO.getSerializedProductId());
        model.setUseType(formData.getUseType());
        model.setOwnerType(formData.getOwnerType());
        model.setBatteryId1(formData.getBatteryId1());
        model.setBatteryId2(formData.getBatteryId2());
        cmmSerializedProductManager.doCreateRegistrationDocument(salesOrderVO, model);

        DeliveryOrderVO deliveryOrderVO = DeliveryOrderVO.create();
        DeliveryOrderItemVO deliveryOrderItemVO = DeliveryOrderItemVO.create();
        deliveryOrderManager.doUnitRetailCreateDeliveryOrder(salesOrderVO, salesOrderItemVO, deliveryOrderVO, deliveryOrderItemVO);

        deliveryOrderManager.doUnitRetailShippingCompletion(deliveryOrderVO);
        inventoryManager.doUnitRetailShippingCompletion(deliveryOrderVO, deliveryOrderItemVO, BigDecimal.ONE);
        deliveryOrderRepository.save(BeanMapUtils.mapTo(deliveryOrderVO, DeliveryOrder.class));
        deliveryOrderItemRepository.save(BeanMapUtils.mapTo(deliveryOrderItemVO, DeliveryOrderItem.class));

        ProductStockStatusVO productStockStatusVO = BeanMapUtils.mapTo(productStockStatusRepository.findProductStockStatus(form.getSiteId(), formData.getPointId(), formData.getProductId(), SdStockStatus.ONHAND_QTY.getCodeDbid()), ProductStockStatusVO.class);
        ProductCostVO productCostVO = BeanMapUtils.mapTo(productCostRepository.findByProductIdAndCostTypeAndSiteId(formData.getProductId(), CostType.RECEIVE_COST, form.getSiteId()), ProductCostVO.class);
        InventoryTransactionVO inventoryTransactionVO = inventoryManager.generateInventoryTransactionVO(form.getSiteId()
                                                                                                      , InOutType.OUT
                                                                                                      , deliveryOrderVO.getFromFacilityId()
                                                                                                      , deliveryOrderVO.getFromFacilityId()
                                                                                                      , deliveryOrderVO.getToFacilityId()
                                                                                                      , deliveryOrderItemVO.getProductId()
                                                                                                      , deliveryOrderItemVO.getProductCd()
                                                                                                      , deliveryOrderItemVO.getProductNm()
                                                                                                      , deliveryOrderVO.getInventoryTransactionType()
                                                                                                      , deliveryOrderItemVO.getDeliveryQty()
                                                                                                      , productStockStatusVO.getQuantity()
                                                                                                      , deliveryOrderItemVO.getProductCost()
                                                                                                      , deliveryOrderVO.getDeliveryOrderId()
                                                                                                      , deliveryOrderVO.getDeliveryOrderNo()
                                                                                                      , deliveryOrderVO.getFromOrganizationId()
                                                                                                      , deliveryOrderVO.getToOrganizationId()
                                                                                                      , null
                                                                                                      , productCostVO
                                                                                                      , null
                                                                                                      , form.getPersonId()
                                                                                                      , form.getPersonNm()
                                                                                                      , ProductClsType.GOODS.getCodeDbid());
        inventoryTransactionRepository.save(BeanMapUtils.mapTo(inventoryTransactionVO, InventoryTransaction.class));

        serializedProductManager.doUnitRetailShippingCompletion(deliveryOrderItemVO, serializedProductVO, form.getPersonNm());
        InvoiceVO invoiceVO = invoiceManager.doUnitRetailCreateInvoice(deliveryOrderVO);

        List<RemindScheduleVO> remindScheduleVOList = remindManager.generateSalesFollowUpRemind(salesOrderVO);
        remindScheduleRepository.saveInBatch(BeanMapUtils.mapListTo(remindScheduleVOList, RemindSchedule.class));

        remindManager.setupSerializedProFscRemind(salesOrderVO.getSiteId(), salesOrderVO.getFacilityId(), salesOrderVO.getSalesOrderId(), serializedProductVO.getCmmSerializedProductId(), sysDate, salesOrderItemVO.getServiceDemandId());
        
        CmmConsumerSerialProRelationVO owner = this.registerConsumerMotorRelation(serializedProductVO.getCmmSerializedProductId(), formData.getOwnerConsumerId(), ConsumerSerialProRelationType.OWNER.getCodeDbid());
        cmmConsumerSerialProRelationRepository.save(BeanMapUtils.mapTo(owner, CmmConsumerSerialProRelation.class));
        
        QueueDataVO queueDataVO = QueueDataVO.create(salesOrderVO.getSiteId(), InterfCode.DMS_TO_MYY_SV_VINCODETEL, "cmm_consumer_serial_pro_relation", owner.getConsumerSerializedProductRelationId(), JsonUtils.toString(List.of(this.prepareVinCodeTelIFBOByOwnerChange(owner.getConsumerSerializedProductRelationId(), salesOrderVO)).stream().toList()));
        queueDataRepository.save(BeanMapUtils.mapTo(queueDataVO, QueueData.class));

        if (StringUtils.isNotBlankText(formData.getUserLastNm())) {
            CmmConsumerSerialProRelationVO user = this.registerConsumerMotorRelation(serializedProductVO.getCmmSerializedProductId(), formData.getUserConsumerId(), ConsumerSerialProRelationType.USER.getCodeDbid());
            cmmConsumerSerialProRelationRepository.save(BeanMapUtils.mapTo(user, CmmConsumerSerialProRelation.class));
        }

        registrationDocumentManager.sendToSW(model.getCmmRegistrationDocument(), InterfCode.XM03_INTERF_O_SV_REG_DOC);

        formData.setPromotionFlag(CommonConstants.CHAR_N);
        CmmUnitPromotionItemVO cmmUnitPromotionItemVO = cmmUnitPromotionItemRepository.getPromotionInfoByFrameNo(formData.getFrameNo());
        // 判断是否为促销车
        if (null != cmmUnitPromotionItemVO) {
            CmmSiteMasterVO cmmSiteMasterVO = BeanMapUtils.mapTo(cmmSiteMasterRepository.findFirstBySiteId(form.getSiteId()), CmmSiteMasterVO.class);
            CmmPromotionOrderVO cmmPromotionOrderVO = new CmmPromotionOrderVO();
            cmmPromotionOrderVO.setPromotionListId(cmmUnitPromotionItemVO.getPromotionListId());
            cmmPromotionOrderVO.setSiteId(form.getSiteId());
            cmmPromotionOrderVO.setSiteNm(cmmSiteMasterVO.getSiteNm());
            cmmPromotionOrderVO.setFacilityId(formData.getFacilityId());
            cmmPromotionOrderVO.setFacilityCd(formData.getFacilityCd());
            cmmPromotionOrderVO.setFacilityNm(formData.getFacilityNm());
            cmmPromotionOrderVO.setLocalOrderId(salesOrderVO.getSalesOrderId());
            cmmPromotionOrderVO.setLocalOrderNo(salesOrderVO.getOrderNo());
            cmmPromotionOrderVO.setCmmProductId(formData.getProductId());
            cmmPromotionOrderVO.setProductCd(formData.getModelCd());
            cmmPromotionOrderVO.setProductNm(formData.getModelNm());
            cmmPromotionOrderVO.setLocalSerialProductId(serializedProductVO.getSerializedProductId());
            cmmPromotionOrderVO.setCmmSerialProductId(serializedProductVO.getCmmSerializedProductId());
            cmmPromotionOrderVO.setCmmCustomerId(ownerForm.getConsumerId());
            cmmPromotionOrderVO.setCustomerNm(this.getConsumerFullNm(ownerForm.getLastNm(), ownerForm.getMiddleNm(), ownerForm.getFirstNm()));
            cmmPromotionOrderVO.setFrameNo(formData.getFrameNo());
            cmmPromotionOrderVO.setOrderDate(sysDate);
            cmmPromotionOrderVO.setLocalInvoiceId(invoiceVO.getInvoiceId());
            cmmPromotionOrderVO.setLocalInvoiceNo(invoiceVO.getInvoiceNo());
            cmmPromotionOrderVO.setLocalDeliveryOrderId(deliveryOrderVO.getDeliveryOrderId());
            cmmPromotionOrderVO.setLocalDeliveryOrderNo(deliveryOrderVO.getDeliveryOrderNo());
            cmmPromotionOrderVO.setSalesMethod("SalesToCustomer");
            cmmPromotionOrderVO.setSalesPic(salesOrderVO.getEntryPicId());
            cmmPromotionOrderVO.setJugementStatus(JudgementStatus.WAITINGUPLOAD);
            cmmPromotionOrderVO.setCanEnjoyPromotion(CommonConstants.CHAR_ONE);
            cmmPromotionOrderRepository.save(BeanMapUtils.mapTo(cmmPromotionOrderVO, CmmPromotionOrder.class));

            formData.setPromotionFlag(CommonConstants.CHAR_Y);
            formData.setPromotionOrderId(cmmPromotionOrderVO.getPromotionOrderId());

            CmmUnitPromotionListVO cmmUnitPromotionListVO = BeanMapUtils.mapTo(cmmUnitPromotionListRepository.findByPromotionListId(cmmUnitPromotionItemVO.getPromotionListId()), CmmUnitPromotionListVO.class);
            formData.setPromotion(cmmUnitPromotionListVO.getPromotionCd() + CommonConstants.CHAR_SPACE + cmmUnitPromotionListVO.getPromotionNm());
        }
        formData.setSalesOrderId(salesOrderVO.getSalesOrderId());
        return formData;
    }
}
