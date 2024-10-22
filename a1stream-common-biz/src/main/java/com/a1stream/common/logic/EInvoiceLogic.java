package com.a1stream.common.logic;

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.bo.EInvoiceInvBO;
import com.a1stream.common.bo.EInvoiceInvoicesBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.manager.BaseTnvoiceManager;
import com.a1stream.common.model.ConstantsBO;
import com.a1stream.common.model.EInvoiceBO;
import com.a1stream.common.model.InvoiceSoapResponseResult;
import com.a1stream.domain.bo.common.EInvProductsBO;
import com.a1stream.domain.bo.common.EInvoiceInvoiceBO;
import com.a1stream.domain.bo.common.EInvoiceProductsBO;
import com.a1stream.domain.entity.CmmMessageRemind;
import com.a1stream.domain.entity.QueueEinvoice;
import com.a1stream.domain.entity.QueueTaxAuthority;
import com.a1stream.domain.repository.CmmMessageRemindRepository;
import com.a1stream.domain.repository.EInvoiceMasterRepository;
import com.a1stream.domain.repository.ProductTaxRepository;
import com.a1stream.domain.repository.QueueEinvoiceRepository;
import com.a1stream.domain.repository.QueueTaxAuthorityRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.repository.ServiceOrderItemOtherBrandRepository;
import com.a1stream.domain.repository.ServiceOrderJobRepository;
import com.a1stream.domain.repository.ServiceOrderRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.CmmMessageRemindVO;
import com.a1stream.domain.vo.EInvoiceMasterVO;
import com.a1stream.domain.vo.ProductTaxVO;
import com.a1stream.domain.vo.QueueEinvoiceVO;
import com.a1stream.domain.vo.QueueTaxAuthorityVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.ServiceOrderItemOtherBrandVO;
import com.a1stream.domain.vo.ServiceOrderJobVO;
import com.a1stream.domain.vo.ServiceOrderVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

@Component
public class EInvoiceLogic {

    @Resource
    private QueueEinvoiceRepository queueEinvoiceRepository;

    @Resource
    private ConstantsLogic constantsLogic;

    @Resource
    private EInvoiceMasterRepository  eInvoiceMasterRepository;

    @Resource
    private ServiceOrderRepository serviceOrderRepository;

    @Resource
    private SalesOrderItemRepository salesOrderItemRepository;

    @Resource
    private SalesOrderRepository salesOrderRepository;

    @Resource
    private ProductTaxRepository productTaxRepository;

    @Resource
    private SystemParameterRepository systemParameterRepository;

    @Resource
    private QueueTaxAuthorityRepository queueTaxAuthorityRepository;

    @Resource
    private ServiceOrderItemOtherBrandRepository serviceOrderItemOtherBrandRepository;

    @Resource
    private ServiceOrderJobRepository serviceOrderJobRepository;

    @Resource
    private BaseTnvoiceManager baseTnvoiceManager;

    @Resource
    private CmmMessageRemindRepository cmmMessageRemindRepository;

    private static String WEBSERVICE_SD="webservice_SD";
    private static String WEBSERVICE_SP="webservice_SP";
    private static String WEBSERVICE_SW="webservice_SW";
    
    public void sendEInvoice(String siteId,List<QueueEinvoiceVO> requestList) throws Exception{

        List<Long> dbIdList = requestList.stream().map(QueueEinvoiceVO::getDbId).collect(Collectors.toList());

         //初始话数据
        List<QueueEinvoiceVO> mcInvoiceList = new ArrayList<>();
        List<QueueEinvoiceVO> saleInvoiceList = new ArrayList<>();
        List<QueueEinvoiceVO> serviceInvoiceList = new ArrayList<>();

        if(!dbIdList.isEmpty()){

            //查出queue_einvoice中5分钟前等待发送的和error的列表
            List<QueueEinvoiceVO> queueEinvoiceLists = BeanMapUtils.mapListTo(queueEinvoiceRepository.findBySiteIdAndDbIdIn(siteId, dbIdList), QueueEinvoiceVO.class);

            //根据type分类
            this.collectInvoiceByType(queueEinvoiceLists,mcInvoiceList,saleInvoiceList,serviceInvoiceList);

            Map<String, String> interfCodeMap = this.getInterfCodeMap();

            EInvoiceMasterVO eInvocie = BeanMapUtils.mapTo(eInvoiceMasterRepository.findBySiteId(siteId), EInvoiceMasterVO.class);

            String area = eInvocie.getArea();

            if(!mcInvoiceList.isEmpty()){

                this.createEInvoiceModelAndSendReceive(eInvocie,interfCodeMap,siteId,mcInvoiceList,PJConstants.ProductClsType.GOODS.getCodeDbid(),area);
            }

            if(!saleInvoiceList.isEmpty()){

                this.createEInvoiceModelAndSendReceive(eInvocie,interfCodeMap,siteId,saleInvoiceList,PJConstants.ProductClsType.PART.getCodeDbid(),area);
            }

            if(!serviceInvoiceList.isEmpty()){

                this.createEInvoiceModelAndSendReceive(eInvocie,interfCodeMap,siteId,serviceInvoiceList,PJConstants.ProductClsType.SERVICE.getCodeDbid(),area);
            }
        }
    }

