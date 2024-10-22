/**
 *
 */
package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.a1stream.domain.custom.ProductOrderResultSummaryRepositoryCustom;
import com.a1stream.domain.form.master.CMM050901Form;
import com.a1stream.domain.vo.ProductOrderResultSummaryVO;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

/**
* 功能描述:
*
* @author mid2259
*/
public class ProductOrderResultSummaryRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ProductOrderResultSummaryRepositoryCustom {

    @Override
    public PageImpl<ProductOrderResultSummaryVO> searchPartsDemandList(CMM050901Form model,String siteId,List<String> yearList) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("   SELECT distinct product_id  ");
        sql.append("     FROM product_order_result_summary ");
        sql.append("    WHERE site_id = :siteId ");
        sql.append("      AND target_year in (:targetYear) ");
        sql.append("      AND facility_id = :facilityId ");
        if (!ObjectUtils.isEmpty(model.getPartsId())) {
            sql.append(" AND product_id = :partsId ");
            params.put("partsId", model.getPartsId());
        }
        params.put("siteId", siteId);
        params.put("facilityId", model.getPointId());
        params.put("targetYear", yearList);
        sql.append(" ORDER BY product_id ");

        String countSql = "SELECT COUNT(1) FROM ( " + sql.toString() + ") AS subquery; ";
        Pageable pageable = PageRequest.of(model.getCurrentPage() - 1, model.getPageSize());
        if (!model.isPageFlg()) {
            pageable = Pageable.unpaged();
        }

        Integer count = super.queryForSingle(countSql, params, Integer.class);
        return new PageImpl<>(super.queryForPagingList(sql.toString(), params, ProductOrderResultSummaryVO.class, pageable), pageable, count);

    }

}
