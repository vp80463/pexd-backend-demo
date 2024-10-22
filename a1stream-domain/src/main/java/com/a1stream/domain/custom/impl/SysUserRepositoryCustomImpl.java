package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.UserStatusSub;
import com.a1stream.common.model.BaseVLBO;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.master.CMQ070101BO;
import com.a1stream.domain.bo.master.CMQ070102BO;
import com.a1stream.domain.custom.SysUserRepositoryCustom;
import com.a1stream.domain.form.master.CMQ070101Form;
import com.a1stream.domain.logic.RepositoryLogic;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

public class SysUserRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements SysUserRepositoryCustom {

    @Resource
    private RepositoryLogic repositoryLogic;

    @Override
    public List<CMQ070101BO> findUserList(CMQ070101Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT su.user_code       as userCd           ");
        sql.append("      , TO_CHAR(su.date_created,'YYYYMMDD')    as registerDate     ");
        sql.append("      , TO_CHAR(su.effective_date,'YYYYMMDD')  as dateFrom         ");
        sql.append("      , TO_CHAR(su.expired_date,'YYYYMMDD')    as dateTo           ");
        sql.append("      , CASE su.type                           ");
        sql.append("          WHEN :activeUser THEN :activeUserDes ");
        sql.append("          ELSE :inactiveUserDes                ");
        sql.append("        END                as status           ");
        sql.append("      , su.user_id         as userId           ");
        sql.append(" FROM sys_user su                              ");

        if (StringUtils.isNotBlank(model.getRoleId())) {
            sql.append(" LEFT JOIN sys_user_authority sua   ");
            sql.append("        ON sua.user_id = su.user_id ");
        }

        sql.append(" WHERE su.site_id = :siteId ");

        if (StringUtils.isNotBlank(model.getUserCd())) {
            sql.append(" AND UPPER(su.user_code) like :userCd ");
            params.put("userCd", "%" + StringUtils.upperCase(model.getUserCd()) + "%");
        }

        if (StringUtils.isNotBlank(model.getRoleId())) {

            sql.append(" AND jsonb_path_exists(cast(role_list as jsonb), '$[*].roleId ?(@ == \"").append(model.getRoleId()).append("\")') ");
        }

        sql.append(" ORDER BY su.type, su.user_code ");

        params.put("siteId", siteId);
        params.put("activeUser", UserStatusSub.ACTIVE.getCodeDbid());
        params.put("activeUserDes", UserStatusSub.ACTIVE.getCodeData1());
        params.put("inactiveUserDes", UserStatusSub.INACTIVE.getCodeData1());

        return super.queryForList(sql.toString(), params, CMQ070101BO.class);
    }

    @Override
    public CMQ070102BO getUserDetail(String userId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT cp.person_cd || ' ' || cp.person_nm as employee ");
        sql.append("         , su.person_id       as employeeId          ");
        sql.append("         , su.user_code       as userCd              ");
        sql.append("         , TO_CHAR(su.effective_date,'YYYYMMDD')  as dateFrom            ");
        sql.append("         , TO_CHAR(su.expired_date,'YYYYMMDD')    as dateTo              ");
        sql.append("         , su.user_id         as userId              ");
        sql.append("         , su.mail            as email               ");
        sql.append("         , CASE su.type                              ");
        sql.append("               WHEN :activeUser THEN :active         ");
        sql.append("               ELSE :inactive                        ");
        sql.append("           END                as status              ");
        sql.append("      FROM sys_user su                               ");
        sql.append(" LEFT JOIN cmm_person cp                             ");
        sql.append("        ON cp.user_id = su.user_id                   ");
        sql.append("     WHERE su.user_id = :userId                      ");

        params.put("userId", userId);
        params.put("activeUser", UserStatusSub.ACTIVE.getCodeDbid());
        params.put("active", CommonConstants.CHAR_Y);
        params.put("inactive", CommonConstants.FLAG_UNACTIVE);

        return super.queryForSingle(sql.toString(), params, CMQ070102BO.class);
    }

    @Override
    public ValueListResultBO findUserValueList(BaseVLForm model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT user_id      AS id   ");
        sql.append("      , user_code    AS code ");
        sql.append("      , nick_name    AS name ");
        sql.append("      , concat(user_code, ' ', nick_name) AS desc ");
        sql.append("   FROM sys_user             ");
        sql.append("  WHERE 1 = 1                ");

        if (StringUtils.isNotBlank(model.getContent())) {
            sql.append(" AND concat(user_code, ' ', nick_name) LIKE :content ");
            params.put("content", CommonConstants.CHAR_PERCENT + model.getContent() + CommonConstants.CHAR_PERCENT);
        }

        sql.append("  ORDER BY user_code         ");
        // pop-up
        String countSql = "SELECT COUNT(1) FROM ( " + sql.toString() + ") AS subquery; ";
        Integer count = super.queryForSingle(countSql, params, Integer.class);

        // pop-over
        repositoryLogic.appendPagePara(model.getPageSize(), model.getCurrentPage(), sql, params);

        List<BaseVLBO> result = super.queryForList(sql.toString(), params, BaseVLBO.class);

        return new ValueListResultBO(result, count);
    }
}
