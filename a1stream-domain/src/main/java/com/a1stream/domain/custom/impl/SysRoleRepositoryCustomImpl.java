package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.common.RoleBO;
import com.a1stream.domain.bo.common.UserBO;
import com.a1stream.domain.bo.master.CMM070503BO;
import com.a1stream.domain.custom.SysRoleRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;

public class SysRoleRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements SysRoleRepositoryCustom {

	@Override
    public List<RoleBO> getUserRoleList(String roleType, BaseVLForm model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = role_list_sql(roleType, model, params);

        List<RoleBO> roleList = super.queryForList(sql.toString(), params, RoleBO.class);

        return roleList;
    }

    @Override
    public ValueListResultBO findUserRoleList(String roleType
                                       , BaseVLForm model) {

        int pageSize = model.getPageSize();
        int currentPage = model.getCurrentPage();

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = role_list_sql(roleType, model, params);

        // pop-up
        String countSql = "SELECT COUNT(1) FROM ( " + sql.toString() + ") AS subquery; ";
        Integer count = super.queryForSingle(countSql, params, Integer.class);

        // pop-over
        if (pageSize != 0 && currentPage >= 1) {

            sql.append(" OFFSET :offsetValue ");
            sql.append(" LIMIT :limitValue");

            params.put("offsetValue", pageSize * (currentPage - 1));
            params.put("limitValue", pageSize);
        }

        List<RoleBO> roleList = super.queryForList(sql.toString(), params, RoleBO.class);

        return new ValueListResultBO(roleList, count);
    }

	private StringBuilder role_list_sql(String roleType, BaseVLForm model, Map<String, Object> params) {
		StringBuilder sql = new StringBuilder();

        sql.append(" SELECT role_id        as roleId                            ");
        sql.append("      , role_code      as roleCode                          ");
        sql.append("      , role_name      as roleName                          ");
        sql.append("      , concat(role_code, ' ', role_name)  as desc          ");
        sql.append("      , effective_date as effectiveDate                     ");
        sql.append("      , expired_date   as expiredDate                       ");
        sql.append("   FROM sys_role_info                                       ");
        sql.append("  WHERE site_id = :defaultSiteId                            ");
        sql.append("    AND type    = :roleType                                 ");

        if (StringUtils.isNotBlank(model.getContent())) {

            sql.append(" AND concat(role_code, ' ', role_name) like :content ");

            params.put("content", CommonConstants.CHAR_PERCENT + model.getContent() + CommonConstants.CHAR_PERCENT);
        }

        sql.append(" ORDER BY role_code ");

        params.put("roleType", roleType);
        params.put("defaultSiteId", CommonConstants.CHAR_DEFAULT_SITE_ID);
		return sql;
	}

    @Override
    public List<CMM070503BO> findByRoleAndUser(String siteId
                                             , String roleId
                                             , String userSearch
                                             , String roleType) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("   SELECT sri.role_id   AS roleId                                                            ");
        sql.append("        , sri.role_code AS roleCd                                                            ");
        sql.append("        , sri.role_name AS roleNm                                                            ");
        sql.append("     FROM sys_role_info sri                                                                  ");
        sql.append("    WHERE sri.site_id = :defaultSiteId                                                       ");
        sql.append("      AND sri.type    = :roleType                                                            ");
        if (StringUtils.isNotBlank(roleId)) {
            sql.append("      AND sri.role_id = :roleId                                                              ");

            params.put("roleId"    , roleId);
        }
        if (StringUtils.isNotBlank(userSearch)) {
            sql.append("      AND sri.role_id IN ( SELECT JSON_ARRAY_ELEMENTS(sua.role_list) ->> 'roleId' AS roleId  ");
            sql.append("                             FROM sys_user su                                                ");
            sql.append("                                , sys_user_authority sua                                     ");
            sql.append("                            WHERE su.site_id = :siteId                                       ");
            sql.append("                              AND concat(su.user_code , ' ', su.nick_name) LIKE :userSearch  ");
            sql.append("                              AND su.site_id = sua.site_id                                   ");
            sql.append("                              AND su.user_id = sua.user_id )                                 ");

            params.put("siteId"    , siteId);
            params.put("userSearch", CommonConstants.CHAR_PERCENT + userSearch + CommonConstants.CHAR_PERCENT);
        }

