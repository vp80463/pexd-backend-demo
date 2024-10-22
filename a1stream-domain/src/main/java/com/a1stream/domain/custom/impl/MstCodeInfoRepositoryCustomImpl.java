package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.MstCodeConstants.JudgementStatus;
import com.a1stream.common.constants.PJConstants.JudgementStatusType;
import com.a1stream.common.constants.PJConstants.ReservationStatus;
import com.a1stream.common.constants.PJConstants.ReservationTime;
import com.a1stream.common.model.BaseHelperBO;
import com.a1stream.domain.custom.MstCodeInfoRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class MstCodeInfoRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements MstCodeInfoRepositoryCustom {

    @Override
    public List<BaseHelperBO> getJudgementStausList(String type) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        if (JudgementStatusType.YMVNSDACCT.equals(type)) {
            sql.append("     SELECT mci.code_dbid       AS codeDbid                                   ");
            sql.append("          , CASE WHEN (code_data2 IS NULL OR code_data2 = '') THEN code_data1 ");
            sql.append("            WHEN (code_data1 IS NULL OR code_data1 = '') THEN code_data3      ");
            sql.append("          , ELSE code_data2 END AS codeData1                                  ");
            sql.append("       FROM mst_code_info mci                                                 ");
            sql.append("      WHERE mci.code_id = :codeId                                             ");
        } else {
            sql.append("     SELECT t1.codeDbid  AS codeDbid               ");
            sql.append("          , t1.codeData1 AS codeData1              ");
            sql.append("       FROM (                                      ");
            sql.append("                SELECT mci.code_dbid  AS codeDbid  ");
            if (JudgementStatusType.DEALER.equals(type)) {
                sql.append("                 , mci.code_data1 AS codeData1 ");
            } else if (JudgementStatusType.YMVNSD.equals(type)) {
                sql.append("                 , mci.code_data2 AS codeData1 ");
            } else if (JudgementStatusType.ACCT.equals(type)) {
                sql.append("                 , mci.code_data3 AS codeData1 ");
            }
            sql.append("                  FROM mst_code_info mci           ");
            sql.append("                 WHERE mci.code_id = :codeId       ");
            sql.append("              ORDER BY codeDbid                    ");
            sql.append("            ) AS t1                                ");
            sql.append("      WHERE t1.codeData1 <> ''                     ");
        }

        params.put("codeId", JudgementStatus.CODE_ID);

        return super.queryForList(sql.toString(), params, BaseHelperBO.class);
    }

    @Override
    public List<BaseHelperBO> getScheduleTimeList(String siteId, String facilityId, String scheduleDate) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT mci.code_dbid   AS codeDbid                                                ");
        sql.append("         , mci.code_data1  AS codeData1                                               ");
        sql.append("      FROM mst_code_info mci                                                          ");
        sql.append("     WHERE mci.code_id = :codeId                                                      ");

        if(!ObjectUtils.isEmpty(facilityId) && !ObjectUtils.isEmpty(scheduleDate)) {

            sql.append("   AND mci.code_dbid NOT IN (SELECT ss.schedule_time                              ");
            sql.append("                               FROM service_schedule ss                           ");
            sql.append("                              WHERE ss.site_id = :siteId                          ");
            sql.append("                                AND ss.facility_id = :facilityId                  ");
            sql.append("                                AND ss.schedule_date = :scheduleDate              ");
            sql.append("                                AND ss.reservation_status <> :reservationStatus   ");
            sql.append("                           GROUP BY ss.schedule_time)                             ");
            params.put("siteId", siteId);
            params.put("facilityId", Long.parseLong(facilityId));
            params.put("scheduleDate", scheduleDate);
            params.put("reservationStatus", ReservationStatus.CANCELLED.getCodeDbid());
        }

        params.put("codeId", ReservationTime.CODE_ID);

        return super.queryForList(sql.toString(), params, BaseHelperBO.class);
    }
}
