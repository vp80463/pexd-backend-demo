package com.a1stream.web.app.controller.master;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.Resource;
import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import  com.a1stream.domain.bo.master.CMQ020101BO;
import com.a1stream.master.facade.CMQ0201Facade;

/**
 * @author li jiajun
 */
@RestController
@RequestMapping("master/cmq0201")
@FunctionId("CMQ0201")
public class CMQ0201Controller {

    @Resource
    private CMQ0201Facade cmq0201Facade;

    @PostMapping(value = "/upload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void upload( @RequestPart(value = "uploadFile") MultipartFile file
                      , @AuthenticationPrincipal final PJUserDetails uc) {
        cmq0201Facade.upload(file);
    }

    @PostMapping(value = "/init.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public CMQ020101BO init(@AuthenticationPrincipal final PJUserDetails uc) {
        return cmq0201Facade.init(uc.getDealerCode(),uc.getUserId());    
    }
}
