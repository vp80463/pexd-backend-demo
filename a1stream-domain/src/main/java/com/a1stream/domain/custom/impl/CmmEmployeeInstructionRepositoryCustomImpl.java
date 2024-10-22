package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.bo.unit.SDM030501BO;
import com.a1stream.domain.custom.CmmEmployeeInstructionRepositoryCustom;
import com.a1stream.domain.form.unit.SDM030501Form;
import com.ymsl.solid.base.util.StringUtils;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class CmmEmployeeInstructionRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmEmployeeInstructionRepositoryCustom {

    @Override
    public List<SDM030501BO> getEmployeeInstructionList(SDM030501Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT site_id                        AS dealerCd                     ");
        sql.append("         , order_no                       AS orderNo                      ");
        sql.append("         , order_status                   AS orderStatus                  ");
        sql.append("         , model_cd                       AS modelCd                      ");
        sql.append("         , last_nm                        AS lastNm                       ");
        sql.append("         , middle_nm                      AS middleNm                     ");
        sql.append("         , first_nm                       AS firstNm                      ");
        sql.append("         , mobile_phone                   AS mobilePhone                  ");
        sql.append("         , sns                            AS sns                          ");
        sql.append("         , gender                         AS gender                       ");
        sql.append("         , CONCAT(birth_year, birth_date) AS birthday                     ");
        sql.append("         , province_geography_nm          AS province                     ");
        sql.append("         , city_geography_nm              AS district                     ");
        sql.append("         , address                        AS address                      ");
        sql.append("         , email                          AS email                        ");
        sql.append("         , employee_cd                    AS employeeCd                   ");
        sql.append("         , employee_nm                    AS employeeNm                   ");
        sql.append("         , purchase_type                  AS purchaseType                 ");
        sql.append("         , employee_discount              AS employeeDiscount             ");
        sql.append("         , cmm_employee_instruction_id    AS cmmEmployeeInstructionId     ");
        sql.append("      FROM cmm_employee_instruction cei                                   ");
        sql.append("     WHERE 1 = 1                                                          ");

        if (!StringUtils.isEmpty(form.getOrderMonth())) {
            sql.append(" AND LEFT(order_date, '6') >= :orderMonth ");
            params.put("orderMonth", form.getOrderMonth());
        }

        if (!StringUtils.isEmpty(form.getOrderStatus())) {
            sql.append(" AND order_status = :orderStatus ");
            params.put("orderStatus", form.getOrderStatus());
        }

        if (!StringUtils.isEmpty(form.getEmployeeCd())) {
            sql.append(" AND employee_cd = :employeeCd ");
            params.put("employeeCd", form.getEmployeeCd());
        }

        if ("DEALER".equals(form.getUserType())) {
            sql.append(" AND site_id = :siteId ");
            params.put("siteId", form.getSiteId());
        }

        return super.queryForList(sql.toString(), params, SDM030501BO.class);
    }
}
