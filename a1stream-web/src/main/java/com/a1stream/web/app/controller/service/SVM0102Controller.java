package com.a1stream.web.app.controller.service;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.common.facade.MenuFacade;
import com.a1stream.common.model.BaseResult;
import com.a1stream.common.model.ServiceJobVLBO;
import com.a1stream.domain.bo.service.SVM010201BO;
import com.a1stream.domain.bo.service.SVM010201FreeCouponConditionBO;
import com.a1stream.domain.bo.service.SVM010201ServiceHistoryBO;
import com.a1stream.domain.bo.service.SpecialClaimBO;
import com.a1stream.domain.form.service.SVM010201Form;
import com.a1stream.service.facade.SVM0102Facade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import software.amazon.awssdk.utils.StringUtils;

/**
* 功能描述:Service Order明细画面
*
* mid1341
* 2024年6月7日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/07   He Xiaochuan     New
*/
@RestController
@RequestMapping("service/svm0102")
@FunctionId("SVM0102")
public class SVM0102Controller implements RestProcessAware{

    public static final String UPDATE_PROGRAM = "svm0102";

    @Resource
    private SVM0102Facade svm0102Facade;
    @Resource
    private MenuFacade menuFacade;
    @Resource
    private ExcelFileExporter exporter;

    @PostMapping(value = "/getServiceDetailByIdOrNo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SVM010201BO getServiceDetailByIdOrNo(@RequestBody final SVM010201Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm0102Facade.getServiceDetailByIdOrNo(model.getOrderInfo().getServiceOrderId(), model.getOrderInfo().getOrderNo(), model.getOrderInfo().getPointId(), model.getOrderInfo().getFrameNo() , model.getOrderInfo().getPlateNo(),uc);
    }

    @PostMapping(value = "/newOrModifyServiceOrder.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long newOrModifyServiceOrder(@RequestBody final SVM010201Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        model.setSiteId(uc.getDealerCode());
        svm0102Facade.newOrModifyServiceOrder(model, uc);

        return model.getOrderInfo().getServiceOrderId();
    }

    @PostMapping(value = "/settleServiceOrder.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long settleServiceOrder(@RequestBody final SVM010201Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        model.setSiteId(uc.getDealerCode());
        svm0102Facade.settleOperationForServiceOrder(model, uc);

        return model.getOrderInfo().getServiceOrderId();
    }

    @PostMapping(value = "/cancelServiceOrder.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long cancelServiceOrder(@RequestBody final SVM010201Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        svm0102Facade.cancelOperationForServiceOrder(model, uc);

        return model.getOrderInfo().getServiceOrderId();
    }

    @PostMapping(value = "/getMotorDetailByPlateOrFrame.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SVM010201BO getMotorDetailByPlateOrFrame(@RequestBody final SVM010201Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm0102Facade.getMotorDetailByPlateOrFrame(model.getOrderInfo(), uc.getDoFlag(), uc.getDealerCode());
    }

    @PostMapping(value = "/getSpecialClaimDetail.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SpecialClaimBO getSpecialClaimDetail(@RequestBody final SVM010201Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm0102Facade.getSpecialClaimDetail(model.getOrderInfo().getSpecialClaimId(), model.getOrderInfo().getCmmSerializedProductId(), model.getOrderInfo().getModelCd(), uc.getTaxPeriod());
    }

    @PostMapping(value = "/getFreeCouponDetail.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ServiceJobVLBO getFreeCouponJobDetail(@RequestBody final SVM010201Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm0102Facade.getFreeCouponDetail(model.getOrderInfo().getJobCd(), model.getOrderInfo().getModelCd(), uc.getTaxPeriod());
    }

    @PostMapping(value = "/getServicePackageDetail.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SVM010201BO getServicePackageDetail(@RequestBody final SVM010201Form model, @AuthenticationPrincipal final PJUserDetails uc) {
        model.setSiteId(uc.getDealerCode());
        return svm0102Facade.getServicePackageDetail(model, uc.getTaxPeriod());
    }

    @PostMapping(value = "/getServiceHistory.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SVM010201ServiceHistoryBO> getServiceHistory(@RequestBody final SVM010201Form model, @AuthenticationPrincipal final PJUserDetails uc) {
        return svm0102Facade.listServiceHistory(model.getOrderInfo().getCmmSerializedProductId(), uc.getDealerCode());
    }

    @PostMapping(value = "/getFreeCouponHistory.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SVM010201FreeCouponConditionBO getFreeCouponHistory(@RequestBody final SVM010201Form model){
        return svm0102Facade.getFreeCouponHistory(model.getOrderInfo().getCmmSerializedProductId(), model.getOrderInfo().getModelCd(), model.getOrderInfo().getSoldDate());
    }

    @PostMapping(value = "/printServiceJobCard.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printServiceJobCard(@RequestBody final SVM010201Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        svm0102Facade.prepareServiceOrderEditHisForPrint(form.getOrderInfo().getServiceOrderId(), uc);
        return svm0102Facade.printServiceJobCard(form.getOrderInfo().getServiceOrderId(), uc.getDealerCode());
    }

    @PostMapping(value = "/printServiceJobCardForDO.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printServiceJobCardForDO(@RequestBody final SVM010201Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        svm0102Facade.prepareServiceOrderEditHisForPrint(form.getOrderInfo().getServiceOrderId(), uc);
        return svm0102Facade.printServiceJobCardForDO(form.getOrderInfo().getServiceOrderId(), uc.getDealerCode());
    }

    @PostMapping(value = "/printServicePayment.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printServicePayment(@RequestBody final SVM010201Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        svm0102Facade.prepareServiceOrderEditHisForPrint(form.getOrderInfo().getServiceOrderId(), uc);
        return svm0102Facade.printServicePayment(form.getOrderInfo().getServiceOrderId(), uc.getUsername());
    }

    @PostMapping(value = "/printServicePaymentForDO.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printServicePaymentForDO(@RequestBody final SVM010201Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        svm0102Facade.prepareServiceOrderEditHisForPrint(form.getOrderInfo().getServiceOrderId(), uc);
        return svm0102Facade.printServicePaymentForDO(form.getOrderInfo().getServiceOrderId());
    }

    @PostMapping(value = "/exportEditHistory.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView downloadExcel(@RequestBody final SVM010201Form model) {
        DownloadResponse excel = exporter.generate(FileConstants.EXCEL_TEMPLATE_SVM0102_01_ORDERHISTORY,svm0102Facade.exportOrderHistoryList(model.getOrderInfo().getServiceOrderId()), FileConstants.EXCEL_EXPORT_SVM0102_01_ORDERHISTORY);
        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/privacyPolicyResultsFileUpload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult privacyPolicyResultsFileUpload(final HttpServletRequest request
            , @RequestParam(value = "file") MultipartFile[] files
            , @RequestParam(value = "formData", required = false) String formDataJson
            , @AuthenticationPrincipal final PJUserDetails uc) {

        BaseResult result = new BaseResult();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SVM010201BO model = new SVM010201BO();
        if (StringUtils.isNotBlank(formDataJson)){
            try {
                model = objectMapper.readValue(formDataJson, SVM010201BO.class);
            } catch (JsonProcessingException e) {
                throw new BusinessCodedException("Parameter parsing fails, upload fails, please contact the administrator!");
            }
        }
        SVM010201Form form = new SVM010201Form();
        form.setOrderInfo(model);
        form.setSiteId(uc.getDealerCode());
        result.setData(svm0102Facade.privacyPolicyResultsFileUpload(form, files));
        result.setMessage(BaseResult.SUCCESS_MESSAGE);
        return result;
    }
}