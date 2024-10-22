package com.a1stream.web.app.controller.unit;

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
import com.a1stream.domain.bo.unit.SDQ010701BO;
import com.a1stream.domain.bo.unit.SDQ010702BO;
import com.a1stream.domain.form.unit.SDQ010701Form;
import com.a1stream.domain.form.unit.SDQ010702Form;
import com.a1stream.unit.facade.SDQ0107Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author mid2259
 */
@RestController
@RequestMapping("unit/sdq0107")
@FunctionId("SDQ0107")
public class SDQ0107Controller implements RestProcessAware{

    @Resource
    private SDQ0107Facade sdq0107Facade;

    @PostMapping(value = "/findReceiptManifestList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SDQ010701BO> findReceiptManifestList(@RequestBody final SDQ010701Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return sdq0107Facade.findReceiptManifestList(form);
    }

    @PostMapping(value = "/fileUpload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void escortFileUploadTest(final HttpServletRequest request
            , @RequestParam(value = "uploadFile") MultipartFile[] files
            , @AuthenticationPrincipal final PJUserDetails uc) {

        sdq0107Facade.fileUpload(files,uc.getDealerCode());
    }

    //sdq010702
    @PostMapping(value = "/getReceiptManifestMaintenance.json")
    public SDQ010702BO getReceiptManifestMaintenance(@RequestBody final SDQ010702Form form) {

        return sdq0107Facade.getReceiptManifestMaintenance(form.getReceiptManifestId());
    }

    @PostMapping(value = "/doIssue.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void confirmReceiptManifestMaintenance(@RequestBody final SDQ010702Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        sdq0107Facade.confirmRecManMaint(form,uc);
    }
}
