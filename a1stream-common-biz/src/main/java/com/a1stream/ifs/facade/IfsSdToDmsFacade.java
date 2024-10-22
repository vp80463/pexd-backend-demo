package com.a1stream.ifs.facade;

import org.springframework.stereotype.Component;

import com.a1stream.common.bo.SdManifestItemBO;
import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.ifs.bo.SdProductImportBO;
import com.alibaba.fastjson.JSON;

import jakarta.annotation.Resource;

@Component
public class IfsSdToDmsFacade {

    @Resource
    private SdProductImportFacade sdProductImportFacade;

    @Resource
    private SdManifestFacade sdManifestImportFacade;

    public void doBusinessProcess(String interfCd, String detail) {

        switch(interfCd) {
            case InterfCode.IMPORTTODMS_PARTSINFO:
                sdProductImportFacade.sdProductImport(JSON.parseArray(detail, SdProductImportBO.class));
                break;
            case InterfCode.SD_TO_DMS_SDMANIFEST:
                sdManifestImportFacade.sdManifestImport(JSON.parseArray(detail, SdManifestItemBO.class));
                break;
        }
    }
}
