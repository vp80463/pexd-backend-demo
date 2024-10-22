package com.a1stream.unit.facade;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.bo.SdManifestItemBO;
import com.a1stream.common.bo.SdManifestXmlBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.constants.PJConstants.ManifestStatus;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.ReceiptSlipStatus;
import com.a1stream.common.constants.PJConstants.SdStockStatus;
import com.a1stream.common.constants.PJConstants.SerialproductStockStatus;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.domain.bo.unit.SDQ010701BO;
import com.a1stream.domain.bo.unit.SDQ010702BO;
import com.a1stream.domain.bo.unit.SDQ010702DetailBO;
import com.a1stream.domain.bo.unit.SDQ010702HeaderBO;
import com.a1stream.domain.form.unit.SDQ010701Form;
import com.a1stream.domain.form.unit.SDQ010702Form;
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
import com.a1stream.unit.service.SDQ0107Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
/**
* 功能描述:
*
* @author mid2259
*/
@Component
public class SDQ0107Facade {

    //获取xml里数据
    private static final String ARG_0 = "<Detail>";
    private static final String ARG_1 = "</Detail>";
    private static final String ARG_2 = "[";
    private static final String ARG_3 = "]";
    //正则表达式
    private static final String REGEX_XML = "<Detail>(.*?)</Detail>";

    @Resource
    private SDQ0107Service sdq0107Service;

    @Resource
    private HelperFacade helperFacade;

    public List<SDQ010701BO> findReceiptManifestList(SDQ010701Form form) {

        this.validateData(form);
        List<SDQ010701BO> receiptManifestList = sdq0107Service.getReceiptManifestListForSD(form);
        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(ManifestStatus.CODE_ID);
        for(SDQ010701BO bo : receiptManifestList) {
            bo.setManifestStatus(codeMap.get(bo.getManifestStatus()));
        }
        return receiptManifestList;
    }

    private void validateData(SDQ010701Form form){

        if(!ObjectUtils.isEmpty(form.getDateFrom()) && !ObjectUtils.isEmpty(form.getDateTo())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);
            LocalDate dateFrom = LocalDate.parse(form.getDateFrom(), formatter);
            LocalDate dateTo = LocalDate.parse(form.getDateTo(), formatter);

            if (dateFrom.isAfter(dateTo)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("error.dateEqAfter", new String[] {
                                                 CodedMessageUtils.getMessage("label.toDate"),
                                                 CodedMessageUtils.getMessage("label.fromDate")}));
            }

