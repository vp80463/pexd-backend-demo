package com.a1stream.common.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.domain.entity.MstProduct;
import com.a1stream.domain.entity.MstProductRelation;
import com.a1stream.domain.repository.MstProductRelationRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.validation.ValidationException;

import jakarta.annotation.Resource;

@Component
public class ProductManager {

    @Resource
    private MstProductRelationRepository productRelationRepo;
    @Resource
    private MstProductRepository mstProductRepository;

    private final static int MAX_SUPER_SEDING_RECURSIVE_LEVEL = 7;

    public boolean checkPartsSupersedingRelation(Long toProductId, Long fromProductId) {

        return this.checkSuperSedingRecursively(1, toProductId, fromProductId);
    }

    private boolean checkSuperSedingRecursively(int level, Long toProductId, Long fromProductId) {

        boolean checkResult = false;
        if (level <= MAX_SUPER_SEDING_RECURSIVE_LEVEL) {

            if (toProductId == null || fromProductId == null) {
                // do nothing
            } else if (toProductId.equals(fromProductId)) {
                checkResult = true;
            } else {
                List<MstProductRelation> productRelas = productRelationRepo.findByToProductId(toProductId);
                Set<Long> foundFromProductIds = productRelas.stream().map(MstProductRelation::getFromProductId).collect(Collectors.toSet());
                if (foundFromProductIds.contains(fromProductId)) {
                    checkResult = true;
                } else if (foundFromProductIds != null && foundFromProductIds.size() > 0) {
                    checkResult = checkSuperSedingRecursively(level++, foundFromProductIds.iterator().next(), fromProductId);
                }

            }
        }

        return checkResult;
    }

    public Map<Long, Long> getProductSupersededRelationMap(Set<Long> targetProductIds){

        return this.getProductSupersededRelationMapByLoop(targetProductIds, new HashMap<>(), CommonConstants.INTEGER_ZERO);
    }

    public Map<Long, Long> getProductSupersededRelationMapByLoop(Set<Long> targetProductIds, Map<Long, Long> productSupersededRelationMap, int loopCount){

        Map<Long, Long> result = productSupersededRelationMap;

        Set<Long> newSupersededProductIds = new HashSet<>();

        List<MstProductRelation> productSupersededRelationList = productRelationRepo.findByValidRelationType(CommonConstants.CHAR_DEFAULT_SITE_ID
                                                                                                           , PJConstants.ProductRelationClass.SUPERSEDING
                                                                                                           , targetProductIds
                                                                                                           , DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD)
                                                                                                           , DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));

        for (MstProductRelation productRelation : productSupersededRelationList){

            result.put(productRelation.getToProductId(), productRelation.getFromProductId());

            newSupersededProductIds.add(productRelation.getFromProductId());
        }

        if (!newSupersededProductIds.isEmpty()){

            if(loopCount == MAX_SUPER_SEDING_RECURSIVE_LEVEL) {

                throw new ValidationException("ME.00155", new String[]{String.valueOf(MAX_SUPER_SEDING_RECURSIVE_LEVEL)});
            }

            loopCount++;
            result = this.getProductSupersededRelationMapByLoop(newSupersededProductIds, result, loopCount);
        }

        return result;
    }

    public Map<Long, Long> getProductReleationInBulk(String siteId, String productReleationTypeId, Set<Long> targetProductInfos){

        Map<Long, Long> result = new HashMap<>();
        int i = 0;
        result = getProductReleationInfos(siteId, productReleationTypeId, targetProductInfos, result, i);

        return result;
    }

    private Map<Long, Long> getProductReleationInfos(String siteId
                                                         , String productReleationTypeId
                                                         , Set<Long> targetProductInfos
                                                         , Map<Long, Long> productReleaInfoResultMap, int i){

        Map<Long, Long> result = productReleaInfoResultMap;
        List<MstProductRelation> productRelationInfos = productRelationRepo.findByValidRelationType(siteId
                                                                                                  , productReleationTypeId
                                                                                                  , targetProductInfos
                                                                                                  , DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER)
                                                                                                  , DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER) );


        //Set superseding product info
        Set<Long> newSupersededProductIds = new HashSet<>();
        Long supersedingProductId = null;
        for (MstProductRelation member : productRelationInfos){

            supersedingProductId = member.getFromProductId();
            result.put(member.getToProductId(), supersedingProductId);
            if (supersedingProductId != null){
                newSupersededProductIds.add(supersedingProductId);
            }
        }

        if (newSupersededProductIds.size() > 0){

            if(i == 7) {

                MstProduct product = this.mstProductRepository.findByProductId(newSupersededProductIds.iterator().next());
                throw new ValidationException("M.W.10145",new String[] {product.getProductCd()});
            }

            i++;
            result = getProductReleationInfos(siteId, productReleationTypeId, newSupersededProductIds, result, i);
        }

        return result;
    }
}