    private void collectInvoiceByType(List<QueueEinvoiceVO> queueEinvoiceVOs,
                                      List<QueueEinvoiceVO> mcInvoices,
                                      List<QueueEinvoiceVO> saleInvoices,
                                      List<QueueEinvoiceVO> serviceInvoices){

        for (QueueEinvoiceVO queueEinvoiceVO : queueEinvoiceVOs) {
            
            if(StringUtils.equals(queueEinvoiceVO.getInterfCode(), PJConstants.ProductClsType.GOODS.getCodeDbid())){

                mcInvoices.add(queueEinvoiceVO);
            }else if(StringUtils.equals(queueEinvoiceVO.getInterfCode(), PJConstants.ProductClsType.PART.getCodeDbid())){

                saleInvoices.add(queueEinvoiceVO);
            }else if(StringUtils.equals(queueEinvoiceVO.getInterfCode(), PJConstants.ProductClsType.SERVICE.getCodeDbid())){

                serviceInvoices.add(queueEinvoiceVO);
            }
        }

    }

    private Map<String, String> getInterfCodeMap(){

        Map<String, Field[]> constantsFieldMap =  PJConstants.ConstantsFieldMap;
        List<ConstantsBO> constants = constantsLogic.getConstantsData(constantsFieldMap.get(PJConstants.ProductClsType.CODE_ID));
        Map<String, String> map = new HashMap<>();

        for(ConstantsBO item : constants) {
            map.put(item.getCodeDbid(), item.getCodeData2());
        }
        return map;

    }



    private void createEInvoiceModelAndSendReceive(EInvoiceMasterVO eInvoiceMasterVO,
                                                   Map<String, String> interfCodeMap,
                                                   String siteId,
                                                   List<QueueEinvoiceVO> invoices,
                                                   String interfCode,
                                                   String area) throws Exception {

        EInvoiceBO result = new EInvoiceBO();
        //设置邮件参数
        setEinvoiceParameter(result, eInvoiceMasterVO);

        for (QueueEinvoiceVO data : invoices) {
            
            Long invoiceId = data.getRelatedInvoiceId();
            Long orderId = data.getRelatedOrderId();

            //获取XML文本内容，并在服务器上进行保存
            String xmlData = this.getXmlInvData(data, invoiceId, orderId, siteId, interfCode);
            if(StringUtils.isNotBlank(xmlData)){
                result.setXmlInvData(xmlData);
                //this.updateStatus(data,baseTnvoiceManager.getEInvoiceResult(result, interfCode, area), area);
            }
        }
    }

    private void setEinvoiceParameter(EInvoiceBO eInvoiceModel,EInvoiceMasterVO eInvoiceMasterVO){
        
        eInvoiceModel.setAccount(eInvoiceMasterVO.getAccount());
        eInvoiceModel.setACpass(eInvoiceMasterVO.getAcpass());
        eInvoiceModel.setUsername(eInvoiceMasterVO.getUserName());
        eInvoiceModel.setPass(eInvoiceMasterVO.getUserpass());
        eInvoiceModel.setSerialSD(eInvoiceMasterVO.getSerialsd());
        eInvoiceModel.setSerialSPSV(eInvoiceMasterVO.getSerialspsv());
        eInvoiceModel.setACpass(eInvoiceMasterVO.getAcpass());
        eInvoiceModel.setPatternSD(eInvoiceMasterVO.getPatternsd());
        eInvoiceModel.setPatternSPSV(eInvoiceMasterVO.getPatternspsv());
        eInvoiceModel.setConvert(Integer.parseInt(eInvoiceMasterVO.getConvert()));
    }

