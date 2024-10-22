package com.a1stream.domain.custom;

import com.a1stream.domain.entity.CmmMessage;

import java.util.List;

/**
 * @author dong zhen
 */
public interface CmmMessageRepositoryCustom {

    List<CmmMessage> getImportantRemindList(List<String> siteIds, String userId);
}
