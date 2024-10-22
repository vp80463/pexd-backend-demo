package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.bo.parts.SPQ050401BO;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Repository
public interface SpWhDwRepositoryCustom {

    /**
     * author: Tang Tiantian
     */
    List<SPQ050401BO> findWhList(String siteId, String facilityCd, String targetYear, String targetMonth);

}