    private static final String CHAR_SVM0102 = "SVM0102_01";
    private static final String CHAR_SVM0213 = "SVM0213_02";
    private static final String CHAR_SVM0303 = "SPM0303_01";
    private static final String CHAR_SVM0120 = "SVM0120_01";
    private static final String CHAR_TIMETASK = "timedtask";
    private static final String CHAR_DBUPDATE = "dbupdate";
    private static final String CHAR_P8       = "P8";
    private static final String CHAR_P10      = "P10";
    private static final String CHAR_P        = "P";
    private static final String CHAR_J        = "J";

    private String getXmlInvData(QueueEinvoiceVO queueEInvoiceVO,Long invoiceId,Long orderId,String siteId,String interCode){

        SystemParameterVO parameterDate = BeanMapUtils.mapTo(systemParameterRepository.findBySystemParameterTypeId(MstCodeConstants.SystemParameterType.TAXPERIOD), SystemParameterVO.class);

        String date = parameterDate.getParameterValue();
 
        List<EInvoiceInvBO> invModelList = new ArrayList<>();
        EInvoiceInvoicesBO invoicesModel = new EInvoiceInvoicesBO();
        EInvoiceInvBO invModel = new EInvoiceInvBO();
        StringBuilder invoicesXML = new StringBuilder();
    
        if(StringUtils.equals(interCode, PJConstants.ProductClsType.SERVICE.getCodeDbid())){

            ServiceOrderVO serviceOrder = BeanMapUtils.mapTo(serviceOrderRepository.findByServiceOrderId(orderId), ServiceOrderVO.class);

            EInvoiceInvBO invPartsModel = new EInvoiceInvBO();
            EInvoiceInvBO invJobModel = new EInvoiceInvBO();
            EInvoiceInvBO invPartsModelP8 = new EInvoiceInvBO();
            EInvoiceInvBO invPartsModelP10 = new EInvoiceInvBO();
            List<EInvoiceProductsBO>  serviceProductsModelList = new ArrayList<>();
            List<EInvoiceProductsBO>  serviceProductsModelList8 = new ArrayList<>();
            List<EInvoiceProductsBO>  serviceProductsModelList10 = new ArrayList<>();
            List<EInvoiceProductsBO>  jobModelList = new ArrayList<>();
            List serviceItem = new ArrayList<>();
            String product = CommonConstants.CHAR_ZERO;
            String job = CommonConstants.CHAR_ONE;

            Set<String> updates = Set.of(CHAR_SVM0102, CHAR_SVM0213,CHAR_SVM0303, CHAR_TIMETASK, CHAR_DBUPDATE);

            if(updates.contains(serviceOrder.getUpdateProgram())){

                List<SalesOrderItemVO> items = BeanMapUtils.mapListTo(salesOrderItemRepository.findBySalesOrderIdAndSiteId(orderId,siteId), SalesOrderItemVO.class);

                if(!items.isEmpty()){

                    //将item通过不同的税率进行区分
                    List<Long> list8 = new ArrayList<>();
                    List<Long> list10 = new ArrayList<>();

                    Set<Long> partsIds = items.stream().map(SalesOrderItemVO::getProductId).collect(Collectors.toSet());
                    List<ProductTaxVO> productTaxList = BeanMapUtils.mapListTo(productTaxRepository.findByProductIdIn(partsIds), ProductTaxVO.class);

                    Map<Long, BigDecimal> productMaps = productTaxList.stream().collect(Collectors.toMap(ProductTaxVO::getProductId, ProductTaxVO::getTaxRate));

                    for(SalesOrderItemVO item : items){
                        
                        BigDecimal taxRate = productMaps.get(item.getProductId());
                        String orderDate = serviceOrder.getOrderDate();

                        if(taxRate != null && orderDate.compareTo(date) >=0){
                            list8.add(item.getProductId());
                        }else{
                            list10.add(item.getProductId());
                        }
                    }

                    if(!list8.isEmpty()){
                        serviceProductsModelList8 = salesOrderItemRepository.getServiceProductsModelsForParts(list8, job, orderId);
                    }

                    if(!list10.isEmpty()){
                        serviceProductsModelList10 = salesOrderItemRepository.getServiceProductsModelsForParts(list10, job, orderId);
                    }

                    if(!serviceProductsModelList8.isEmpty()){

                        String key = invoiceId + CHAR_P8;
                        invPartsModelP8 = this.getInvModelData(orderId, interCode, serviceProductsModelList8, product, serviceOrder, null, key, CommonConstants.CHAR_Y)  ; 
                        invModelList.add(invPartsModelP8);
                        createQueueTaxAuthority(queueEInvoiceVO,key);
                    }
                    if(!serviceProductsModelList10.isEmpty()){

                        String key  = invoiceId + CHAR_P10;
                        invPartsModelP10 = this.getInvModelData(orderId, interCode, serviceProductsModelList10, product, serviceOrder, null, key, CommonConstants.CHAR_Y)  ;
                        invModelList.add(invPartsModelP10);
                        createQueueTaxAuthority(queueEInvoiceVO,key);
                    }
                }
            }else if(StringUtils.equals(serviceOrder.getUpdateProgram(), CHAR_SVM0120)){

                List<ServiceOrderItemOtherBrandVO> item = BeanMapUtils.mapListTo(serviceOrderItemOtherBrandRepository.findByServiceOrderIdAndProductClassificationAndSiteId(orderId, PJConstants.ProductClsType.PART.getCodeDbid(), siteId),
                                                                                 ServiceOrderItemOtherBrandVO.class);
                if(!item.isEmpty()){
                    serviceProductsModelList = serviceOrderItemOtherBrandRepository.getServiceProductsForOtherBrandModels(orderId,product);
                }

                if(!serviceProductsModelList.isEmpty()){
                    String key = invoiceId + CHAR_P;
                    invPartsModel = this.getInvModelData(orderId, interCode, serviceProductsModelList, product, serviceOrder, null, key, CommonConstants.CHAR_Y);
                    invModelList.add(invPartsModel);
                    createQueueTaxAuthority(queueEInvoiceVO,key);
                }
            }

            // get job xml ,in case settle type ='S013CUSTOMER'
            if(updates.contains(serviceOrder.getUpdateProgram())){

                serviceItem = BeanMapUtils.mapListTo(serviceOrderJobRepository.findByServiceOrderIdAndSiteIdAndSettleTypeId(orderId, siteId, PJConstants.SettleType.CUSTOMER.getCodeDbid()),ServiceOrderJobVO.class);
            }else if(StringUtils.equals(serviceOrder.getUpdateProgram(), CHAR_SVM0120)){

                serviceItem = BeanMapUtils.mapListTo(serviceOrderItemOtherBrandRepository.findByServiceOrderIdAndProductClassificationAndSiteIdAndSettleType(orderId, PJConstants.ProductClsType.SERVICE.getCodeDbid(), siteId, PJConstants.SettleType.CUSTOMER.getCodeDbid()),ServiceOrderItemOtherBrandVO.class);
            }

            if(!serviceItem.isEmpty()){

                if(updates.contains(serviceOrder.getUpdateProgram())){

                    jobModelList = serviceOrderRepository.getServiceProductsModels(orderId);
                }else if (StringUtils.equals(serviceOrder.getUpdateProgram(), CHAR_SVM0120)){

                    jobModelList = serviceOrderItemOtherBrandRepository.getServiceProductsForOtherBrandModels(orderId, job);
                }

                if(!jobModelList.isEmpty()){

                    String key = invoiceId + CHAR_J;
                    invJobModel = getInvModelData(orderId, interCode, jobModelList, job, serviceOrder, null, key, CommonConstants.CHAR_Y);
                    invModelList.add(invJobModel);
                }
            }
        }else if(StringUtils.equals(interCode, PJConstants.ProductClsType.PART.getCodeDbid())){

            SalesOrderVO  salesOrder = BeanMapUtils.mapTo(salesOrderRepository.findBySalesOrderId(orderId), SalesOrderVO.class);
            List<SalesOrderItemVO> items = BeanMapUtils.mapListTo(salesOrderItemRepository.findBySalesOrderIdAndSiteId(orderId, siteId),SalesOrderItemVO.class);

            //将item通过不同的税率进行区分
            List<Long> list8 = new ArrayList<>();
            List<Long> list10 = new ArrayList<>();
            EInvoiceInvBO invPartsModelP8 = new EInvoiceInvBO();
            EInvoiceInvBO invPartsModelP10 = new EInvoiceInvBO();
            List<EInvoiceProductsBO> productsModelList8 = new ArrayList<>();
            List<EInvoiceProductsBO> productsModelList10 = new ArrayList<>();
            String product = CommonConstants.CHAR_ZERO;

            Set<Long> partsIds = items.stream().map(SalesOrderItemVO::getProductId).collect(Collectors.toSet());
            List<ProductTaxVO> productTaxList = BeanMapUtils.mapListTo(productTaxRepository.findByProductIdIn(partsIds), ProductTaxVO.class);
        
            Map<Long, BigDecimal> productMaps = productTaxList.stream().collect(Collectors.toMap(ProductTaxVO::getProductId, ProductTaxVO::getTaxRate));

            for(SalesOrderItemVO item : items){
    
                BigDecimal taxRate = productMaps.get(item.getProductId());
                String orderDate = salesOrder.getOrderDate();
                if(taxRate != null && orderDate.compareTo(date) >=0){
                    list8.add(item.getProductId());
                }else{
                    list10.add(item.getProductId());
                }

                if(!list8.isEmpty()){
                    productsModelList8 = salesOrderItemRepository.getServiceProductsModelsForParts(list8, product, orderId);
                }

                if(!list10.isEmpty()){
                    productsModelList10 = salesOrderItemRepository.getServiceProductsModelsForParts(list10, product, orderId);
                }

                if(!productsModelList8.isEmpty()){
                    String key = invoiceId + CHAR_P8;
                    invPartsModelP8 = this.getInvModelData(orderId, interCode, productsModelList8, product, null, salesOrder, key, CommonConstants.CHAR_N)  ; 
                    invModelList.add(invPartsModelP8);
                    createQueueTaxAuthority(queueEInvoiceVO,key);
                }
                if(!productsModelList10.isEmpty()){
                    String key  = invoiceId + CHAR_P10;
                    invPartsModelP10 = this.getInvModelData(orderId, interCode, productsModelList10, product, null, salesOrder, key, CommonConstants.CHAR_N)  ;
                    invModelList.add(invPartsModelP10);
                    createQueueTaxAuthority(queueEInvoiceVO,key);
                }
            }
        }else if(StringUtils.equals(interCode, PJConstants.ProductClsType.SERVICE.getCodeDbid())){

            ServiceOrderVO serviceOrder = BeanMapUtils.mapTo(serviceOrderRepository.findByServiceOrderId(orderId), ServiceOrderVO.class);
            invModel = this.getInvModelData(orderId, interCode, null, null, serviceOrder, null, null, CommonConstants.CHAR_Y);
            invModelList.add(invModel);
            String key = CommonConstants.CHAR_BLANK;
            createQueueTaxAuthority(queueEInvoiceVO, key);
        }

        if(!invModelList.isEmpty()){

            invoicesModel.setInvModels(invModelList);
            //存储文件
            invoicesXML.append(transforXmlData(invoicesModel));

            return invoicesXML.toString();
        }else{

            return null;
        }

    }



