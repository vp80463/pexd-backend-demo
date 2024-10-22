package com.a1stream.domain.custom.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVLBO;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ServicePackageVLBO;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.master.CMM060201BO;
import com.a1stream.domain.custom.ServicePackageRepositoryCustom;
import com.a1stream.domain.form.master.CMM060201Form;
import com.a1stream.domain.logic.RepositoryLogic;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

public class ServicePackageRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ServicePackageRepositoryCustom {

    @Resource
    private RepositoryLogic repositoryLogic;

    @Override
    public ValueListResultBO findPackageList(BaseVLForm model, String siteId) {

        int pageSize = model.getPageSize();
        int currentPage = model.getCurrentPage();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtils.FORMAT_YMD_NODELIMITER);

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT service_package_id  AS id   ");
        sql.append("      , package_cd          AS code ");
        sql.append("      , sales_description   AS name ");
        sql.append("      , concat(package_cd, ' ', sales_description)    AS desc ");
        sql.append("   FROM service_package             ");
        sql.append("  WHERE 1 = 1                       ");
        sql.append("    AND site_id    = :siteId        ");
        sql.append("    AND from_date <= :sysDate       ");
        sql.append("    AND to_date   >= :sysDate       ");

        params.put("siteId", siteId);
        params.put("sysDate", LocalDate.now().format(formatter));

        if (StringUtils.isNotBlank(model.getContent())) {
            sql.append(" AND concat(package_cd, ' ', sales_description) LIKE :content ");
            params.put("content", CommonConstants.CHAR_PERCENT + model.getContent() + CommonConstants.CHAR_PERCENT);
        }

        sql.append(" ORDER BY package_cd ");

        // pop-up
        String countSql = "SELECT COUNT(1) FROM ( " + sql.toString() + ") AS subquery; ";
        Integer count = super.queryForSingle(countSql, params, Integer.class);

        // pop-over
        if (pageSize != 0 && currentPage >= 1) {

            sql.append(" OFFSET :offsetValue ");
            sql.append(" LIMIT :limitValue");

            params.put("offsetValue", pageSize * (currentPage - 1));
            params.put("limitValue", pageSize);
        }

        List<BaseVLBO> result = super.queryForList(sql.toString(), params, BaseVLBO.class);

        return new ValueListResultBO(result, count);
    }

    @Override
    public List<CMM060201BO> getSvPackageData(CMM060201Form model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT sp.package_cd                    as  packageNo         ");
        sql.append("          , sp.local_description             as  packageName       ");
        sql.append("          , sp.from_date                     as  validDateFrom     ");
        sql.append("          , sp.to_date                       as  validDateTo       ");
        sql.append("          , sp.service_package_id            as  servicePackageId  ");
        sql.append("          , STRING_AGG(mpc.category_nm,';')  as  productCategory   ");
        sql.append("       FROM service_package sp                                     ");
        sql.append("  LEFT JOIN service_package_category spc                           ");
        sql.append("         ON spc.service_package_id = sp.service_package_id         ");
        sql.append("  LEFT JOIN mst_product_category mpc                               ");
        sql.append("         ON spc.product_category_id = mpc.product_category_id      ");
        sql.append("      WHERE sp.site_id = :siteId                                   ");

        if(!ObjectUtils.isEmpty(model.getPackageCd())) {

            sql.append("    AND sp.package_cd = :packageCd                             ");
            params.put("packageCd", model.getPackageCd());
        }

        sql.append("        AND sp.from_date <= :validDate                             ");
        sql.append("        AND sp.to_date >= :validDate                               ");

        if(model.getProductCategoryId() != null) {

            sql.append("    AND EXISTS(                                                ");
            sql.append("        SELECT 1                                               ");
            sql.append("          FROM service_package_category                        ");
            sql.append("          WHERE site_id = :siteId                              ");
            sql.append("            AND product_category_id = :prodCtgId               ");
            sql.append("            AND service_package_id = sp.service_package_id )   ");
            params.put("siteId", model.getSiteId());
            params.put("prodCtgId", model.getProductCategoryId());
        }

        sql.append("   GROUP BY sp.service_package_id                                  ");

        params.put("siteId", model.getSiteId());
        params.put("validDate", model.getValidDate());

        return super.queryForList(sql.toString(), params, CMM060201BO.class);
    }

    @Override
    public Integer existServicePackage(String packageCd, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT count(1)                 ");
        sql.append("   FROM service_package          ");
        sql.append("  WHERE site_id = :siteId        ");
        sql.append("    AND package_cd = :packageCd  ");

        params.put("siteId", siteId);
        params.put("packageCd", packageCd);

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }

    @Override
    public ValueListResultBO findServicePackageByMc(BaseVLForm model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("         SELECT sp.package_cd               as  packageNo                      ");
        sql.append("         , sp.local_description             as  packageName                    ");
        sql.append("         , sp.from_date                     as  validDateFrom                  ");
        sql.append("         , sp.to_date                       as  validDateTo                    ");
        sql.append("         , sp.service_package_id            as  servicePackageId               ");
        sql.append("         , sp.service_category                                                 ");
        sql.append("         , STRING_AGG(mpc.category_nm,';')  as  productCategory                ");
        sql.append("      FROM service_package sp                                                  ");
        sql.append(" LEFT JOIN service_package_category spc                                        ");
        sql.append("        ON spc.service_package_id = sp.service_package_id                      ");
        sql.append(" LEFT JOIN mst_product_category mpc                                            ");
        sql.append("        ON spc.product_category_id = mpc.product_category_id                   ");
        sql.append("     WHERE sp.site_id = :siteId                                                ");
        sql.append("       AND sp.from_date <= :nowDate                                            ");
        sql.append("       AND sp.to_date >= :nowDate                                              ");
        sql.append("       AND (sp.service_category IS NULL OR sp.service_category = :serviceCategory  ) ");
        sql.append("       AND EXISTS                                                              ");
        sql.append("           (                                                                   ");
        sql.append("                 SELECT 1                                                      ");
        sql.append("                   FROM service_package_category                               ");
        sql.append("                   WHERE site_id = :siteId                                     ");
        sql.append("                     AND service_package_id = sp.service_package_id            ");
        sql.append("                     AND product_category_id IN (                              ");
        sql.append("                       SELECT lv0.product_category_id                          ");
        sql.append("                           FROM cmm_serialized_product csp                     ");
        sql.append("                           INNER JOIN mst_product lv1                          ");
        sql.append("                                 ON lv1.product_id = csp.product_id            ");
        sql.append("                           INNER JOIN mst_product lv0                          ");
        sql.append("                                ON lv0.product_id = lv1.to_product_id          ");
        sql.append("                           WHERE csp.serialized_product_id = :mcId             ");
        sql.append("                     )                                                         ");
        sql.append("           )                                                                   ");
        sql.append("     GROUP BY sp.service_package_id                                            ");

        params.put("siteId", siteId);
        params.put("nowDate", ComUtil.date2str(LocalDate.now()));
        params.put("mcId", Long.valueOf(model.getArg0()));
        params.put("serviceCategory", model.getArg1());

        // pop-up
        String countSql = "SELECT COUNT(1) FROM ( " + sql.toString() + ") AS subquery; ";
        Integer count = super.queryForSingle(countSql, params, Integer.class);

        repositoryLogic.appendPagePara(model.getPageSize(), model.getCurrentPage(), sql, params);

        List<ServicePackageVLBO> result = super.queryForList(sql.toString(), params, ServicePackageVLBO.class);

        return new ValueListResultBO(result, count);
    }
}
