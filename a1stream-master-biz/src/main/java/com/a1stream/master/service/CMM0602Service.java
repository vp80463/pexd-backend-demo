package com.a1stream.master.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.master.CMM060201BO;
import com.a1stream.domain.bo.master.CMM060202Detail;
import com.a1stream.domain.entity.ServicePackage;
import com.a1stream.domain.entity.ServicePackageCategory;
import com.a1stream.domain.entity.ServicePackageItem;
import com.a1stream.domain.form.master.CMM060201Form;
import com.a1stream.domain.repository.ServicePackageCategoryRepository;
import com.a1stream.domain.repository.ServicePackageItemRepository;
import com.a1stream.domain.repository.ServicePackageRepository;
import com.a1stream.domain.vo.ServicePackageCategoryVO;
import com.a1stream.domain.vo.ServicePackageItemVO;
import com.a1stream.domain.vo.ServicePackageVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid1966
*/
@Service
public class CMM0602Service {

    @Resource
    private ServicePackageRepository svPackageRepo;

    @Resource
    private ServicePackageCategoryRepository svPackageCtgRepo;

    @Resource
    private ServicePackageItemRepository svPackageItemRepo;

    public List<CMM060201BO> getSvPackageData(CMM060201Form model) {

        return svPackageRepo.getSvPackageData(model);
    }

    public ServicePackageVO findServicePackage(Long servicePackageId) {

        return BeanMapUtils.mapTo(svPackageRepo.findByServicePackageId(servicePackageId), ServicePackageVO.class);
    }

    public List<CMM060202Detail> getCategoryDetails(Long servicePackageId, String siteId) {

        return svPackageCtgRepo.getCategoryDetails(servicePackageId, siteId);
    }

    public Integer existServicePackage(String packageCd, String siteId) {

        return svPackageRepo.existServicePackage(packageCd, siteId);
    }

    public Map<String, List<CMM060202Detail>> getSvPackageDetailMap(Long servicePackageId, String siteId) {

        List<CMM060202Detail> svPackageItems = svPackageItemRepo.getSvPackageItemList(servicePackageId, siteId);

        return svPackageItems.stream().collect(Collectors.groupingBy(CMM060202Detail::getProductClsType));
    }

    public Map<Long, ServicePackageCategoryVO> getSvPackageCtgMap(String siteId, Long servicePackageId) {

        List<ServicePackageCategoryVO> svPackageCtgList = BeanMapUtils.mapListTo(svPackageCtgRepo.findBySiteIdAndServicePackageId(siteId, servicePackageId), ServicePackageCategoryVO.class);

        return svPackageCtgList.stream().collect(Collectors.toMap(ServicePackageCategoryVO::getServicePackageCategoryId, Function.identity()));
    }

    public Map<String, List<ServicePackageItemVO>> getSvPackageItemMap(Long servicePackageId, String siteId) {

        List<ServicePackageItemVO> svPackageItems = BeanMapUtils.mapListTo(svPackageItemRepo.findSvPackageItmes(siteId, servicePackageId), ServicePackageItemVO.class);

         return svPackageItems.stream().collect(Collectors.groupingBy(ServicePackageItemVO::getProductClassification));
    }

    public void maintainData(ServicePackageVO svPackage
                             ,List<ServicePackageCategoryVO> svPackageCtgList, List<Long> removeSvPackageCtgId
                             ,List<ServicePackageItemVO> svPackageItemList, List<Long> removeSvPackageItemId) {

        svPackageRepo.save(BeanMapUtils.mapTo(svPackage, ServicePackage.class));

        svPackageCtgRepo.saveInBatch(BeanMapUtils.mapListTo(svPackageCtgList, ServicePackageCategory.class));
        svPackageItemRepo.saveInBatch(BeanMapUtils.mapListTo(svPackageItemList, ServicePackageItem.class));
        if (!removeSvPackageCtgId.isEmpty()) {
            svPackageCtgRepo.deleteAllByIdInBatch(removeSvPackageCtgId);
        }
        if (!removeSvPackageItemId.isEmpty()) {
            svPackageItemRepo.deleteAllByIdInBatch(removeSvPackageItemId);
        }
    }
}