            if (dateFrom.plusMonths(CommonConstants.INTEGER_TWELVE).isBefore(dateTo)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10296", new String[] {
                                                 CodedMessageUtils.getMessage("label.fromDate"),
                                                 CodedMessageUtils.getMessage("label.toDate")}));
            }
        }
    }

    public List<SdManifestItemBO> parseXml(String xmlData) throws JAXBException  {

        Pattern pattern = Pattern.compile(REGEX_XML, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xmlData);
        if (matcher.find()) {
            String detailXml = ARG_0 + matcher.group(CommonConstants.INTEGER_ONE) + ARG_1;
            JAXBContext context = JAXBContext.newInstance(SdManifestXmlBO.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(detailXml);
            SdManifestXmlBO detail = (SdManifestXmlBO) unmarshaller.unmarshal(reader);

            return detail.getRows();
        }

        return new ArrayList<>();
    }

    public void fileUpload(MultipartFile[] files,String siteId){

        List<SdManifestItemBO> items=null;
        try{
            List<String> fileContents = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    StringBuilder content = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            content.append(line).append(CommonConstants.CHAR_NEW_LINE);
                        }
                    }
                    fileContents.add(content.toString());
                }
            }
            items = parseXml(fileContents.toString().substring(fileContents.toString().indexOf(ARG_2) + CommonConstants.INTEGER_ONE, fileContents.toString().lastIndexOf(ARG_3)));
        }catch(JAXBException e){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10170"));
        }catch(IOException e){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10170"));
        }

        sdq0107Service.doManifestImportsForSD(items,siteId);

    }

    //sdq010702
    public SDQ010702BO getReceiptManifestMaintenance(Long receiptManifestId) {

        SDQ010702BO sdq010702BO = new SDQ010702BO();
        SDQ010702HeaderBO header = sdq0107Service.getReceiptManifestMaintenanceHeader(receiptManifestId);
        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(ManifestStatus.CODE_ID);
        header.setManifestStatusDisplay(codeMap.get(header.getManifestStatus()));
        List<SDQ010702DetailBO> detailList = sdq0107Service.getReceiptManifestMaintenanceDetail(receiptManifestId);
        sdq010702BO.setHeader(header);
        sdq010702BO.setDetailList(detailList);
        return sdq010702BO;
    }

    public void confirmRecManMaint(SDQ010702Form form, PJUserDetails uc) {

        //根据id获取receipt_manifest表数据
        ReceiptManifestVO receiptManifestVO = sdq0107Service.findReceiptManifestVO(form.getReceiptManifestId());
        MstOrganizationVO mstOrganizationVO = sdq0107Service.findMstOrganizationVO(uc.getDealerCode());

        //新增receipt_slip表
        ReceiptSlipVO receiptSlipVO = ReceiptSlipVO.create();
        receiptSlipVO.setSiteId(receiptManifestVO.getSiteId());
        receiptSlipVO.setReceivedFacilityId(receiptManifestVO.getToFacilityId());
        receiptSlipVO.setReceivedOrganizationId(mstOrganizationVO.getOrganizationId());
        receiptSlipVO.setReceivedPicId(uc.getPersonId());
        receiptSlipVO.setReceivedPicNm(uc.getPersonName());
        receiptSlipVO.setSupplierDeliveryDate(receiptManifestVO.getSupplierShippedDate());
        receiptSlipVO.setFromOrganizationId(receiptManifestVO.getFromOrganization());
        receiptSlipVO.setFromFacilityId(receiptManifestVO.getFromFacilityId());
        receiptSlipVO.setReceiptSlipStatus(ReceiptSlipStatus.ONTRANSIT.getCodeDbid());
        receiptSlipVO.setInventoryTransactionType(InventoryTransactionType.PURCHASESTOCKIN.getCodeDbid());
        receiptSlipVO.setCommercialInvoiceNo(receiptManifestVO.getSupplierShipmentNo());
        receiptSlipVO.setProductClassification(ProductClsType.GOODS.getCodeDbid());
        sdq0107Service.newSlipNo(receiptSlipVO ,receiptManifestVO.getSiteId());

        //根据id获取receipt_manifest_item信息
        List<ReceiptManifestItemVO> receiptManifestItemVOs = sdq0107Service.findReceiptManifestItem(receiptManifestVO.getReceiptManifestId());
        List<ReceiptSlipItemVO> receiptSlipItemAddVOs = new ArrayList<>();
        Set<Long> productIds = receiptManifestItemVOs.stream()
                               .map(ReceiptManifestItemVO::getReceiptProductId)
                               .collect(Collectors.toSet());
        Map<Long, MstProductVO> productMap = getProductMap(productIds);

        //修改receipt Manifest serialized item的 receipt slip id
        Set<Long> receiptManifestItemId = receiptManifestItemVOs.stream().map(ReceiptManifestItemVO::getReceiptManifestItemId).collect(Collectors.toSet());
        List<ReceiptManifestSerializedItemVO> receiptManifestSerializedItems = sdq0107Service.findByReceiptManifestItemIdIn(receiptManifestItemId);
        receiptManifestSerializedItems.forEach(item -> item.setReceiptSlipId(receiptSlipVO.getReceiptSlipId()));

        Map<Long, Long> receiptManifestItemIdMap = new HashMap<>();

        //新增表receipt_slip_item
        for(ReceiptManifestItemVO bo:receiptManifestItemVOs) {
            ReceiptSlipItemVO receiptSlipItemVO = ReceiptSlipItemVO.create();
            receiptSlipItemVO.setSiteId(receiptSlipVO.getSiteId());
            receiptSlipItemVO.setReceiptSlipId(receiptSlipVO.getReceiptSlipId());
            receiptSlipItemVO.setProductId(bo.getReceiptProductId());
            receiptSlipItemVO.setProductCd(productMap.get(bo.getReceiptProductId()).getProductCd());
            receiptSlipItemVO.setProductNm(productMap.get(bo.getReceiptProductId()).getSalesDescription());
            receiptSlipItemVO.setColorNm(productMap.get(bo.getReceiptProductId()).getColorNm());
            receiptSlipItemVO.setReceiptQty(bo.getReceiptQty());
            receiptSlipItemVO.setFrozenQty(bo.getFrozenQty());
            receiptSlipItemVO.setReceiptPrice(bo.getReceiptPrice());
            receiptSlipItemVO.setSupplierInvoiceNo(receiptManifestVO.getSupplierInvoiceNo());
            //整车不使用caseNo，该字段放空
            receiptSlipItemVO.setProductClassification(ProductClsType.GOODS.getCodeDbid());
            receiptSlipItemAddVOs.add(receiptSlipItemVO);
            receiptManifestItemIdMap.put(bo.getReceiptManifestItemId(),receiptSlipItemVO.getReceiptSlipItemId());
        }

        Map<Long, ReceiptManifestItemVO> receiptManifestItemMap = receiptManifestItemVOs.stream()
                .collect(Collectors.toMap(
                    ReceiptManifestItemVO::getReceiptManifestItemId,
                    vo -> vo
                ));

        //新增表receipt_serialized_item
        List<ReceiptSerializedItemVO> receiptSerializedItemAddVOs = new ArrayList<>();
        for(ReceiptManifestSerializedItemVO bo:receiptManifestSerializedItems) {
            ReceiptSerializedItemVO receiptSerializedItemVO = ReceiptSerializedItemVO.create();
            receiptSerializedItemVO.setSiteId(receiptSlipVO.getSiteId());
            receiptSerializedItemVO.setCompleteFlag(CommonConstants.CHAR_N);
            receiptSerializedItemVO.setReceiptSlipId(receiptSlipVO.getReceiptSlipId());
            receiptSerializedItemVO.setReceiptSlipItemId(receiptManifestItemIdMap.get(bo.getReceiptManifestItemId()));
            receiptSerializedItemVO.setSerializedProductId(bo.getSerializedProductId());
            receiptSerializedItemVO.setInCost(receiptManifestItemMap.get(bo.getReceiptManifestItemId()).getReceiptPrice());
            receiptSerializedItemAddVOs.add(receiptSerializedItemVO);
        }

        //循环新增的receipt_slip_item表内容
        List<Long> receiptSlipItemIds = receiptSlipItemAddVOs.stream()
                                       .map(ReceiptSlipItemVO::getReceiptSlipItemId)
                                       .toList();
        List<ReceiptSerializedItemVO> receiptSerializedItemVOs = sdq0107Service.findReceiptSerializedItem(receiptSlipItemIds);

        Map<Long, List<ReceiptSerializedItemVO>> serializedItemMap = receiptSerializedItemVOs.stream()
        .collect(Collectors.groupingBy(ReceiptSerializedItemVO::getReceiptSlipItemId));

        //可以直接拿全数据，在双循环中使用
        List<SerializedProductVO> serializedProductVOs = sdq0107Service.findSerializedProductVO(uc.getDealerCode());
        Map<Long, SerializedProductVO> serializedProductMap = serializedProductVOs.stream()
                .collect(Collectors.toMap(SerializedProductVO::getSerializedProductId, Function.identity()));

        List<ProductStockStatusVO> productStockStatusVOs = sdq0107Service.findProductStockStatusVO(receiptManifestVO.getToFacilityId(), productIds);
        Map<String, ProductStockStatusVO> productStockStatusMap = productStockStatusVOs.stream()
                .collect(Collectors.toMap(
                    item -> item.getProductId() + "-" + item.getProductStockStatusType(), // 使用 "-" 作为分隔符
                    Function.identity()
                ));

        List<SerializedProductVO> receiptSlipItemUpdateVOs = new ArrayList<>();
        List<ProductStockStatusVO> productStockStatusUpdateVOs = new ArrayList<>();

        for(ReceiptSlipItemVO item:receiptSlipItemAddVOs) {
            Long itemId = item.getReceiptSlipItemId();
            List<ReceiptSerializedItemVO> serializedItemVOs = serializedItemMap.get(itemId);
            if(serializedItemVOs != null) {
                for(ReceiptSerializedItemVO vo:receiptSerializedItemAddVOs) {
                    //目前不进行判断 且只更新serialized_product表stock_status字段为S033ONTRANSIT
                    SerializedProductVO serializedProductVO = serializedProductMap.get(vo.getSerializedProductId());
                    serializedProductVO.setStockStatus(SerialproductStockStatus.ONTRANSFER);
                    receiptSlipItemUpdateVOs.add(serializedProductVO);
                }
            }

            ProductStockStatusVO productStockStatusVO = productStockStatusMap.get(item.getProductId() + "-" + SdStockStatus.ONTRANSIT_QTY.getCodeDbid());
            if(productStockStatusVO == null) {
                productStockStatusVO = ProductStockStatusVO.create();
                productStockStatusVO.setSiteId(item.getSiteId());
                productStockStatusVO.setFacilityId(receiptManifestVO.getToFacilityId());
                productStockStatusVO.setProductId(item.getProductId());
                productStockStatusVO.setProductClassification(ProductClsType.GOODS.getCodeDbid());
                productStockStatusVO.setProductStockStatusType(SdStockStatus.ONTRANSIT_QTY.getCodeDbid());
                productStockStatusVO.setQuantity(item.getReceiptQty());
                productStockStatusUpdateVOs.add(productStockStatusVO);
            }else {
                productStockStatusVO.setQuantity(productStockStatusVO.getQuantity().add(item.getReceiptQty()));
                productStockStatusUpdateVOs.add(productStockStatusVO);
            }
        }

        //更新receipt_manifest表
        receiptManifestVO.setManifestStatus(ManifestStatus.ISSUED.getCodeDbid());
        receiptManifestVO.setToFacilityId(form.getPointId());

        sdq0107Service.doIssue(receiptSlipVO, receiptSlipItemAddVOs, productStockStatusUpdateVOs, receiptSlipItemUpdateVOs, receiptManifestVO, receiptManifestSerializedItems,receiptSerializedItemAddVOs);
    }

    private Map<Long, MstProductVO> getProductMap(Set<Long> productIds) {

        Map<Long, MstProductVO> vomap = new HashMap<>();

        if(!productIds.isEmpty()) {
            vomap = sdq0107Service.getProductByIds(productIds).stream().collect(Collectors.toMap(v -> v.getProductId(),Function.identity()));
        }
        return vomap;
    }
}
