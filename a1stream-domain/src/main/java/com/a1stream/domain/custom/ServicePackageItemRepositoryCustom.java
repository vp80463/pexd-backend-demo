package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.master.CMM060202Detail;

public interface ServicePackageItemRepositoryCustom {

    List<CMM060202Detail> getSvPackageItemList(Long servicePackageId, String siteId);
}
