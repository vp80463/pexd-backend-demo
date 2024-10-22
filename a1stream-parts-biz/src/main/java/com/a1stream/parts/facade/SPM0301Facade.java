package com.a1stream.parts.facade;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.bo.SpManifestItemBO;
import com.a1stream.common.bo.SpManifestXmlBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.PartsStoringListForFinancePrintBO;
import com.a1stream.domain.bo.parts.PartsStoringListForFinancePrintDetailBO;
import com.a1stream.domain.bo.parts.SPM030101BO;
import com.a1stream.domain.bo.parts.SPM030102BO;
import com.a1stream.domain.form.parts.SPM030101Form;
import com.a1stream.domain.form.parts.SPM030102Form;
import com.a1stream.domain.vo.PurchaseOrderItemVO;
import com.a1stream.domain.vo.PurchaseOrderVO;
import com.a1stream.domain.vo.ReceiptManifestItemVO;
import com.a1stream.domain.vo.ReceiptManifestVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.parts.service.SPM0301Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.CollectionUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.report.exporter.PdfReportExporter;
import com.ymsl.solid.web.download.DownloadResponseView;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
/**
* 功能描述:
*
* MID2303
* 2024年6月14日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/14   Ruan Hansheng     New
*/
@Component
public class SPM0301Facade {

    //获取xml里数据
    private static final String ARG_0 = "<Detail>";
    private static final String ARG_1 = "</Detail>";
    private static final String ARG_2 = "[";
    private static final String ARG_3 = "]";
    //正则表达式
    private static final String REGEX_XML = "<Detail>(.*?)</Detail>";

    private static final String ARG_ONE = "requestId";
    private static final String ARG_TWO = "requestTime";
    private static final String ARG_THREE = "dealerCd";
    private static final String ARG_FOUR = "shipmentNo";
    private static final String ARG_FIVE = "invoiceNo";
    private static final String ARG_SIX = "consigneeCd";
    private static final String ARG_SEVEN = "sysOwnerCd";

    @Resource
    private SPM0301Service spm0301Service;

    @Resource
    public void setPdfReportExporter(PdfReportExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
    }

    private PdfReportExporter pdfExporter;

    public List<SPM030101BO> getReceiptManifestList(SPM030101Form form, PJUserDetails uc) {

        return spm0301Service.getReceiptManifestList(form, uc);
    }

    public List<Long> confirmReceiptManifest(SPM030101Form form, PJUserDetails uc) {

        List<ReceiptSlipVO> receiptSlipVOList = spm0301Service.confirmReceiptManifest(form, uc);

        List<Long> receiptSlipIds = new ArrayList<>();

        for (ReceiptSlipVO receiptSlipVO : receiptSlipVOList) {
            receiptSlipIds.add(receiptSlipVO.getReceiptSlipId());
        }

        return receiptSlipIds;
    }

    //获取xml里数据
    public List<SpManifestItemBO> parseXml(String xmlData) throws JAXBException  {

        Pattern pattern = Pattern.compile(REGEX_XML, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xmlData);
        if (matcher.find()) {
            String detailXml = ARG_0 + matcher.group(CommonConstants.INTEGER_ONE) + ARG_1;
            JAXBContext context = JAXBContext.newInstance(SpManifestXmlBO.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(detailXml);
            SpManifestXmlBO detail = (SpManifestXmlBO) unmarshaller.unmarshal(reader);

            return detail.getRows();
        }

        return new ArrayList<>();
    }

    public void fileUpload(MultipartFile[] files){

        //将文件里的内容提取出来
        List<SpManifestItemBO> items=null;
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
        }catch(Exception e){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10170"));
        }

        //将xml里的数据新增至数据库中
        if(!items.isEmpty()){

            List<String> invoiceNo = items.stream().map(SpManifestItemBO :: getInvoiceNo).collect(Collectors.toList());
            List<SPM030101BO> data = spm0301Service.getInoviceNoAndCaseNoList(invoiceNo);

            // 自定义比较方法
            Set<String> spmKeys = data.stream()
                                      .map(bo -> generateKey(bo.getSiteId(), bo.getInvoiceNo(), bo.getCaseNo()))
                                      .collect(Collectors.toSet());

            List<SpManifestItemBO> filteredItems = items.stream()
                                                        .filter(item -> !spmKeys.contains(generateKey(item.getDealerCode(), item.getInvoiceNo(), item.getCaseNo())))
                                                        .collect(Collectors.toList());

            spm0301Service.doManifestImports(filteredItems);

        }else{
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10170"));
        }
    }

