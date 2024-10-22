package com.a1stream.service.facade;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.common.constants.PJConstants.ServicePaymentStatus;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.domain.bo.batch.SvServicePaymentIFBO;
import com.a1stream.domain.bo.service.SVM020201BO;
import com.a1stream.domain.bo.service.SVM020202BO;
import com.a1stream.domain.bo.service.SVM0202PrintBO;
import com.a1stream.domain.form.service.SVM020201Form;
import com.a1stream.domain.form.service.SVM020202Form;
import com.a1stream.domain.vo.QueueDataVO;
import com.a1stream.domain.vo.ServicePaymentEditHistoryVO;
import com.a1stream.domain.vo.ServicePaymentVO;
import com.a1stream.service.service.SVM0202Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.base.json.JsonUtils;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.report.exporter.PdfReportExporter;
import com.ymsl.solid.web.download.DownloadResponseView;

import jakarta.annotation.Resource;


/**
* 功能描述:
*
* @author mid1966
*/
@Component
public class SVM0202Facade {

    @Resource
    private SVM0202Service svm0202Service;

    @Resource
    private HelperFacade helperFacade;

    @Resource
    public void setPdfReportExporter(PdfReportExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
    }
    private PdfReportExporter pdfExporter;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

    public List<SVM020201BO> findServicePaymentList(SVM020201Form form, String siteId) {

        this.dateRangeCheck(form);

        List<SVM020201BO> resultList = svm0202Service.findServicePaymentList(form, siteId);

        Map<String, String> codeMapWithCodeData1 = helperFacade.getMstCodeInfoMap(ServiceCategory.CODE_ID);
        Map<String, String> codeMapWithCodeData2 = svm0202Service.getCodeMstS028(ServicePaymentStatus.CODE_ID);

        for(SVM020201BO bo : resultList) {

            bo.setDisplayCategory(codeMapWithCodeData1.get(bo.getCategory()));
            bo.setStatus(codeMapWithCodeData2.get(bo.getStatus()));
        }

        return resultList;
    }

    public SVM020201BO getServicePaymentDetaiList(SVM020202Form form) {

        SVM020201BO servicePayment = svm0202Service.getSpDetailHeader(form);
        ServicePaymentVO servicePaymentVO = svm0202Service.findServicePaymentVO(form.getPaymentId(), form.getSiteId());

        form.setPaymentMonth(servicePaymentVO.getTargetMonth());
        form.setPaymentCategory(servicePaymentVO.getPaymentCategory());
        servicePayment.setCategory(servicePaymentVO.getPaymentCategory());

        List<SVM020202BO> tableDataList = svm0202Service.getSpDetailList(form);

        // 设置servicePayment是否可以issue、confirm
        if(ServicePaymentStatus.CONFIRMISSUED.getCodeDbid().equals(servicePayment.getStatus())
                || ServicePaymentStatus.STATEMENTRECEIPT.getCodeDbid().equals(servicePayment.getStatus())) {

            servicePayment.setDisableFlg(CommonConstants.TRUE_CODE);
        } else {

            servicePayment.setDisableFlg(CommonConstants.FALSE_CODE);
        }

        // 数据库为codeDbid 前台显示为codeData1
        Map<String, String> codeMapWithCodeData1 = helperFacade.getMstCodeInfoMap(ServiceCategory.CODE_ID);
        Map<String, String> codeMapWithCodeData2 = svm0202Service.getCodeMstS028(ServicePaymentStatus.CODE_ID);

        servicePayment.setStatus(codeMapWithCodeData2.get(servicePayment.getStatus()));

        for(SVM020202BO bo : tableDataList) {

            bo.setCategory(codeMapWithCodeData1.get(bo.getCategory()));
        }

        servicePayment.setTableDataList(tableDataList);

        return servicePayment;
    }

