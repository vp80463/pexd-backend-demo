package com.a1stream.domain.custom.impl;

import com.a1stream.domain.custom.CmmMessageRepositoryCustom;
import com.a1stream.domain.entity.CmmMessage;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author dong zhen
 */
public class CmmMessageRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmMessageRepositoryCustom {

    @Override
    public List<CmmMessage> getImportantRemindList(List<String> siteIds, String userId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT *                                                                     ");
        sql.append(" FROM cmm_message cm                                                          ");
        sql.append(" JOIN sys_user_authority su ON EXISTS (                                       ");
        sql.append("     SELECT 1                                                                 ");
        sql.append("     FROM jsonb_array_elements(cm.sys_role_id_list::jsonb) AS role_id_element ");
        sql.append("     WHERE (role_id_element ->> 'roleId')::text IN (                          ");
        sql.append("         SELECT (rl ->> 'roleId')::text                                       ");
        sql.append("         FROM jsonb_array_elements(su.role_list::jsonb) AS rl                 ");
        sql.append("     )                                                                        ");
        sql.append(" )                                                                            ");
        sql.append(" WHERE su.user_id = :userId                                                   ");
        sql.append("   AND su.site_id in ( :siteIdList )                                          ");
        sql.append(" ORDER BY cm.last_updated DESC                                                ");

        params.put("userId", userId);
        params.put("siteIdList", siteIds);

        return super.queryForList(sql.toString(), params, CmmMessage.class);
    }
}