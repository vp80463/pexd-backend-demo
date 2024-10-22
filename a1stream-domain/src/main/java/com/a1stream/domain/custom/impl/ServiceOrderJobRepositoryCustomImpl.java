package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.bo.service.JobDetailBO;
import com.a1stream.domain.bo.service.SVM0102PrintServiceJobBO;
import com.a1stream.domain.bo.service.SVM0130PrintServiceJobBO;
import com.a1stream.domain.custom.ServiceOrderJobRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ServiceOrderJobRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ServiceOrderJobRepositoryCustom {

    @Override
    public List<JobDetailBO> listServiceJobByOrderId(Long serviceOrderId){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT soj.service_category_id as serviceCategoryId       ");
        sql.append("      , soj.service_demand_id as serviceDemandId           ");
        sql.append("      , soj.service_demand_content as serviceDemandContent ");
        sql.append("      , soj.settle_type_id as settleTypeId                 ");
        sql.append("      , soj.symptom_id as symptomId                        ");
        sql.append("      , soj.job_id as jobId                                ");
        sql.append("      , soj.job_cd as jobCd                                ");
        sql.append("      , soj.job_nm as jobNm                                ");
        sql.append("      , COALESCE (soj.std_manhour, 0)    as manhour        ");
        sql.append("      , COALESCE (soj.standard_price, 0) as standardPrice  ");
        sql.append("      , soj.discount_amt                 as discountAmt    ");
        sql.append("      , soj.discount                     as discount       ");
        sql.append("      , soj.special_price                as specialPrice   ");
        sql.append("      , COALESCE (soj.selling_price, 0)  as sellingPrice   ");
        sql.append("      , soj.vat_rate                     as taxRate        ");
        sql.append("      , soj.service_package_id as servicePackageId         ");
        sql.append("      , soj.service_package_cd as servicePackageCd         ");
        sql.append("      , soj.service_package_nm as servicePackageNm         ");
        sql.append("      , soj.service_order_job_id  as serviceOrderJobId     ");
        sql.append("      , soj.update_count  as updateCounter                 ");
        sql.append("   FROM service_order_job soj                              ");
        sql.append("  WHERE soj.service_order_id = :serviceOrderId             ");
        sql.append("  ORDER BY soj.seq_no                                      ");

        params.put("serviceOrderId", serviceOrderId);

        return super.queryForList(sql.toString(), params, JobDetailBO.class);
    }

    @Override
    public List<SVM0102PrintServiceJobBO> getServiceJobPrintList(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT job_cd      AS jobCode               ");
        sql.append("         , job_nm      AS jobName               ");
        sql.append("         , std_manhour AS stdManhour            ");
        sql.append("      FROM service_order_job                    ");
        sql.append("     WHERE service_order_id = :serviceOrderId   ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForList(sql.toString(), params, SVM0102PrintServiceJobBO.class);
    }

    @Override
    public List<SVM0102PrintServiceJobBO> getServicePaymentJobPrintList(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT service_category_id    AS serviceCategoryId     ");
        sql.append("         , service_demand_content AS serviceDemandContent  ");
        sql.append("         , job_nm                 AS jobName               ");
        sql.append("         , std_manhour            AS stdManhour            ");
        sql.append("         , discount               AS discount              ");
        sql.append("         , special_price          AS specialPrice          ");
        sql.append("         , selling_price          AS amount                ");
        sql.append("         , settle_type_id         AS settleTypeId          ");
        sql.append("      FROM service_order_job                               ");
        sql.append("     WHERE service_order_id = :serviceOrderId              ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForList(sql.toString(), params, SVM0102PrintServiceJobBO.class);
    }

    @Override
    public List<SVM0102PrintServiceJobBO> getServicePaymentJobForDoPrintList(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT service_category_id AS serviceCategoryId   ");
        sql.append("         , job_nm              AS jobName             ");
        sql.append("         , std_manhour         AS stdManhour          ");
        sql.append("         , vat_rate            AS taxRate             ");
        sql.append("         , standard_price      AS stdPrice            ");
        sql.append("         , discount_amt        AS discountAmt         ");
        sql.append("         , discount            AS discount            ");
        sql.append("         , selling_price       AS amount              ");
        sql.append("         , settle_type_id      AS settleTypeId        ");
        sql.append("    FROM service_order_job                            ");
        sql.append("    WHERE service_order_id = :serviceOrderId          ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForList(sql.toString(), params, SVM0102PrintServiceJobBO.class);
    }

    @Override
    public List<SVM0130PrintServiceJobBO> get0KmJobCardJobList(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT job_cd      AS serviceJob           ");
        sql.append("         , job_nm      AS serviceJobName       ");
        sql.append("         , std_manhour AS stdmenhour           ");
        sql.append("    FROM service_order_job                     ");
        sql.append("    WHERE service_order_id = :serviceOrderId   ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForList(sql.toString(), params, SVM0130PrintServiceJobBO.class);
    }

    @Override
    public List<SVM0130PrintServiceJobBO> get0KmServicePaymentJobList(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT service_category_id    AS serviceCategoryId     ");
        sql.append("         , service_demand_content AS serviceDemandContent  ");
        sql.append("         , job_nm                 AS jobName               ");
        sql.append("         , std_manhour            AS stdmenhour            ");
        sql.append("         , discount               AS discount              ");
        sql.append("         , special_price          AS specialPrice          ");
        sql.append("         , selling_price          AS amount                ");
        sql.append("      FROM service_order_job                               ");
        sql.append("     WHERE service_order_id = :serviceOrderId              ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForList(sql.toString(), params, SVM0130PrintServiceJobBO.class);
    }
}
