package com.a1stream.web.app.controller.parts;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.parts.SPM030101BO;
import com.a1stream.domain.bo.parts.SPM030102BO;
import com.a1stream.domain.form.parts.SPM030101Form;
import com.a1stream.domain.form.parts.SPM030102Form;
import com.a1stream.parts.facade.SPM0301Facade;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;
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
@RestController
@RequestMapping("parts/spm0301")
@FunctionId("SPM0301")
public class SPM0301Controller implements RestProcessAware{

    @Resource
    private SPM0301Facade spm0301Facade;

    @PostMapping(value = "/getReceiptManifestList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPM030101BO> getReceiptManifestList(@RequestBody final SPM030101Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return spm0301Facade.getReceiptManifestList(form, uc);
    }

    @PostMapping(value = "/confirmReceiptManifest.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Long> confirmReceiptManifest(@RequestBody final SPM030101Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return spm0301Facade.confirmReceiptManifest(form, uc);
    }

    @PostMapping(value = "/getReceiptManifestItemList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM030101BO getReceiptManifestItemList(@RequestBody final SPM030102Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return spm0301Facade.getReceiptManifestItemList(form, uc);
    }

    @PostMapping(value = "/confirmReceiptManifestItem.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void confirmReceiptManifestItem(@RequestBody final SPM030102Form form) {

        spm0301Facade.confirmReceiptManifestItem(form);
    }

    @PostMapping(value = "/deleteReceiptManifestItem.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteReceiptManifestItem(@RequestBody final SPM030102Form form) {

        spm0301Facade.deleteReceiptManifestItem(form);
    }

    @PostMapping(value = "/check.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void check(@RequestBody final SPM030102Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        spm0301Facade.check(form, uc);
    }

    @PostMapping(value = "/getPurchaseOrderData.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM030102BO getPurchaseOrderData(@RequestBody final SPM030102Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return spm0301Facade.getPurchaseOrderData(form, uc);
    }

    @PostMapping(value = "/fileUpload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void escortFileUploadTest(@RequestPart(value = "uploadFile") MultipartFile[] file
            , @AuthenticationPrincipal final PJUserDetails uc) {

        spm0301Facade.fileUpload(file);
    }

    @PostMapping(value = "/printPartsStoringListForFinance.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printPartsStoringListForFinance(@RequestBody final SPM030101Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return spm0301Facade.printPartsStoringListForFinance(form.getReceiptSlipIds());
    }

    @PostMapping(value = "/download.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void download(@RequestBody final SPM030101Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        spm0301Facade.download(form);
    }

}
