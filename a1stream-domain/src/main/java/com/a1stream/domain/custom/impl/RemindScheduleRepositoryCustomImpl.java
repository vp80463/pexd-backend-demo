package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.domain.bo.service.SVM010601BO;
import com.a1stream.domain.custom.RemindScheduleRepositoryCustom;
import com.a1stream.domain.form.service.SVM010601Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;

/**
 * @author dong zhen
 */
public class RemindScheduleRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements RemindScheduleRepositoryCustom {

    @Override
    public List<SVM010601BO> findServiceRemindList(SVM010601Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT csp.refuse_call           AS refuseCall                              ");
        sql.append("          , rs.reminded_date          AS remindedDate                            ");
        sql.append("          , rs.remind_type            AS remindTypeId                            ");
        sql.append("          , rs.remind_due_date        AS validDate                               ");
        sql.append("          , rs.expire_date            AS invalidDate                             ");
        sql.append("          , rs.remind_contents        AS description                             ");
        sql.append("          , rs.remind_schedule_id     AS remindScheduleId                        ");
        sql.append("          , rs.facility_id            AS pointId                                 ");
        sql.append("          , mp.product_cd             AS model                                   ");
        sql.append("          , mp.product_id             AS modelId                                 ");
        sql.append("          , csp.plate_no              AS plateNo                                 ");
        sql.append("          , csp.serialized_product_id AS serializedProductId                     ");
        sql.append("          , cc.consumer_id            AS consumerId                              ");
        sql.append("          , cc.consumer_full_nm       AS consumerName                            ");
        sql.append("          , cc.telephone              AS mobilePhone                             ");
        sql.append("          , mf.facility_cd            AS pointCd                                 ");
        sql.append("       FROM remind_schedule rs                                                   ");
        sql.append(" INNER JOIN mst_facility mf                                                      ");
        sql.append("         ON rs.facility_id = mf.facility_id                                      ");
        sql.append("  LEFT JOIN cmm_serialized_product csp                                           ");
        sql.append("         ON rs.serialized_product_id = csp.serialized_product_id                 ");
        sql.append("  LEFT JOIN cmm_consumer_serial_pro_relation ccspr                               ");
        sql.append("         ON csp.serialized_product_id = ccspr.serialized_product_id              ");
        sql.append("        AND ccspr.consumer_serialized_product_relation_type_id = :relationTypeId ");
        sql.append("  LEFT JOIN cmm_consumer cc                                                      ");
        sql.append("         ON ccspr.consumer_id = cc.consumer_id                                   ");
        sql.append("  LEFT JOIN mst_product mp                                                       ");
        sql.append("         ON csp.product_id = mp.product_id                                        ");
        sql.append("       JOIN sys_user_authority sua ON EXISTS (                                   ");
        sql.append("           SELECT 1                                                              ");
        sql.append("             FROM jsonb_array_elements(rs.role_list::jsonb) AS role_id_element   ");
        sql.append("            WHERE (role_id_element ->> 'roleId')::text IN (                      ");
        sql.append("                SELECT (rl ->> 'roleId')::text                                   ");
        sql.append("                  FROM jsonb_array_elements(sua.role_list::jsonb) AS rl          ");
        sql.append("                  )                                                              ");
        sql.append("          )                                                                      ");
        sql.append("      WHERE rs.site_id = :siteId                                                 ");
        sql.append("        AND sua.user_id = :userId                                                ");
        sql.append("        AND rs.facility_id = :pointId                                            ");
        sql.append("        AND rs.remind_due_date <= :remindDate                                    ");
        sql.append("        AND rs.expire_date >= :remindDate                                        ");

        if (!StringUtils.isEmpty(form.getRemindType())){
            sql.append("    AND rs.remind_type = :remindType                                         ");
            params.put("remindType", form.getRemindType());
        }

        if (form.getModelId() != null){
            sql.append("    AND rs.product_id = :modelId                                             ");
            params.put("modelId", form.getModelId());
        }

        if (!StringUtils.isEmpty(form.getPlateNo())){
            sql.append("    AND csp.plate_no = :plateNo                                              ");
            params.put("plateNo", form.getPlateNo());
        }

        if (CommonConstants.CHAR_Y.equals(form.getShowReminded())){
            sql.append("    AND rs.reminded_date IS NOT NULL AND rs.reminded_date <> :charBlank      ");
            params.put("charBlank", CommonConstants.CHAR_BLANK);
        } else {
            sql.append("    AND (rs.reminded_date IS NULL OR rs.reminded_date = :charBlank)          ");
            params.put("charBlank", CommonConstants.CHAR_BLANK);
        }

        sql.append("  ORDER BY rs.remind_due_date ASC                                               ");

        params.put("siteId", form.getSiteId());
        params.put("userId", form.getUserId());
        params.put("pointId", form.getPointId());
        params.put("relationTypeId", PJConstants.ConsumerSerialProRelationType.OWNER.getCodeDbid());
        params.put("remindDate", form.getRemindDate());

        return super.queryForList(sql.toString(), params, SVM010601BO.class);
    }
}