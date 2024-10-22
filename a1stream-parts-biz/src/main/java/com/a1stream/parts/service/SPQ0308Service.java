package com.a1stream.parts.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.parts.SPQ030801BO;
import com.a1stream.domain.form.parts.SPQ030801Form;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;

import jakarta.annotation.Resource;

/**
* 功能描述:Parts Location Usage Inquiry
*
* @author mid2178
*/
@Service
public class SPQ0308Service {

    @Resource
    private LocationRepository locationRepo;

    @Resource
    private ProductInventoryRepository productInventoryRepo;

    public List<SPQ030801BO> getLocationAll(SPQ030801Form form, String siteId) {

        return locationRepo.getUsageLocationAll(siteId, form.getPointId(), form.getBinTypeId(), form.getLocationTypeId());
    }

    public Map<String, SPQ030801BO> getLocationInUse(SPQ030801Form form, String siteId) {

        return productInventoryRepo.getLocationInUse(siteId, form.getPointId(), form.getBinTypeId(), form.getLocationTypeId());
    }
}