    private static final String CHAR_SDM0304 = "SDM0304_01";

    private EInvoiceInvBO getInvModelData(Long orderId,
                                         String interCode,
                                         List<EInvoiceProductsBO> detailModel,
                                         String type,
                                         ServiceOrderVO serviceOrderVO,
                                         SalesOrderVO salesOrder,
                                         String key,
                                         String serviceOrSalesFlag){

        EInvoiceInvBO invModel = new EInvoiceInvBO();
        EInvoiceInvoiceBO invoiceModel = new EInvoiceInvoiceBO();
        EInvProductsBO productModel = new EInvProductsBO();
        String flag = CommonConstants.CHAR_N;
        //Y代表Service，N代表Sales
        if(StringUtils.equals(serviceOrSalesFlag,CommonConstants.CHAR_Y)){
            if(StringUtils.isNotBlank(serviceOrderVO.getEvFlag())){
                flag = serviceOrderVO.getEvFlag();
            }
        }else{
            if(StringUtils.isNotBlank(salesOrder.getEvOrderFlag())){
                flag = salesOrder.getEvOrderFlag();
            }
        }

        invModel.setKey(key);
        if(StringUtils.equals(interCode, PJConstants.ProductClsType.GOODS.getCodeDbid())){

            if(StringUtils.equals(serviceOrderVO.getUpdateProgram(), CHAR_SDM0304)){


                invoiceModel = salesOrderRepository.getSpecialMCInvoiceinfo(orderId);

                List<EInvoiceProductsBO> mcProductsModelList = salesOrderRepository.getMCSpecialProductsInfo(orderId,flag);

                if(StringUtils.equals(invoiceModel.getCusName(), CommonConstants.CHAR_BLANK) || StringUtils.isBlank(invoiceModel.getCusAddress())){

                    invoiceModel.setCusAddress(CommonConstants.CHAR_BLANK);
                }
                productModel.setProducts(mcProductsModelList);
                invoiceModel.setProducts(productModel);
                invoiceModel.setNguonDuLieu(WEBSERVICE_SD);

            }else{

                invoiceModel = salesOrderRepository.getMCInvoiceinfo(orderId);

                List<EInvoiceProductsBO> mcProductsModelList = salesOrderRepository.getMCProductsInfo(orderId,flag);

                if(StringUtils.equals(invoiceModel.getCusName(), CommonConstants.CHAR_BLANK) || StringUtils.isBlank(invoiceModel.getCusAddress())){

                    invoiceModel.setCusAddress(CommonConstants.CHAR_BLANK);
                }
                productModel.setProducts(mcProductsModelList);
                invoiceModel.setProducts(productModel);
                invoiceModel.setNguonDuLieu(WEBSERVICE_SD);
            }
        }else if(StringUtils.equals(interCode, PJConstants.ProductClsType.PART.getCodeDbid())){

            invoiceModel = setInvoiceModelForSales(orderId, detailModel);
            invoiceModel.setNguonDuLieu(WEBSERVICE_SP);

        }else if(StringUtils.equals(interCode, PJConstants.ProductClsType.SERVICE.getCodeDbid()) && detailModel.size() > CommonConstants.INTEGER_ONE){

                invoiceModel = setInvoiceModelForService(orderId, serviceOrderVO.getUpdateProgram(), detailModel);
                if(StringUtils.equals(type, CommonConstants.CHAR_ONE)){
                    invoiceModel.setNguonDuLieu(WEBSERVICE_SW);
                }else{
                    invoiceModel.setNguonDuLieu(WEBSERVICE_SP);
                }
            }

        BigDecimal amount = new BigDecimal(invoiceModel.getAmount());
        invoiceModel.setAmountInWords(amount == null ? CommonConstants.CHAR_BLANK : VietnamCurrencyConvertorLogic.convert(new BigDecimal(invoiceModel.getAmount())));
        invoiceModel.setDonViDieuDong(CommonConstants.CHAR_BLANK);
        invoiceModel.setVeViec(CommonConstants.CHAR_BLANK);
        invoiceModel.setNguoiVanChuyen(CommonConstants.CHAR_BLANK);
        invoiceModel.setSoHopDong(CommonConstants.CHAR_BLANK);
        invoiceModel.setPhuongTienvc(CommonConstants.CHAR_BLANK);
        invoiceModel.setXuatTaiKho(CommonConstants.CHAR_BLANK);
        invoiceModel.setNhapTaiKho(CommonConstants.CHAR_BLANK);
        invoiceModel.setPycgh(CommonConstants.CHAR_BLANK);
        invoiceModel.setPxnddh(CommonConstants.CHAR_BLANK);
        invoiceModel.setProductLine(CommonConstants.CHAR_BLANK);
        invoiceModel.setNoteYamaha(CommonConstants.CHAR_BLANK);
        invoiceModel.setExtra5(CommonConstants.CHAR_BLANK);
        invoiceModel.setExtra6(CommonConstants.CHAR_BLANK);
        invoiceModel.setDiaChiGiao(CommonConstants.CHAR_BLANK);
        invoiceModel.setDealerCode(CommonConstants.CHAR_BLANK);
        invoiceModel.setConsigneeCode(CommonConstants.CHAR_BLANK);
        invoiceModel.setInvoiceCKs(CommonConstants.CHAR_BLANK);
        invModel.setInvoice(invoiceModel);

        return invModel;
    }

