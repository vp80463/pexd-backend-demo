package com.a1stream.domain.custom.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.LocationType;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.common.model.LocationVLBO;
import com.a1stream.common.model.LocationVLForm;
import com.a1stream.domain.bo.master.CMM020101BO;
import com.a1stream.domain.bo.parts.DIM020601BO;
import com.a1stream.domain.bo.parts.SPQ030801BO;
import com.a1stream.domain.custom.LocationRepositoryCustom;
import com.a1stream.domain.form.master.CMM020101Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;

public class LocationRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements LocationRepositoryCustom {

    @Override
    public PageImpl<LocationVLBO> findLocationList(LocationVLForm model, String siteId,List<String> locationTypes) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        StringBuilder selSql = new StringBuilder();

        selSql.append(" SELECT l.location_cd   as locationNo          ");
        selSql.append("      , l.location_type as locationTypeCd      ");
        selSql.append("      , w.workzone_id   as workzoneId          ");
        selSql.append("      , w.workzone_cd   as workzone            ");
        selSql.append("      , w.description   as workzoneDescription ");
        selSql.append("      , bt.bin_type_cd  as binType             ");
        selSql.append("      , bt.bin_type_id  as binTypeId           ");
        selSql.append("      , bt.description  as binTypeDescription  ");
        selSql.append("      , l.primary_flag  as mainLocation        ");
        selSql.append("      , l.location_id   as locationId          ");
        selSql.append("      , mci.code_data1  as locationTypeCode    ");
        sql.append("   FROM location l                             ");
        sql.append("   LEFT JOIN workzone w                        ");
        sql.append("     ON l.workzone_id = w.workzone_id          ");
        sql.append("   LEFT JOIN bin_type bt                       ");
        sql.append("     ON l.bin_type_id = bt.bin_type_id         ");
        sql.append("   LEFT JOIN mst_code_info mci                 ");
        sql.append("     ON mci.code_dbid = l.location_type        ");

        sql.append("  WHERE l.facility_id = :pointId ");
        params.put("pointId", model.getPointId());

        sql.append("    AND l.site_id = :siteId ");
        params.put("siteId", siteId);

        sql.append("    AND l.location_type in (:locationTypeList)");

        params.put("locationTypeList", locationTypes);

        if(!ObjectUtils.isEmpty(model.getWorkzone())){
            sql.append(" AND l.workzone_id = :workzone ");
            params.put("workzone", model.getWorkzone());
        }
        if(!ObjectUtils.isEmpty(model.getBinType())){
            sql.append(" AND l.bin_type_id = :binType ");
            params.put("binType", model.getBinType());
        }
        if(StringUtils.isNotBlank(model.getLocationCd())){
            sql.append(" AND l.location_cd = :locationCd ");
            params.put("locationCd", model.getLocationCd());
        }
        if(StringUtils.isNotBlank(model.getLocationType())){
            sql.append(" AND l.location_type = :locationType ");
            params.put("locationType", model.getLocationType());
        }
        if(StringUtils.isNotBlank(model.getMainLocationSign())){
            sql.append(" AND l.primary_flag in (:flag)");
            if (StringUtils.equals(CommonConstants.CHAR_ALL, model.getMainLocationSign())) {
                List<String> flags = new ArrayList<String>();
                flags.add(CommonConstants.CHAR_Y);
                flags.add(CommonConstants.CHAR_N);
                params.put("flag", flags);
            }
            if (StringUtils.equals(CommonConstants.CHAR_YES, model.getMainLocationSign())) {
                params.put("flag", CommonConstants.CHAR_Y);
            }
            if (StringUtils.equals(CommonConstants.CHAR_NO, model.getMainLocationSign())) {
                params.put("flag", CommonConstants.CHAR_N);
            }
        }

        sql.append(" ORDER BY l.location_cd ");

