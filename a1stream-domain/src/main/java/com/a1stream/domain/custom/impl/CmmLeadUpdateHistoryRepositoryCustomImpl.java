package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.bo.unit.SVM010603BO;
import com.a1stream.domain.custom.CmmLeadUpdateHistoryRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class CmmLeadUpdateHistoryRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmLeadUpdateHistoryRepositoryCustom {

    @Override
    public List<SVM010603BO> getCmmLeadUpdHistory(Long leadResultId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT telephone               AS detailPhoneNo                    ");
        sql.append("         , customer_nm             AS detailCustomerNm                 ");
        sql.append("         , call_date_by_dealer     AS detailDealerCallDate             ");
        sql.append("         , contact_status          AS detailContactStatus              ");
        sql.append("     FROM cmm_lead_update_history cluh                                 ");
        sql.append("    WHERE lead_management_result_id = :leadManagementResultId          ");
        sql.append(" ORDER BY call_date_by_dealer                                          ");

        params.put("leadManagementResultId", leadResultId);

        return super.queryForList(sql.toString(), params, SVM010603BO.class);
    }
}
