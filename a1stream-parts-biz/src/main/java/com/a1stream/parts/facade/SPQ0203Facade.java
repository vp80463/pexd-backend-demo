package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.common.constants.PJConstants.InvoiceType;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.SPM020103PrintBO;
import com.a1stream.domain.bo.parts.SPM020103PrintDetailBO;
import com.a1stream.domain.bo.parts.SPQ020301BO;
import com.a1stream.domain.bo.parts.SPQ020302BO;
import com.a1stream.domain.form.parts.SPQ020301Form;
import com.a1stream.domain.form.parts.SPQ020302Form;
import com.a1stream.parts.service.SPM0201Service;
import com.a1stream.parts.service.SPQ0203Service;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.report.exporter.PdfReportExporter;
import com.ymsl.solid.web.download.DownloadResponseView;

import jakarta.annotation.Resource;
/**
* 功能描述:
*
* MID2303
* 2024年6月28日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/28   Ruan Hansheng     New
*/
@Component
public class SPQ0203Facade {

    @Resource
    private SPQ0203Service spq0203Service;

    @Resource
    private HelperFacade helperFacade;

    @Resource
    private SPM0201Service spm0201Service;

    @Resource
    public void setPdfReportExporter(PdfReportExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
    }
    private PdfReportExporter pdfExporter;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

    private static final String ZERO_PERCENT_STRING = "0.00%";

    public Page<SPQ020301BO> getInvoiceList(SPQ020301Form form, PJUserDetails uc) {

        Page<SPQ020301BO> resultPage = spq0203Service.getInvoiceList(form, uc);
        // 数据库为codeDbid 前台显示为codeData1
        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(InvoiceType.CODE_ID);
        BigDecimal totalInvoiceAmount = BigDecimal.ZERO;
        BigDecimal totalInvoiceAmountWithVAT = BigDecimal.ZERO;
        List<SPQ020301BO> content = new ArrayList<>(resultPage.getContent());
        for (SPQ020301BO bo : content) {
            bo.setPoint(form.getPoint());
            bo.setPointId(form.getPointId());
            bo.setPointCd(form.getPointCd());
            bo.setPointNm(form.getPointNm());
            bo.setInvoiceType(codeMap.get(bo.getInvoiceType()));
            if (!form.isPageFlg()) {
                bo.setInvoiceDate(LocalDate.parse(bo.getInvoiceDate(), DateTimeFormatter.BASIC_ISO_DATE).format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DATE_FORMAT)));
            }
            totalInvoiceAmount = totalInvoiceAmount.add(bo.getInvoiceAmount());
            totalInvoiceAmountWithVAT = totalInvoiceAmountWithVAT.add(bo.getInvoiceAmountWithVAT());
        }

        if (!form.isPageFlg()) {
            //底部合计处理
            SPQ020301BO totalRow = new SPQ020301BO();
            totalRow.setInvoiceType("Total:");
            totalRow.setInvoiceAmount(totalInvoiceAmount);
            totalRow.setInvoiceAmountWithVAT(totalInvoiceAmountWithVAT);
            content.add(totalRow);

            Pageable pageable = resultPage.getPageable();
            long totalElements = resultPage.getTotalElements();

            return new PageImpl<>(content, pageable, totalElements);
        }

        return resultPage;
    }

    public Page<SPQ020302BO> getInvoiceItemList(SPQ020302Form form, PJUserDetails uc) {

        return spq0203Service.getInvoiceItemList(form, uc);
    }

    public DownloadResponseView printFastSalesOrderReportForDO(Long salesOrderId,PJUserDetails uc) {

        List<SPM020103PrintBO> dataList = new ArrayList<>();

        //HeaderData
        SPM020103PrintBO data = spm0201Service.getFastSalesOrderReportData(salesOrderId);

        //DetailData
        List<SPM020103PrintDetailBO> details = spm0201Service.getFastSalesOrderReportDetailData(salesOrderId);

        String sysTaxRate = spm0201Service.getTaxRate().getParameterValue();
        String sysTaxPeriod = uc.getTaxPeriod();

        for (SPM020103PrintDetailBO detail : details) {
            detail.setParts(PartNoUtil.format(detail.getParts()));
            detail.setPartsNo(PartNoUtil.format(detail.getPartsNo()));
            detail.setSl(NumberUtil.add(detail.getAllocatedQty(), detail.getBoQty()));
            detail.setCurrencyVat(NumberUtil.multiply(detail.getSellingPrice(), detail.getSl().setScale(0, RoundingMode.HALF_UP)));
            detail.setDiscountOffRate(StringUtils.equals(ZERO_PERCENT_STRING, detail.getDiscountOffRate()) ? StringUtils.EMPTY : detail.getDiscountOffRate());

            if (StringUtils.isNotBlank(detail.getOrderDate()) && StringUtils.isNotBlank(sysTaxPeriod)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

                LocalDate orderDate = LocalDate.parse(detail.getOrderDate(), formatter);
                LocalDate taxPeriod = LocalDate.parse(sysTaxPeriod, formatter);

                if (orderDate.isAfter(taxPeriod) || orderDate.isEqual(taxPeriod)) {
                    if (NumberUtil.larger(detail.getProductTax(), BigDecimal.ZERO)) {
                        detail.setTaxRate(detail.getProductTax().toString() + CommonConstants.CHAR_PERCENT);
                    } else {
                        detail.setTaxRate(sysTaxRate + CommonConstants.CHAR_PERCENT);
                    }
                }
            }
        }

        if (!Nulls.isNull(data, details)) {
            data.setPointAddress(StringUtils.equals(CommonConstants.CHAR_Y, data.getMultiAddressFlag()) ? data.getMultiAddressFlag() : null);
            data.setLogo(FileConstants.LOGOPATH_YAMAHA_BLUE);
            data.setDate(DateUtils.getCurrentDateString(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS));
            data.setOrderDate(LocalDate.parse(data.getOrderDate(), DATE_FORMATTER).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            data.setDetailList(details);
            dataList.add(data);
        }

        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_SPM0201_03_FASTSALESORDERREPORTFORDO, dataList, StringUtils.EMPTY);
        return new DownloadResponseView(rp);
    }
}