        String countSql = "SELECT COUNT(1) FROM ( " + "SELECT * " + sql.toString() + ") AS subquery; ";
        Pageable pageable = PageRequest.of(model.getCurrentPage() - 1, model.getPageSize());
        Integer count = super.queryForSingle(countSql, params, Integer.class);
        params.putAll(params);

        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, LocationVLBO.class, pageable), pageable, count);
    }

    @Override
    public PageImpl<CMM020101BO> findLocInfoInquiryList(CMM020101Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        StringBuilder selSql = new StringBuilder();

        selSql.append("        SELECT l.location_cd   AS location         ");
        selSql.append("             , l.location_id   AS locationId       ");
        selSql.append("             , l.facility_id   AS pointId          ");
        selSql.append("             , l.location_type AS locationTypeId   ");
        selSql.append("             , l.bin_type_id   AS binTypeId        ");
        selSql.append("             , l.workzone_id   AS wzId             ");
        selSql.append("             , l.primary_flag  AS mainLocation     ");
        
        sql.append("          FROM location l                          ");
        sql.append("         WHERE l.site_id = :siteId                 ");
        sql.append("           AND l.facility_id = :pointId            ");

        params.put("siteId",siteId);
        params.put("pointId",model.getPointId());

        if(StringUtils.equals(model.getMainLocation(), CommonConstants.CHAR_Y)){
            sql.append("           AND l.primary_flag = :mainLocation   ");
            params.put("mainLocation",model.getMainLocation());
        }

        if(model.getLocationId()!=null) {
            sql.append("       AND l.location_id = :locationId            ");
            params.put("locationId",model.getLocationId());
        }

        if (StringUtils.isNotBlank(model.getLocationTypeId())) {
            sql.append("       AND l.location_type = :locationTypeId            ");
            params.put("locationTypeId",model.getLocationTypeId());
        }

        if(model.getBinTypeId()!=null) {
            sql.append("       AND l.bin_type_id = :binType            ");
            params.put("binType",model.getBinTypeId());
        }

        if(model.getWzId()!=null) {
            sql.append("       AND l.workzone_id = :wzId            ");
            params.put("wzId",model.getWzId());
        }

        sql.append("  ORDER BY l.location_cd        ");

        String countSql = "SELECT COUNT(1) FROM ( " + "SELECT 1 " + sql.toString() + ") AS subquery; ";
        Pageable pageable = PageRequest.of(model.getCurrentPage() - 1, model.getPageSize());

        Integer count = super.queryForSingle(countSql, params, Integer.class);
        params.putAll(params);

        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, CMM020101BO.class, pageable), pageable, count);
    }

    /**
     * 功能描述:spq0308查出所有库位
     *
     * @author mid2178
     */
    @Override
    public List<SPQ030801BO> getUsageLocationAll(String siteId, Long pointId, Long binTypeId, String locationTypeId){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     select mf.facility_cd       as pointCd       ");
        sql.append("          , mf.facility_nm       as pointNm       ");
        sql.append("          , mci.code_data1       as locationType  ");
        sql.append("          , bt.description       as binType       ");
        sql.append("          , count(l.location_cd) as totalQty      ");
        sql.append("       from location l                            ");
        sql.append(" inner join mst_facility mf                       ");
        sql.append("         on mf.facility_id = l.facility_id        ");
        sql.append("        and mf.site_id = l.site_id                ");
        sql.append(" inner join mst_code_info mci                     ");
        sql.append("         on mci.code_id =:codeId                  ");
        sql.append("        and mci.site_id =:cmmSiteId               ");
        sql.append("        and mci.code_dbid = l.location_type       ");
        sql.append(" inner join bin_type bt                           ");
        sql.append("         on bt.bin_type_id = l.bin_type_id        ");
        sql.append("       where l.site_id =:siteId                   ");
        sql.append("         and l.facility_id =:pointId              ");

        if(binTypeId != null) {
            sql.append("    and l.bin_type_id =:binTypeId             ");
            params.put("binTypeId",binTypeId);
        }
        if(StringUtils.isNotBlank(locationTypeId)) {
            sql.append("   and l.location_type =:locationTypeId       ");
            params.put("locationTypeId",locationTypeId);
        }
        sql.append("    group by mf.facility_cd , mf.facility_nm , mci.code_data1 , bt.description ");
        sql.append("    order by  mf.facility_cd , mci.code_data1 , bt.description                 ");

        params.put("siteId",siteId);
        params.put("pointId",pointId);
        params.put("cmmSiteId",CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("codeId",PJConstants.LocationType.CODE_ID);

        return super.queryForList(sql.toString(), params, SPQ030801BO.class);
    }

    /**
    * 功能描述:dim0206 根据locationId查locationType，并转成对应的productStockStatusType
    *
    * @author mid2178
    */
    @Override
    public Map<Long, String> getlocTypeByIds(Set<Long> locationIds){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        Map<Long, String> returnMap = new HashMap<>();

        sql.append("  select location_id as locationId                                          ");
        sql.append("       , case when location_type in (:TENTATIVE,:NORMAL) then (:ONHANDQTY)  ");
        sql.append("              when location_type in (:FROZEN) then (:ONFROZENQTY)           ");
        sql.append("              when location_type in (:SERVICE) then (:ONSERVICEQTY)         ");
        sql.append("              end as type                                                   ");
        sql.append("  from location                                                             ");
        sql.append("  where location_id in (:locationIds)                                       ");

        params.put("locationIds", locationIds);
        params.put("TENTATIVE", LocationType.TENTATIVE.getCodeDbid());
        params.put("NORMAL", LocationType.NORMAL.getCodeDbid());
        params.put("SERVICE", LocationType.SERVICE.getCodeDbid());
        params.put("FROZEN", LocationType.FROZEN.getCodeDbid());
        params.put("ONSERVICEQTY", SpStockStatus.ONSERVICE_QTY.getCodeDbid());
        params.put("ONFROZENQTY", SpStockStatus.ONFROZEN_QTY.getCodeDbid());
        params.put("ONHANDQTY", SpStockStatus.ONHAND_QTY.getCodeDbid());

        List<DIM020601BO> bos = super.queryForList(sql.toString(), params, DIM020601BO.class);
        for (DIM020601BO bo : bos) {
            returnMap.put(bo.getLocationId() , bo.getType());
        }

        return returnMap;
    }
}
