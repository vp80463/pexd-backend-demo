package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.common.EInvoiceProductsBO;
import com.a1stream.domain.bo.service.SVM0120PrintServiceJobBO;
import com.a1stream.domain.bo.service.SVM0120PrintServicePartBO;
import com.a1stream.domain.custom.ServiceOrderItemOtherBrandRepositoryCustom;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ServiceOrderItemOtherBrandRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ServiceOrderItemOtherBrandRepositoryCustom {

    @Override
    public List<SVM0120PrintServiceJobBO> getOtherBrandJobCardJobList(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        
        sql.append("     SELECT svOtherJob.item_cd      AS itemCd                         ");
        sql.append("          , svOtherJob.item_content AS itemContent                    ");
        sql.append("          , svOtherJob.std_manhour  AS stdmenhour                     ");
        sql.append("       FROM service_order_item_other_brand svOtherJob                 ");
        sql.append("      WHERE service_order_id = :serviceOrderId                        ");
        sql.append("        AND product_classification = :S001SERVICE                     ");

        params.put("S001SERVICE", ProductClsType.SERVICE.getCodeDbid());
        params.put("serviceOrderId", serviceOrderId);
        return super.queryForList(sql.toString(), params, SVM0120PrintServiceJobBO.class);
    }

    @Override
    public List<SVM0120PrintServicePartBO> getOtherBrandJobCardPartList(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT svOtherJob.item_content AS partNo                         ");
        sql.append("         , svOtherJob.order_qty    AS qty                            ");
        sql.append("      FROM service_order_item_other_brand svOtherJob                 ");
        sql.append("     WHERE service_order_id = :serviceOrderId                        ");
        sql.append("       AND product_classification = :S001PART                        ");

        params.put("serviceOrderId", serviceOrderId);
        params.put("S001PART", ProductClsType.PART.getCodeDbid());
        return super.queryForList(sql.toString(), params, SVM0120PrintServicePartBO.class);
    }

    @Override
    public List<SVM0120PrintServiceJobBO> getOtherBrandPaymentJobList(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT svOtherJob.item_cd          AS itemCd          ");
        sql.append("         , svOtherJob.item_content     AS itemContent     ");
        sql.append("         , svOtherJob.std_manhour      AS stdmenhour      ");
        sql.append("         , svOtherJob.standard_price   AS stdPrice        ");
        sql.append("         , svOtherJob.selling_price    AS amount          ");
        sql.append("         , svOtherJob.service_category AS serviceCategory ");
        sql.append("      FROM service_order_item_other_brand svOtherJob      ");
        sql.append("     WHERE service_order_id = :serviceOrderId             ");
        sql.append("       AND product_classification = :S001SERVICE          ");

        params.put("S001SERVICE", ProductClsType.SERVICE.getCodeDbid());
        params.put("serviceOrderId", serviceOrderId);
        return super.queryForList(sql.toString(), params, SVM0120PrintServiceJobBO.class);
    }

    @Override
    public List<SVM0120PrintServicePartBO> getOtherBrandPaymentPartList(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT svOtherJob.item_content                       AS part          ");
        sql.append("      , svOtherJob.order_qty                          AS qty           ");
        sql.append("      , svOtherJob.standard_price                     AS stdPrice      ");
        sql.append("      , svOtherJob.selling_price                      AS sellingPrice  ");
        sql.append("      , svOtherJob.order_qty*svOtherJob.selling_price AS amount        ");
        sql.append("   FROM service_order_item_other_brand svOtherJob                      ");
        sql.append("  WHERE service_order_id = :serviceOrderId                             ");
        sql.append("    AND product_classification = :S001PART                             ");

        params.put("S001PART", ProductClsType.PART.getCodeDbid());
        params.put("serviceOrderId", serviceOrderId);
        return super.queryForList(sql.toString(), params, SVM0120PrintServicePartBO.class);
    }

    @Override
    public List<EInvoiceProductsBO> getServiceProductsForOtherBrandModels(Long orderId, String type) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        if(StringUtils.equals(type, CommonConstants.CHAR_ZERO)){
            
            //get parts price
            sql.append(" SELECT '' AS code, ");
            sql.append(" soiob.item_content AS prodName, ");
            sql.append(" (SELECT parameter_value_ ");
            sql.append(" FROM system_parameter_info ");
            sql.append(" WHERE system_parameter_type_id_ = 'S074EINVOICEPRODUNIT') AS prodUnit, ");
            sql.append(" round(soiob.selling_price_not_vat,0) AS prodPrice, ");
            sql.append(" round(soiob.standard_price,0) AS prodPrice,  ");
            sql.append(" round(soiob.order_qty_*soiob.selling_price_not_vat,0) AS amount,  ");
            sql.append(" soiob.quantity_ AS prodQuantity,  ");
            sql.append(" round(soiob.discount_,0) AS discount, ");
            sql.append(" (case when osoiobi.discount_ > 0 then 2 else 0 end) AS isSum, ");
            sql.append(" round(soiob.discount_/100*round(soiob.order_qty_*soiob.selling_price_,0) ,0) AS discountAmount ");
        }else{

            //get job price
            sql.append(" SELECT item_cd AS code, ");
            sql.append(" item_content AS prodName, ");
            sql.append(" ' ' AS prodUnit,  ");
            sql.append(" '1' AS prodQuantity,");
            sql.append(" round(soiob.discount_,0) AS discount, ");
            sql.append(" (case when soiob.discount_ > 0 then 2 else 0 end) AS isSum, ");
            sql.append(" (case when soiob.special_price_ > 0 then (round(soiob.special_price_/tax_rate,0))) AS prodPrice, ");
            sql.append(" (case when oi.special_price_ > 0 then ( round(oi.special_price_ /tax_rate, 0) ");
            sql.append(" else (case when oi.discount_ > 0 then (round(round(oi.standard_price_ /tax_rate, 0)*((100 - oi.discount_)/ 100) , 0) ");
            sql.append(" else (round(oi.standard_price_ /tax_rate, 0) end) ");
            sql.append(" end)AS amount, ");
            sql.append(" (case when oi.discount_ > 0 then  ");
            sql.append(" (round(oi.standard_price_ / tax_rate, 0)- (round(round(oi.standard_price_ /tax_rate, 0)  ");
            sql.append(" else round(round(oi.standard_price_ /tax_rate, 0)*((100 - oi.discount_)/ 100) , 0)end) else 0 end) AS discountAmount ");
        }

        sql.append(" FROM service_order_item_other_brand soiob ");
        sql.append(" and soiob.settle_type_id= 'S013CUSTOMER' ");
        if(StringUtils.equals(type, CommonConstants.CHAR_ZERO)){
            sql.append(" and soiob.product_classification='S001PART' ");
        }else{
            sql.append(" and soiob.product_classification='S001SERVICE' ");
        }
        
        return super.queryForList(sql.toString(), params, EInvoiceProductsBO.class);
    }
}
