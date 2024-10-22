package com.a1stream.common.facade;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.common.service.ReportCommonService;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.PartsPickingListByOrderPrintBO;
import com.a1stream.domain.bo.parts.PartsSalesReturnInvoiceForFinancePrintBO;
import com.a1stream.domain.bo.parts.PartsSalesReturnInvoiceForFinancePrintDetailBO;
import com.a1stream.domain.bo.parts.PartsStoringListForWarehousePrintBO;
import com.a1stream.domain.bo.parts.PartsStoringListForWarehousePrintDetailBO;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.report.exporter.PdfReportExporter;
import com.ymsl.solid.web.download.DownloadResponseView;

import jakarta.annotation.Resource;

@Component
public class ReportFacade {

    @Resource
    private ReportCommonService reportCommonService;

    @Resource
    public void setPdfReportExporter(PdfReportExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
    }

    private PdfReportExporter pdfExporter;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

    //PartsPickingList(ByOrder)共通
    public DownloadResponseView printPartsPickingListByOrderReport(Long deliveryOrderId) {

        List<PartsPickingListByOrderPrintBO> dataList = reportCommonService.getPartsPickingListByOrderReportData(deliveryOrderId);

        if (dataList.isEmpty()) {
            PartsPickingListByOrderPrintBO bo = new PartsPickingListByOrderPrintBO();
            bo.setDate(DateUtils.getCurrentDateString(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS));
            dataList.add(bo);
        }

        for(PartsPickingListByOrderPrintBO data : dataList) {
            data.setDate(DateUtils.getCurrentDateString(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS));
            data.setPartsNo(PartNoUtil.format(data.getPartsNo()));
        }
        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_SPM0201_03_PARTSPICKINGLISTBYORDER, dataList, StringUtils.EMPTY);
        return new DownloadResponseView(rp);
    }

    public DownloadResponseView printPartsStoringListForWarehouse(List<Long> receiptSlipIds) {

        List<PartsStoringListForWarehousePrintBO> dataList = new ArrayList<>();

        //HeaderData
        PartsStoringListForWarehousePrintBO printBO = new PartsStoringListForWarehousePrintBO();
        printBO.setDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMDHMS));

        //DetailData
        List<PartsStoringListForWarehousePrintDetailBO> detail = reportCommonService.getPrintPartsStoringListForWarehouseData(receiptSlipIds);

        Set<String> seenLineNos = new HashSet<>();

        for(PartsStoringListForWarehousePrintDetailBO data : detail) {
            String lineNo = data.getLineNo();

            //这里数量需要置空,配合报表软件blank when NULL,SQL文需要对null值置0
            if (seenLineNos.contains(lineNo)) {
                data.setPartsNo(null); // 置空 partsNo
                data.setLineNo(null);
                data.setCaseNo(null);
                data.setPartsName(null);
                data.setBoQty(null);
                data.setReceiptQty(null);
                data.setOrderNo(null);
            } else {
                seenLineNos.add(lineNo); // 第一次出现，添加到集合中
                data.setPartsNo(PartNoUtil.format(data.getPartsNo())); // 格式化 partsNo
            }
        }

        detail.stream().findFirst().ifPresent(temp -> {
            printBO.setPointAbbr(temp.getPointAbbr() == null ? temp.getFromPoint() : temp.getPointAbbr());
            printBO.setDeliveryNo(temp.getSupplier());
            printBO.setReceiptNo(temp.getReceiptNo());
            printBO.setReceiptDate(LocalDate.parse(temp.getReceiptDate(), formatter).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            printBO.setDetailPrintList(detail);
        });

        dataList.add(printBO);
        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_SPQ0301_01_PARTSSTORINGLISTFORWAREHOUSE, dataList, StringUtils.EMPTY);
        return new DownloadResponseView(rp);
    }

    public DownloadResponseView printPartsSalesReturnInvoiceForFinance(Long returnInvoiceId) {

        List<PartsSalesReturnInvoiceForFinancePrintBO> dataList = new ArrayList<>();

        //HeaderData
        PartsSalesReturnInvoiceForFinancePrintBO data = reportCommonService.getPartsSalesReturnInvoiceForFinanceData(returnInvoiceId);

        if (Nulls.isNull(data)) {
            PartsSalesReturnInvoiceForFinancePrintBO bo = new PartsSalesReturnInvoiceForFinancePrintBO();
            bo.setLogo(FileConstants.LOGOPATH_YAMAHA_BLUE);
            bo.setDate(DateUtils.getCurrentDateString(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS));
            dataList.add(bo);
        }

        //DetailData
        List<PartsSalesReturnInvoiceForFinancePrintDetailBO> details = reportCommonService.getPartsSalesReturnInvoiceForFinanceDetailList(returnInvoiceId);

        for (PartsSalesReturnInvoiceForFinancePrintDetailBO detail : details) {
            detail.setPartsNo(PartNoUtil.format(detail.getPartsNo()));
            if (!Nulls.isNull(detail.getReturnQty(), detail.getReturnPrice())) {
                detail.setReturnAmount(NumberUtil.multiply(detail.getReturnQty(), detail.getReturnPrice()).setScale(0, RoundingMode.DOWN));
            }
        }

        if (!Nulls.isNull(data, details)) {
            data.setLogo(FileConstants.LOGOPATH_YAMAHA_BLUE);
            data.setDate(DateUtils.getCurrentDateString(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS));
            data.setReturnDate(LocalDate.parse(data.getReturnDate(), DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            data.setDetailList(details);
        }

        dataList.add(data);
        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_SPM0202_01_PARTSSALESRETURNINVOICEFORFINANCE, dataList, StringUtils.EMPTY);
        return new DownloadResponseView(rp);
    }
}
