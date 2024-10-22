package com.a1stream.ifs.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.service.ProductBO;
import com.a1stream.domain.vo.CmmWarrantyModelPartVO;
import com.a1stream.ifs.bo.WarrantyPartsBO;
import com.a1stream.ifs.bo.WarrantyPartsItemBO;
import com.a1stream.ifs.service.SvWarrantyPartsService;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Component
public class SvWarrantyPartsFacade {

    @Resource
    private SvWarrantyPartsService warrantyPartsSer;

    private static final String SITE_666N = CommonConstants.CHAR_DEFAULT_SITE_ID;
    private static final String GOODS = ProductClsType.GOODS.getCodeDbid();
    private static final String PARTS = ProductClsType.PART.getCodeDbid();

    /**
     * IX_warrantyParts
     * serviceOrderManager doWarrantyPartsImport
     */
    public void importWarrantyParts(List<WarrantyPartsBO> dataList) {

        if(dataList ==null || dataList.isEmpty()) { return; }

        warrantyPartsSer.deleteAllWarrantyModelPart();

        List<CmmWarrantyModelPartVO> updWarrantyModelPartList = new ArrayList<>();

        Set<String> productCds = dataList.stream().map(WarrantyPartsBO::getModelCode).map(modelCode -> modelCode.substring(0, 4)) // 截取每个字符串的前四个字符
                .collect(Collectors.toSet());

        Set<String> partsNo = dataList.stream().filter(item -> item.getResultList() != null)
                .flatMap(item -> item.getResultList().stream())
                .map(WarrantyPartsItemBO::getPartNo).collect(Collectors.toSet());

        Map<String, ProductBO> goodsMap = warrantyPartsSer.getMstProduct(productCds, GOODS);

        Map<String, ProductBO> partsMap = warrantyPartsSer.getMstProduct(partsNo, PARTS);

        for(WarrantyPartsBO model : dataList) {

            Map<String, Map<Long, String>> partResultMap = new HashMap<>();

            Map<Long, String> modelMap = new HashMap<>();
            existLikeProductCd(goodsMap, model.getModelCode().substring(0, 4), GOODS, modelMap);

            if (!modelMap.keySet().isEmpty()) {
                // get partsMap by partNo
                List<WarrantyPartsItemBO> resultList = model.getResultList();
                for (WarrantyPartsItemBO item : resultList) {

                    Map<Long, String> existPartsMap = new HashMap<>();
                    existLikeProductCd(partsMap, item.getPartNo(), PARTS, existPartsMap);
                    if (!existPartsMap.keySet().isEmpty()) {
                        partResultMap.put(item.getPartNo(), existPartsMap);
                    }
                }
                // set data into model
                if (!partResultMap.isEmpty()) {
                    for (Long modelId : modelMap.keySet()) {
                        for (String modelPartNo : partResultMap.keySet()) {
                            for (Long partId : partResultMap.get(modelPartNo).keySet()) {

                                String modelCd = modelMap.get(modelId);
                                String partCd = partResultMap.get(modelPartNo).get(partId);

                                buildCmmWarrantyModelPartVO(model, modelCd, modelId, partCd, updWarrantyModelPartList);
                            }
                        }
                    }
                } else {
                    for (Long modelId : modelMap.keySet()) {

                        String modelCd = modelMap.get(modelId);
                        String partCd = "";

                        buildCmmWarrantyModelPartVO(model, modelCd, modelId, partCd, updWarrantyModelPartList);
                    }
                }
            }
        }

        warrantyPartsSer.maintainData(updWarrantyModelPartList);
    }

    private void buildCmmWarrantyModelPartVO(WarrantyPartsBO model, String modelCd, Long modelId, String partCd, List<CmmWarrantyModelPartVO> updWarrantyModelPartList) {

        CmmWarrantyModelPartVO modelParts = new CmmWarrantyModelPartVO();

        modelParts.setSiteId(SITE_666N);
        modelParts.setWarrantyType(model.getType());
        modelParts.setWarrantyValue(model.getValueData());
        modelParts.setModelCd(modelCd);
        modelParts.setModelId(modelId);
        modelParts.setPartCd(partCd);

        updWarrantyModelPartList.add(modelParts);
    }

    private void existLikeProductCd(Map<String, ProductBO> productMap, String productCd, String productCls, Map<Long, String> existProductMap) {

        ProductBO productBO;
        for (String key : productMap.keySet()) {
            if (StringUtils.equals(GOODS, productCls)) {
                if (key.startsWith(productCd)) {
                    productBO = productMap.get(key);
                    existProductMap.put(productBO.getProductId(), productBO.getProductCd());
                }
            } else if (StringUtils.equals(PARTS, productCls)) {
                if (productCd.length() == 5) {
                    if (key.contains(productCd)) {
                        productBO = productMap.get(key);
                        existProductMap.put(productBO.getProductId(), productBO.getProductCd());
                    }
                } else {
                    if (StringUtils.equals(key, productCd)) {
                        productBO = productMap.get(key);
                        existProductMap.put(productBO.getProductId(), productBO.getProductCd());
                    }
                }
            }

        }
    }
}
