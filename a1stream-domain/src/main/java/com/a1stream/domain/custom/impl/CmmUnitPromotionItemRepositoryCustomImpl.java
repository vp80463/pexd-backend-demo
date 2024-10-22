package com.a1stream.domain.custom.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.unit.SDM010601BO;
import com.a1stream.domain.custom.CmmUnitPromotionItemRepositoryCustom;
import com.a1stream.domain.vo.CmmUnitPromotionItemVO;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class CmmUnitPromotionItemRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmUnitPromotionItemRepositoryCustom {

    @Override
    public List<SDM010601BO> getPromotionInfoByFrameNoList(List<String> frameNos) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT cupl.promotion_list_id      AS promotionListId    ");
        sql.append("         , cupi.frame_no               AS frameNo            ");
        sql.append("         , cupi.unit_promotion_item_id AS promotionItemId    ");
        sql.append("      FROM cmm_unit_promotion_item cupi                      ");
        sql.append(" LEFT JOIN cmm_unit_promotion_list cupl                      ");
        sql.append("        ON cupl.promotion_list_id = cupi.promotion_list_id   ");
        sql.append("     WHERE cupi.frame_no IN (:frameNos)                      ");
        sql.append("       AND cupl.from_date <= :sysDate                        ");
        sql.append("       AND cupl.to_date   >= :sysDate                        ");

        params.put("frameNos", frameNos);
        params.put("sysDate", LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)));
        return super.queryForList(sql.toString(), params, SDM010601BO.class);
    }

    @Override
    public CmmUnitPromotionItemVO getPromotionInfoByFrameNo(String frameNo) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT *                                                             ");
        sql.append("      FROM cmm_unit_promotion_item cupi                                  ");
        sql.append("     WHERE frame_no = :frameNo                                           ");
        sql.append("       AND EXISTS(SELECT 1                                               ");
        sql.append("                    FROM cmm_unit_promotion_list cupl                    ");
        sql.append("                   WHERE cupl.promotion_list_id = cupi.promotion_list_id ");
        sql.append("                     AND cupl.from_date <= :systemDay                    ");
        sql.append("                     AND cupl.to_date >= :systemDay                      ");
        sql.append("                 )                                                       ");

        params.put("frameNo", frameNo);
        params.put("systemDay", LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)));
        return super.queryForSingle(sql.toString(), params, CmmUnitPromotionItemVO.class);
    }

}
