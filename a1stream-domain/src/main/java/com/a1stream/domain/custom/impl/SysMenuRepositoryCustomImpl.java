package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.common.MenuBO;
import com.a1stream.domain.custom.SysMenuRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class SysMenuRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements SysMenuRepositoryCustom {

    /**
     * @author mid1341
     */
    @Override
    public List<MenuBO> findMenuListByUserId(String userId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("   SELECT menu_id AS id                            ");
        sql.append("        , menu_code AS name                        ");
        sql.append("        , link_url AS path                         ");
        sql.append("        , menu_seq AS rank                         ");
        sql.append("        , menu_name AS title                       ");
        sql.append("        , parent_menu_id AS parentId               ");
        sql.append("        , CASE WHEN visitable_flag = '0' THEN FALSE ELSE TRUE END AS showLink            ");
        sql.append("        , CASE WHEN length(window_target) > 0 THEN window_target ELSE link_url END AS activePath            ");
        sql.append("     FROM sys_menu sm                              ");
        sql.append("    WHERE sm.menu_id IN (                          ");
        sql.append("        SELECT menu_id                             ");
        sql.append("          FROM (                                   ");
        sql.append("             SELECT menu_id, JSON_ARRAY_ELEMENTS(authority_list -> 'roleAuthData') ->> 'roleId' AS role_Id ");
        sql.append("               FROM sys_menu_authority             ");
        sql.append("               ) menu_auth                         ");
        sql.append("         WHERE menu_auth.role_id IN (              ");
        sql.append("              SELECT JSON_ARRAY_ELEMENTS(sua.role_list) ->> 'roleId' ");
        sql.append("                FROM sys_user_authority sua        ");
        sql.append("               WHERE sua.user_id = :userId         ");
        sql.append("        )                                          ");
        sql.append("    )                                              ");

        params.put("userId", userId);

        return super.queryForList(sql.toString(), params, MenuBO.class);
    }

    /**
     * @author mid1341
     */
    @Override
    public List<MenuBO> findMenuListBySwitchDealer(String userId, String switchRoleType){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("   SELECT menu_id AS id                            ");
        sql.append("        , menu_code AS name                        ");
        sql.append("        , link_url AS path                         ");
        sql.append("        , menu_seq AS rank                         ");
        sql.append("        , menu_name AS title                       ");
        sql.append("        , parent_menu_id AS parentId               ");
        sql.append("        , CASE WHEN visitable_flag = '0' THEN FALSE ELSE TRUE END AS showLink            ");
        sql.append("        , CASE WHEN length(window_target) > 0 THEN window_target ELSE link_url END AS activePath           ");
        sql.append("     FROM sys_menu sm                              ");
        sql.append("    WHERE sm.menu_id IN (                          ");
        sql.append("        SELECT menu_id                             ");
        sql.append("          FROM (                                   ");
        sql.append("             SELECT menu_id, JSON_ARRAY_ELEMENTS(authority_list -> 'roleAuthData') ->> 'roleId' AS role_Id ");
        sql.append("               FROM sys_menu_authority             ");
        sql.append("               ) menu_auth                         ");
        sql.append("         WHERE menu_auth.role_id IN (              ");
        sql.append("                SELECT role_id                     ");
        sql.append("                  FROM (                           ");
        sql.append("                     SELECT role_id , JSON_ARRAY_ELEMENTS(sri.extend_list) ->> 'roleId' AS parent_role_id  ");
        sql.append("                       FROM sys_role_info sri      ");
        sql.append("                      WHERE sri.type = :switchRoleType    ");
        sql.append("                      ) switch_role                       ");
        sql.append("                 WHERE switch_role.parent_role_id IN (    ");
        sql.append("                     SELECT JSON_ARRAY_ELEMENTS(sua.role_list) ->> 'roleId'     ");
        sql.append("                       FROM sys_user_authority sua                              ");
        sql.append("                      WHERE sua.user_id = :userId                               ");
        sql.append("                 )                                                              ");
        sql.append("         )                                          ");
        sql.append("    )                                              ");

        params.put("userId", userId);
        params.put("switchRoleType", switchRoleType);

        return super.queryForList(sql.toString(), params, MenuBO.class);
    }

    /**
     * @author mid1341
     */
    @Override
    public String findMenuJson() {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" WITH base_menu_list AS (                                                                    ");
        sql.append("     SELECT menu_id, menu_code, menu_label, parent_menu_id, menu_seq                        ");
        sql.append("       FROM sys_menu sm                                                                    ");
        sql.append("      WHERE site_id = :defaultSiteId and visitable_flag = '1'                              ");
        sql.append(" ), role_menu_list AS (                                                                    ");
        sql.append("     SELECT bml.menu_code  AS menu_code1                                                   ");
        sql.append("          , bml.menu_id    AS menu_id1                                                     ");
        sql.append("          , bml.menu_label  AS menu_name1                                                   ");
        sql.append("          , bml.menu_seq   AS menu_seq1                                                    ");
        sql.append("          , bml2.menu_code AS menu_code2                                                   ");
        sql.append("          , bml2.menu_id   AS menu_id2                                                     ");
        sql.append("          , bml2.menu_label AS menu_name2                                                   ");
        sql.append("          , bml2.menu_seq  AS menu_seq2                                                    ");
        sql.append("          , bml3.menu_code AS menu_code3                                                   ");
        sql.append("          , bml3.menu_id   AS menu_id3                                                     ");
        sql.append("          , bml3.menu_label AS menu_name3                                                   ");
        sql.append("          , bml3.menu_seq  AS menu_seq3                                                    ");
        sql.append("       FROM base_menu_list bml                                                             ");
        sql.append("  LEFT JOIN base_menu_list bml2                                                            ");
        sql.append("         ON bml2.parent_menu_id = bml.menu_id                                              ");
        sql.append("  LEFT JOIN base_menu_list bml3                                                            ");
        sql.append("         ON bml3.parent_menu_id = bml2.menu_id                                             ");
        sql.append("      WHERE bml.parent_menu_id is null                                                     ");
        sql.append("      AND bml3.menu_code is not null                                                       ");
        sql.append("   ORDER BY bml.menu_seq,bml2.menu_seq,bml3.menu_seq                                       ");
        sql.append(" ), third_list AS (                                                                        ");
        sql.append("     SELECT rml.menu_code1                                                                 ");
        sql.append("          , rml.menu_id1                                                                   ");
        sql.append("          , rml.menu_name1                                                                 ");
        sql.append("          , rml.menu_seq1                                                                  ");
        sql.append("          , rml.menu_code2                                                                 ");
        sql.append("          , rml.menu_id2                                                                   ");
        sql.append("          , rml.menu_name2                                                                 ");
        sql.append("          , rml.menu_seq2                                                                  ");
        sql.append("          , json_agg(json_build_object( 'menuCd'    , rml.menu_code3                       ");
        sql.append("                                      , 'menuName'  , rml.menu_name3                       ");
        sql.append("                                      , 'menuId'  , rml.menu_id3                           ");
        sql.append("                                      , 'menuSeq'   , rml.menu_seq3                        ");
        sql.append("                                      , 'children'  , json_build_array())) as children2    ");
        sql.append("       FROM role_menu_list rml                                                             ");
        sql.append("   GROUP BY rml.menu_code1                                                                 ");
        sql.append("          , rml.menu_id1                                                                   ");
        sql.append("          , rml.menu_name1                                                                 ");
        sql.append("          , rml.menu_seq1                                                                  ");
        sql.append("          , rml.menu_code2                                                                 ");
        sql.append("          , rml.menu_id2                                                                   ");
        sql.append("          , rml.menu_name2                                                                 ");
        sql.append("          , rml.menu_seq2                                                                  ");
        sql.append("   ORDER BY rml.menu_seq1,rml.menu_seq2                                                    ");
        sql.append(" ) , second_list AS (                                                                      ");
        sql.append("     SELECT tl.menu_code1                                                                  ");
        sql.append("          , tl.menu_id1                                                                    ");
        sql.append("          , tl.menu_name1                                                                  ");
        sql.append("          , tl.menu_seq1                                                                   ");
        sql.append("          , json_agg(jsonb_build_object( 'menuCd'  , tl.menu_code2                         ");
        sql.append("                              , 'menuName', tl.menu_name2                                  ");
        sql.append("                              , 'menuId', tl.menu_id2                                      ");
        sql.append("                              , 'menuSeq' , tl.menu_seq2                                   ");
        sql.append("                              , 'children', tl.children2 )) as children1                   ");
        sql.append("      FROM third_list tl                                                                   ");
        sql.append("  GROUP BY tl.menu_code1                                                                   ");
        sql.append("         , tl.menu_id1                                                                     ");
        sql.append("         , tl.menu_name1                                                                   ");
        sql.append("         , tl.menu_seq1                                                                    ");
        sql.append("  ORDER BY tl.menu_seq1                                                                    ");
        sql.append(" )                                                                                         ");
        sql.append(" SELECT json_agg(jsonb_build_object( 'menuCd'     , sl.menu_code1                          ");
        sql.append("                                   , 'menuId'   , sl.menu_id1                              ");
        sql.append("                                   , 'menuName'   , sl.menu_name1                          ");
        sql.append("                                   , 'menuSeq'    , sl.menu_seq1                           ");
        sql.append("                                   , 'children'   , sl.children1 )) as menuJson            ");
        sql.append("   FROM second_list sl                                                                     ");

        params.put("defaultSiteId", CommonConstants.CHAR_DEFAULT_SITE_ID);

        return super.queryForSingle(sql.toString(), params, String.class);
    }


}