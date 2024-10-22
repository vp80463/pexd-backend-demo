package com.a1stream.ifs.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.common.bo.SdManifestItemBO;
import com.a1stream.common.manager.ReceiptSlipManager;

import jakarta.annotation.Resource;

@Service
public class SdManifestService {

    @Resource
    private ReceiptSlipManager receiptSlipManager;

    public void doManifestImportsForSD(List<SdManifestItemBO> sdManifestItemBOList, String siteId){
        receiptSlipManager.importDataForSD(sdManifestItemBOList, siteId);
    }

}