    private EInvoiceInvoiceBO setInvoiceModelForService(Long orderId, String programId, List<EInvoiceProductsBO> productsModelList){

        EInvoiceInvoiceBO invoiceModel = new EInvoiceInvoiceBO();
        EInvProductsBO productModel = new EInvProductsBO();

        invoiceModel = serviceOrderRepository.getInvoiceinfoForService(orderId,programId);

        if(StringUtils.equals(invoiceModel.getCusName(), CommonConstants.CHAR_BLANK) || StringUtils.isBlank(invoiceModel.getCusAddress())){
            invoiceModel.setCusAddress(CommonConstants.CHAR_BLANK);
        }

        BigDecimal vatRate = divideBigDecimal(new BigDecimal(invoiceModel.getVatRate()), new BigDecimal(CommonConstants.INTEGER_HUNDRED)).setScale(CommonConstants.INTEGER_TWO,RoundingMode.HALF_UP);
        Integer total =productsModelList.stream().mapToInt(EInvoiceProductsBO::getAmount).sum();
        BigDecimal vateAmount = multiplyBigDecimal(new BigDecimal(total),vatRate).setScale(CommonConstants.INTEGER_ZERO,RoundingMode.HALF_UP);

        productModel.setProducts(productsModelList);
        invoiceModel.setProducts(productModel);
        invoiceModel.setTotal(total);
        invoiceModel.setAmount(total.intValue() + vateAmount.intValue());
        invoiceModel.setVatAmount(vateAmount.intValue());
        invoiceModel.setAmountInWords(VietnamCurrencyConvertorLogic.convert(new BigDecimal(total.intValue()+vateAmount.intValue())));

        return invoiceModel;
    }