        sql.append(" ORDER BY sri.role_code                                                                      ");

        params.put("defaultSiteId", CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("roleType"     , roleType);

        return super.queryForList(sql.toString(), params, CMM070503BO.class);
    }

    @Override
    public List<UserBO> findUserListByRoleId(String siteId
                                           , String roleId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" WITH user_list AS (                                                  ");
        sql.append("     SELECT su.user_id   AS userId                                    ");
        sql.append("          , su.user_code AS userCode                                  ");
        sql.append("          , su.nick_name AS nickName                                  ");
        sql.append("          , json_array_elements(sua.role_list) ->> 'roleId' AS roleId ");
        sql.append("       FROM sys_user su                                               ");
        sql.append("          , sys_user_authority sua                                    ");
        sql.append("      WHERE su.site_id = :siteId                                      ");
        sql.append("        AND su.site_id = sua.site_id                                  ");
        sql.append("        AND su.user_id = sua.user_id )                                ");
        sql.append("     SELECT userCode                                                  ");
        sql.append("          , nickName                                                  ");
        sql.append("     FROM user_list                                                   ");
        sql.append("    WHERE roleId = :roleId                                            ");
        sql.append(" ORDER BY userCode                                                    ");

        params.put("siteId", siteId);
        params.put("roleId", roleId);

