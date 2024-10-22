package com.a1stream.parts.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.parts.SPQ050401BO;
import com.a1stream.domain.custom.SpWhDwRepositoryCustom;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Service
public class SPQ0504Service{

    @Resource
    private SpWhDwRepositoryCustom spWhDwRepositoryCustom;

    public List<SPQ050401BO> retrieveWhList(String siteId, String pointCd, String year, String month) {

        return BeanMapUtils.mapListTo(spWhDwRepositoryCustom.findWhList(siteId, pointCd, year, month),SPQ050401BO.class);
    }

}