    private EInvoiceInvoiceBO setInvoiceModelForSales(Long orderId, List<EInvoiceProductsBO> productsModelList){

        EInvoiceInvoiceBO invoiceModel = new EInvoiceInvoiceBO();
        EInvProductsBO productModel = new EInvProductsBO();
        invoiceModel = salesOrderRepository.getInvoiceinfoForParts(orderId);
        
        if(StringUtils.equals(invoiceModel.getCusName(), CommonConstants.CHAR_BLANK) || StringUtils.isBlank(invoiceModel.getCusAddress())){
            invoiceModel.setCusAddress(CommonConstants.CHAR_BLANK);
        }

        BigDecimal vatRate = divideBigDecimal(new BigDecimal(invoiceModel.getVatRate()), new BigDecimal(CommonConstants.INTEGER_HUNDRED)).setScale(CommonConstants.INTEGER_TWO,RoundingMode.HALF_UP);
        
        Integer total =productsModelList.stream().mapToInt(EInvoiceProductsBO::getAmount).sum();

        BigDecimal vateAmount = multiplyBigDecimal(new BigDecimal(total),vatRate).setScale(0,RoundingMode.HALF_UP);
        productModel.setProducts(productsModelList);
        invoiceModel.setProducts(productModel);
        invoiceModel.setTotal(total);
        invoiceModel.setVatAmount(vateAmount.intValue());
        invoiceModel.setAmount(total.intValue() + vateAmount.intValue());
        invoiceModel.setAmountInWords(VietnamCurrencyConvertorLogic.convert(new BigDecimal(total.intValue()+vateAmount.intValue())));

        return invoiceModel;
    }

