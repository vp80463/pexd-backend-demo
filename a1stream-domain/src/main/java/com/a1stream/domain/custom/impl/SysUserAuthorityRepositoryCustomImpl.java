package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.Map;

import com.a1stream.common.constants.PJConstants.RoleCode;
import com.a1stream.common.constants.PJConstants.RoleType;
import com.a1stream.domain.bo.unit.SDM050101BO;
import com.a1stream.domain.custom.SysUserAuthorityRepositoryCustom;
import com.a1stream.domain.entity.SysUserAuthority;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class SysUserAuthorityRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements SysUserAuthorityRepositoryCustom {

    @Override
    public SysUserAuthority findUserBySDAndAccount(String userId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("  SELECT *                                                                     ");
        sql.append("  FROM sys_user_authority sua                                                  ");
        sql.append("  WHERE EXISTS (                                                               ");
        sql.append("      SELECT 1                                                                 ");
        sql.append("      FROM jsonb_array_elements(sua.role_list::jsonb) AS role                  ");
        sql.append("      WHERE role->>'roleId' IN (                                               ");
        sql.append("          SELECT sri.role_id                                                   ");
        sql.append("          FROM sys_role_info sri                                               ");
        sql.append("          WHERE sri.type = :SALESCOMPANY                                       ");
        sql.append("            AND (sri.role_code = :YMVNSD OR sri.role_code = :ACCOUNT)          ");
        sql.append("      )                                                                        ");
        sql.append("  )                                                                            ");

        params.put("SALESCOMPANY", RoleType.SALESCOMPANY);
        params.put("YMVNSD", RoleCode.YMVNSD);
        params.put("ACCOUNT", RoleCode.ACCOUNT);

        return super.queryForSingle(sql.toString(), params, SysUserAuthority.class);
    }

    @Override
    public SDM050101BO findDetailAndAddFlag(String userId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("WITH user_roles AS (                                                                                                                                                                                 ");
        sql.append("        SELECT user_id, jsonb_array_elements(role_list::jsonb) AS role                                                                                                                               ");
        sql.append("        FROM sys_user_authority                                                                                                                                                                      ");
        sql.append("        WHERE user_id = :userId                                                                                                                                       ");
        sql.append("    ),                                                                                                                                                                                               ");
        sql.append("    menu_authorities AS (                                                                                                                                                                            ");
        sql.append("        SELECT key, value                                                                                                                                                                            ");
        sql.append("        FROM sys_menu_authority,                                                                                                                                                                     ");
        sql.append("        LATERAL jsonb_each(btn_control_list::jsonb)                                                                                                                                                  ");
        sql.append("        WHERE menu_id = '90601'                                                                                                                                                                      ");
        sql.append("    )                                                                                                                                                                                                ");
        sql.append("    SELECT                                                                                                                                                                                           ");
        sql.append("        bool_or(CASE                                                                                                                                                                                 ");
        sql.append("            WHEN ma.key = 'detail' AND NOT EXISTS (SELECT 1 FROM menu_authorities WHERE key = 'detail') THEN false                                                                                   ");
        sql.append("            WHEN ma.key = 'detail' AND EXISTS (SELECT 1 FROM user_roles ur WHERE ur.role ->> 'roleId' = ANY(SELECT value ->> 'roleId' FROM jsonb_array_elements(ma.value::jsonb) AS value)) THEN true");
        sql.append("            ELSE false                                                                                                                                                                               ");
        sql.append("        END) AS detailFlag,                                                                                                                                                                          ");
        sql.append("        bool_or(CASE                                                                                                                                                                                 ");
        sql.append("            WHEN ma.key = 'add' AND NOT EXISTS (SELECT 1 FROM menu_authorities WHERE key = 'add') THEN false                                                                                         ");
        sql.append("            WHEN ma.key = 'add' AND EXISTS (SELECT 1 FROM user_roles ur WHERE ur.role ->> 'roleId' = ANY(SELECT value ->> 'roleId' FROM jsonb_array_elements(ma.value::jsonb) AS value)) THEN true   ");
        sql.append("            ELSE false                                                                                                                                                                               ");
        sql.append("        END) AS addFlag                                                                                                                                                                              ");
        sql.append("    FROM menu_authorities ma                                                                                                                                                                         ");
        sql.append("    WHERE ma.key IN ('detail', 'add');                                                                                                                                                               ");

        params.put("userId", userId);

        return super.queryForSingle(sql.toString(), params, SDM050101BO.class);
    }
}
