package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.master.CMM060202Detail;

public interface ServicePackageCategoryRepositoryCustom {

    List<CMM060202Detail> getCategoryDetails(Long servicePackageId, String siteId);
}