    private void createQueueTaxAuthority(QueueEinvoiceVO einvoiceVO,String key){

        QueueTaxAuthorityVO queueTaxAuthority = new QueueTaxAuthorityVO();

        queueTaxAuthority.setSiteId(einvoiceVO.getSiteId());
        queueTaxAuthority.setRelatedOrderId(einvoiceVO.getRelatedOrderId());
        queueTaxAuthority.setRelatedOrderNo(einvoiceVO.getRelatedOrderNo());
        queueTaxAuthority.setRelatedInvoiceId(einvoiceVO.getRelatedInvoiceId());
        queueTaxAuthority.setRelatedInvoiceNo(einvoiceVO.getRelatedInvoiceNo());
        if(StringUtils.isBlank(key)){
            queueTaxAuthority.setFkeys(einvoiceVO.getRelatedInvoiceId().toString());
        }else{
            queueTaxAuthority.setFkeys(key);
        }
        queueTaxAuthority.setInterfCode(einvoiceVO.getInterfCode());
        queueTaxAuthority.setInvoiceDate(einvoiceVO.getInvoiceDate());
        queueTaxAuthority.setStatus(PJConstants.InterfaceStatus.WAITINGSEND.getCodeDbid());
        queueTaxAuthority.setStatusMessage(CommonConstants.CHAR_BLANK);
        queueTaxAuthority.setServerNm(einvoiceVO.getServerNm());
        queueTaxAuthority.setSendTimes(einvoiceVO.getSendTimes());
        queueTaxAuthority.setUpdateProgram(einvoiceVO.getUpdateProgram());
        queueTaxAuthority.setUpdateCounter(queueTaxAuthority.getUpdateCounter());
        queueTaxAuthority.setCreatedBy(einvoiceVO.getCreatedBy());
        queueTaxAuthority.setDateCreated(einvoiceVO.getDateCreated());
        queueTaxAuthority.setLastUpdatedBy(einvoiceVO.getLastUpdatedBy());
        queueTaxAuthority.setLastUpdated(einvoiceVO.getLastUpdated());
        
        queueTaxAuthorityRepository.save(BeanMapUtils.mapTo(queueTaxAuthority, QueueTaxAuthority.class));
    }