    private void dateRangeCheck(SVM020201Form form) {

        LocalDate fixDate = LocalDate.parse(form.getFixDate(), DATE_FORMATTER);
        LocalDate fixDateTo = LocalDate.parse(form.getFixDateTo(), DATE_FORMATTER);

        // fixDateTo必须比fixData大
        if(fixDate.isAfter(fixDateTo)) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("error.dateEqAfter", new String[] {CodedMessageUtils.getMessage("label.fixDateTo"),CodedMessageUtils.getMessage("label.fixDate")}));
        }

        // 两个日期之间不能超过一年
        Long daysBetween = ChronoUnit.DAYS.between(fixDate, fixDateTo);
        if(daysBetween > 365) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10296", new String[] {CodedMessageUtils.getMessage("label.fixDate"), CodedMessageUtils.getMessage("label.fixDateTo")}));
        }
    }

    public void confirmServicePaymentList(SVM020202Form form, PJUserDetails uc) {

        this.validateData(form, uc.getDealerCode());

        // service_payment 表更新
        ServicePaymentVO servicePaymentVO = svm0202Service.findServicePaymentVO(form.getPaymentId(), uc.getDealerCode());

        servicePaymentVO.setVatCd(form.getVatCd());
        servicePaymentVO.setInvoiceNo(form.getInvoiceNo());
        servicePaymentVO.setInvoiceDate(form.getInvoiceDate());
        servicePaymentVO.setSerialNo(form.getSerialNo());
        servicePaymentVO.setPaymentStatus(ServicePaymentStatus.CONFIRMED.getCodeDbid());

        // service_payment_edit_history 增改
        ServicePaymentEditHistoryVO servicePaymentEditHistoryVO = svm0202Service.findServicePaymentEditHistoryVO(form.getPaymentId(), uc.getDealerCode(), ServicePaymentStatus.CONFIRMED.getCodeDbid());

        if(servicePaymentEditHistoryVO == null) {

            servicePaymentEditHistoryVO = new ServicePaymentEditHistoryVO();

            servicePaymentEditHistoryVO.setSiteId(uc.getDealerCode());
            servicePaymentEditHistoryVO.setPaymentId(form.getPaymentId());
            servicePaymentEditHistoryVO.setUpdateCount(0);
        }

        servicePaymentEditHistoryVO.setPaymentStatus(ServicePaymentStatus.CONFIRMED.getCodeDbid());
        servicePaymentEditHistoryVO.setChangeDate(LocalDateTime.now());
        servicePaymentEditHistoryVO.setReportFacilityId(uc.getPersonId());
        servicePaymentEditHistoryVO.setReportPicId(uc.getPersonId());
        servicePaymentEditHistoryVO.setReportPicCd(uc.getPersonCode());
        servicePaymentEditHistoryVO.setReportPicNm(uc.getPersonName());

        svm0202Service.confirm(servicePaymentVO, servicePaymentEditHistoryVO);
    }

    public void issueServicePaymentList(SVM020202Form form, PJUserDetails uc) {

        this.validateData(form, uc.getDealerCode());

        // service_payment 表更新
        ServicePaymentVO servicePaymentVO = svm0202Service.findServicePaymentVO(form.getPaymentId(), uc.getDealerCode());

        servicePaymentVO.setVatCd(form.getVatCd());
        servicePaymentVO.setInvoiceNo(form.getInvoiceNo());
        servicePaymentVO.setInvoiceDate(form.getInvoiceDate());
        servicePaymentVO.setSerialNo(form.getSerialNo());
        servicePaymentVO.setPaymentStatus(ServicePaymentStatus.CONFIRMISSUED.getCodeDbid());

        // service_payment_edit_history 增改
        ServicePaymentEditHistoryVO servicePaymentEditHistoryVO = new ServicePaymentEditHistoryVO();

        servicePaymentEditHistoryVO.setSiteId(uc.getDealerCode());
        servicePaymentEditHistoryVO.setPaymentId(form.getPaymentId());
        servicePaymentEditHistoryVO.setUpdateCount(0);
        servicePaymentEditHistoryVO.setPaymentStatus(ServicePaymentStatus.CONFIRMISSUED.getCodeDbid());
        servicePaymentEditHistoryVO.setChangeDate(LocalDateTime.now());
        servicePaymentEditHistoryVO.setReportFacilityId(uc.getPersonId());
        servicePaymentEditHistoryVO.setReportPicId(uc.getPersonId());
        servicePaymentEditHistoryVO.setReportPicCd(uc.getPersonCode());
        servicePaymentEditHistoryVO.setReportPicNm(uc.getPersonName());

        SvServicePaymentIFBO paymentBO = new SvServicePaymentIFBO();

        paymentBO.setDealerReceiptDate(servicePaymentVO.getReceiptDate());
        paymentBO.setInvoiceDate(servicePaymentVO.getInvoiceDate());
        paymentBO.setInvoiceNo(servicePaymentVO.getInvoiceNo());
        paymentBO.setPaymentControlNo(servicePaymentVO.getFactoryPaymentControlNo());
        paymentBO.setPaymentDealerCode(servicePaymentVO.getSiteId());
        paymentBO.setSerialNo(servicePaymentVO.getSerialNo());
        paymentBO.setVatCode(servicePaymentVO.getVatCd());

        svm0202Service.issue(servicePaymentVO, servicePaymentEditHistoryVO, QueueDataVO.create(servicePaymentVO.getSiteId(), InterfCode.OX_SV_PAYMENTCONFIRM, "service_payment", servicePaymentVO.getPaymentId(), JsonUtils.toString(List.of(paymentBO).stream().toList())));
    }

    private void validateData(SVM020202Form form, String siteId) {

        // 如果invoiceNo和serialNo在数据库中已存在，则报错
        int checkCount1 = svm0202Service.getCheckCountByInvoiceNoAndSerialNo(form, siteId);

        if(checkCount1 >0) {
            // 后续servicePayment用codeMessage取
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00310", new String[] {CodedMessageUtils.getMessage("label.invoiceNumber"), form.getInvoiceNo(), CodedMessageUtils.getMessage("label.serialNumber"), form.getSerialNo(), CodedMessageUtils.getMessage("label.tableServicePaymentInfo")}));
        }

        // 如果invoiceNo在数据库中已存在，则报错
        int checkCount2 = svm0202Service.getCheckCountByInvoiceNo(form, siteId);

        if(checkCount2 >0) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00309", new String[] {CodedMessageUtils.getMessage("label.invoiceNumber"), form.getInvoiceNo(), CodedMessageUtils.getMessage("label.tableServicePaymentInfo")}));
        }
    }

    public DownloadResponseView print(SVM020202Form form) {

        String paymentCategory = form.getCategory();
        Long paymentId = form.getPaymentId();

        //PaymentCategory = S012CLAIM / S012SPECIALCLAIM 出力 ServiceExpensesClaimStatement
        List<String> claimList = Arrays.asList(ServiceCategory.CLAIM.getCodeDbid(),
                                               ServiceCategory.SPECIALCLAIM.getCodeDbid());

        if (claimList.contains(paymentCategory)) {

            return this.printClaimStatement(paymentId);

        }

        //PaymentCategory = S012CLAIMBATTERY 出力 ServiceExpensesClaimStatementForEV
        if (StringUtils.equals(ServiceCategory.CLAIMBATTERY.getCodeDbid(), paymentCategory)) {

            return this.printClaimStatementForEV(paymentId);

        }

        //PaymentCategory = S012FREECOUPON 出力 ServiceExpensesCouponStatement
        if (StringUtils.equals(ServiceCategory.FREECOUPON.getCodeDbid(), paymentCategory)) {

            return this.printCouponStatement(paymentId, form.getSiteId());

        }

        return null;
    }

    /**
     * ServiceExpensesClaimStatementReport
     */
    private DownloadResponseView printClaimStatement(Long paymentId) {

        List<SVM0202PrintBO> dataList = new ArrayList<>();

        SVM0202PrintBO printBO = svm0202Service.getServiceExpensesClaimStatementPrintData(paymentId);

        if (!Nulls.isNull(printBO)) {

            printBO.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DATE_FORMAT)));

            String claimMonth = printBO.getClaimMonth();
            if (StringUtils.isNotBlank(claimMonth) && claimMonth.length() >= 6) {

                String claimMonthFormatter = claimMonth.substring(4, 6) +
                                             CommonConstants.CHAR_SLASH +
                                             claimMonth.substring(0, 4);

                printBO.setClaimMonth(claimMonthFormatter);
            }

            dataList.add(printBO);
        }

        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_SERVICEEXPENSESCLAIMSTATEMENT_REPORT,
                                                   dataList,
                                                   CommonConstants.CHAR_BLANK);

        return new DownloadResponseView(rp);
    }

    /**
     * ServiceExpensesClaimStatementForEVReport
     */
    private DownloadResponseView printClaimStatementForEV(Long paymentId) {

        List<SVM0202PrintBO> dataList = new ArrayList<>();

        SVM0202PrintBO printBO = svm0202Service.getServiceExpensesClaimStatementForEVPrintData(paymentId);

        if (!Nulls.isNull(printBO)) {

            printBO.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DATE_FORMAT)));

            String claimMonth = printBO.getClaimMonth();
            if (StringUtils.isNotBlank(claimMonth) && claimMonth.length() >= 6) {

                String claimMonthFormatter = claimMonth.substring(4, 6) +
                                             CommonConstants.CHAR_SLASH +
                                             claimMonth.substring(0, 4);

                printBO.setClaimMonth(claimMonthFormatter);
            }

            dataList.add(printBO);
        }

        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_SERVICEEXPENSESCLAIMSTATEMENTFOREV_REPORT,
                                                   dataList,
                                                   CommonConstants.CHAR_BLANK);

        return new DownloadResponseView(rp);
    }

    /**
     * ServiceExpensesCouponStatementReport
     */
    private DownloadResponseView printCouponStatement(Long paymentId, String siteId) {

        List<SVM0202PrintBO> dataList = new ArrayList<>();

        SVM0202PrintBO printBO = svm0202Service.getServiceExpensesCouponStatementPrintData(paymentId);

        if (!Nulls.isNull(printBO)) {

            printBO.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DATE_FORMAT)));

            String claimMonth = printBO.getClaimMonth();
            if (StringUtils.isNotBlank(claimMonth) && claimMonth.length() >= 6) {

                String claimMonthFormatter = claimMonth.substring(4, 6) +
                                             CommonConstants.CHAR_SLASH +
                                             claimMonth.substring(0, 4);

                printBO.setClaimMonth(claimMonthFormatter);

                SVM0202PrintBO detail = svm0202Service.getServiceExpensesCouponStatementPrintDetailData(siteId, claimMonth);

                if (!Nulls.isNull(detail)) {

                    printBO.setFir(detail.getFir());
                    printBO.setSec(detail.getSec());
                    printBO.setThi(detail.getThi());
                    printBO.setFou(detail.getFou());
                    printBO.setFif(detail.getFif());
                    printBO.setSi(detail.getSi());
                    printBO.setSev(detail.getSev());
                    printBO.setEig(detail.getEig());
                    printBO.setNin(detail.getNin());

                }
            }

            dataList.add(printBO);

        }

        DownloadResponse rp = pdfExporter.generate(FileConstants.JASPER_SERVICEEXPENSESCOUPONSTATEMENT_REPORT,
                                                   dataList,
                                                   CommonConstants.CHAR_BLANK);

        return new DownloadResponseView(rp);
    }

}