        return super.queryForList(sql.toString(), params, UserBO.class);
    }

    @Override
    public String findMenuJsonByRole(String roleId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" WITH base_role_list AS (                                                                  ");
        sql.append("     SELECT menu_id                                                                        ");
        sql.append("          , json_array_elements(authority_list -> 'roleAuthData')->>'roleId' AS role_id    ");
        sql.append("       FROM sys_menu_authority                                                             ");
        sql.append("      WHERE site_id = :defaultSiteId                                                       ");
        sql.append("   GROUP BY menu_id, role_id                                                               ");
        sql.append(" ), base_menu_list AS (                                                                    ");
        sql.append("     SELECT menu_id, menu_code, menu_label, parent_menu_id, menu_seq                        ");
        sql.append("       FROM sys_menu sm                                                                    ");
        sql.append("      WHERE site_id = :defaultSiteId                                                       ");
        sql.append(" ), role_menu_list AS (                                                                    ");
        sql.append("     SELECT bml.menu_code  AS menu_code1                                                   ");
        sql.append("          , bml.menu_label  AS menu_name1                                                   ");
        sql.append("          , bml.menu_seq   AS menu_seq1                                                    ");
        sql.append("          , bml2.menu_code AS menu_code2                                                   ");
        sql.append("          , bml2.menu_label AS menu_name2                                                   ");
        sql.append("          , bml2.menu_seq  AS menu_seq2                                                    ");
        sql.append("          , bml3.menu_code AS menu_code3                                                   ");
        sql.append("          , bml3.menu_label AS menu_name3                                                   ");
        sql.append("          , bml3.menu_seq  AS menu_seq3                                                    ");
        sql.append("          , (CASE WHEN brl.menu_id IS NOT NULL THEN :charY                                 ");
        sql.append("             ELSE :charN END) AS accessFlag                                                ");
        sql.append("       FROM base_menu_list bml                                                             ");
        sql.append("  LEFT JOIN base_menu_list bml2                                                            ");
        sql.append("         ON bml2.parent_menu_id = bml.menu_id                                              ");
        sql.append("  LEFT JOIN base_menu_list bml3                                                            ");
        sql.append("         ON bml3.parent_menu_id = bml2.menu_id                                             ");
        sql.append("  LEFT JOIN base_role_list brl                                                             ");
        sql.append("         ON brl.menu_id = bml3.menu_id                                                     ");
        sql.append("        AND brl.role_id = :roleId                                                          ");
        sql.append("      WHERE bml.parent_menu_id is null                                                     ");
        sql.append("      AND bml3.menu_code is not null                                                       ");
        sql.append("   ORDER BY bml.menu_seq,bml2.menu_seq,bml3.menu_seq                                       ");
        sql.append(" ), third_list AS (                                                                        ");
        sql.append("     SELECT rml.menu_code1                                                                 ");
        sql.append("          , rml.menu_name1                                                                 ");
        sql.append("          , rml.menu_seq1                                                                  ");
        sql.append("          , rml.menu_code2                                                                 ");
        sql.append("          , rml.menu_name2                                                                 ");
        sql.append("          , rml.menu_seq2                                                                  ");
        sql.append("          , (CASE WHEN COUNT(1)                                                            ");
        sql.append("             FILTER (WHERE rml.accessFlag = :charY ) = COUNT(1)                            ");
        sql.append("             THEN :charY  ELSE :charN END) AS access_flag2                                 ");
        sql.append("          , json_agg(json_build_object( 'menuCd'    , rml.menu_code3                       ");
        sql.append("                                      , 'menuName'  , rml.menu_name3                       ");
        sql.append("                                      , 'menuSeq'   , rml.menu_seq3                        ");
        sql.append("                                      , 'accessFlag', rml.accessFlag                       ");
        sql.append("                                      , 'children'  , json_build_array())) as children2    ");
        sql.append("       FROM role_menu_list rml                                                             ");
        sql.append("   GROUP BY rml.menu_code1                                                                 ");
        sql.append("          , rml.menu_name1                                                                 ");
        sql.append("          , rml.menu_seq1                                                                  ");
        sql.append("          , rml.menu_code2                                                                 ");
        sql.append("          , rml.menu_name2                                                                 ");
        sql.append("          , rml.menu_seq2                                                                  ");
        sql.append("   ORDER BY rml.menu_seq1,rml.menu_seq2                                                    ");
        sql.append(" ) , second_list AS (                                                                      ");
        sql.append("     SELECT tl.menu_code1                                                                  ");
        sql.append("          , tl.menu_name1                                                                  ");
        sql.append("          , tl.menu_seq1                                                                   ");
        sql.append("          , (CASE WHEN COUNT(1)                                                            ");
        sql.append("             FILTER (WHERE tl.access_flag2 = :charY ) = COUNT(1)                           ");
        sql.append("             THEN :charY  ELSE :charN END) AS access_flag1                                 ");
        sql.append("          , json_agg(jsonb_build_object( 'menuCd'  , tl.menu_code2                         ");
        sql.append("                              , 'menuName', tl.menu_name2                                  ");
        sql.append("                              , 'menuSeq' , tl.menu_seq2                                   ");
        sql.append("                              , 'accessFlag' , tl.access_flag2                             ");
        sql.append("                              , 'children', tl.children2 )) as children1                   ");
        sql.append("      FROM third_list tl                                                                   ");
        sql.append("  GROUP BY tl.menu_code1                                                                   ");
        sql.append("         , tl.menu_name1                                                                   ");
        sql.append("         , tl.menu_seq1                                                                    ");
        sql.append("  ORDER BY tl.menu_seq1                                                                    ");
        sql.append(" )                                                                                         ");
        sql.append(" SELECT json_agg(jsonb_build_object( 'menuCd'     , sl.menu_code1                          ");
        sql.append("                                   , 'menuName'   , sl.menu_name1                          ");
        sql.append("                                   , 'menuSeq'    , sl.menu_seq1                           ");
        sql.append("                                   , 'accessFlag' , sl.access_flag1                        ");
        sql.append("                                   , 'children'   , sl.children1 )) as menuJson            ");
        sql.append("   FROM second_list sl                                                                     ");

        params.put("defaultSiteId", CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("roleId"       , roleId);
        params.put("charY"        , CommonConstants.CHAR_Y);
        params.put("charN"        , CommonConstants.CHAR_N);

        return super.queryForSingle(sql.toString(), params, String.class);
    }
}
