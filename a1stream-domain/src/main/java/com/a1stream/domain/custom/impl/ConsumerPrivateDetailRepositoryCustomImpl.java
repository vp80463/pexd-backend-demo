package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.Map;

import com.a1stream.domain.bo.common.CmmConsumerBO;
import com.a1stream.domain.custom.ConsumerPrivateDetailRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ConsumerPrivateDetailRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ConsumerPrivateDetailRepositoryCustom {

    @Override
    public CmmConsumerBO findConsumerPrivateDetailByConsumerId(String siteId, Long consumerId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("  SELECT cc.consumer_id   as consumerId    ");
        sql.append("       , cc.first_nm      as firstNm       ");
        sql.append("       , cc.middle_nm     as middleNm      ");
        sql.append("       , cc.last_nm       as lastNm        ");
        sql.append("       , cpd.mobile_phone as mobilePhone   ");
        sql.append("       , cc.email         as email         ");
        sql.append("   FROM cmm_consumer cc                    ");
        sql.append("   LEFT JOIN consumer_private_detail cpd                 ");
        sql.append("          ON cpd.consumer_id = cc.consumer_id            ");
        sql.append("         AND cpd.site_id = :siteId                       ");
        sql.append("  WHERE cc.consumer_id = :consumerId                     ");

        params.put("siteId", siteId);
        params.put("consumerId", consumerId);

        return super.queryForSingle(sql.toString(), params, CmmConsumerBO.class);
    }
}