    private static String generateKey(String siteId, String invoiceNo, String caseNo) {
        return siteId + CommonConstants.CHAR_VERTICAL_BAR + invoiceNo + CommonConstants.CHAR_VERTICAL_BAR + caseNo;
    }

    public SPM030101BO getReceiptManifestItemList(SPM030102Form form, PJUserDetails uc) {

        SPM030101BO result = new SPM030101BO();

        List<SPM030102BO> tableDataList = spm0301Service.getReceiptManifestItemList(form, uc);
        if (CollectionUtils.isNotEmpty(tableDataList)) {
            Long receiptManifestId = tableDataList.get(0).getReceiptManifestId();
            ReceiptManifestVO receiptManifestVO = spm0301Service.getReceiptManifestVO(receiptManifestId);
            result.setPoint(form.getPoint());
            result.setPointId(receiptManifestVO.getToFacilityId());
            result.setSupplier(form.getSupplier());
            result.setInvoiceNo(receiptManifestVO.getSupplierInvoiceNo());
            result.setFirstCaseNo(form.getCaseNo());
            result.setReceiptManifestId(receiptManifestId);
            result.setUpdateCount(receiptManifestVO.getUpdateCount());
        }

        result.setTableDataList(tableDataList);

        return result;
    }

    public void confirmReceiptManifestItem(SPM030102Form form) {

        spm0301Service.confirmReceiptManifestItem(form);
    }