    public BigDecimal divideBigDecimal(BigDecimal dec1, BigDecimal dec2) {
        if (dec2 == null || dec2.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        return (dec1 != null ? dec1 : BigDecimal.ZERO).divide(dec2, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal multiplyBigDecimal(BigDecimal dec1, BigDecimal dec2) {
        return (dec1 != null ? dec1 : BigDecimal.ZERO).multiply(dec2 != null ? dec2 : BigDecimal.ZERO);
    }

    private String transforXmlData(EInvoiceInvoicesBO invoicesModel)  {

        try {
            // 创建 JAXB 上下文
            JAXBContext jaxbContext = JAXBContext.newInstance(EInvoiceInvoicesBO.class);

            // 创建 Marshaller 实例
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            
            // 将对象写入文件
            File file = new File("D:\\invoices.xml");
            marshaller.marshal(invoicesModel, file);

            //将对象变成文本
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(invoicesModel, stringWriter);
            return stringWriter.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private void updateStatus(QueueEinvoiceVO queueEinvoiceVO, InvoiceSoapResponseResult eInvoiceResult, String area){

        String status = eInvoiceResult.isSuccess() ? PJConstants.InterfaceStatus.SUCCESS.getCodeDbid(): PJConstants.InterfaceStatus.ERROR.getCodeDbid();
        String reposeMessageString = eInvoiceResult.getMessage();
        if(StringUtils.equals(status, PJConstants.InterfaceStatus.ERROR.getCodeDbid())) {
            this.insertErrorMessage(queueEinvoiceVO, eInvoiceResult);
        }

        if(reposeMessageString != null && reposeMessageString.length() > 298 ) {
            reposeMessageString = reposeMessageString.substring(CommonConstants.INTEGER_ZERO, 298);
        }
        queueEinvoiceVO.setStatus(status);
        queueEinvoiceVO.setStatusMessage(reposeMessageString);
        queueEinvoiceVO.setSendTimes(queueEinvoiceVO.getSendTimes() + CommonConstants.INTEGER_ONE);

        queueEinvoiceRepository.save(BeanMapUtils.mapTo(queueEinvoiceVO, QueueEinvoice.class));
    }

    private void insertErrorMessage(QueueEinvoiceVO queueEinvoiceVO, InvoiceSoapResponseResult eInvoiceResult){

        if(queueEinvoiceVO != null){

            CmmMessageRemindVO  cmmMessageRemindVO = new CmmMessageRemindVO();
            cmmMessageRemindVO.setSiteId(queueEinvoiceVO.getSiteId());
            cmmMessageRemindVO.setReadCategoryType(PJConstants.MessageCategoryType.INFORMATIONREADY);
            cmmMessageRemindVO.setMessage(eInvoiceResult.getMessage());
            cmmMessageRemindVO.setReadType(PJConstants.ReadType.UNREAD);
            cmmMessageRemindVO.setCreateDate(LocalDateTime.now());

            cmmMessageRemindRepository.save(BeanMapUtils.mapTo(cmmMessageRemindVO, CmmMessageRemind.class));
        }
    }
}
