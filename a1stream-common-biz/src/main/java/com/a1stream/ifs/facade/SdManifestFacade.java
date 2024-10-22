package com.a1stream.ifs.facade;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.bo.SdManifestItemBO;
import com.a1stream.ifs.service.SdManifestService;

import jakarta.annotation.Resource;

@Component
public class SdManifestFacade {

    @Resource
    private SdManifestService sdManifestService;

    /**
     * 取得sdMasterI/F文件, 更新sdMaster
     */
    public void sdManifestImport(List<SdManifestItemBO> sdProductInfo) {

        if(sdProductInfo == null || sdProductInfo.isEmpty()) {
            return;
        }

        //按照dealerCode分组后, 循环调用ReceiptSlipManager.importDataForSD共同方法
        Map<String, List<SdManifestItemBO>> manifestMap = sdProductInfo.stream().collect(Collectors.groupingBy(SdManifestItemBO::getDealerCd));

        for (Map.Entry<String, List<SdManifestItemBO>> entry : manifestMap.entrySet()) {

            String siteId = entry.getKey();
            List<SdManifestItemBO> sdManifestItemBOList = entry.getValue();

            sdManifestService.doManifestImportsForSD(sdManifestItemBOList, siteId);
        }

    }

}