    public void deleteReceiptManifestItem(SPM030102Form form) {

        // 获取删除的receiptManifestItem
        Long receiptManifestItemId = form.getReceiptManifestItemId();
        ReceiptManifestItemVO receiptManifestItemVO = spm0301Service.getReceiptManifestItemVO(receiptManifestItemId);
        if (null == receiptManifestItemVO || !form.getUpdateCount().equals(receiptManifestItemVO.getUpdateCount())) {
            throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[] {ComUtil.t("label.purchaseOrderNumber"), form.getOrderNo(), ComUtil.t("title.partsManifestModify_02")}));
        }

        // 获取对应receiptManifestId下的receiptManifestItem
        Long receiptManifestId = receiptManifestItemVO.getReceiptManifestId();
        List<ReceiptManifestItemVO> receiptManifestItemVOList = spm0301Service.getReceiptManifestItemVOListById(receiptManifestId);
        ReceiptManifestVO receiptManifestVO = null;
        // 如果该receiptManifestId下只有一条receiptManifestItem，则删除receiptManifest
        if (1 == receiptManifestItemVOList.size()) {
            receiptManifestVO = spm0301Service.getReceiptManifestVO(receiptManifestId);
        }

        spm0301Service.deleteReceiptManifestItem(receiptManifestVO, receiptManifestItemVO);
    }

    public void check(SPM030102Form form, PJUserDetails uc) {

        List<SPM030102BO> allTableDataList = form.getAllTableDataList();
        Set<String> partsSet = new HashSet<>();
        for (SPM030102BO bo : allTableDataList) {
            // 验证purchaseOrder存在性
            String orderNo = bo.getOrderNo();
            PurchaseOrderVO purchaseOrderVO = spm0301Service.getPurchaseOrderVO(uc.getDealerCode(), form.getPointId(), orderNo);
            if (null == purchaseOrderVO) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[]{CodedMessageUtils.getMessage("label.orderNo"), orderNo, CodedMessageUtils.getMessage("label.tablePurchaseOrder")}));
            }

            // 验证purchaseOrderItem存在性
            Long purchaseOrderId = purchaseOrderVO.getPurchaseOrderId();
            Long orderPartsId = bo.getOrderPartsId();
            PurchaseOrderItemVO purchaseOrderItemVO = spm0301Service.getPurchaseOrderItemVO(uc.getDealerCode(), purchaseOrderId, orderPartsId);
            if (null == purchaseOrderItemVO) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[]{CodedMessageUtils.getMessage("label.orderParts"), bo.getOrderPartsNo(), CodedMessageUtils.getMessage("label.tablePurchaseOrder")}));
            }

            // 验证替代件存在性
            Long receiptPartsId = bo.getReceiptPartsId();
            if (!NumberUtil.equals(receiptPartsId, orderPartsId) && !spm0301Service.checkPartsSupersedingRelation(orderPartsId,receiptPartsId)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10087", new String[]{CodedMessageUtils.getMessage("label.receiptParts"), bo.getReceiptPartsNo(), CodedMessageUtils.getMessage("label.orderParts"), bo.getOrderPartsNo()}));
            }

            // 验证重复性
            String partsKey = orderNo + orderPartsId.toString() + receiptPartsId.toString();
            if (partsSet.contains(partsKey)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00301", new String[]{CodedMessageUtils.getMessage("label.orderParts") + CodedMessageUtils.getMessage("label.receiptParts")}));
            }
            partsSet.add(partsKey);

            // 验证有效性
            if (bo.getTotalReceiptQty().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00200", new String[]{CodedMessageUtils.getMessage("label.totalReceiptQuantity"), CommonConstants.CHAR_ZERO}));
            }

            if (bo.getReceiptCost().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00200", new String[]{CodedMessageUtils.getMessage("label.receiptCost"), CommonConstants.CHAR_ZERO}));
            }

            if (bo.getTotalReceiptQty().compareTo(bo.getOnPurchaseQty()) > 0) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00203", new String[]{CodedMessageUtils.getMessage("label.totalReceiptQuantity"), CodedMessageUtils.getMessage("label.onPurchaseQuantity")}));
            }
        }
    }

    public SPM030102BO getPurchaseOrderData(SPM030102Form form, PJUserDetails uc) {

        return spm0301Service.getPurchaseOrderData(form, uc);
    }

    public DownloadResponseView printPartsStoringListForFinance(List<Long> receiptSlipIds) {
        //data
        List<PartsStoringListForFinancePrintDetailBO> printList = spm0301Service.getPartsStoringListForFinanceReport(receiptSlipIds);

        PartsStoringListForFinancePrintBO reportHeader = spm0301Service.getPartsStoringListForFinanceReportHeader(receiptSlipIds);

        //dataEdit
        for(PartsStoringListForFinancePrintDetailBO bo: printList) {
            bo.setPartsNo(PartNoUtil.format(bo.getPartsNo()));
        }

        List<PartsStoringListForFinancePrintBO> dataList = new ArrayList<>();

        if (!Nulls.isNull(reportHeader, printList)) {
            reportHeader.setDate(DateUtils.getCurrentDateString(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS));
            reportHeader.setReceiptDate(LocalDate.parse(reportHeader.getReceiptDate(), DateTimeFormatter.BASIC_ISO_DATE).format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DATE_FORMAT)));
            reportHeader.setDetailPrintList(printList);
            dataList.add(reportHeader);
        }

        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_SPM0301_01_PARTSSTORINGLISTFORFINANCE, dataList, StringUtils.EMPTY);
        return new DownloadResponseView(rp);
    }

    //直接调用IFS返回的数据直接保存到数据库中
    public void download(SPM030101Form form) {

        String sysOwnerCd = spm0301Service.getSystemParameter(MstCodeConstants.SystemParameterType.YNSPRESPSYSOWNERCD).getParameterValue();

        String dealerCode = form.getSiteId();
        LocalDate currentDate = LocalDate.now();
        String rid = currentDate.toString();

        String[][] ps = new String[][] {
            {ARG_ONE , rid},
            {ARG_TWO, rid},
            {ARG_THREE, "2011"},
            {ARG_FOUR, form.getShipmentNo()},
            {ARG_FIVE, form.getInvoiceNo()},
            {ARG_SIX, "2011A"},
            {ARG_SEVEN, sysOwnerCd}
        };

        String response = spm0301Service.getManifestData(ps);

        //将从IFS中返回的数据插入至数据库中
        if(StringUtils.isBlank(response)){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00400"));
        }else{

            spm0301Service.doManifestImports(response);
        }
    }

}
