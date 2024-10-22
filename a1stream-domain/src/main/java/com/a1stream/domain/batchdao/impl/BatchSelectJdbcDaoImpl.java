package com.a1stream.domain.batchdao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.SystemParameterType;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.ProductRelationClass;
import com.a1stream.common.constants.PJConstants.RopRoqParameter;
import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.domain.batchdao.BatchSelectJdbcDao;
import com.a1stream.domain.bo.batch.BSProductStockStatusBO;
import com.a1stream.domain.bo.batch.CuAbcDefinitionInfoBatchBO;
import com.a1stream.domain.bo.batch.PartsBatchBO;
import com.a1stream.domain.bo.batch.ProductAbcBatchBO;
import com.a1stream.domain.bo.batch.ProductOrderResultSummaryBatchBO;
import com.a1stream.domain.bo.batch.RrDemandForecastMonthItemBatchBO;
import com.a1stream.domain.bo.batch.SiSeasonIndexInfoModelBO;
import com.a1stream.domain.bo.batch.SsmProductAbcBatchBO;
import com.ymsl.solid.base.util.StringUtils;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

@Repository
public class BatchSelectJdbcDaoImpl extends JpaNativeQuerySupportRepository implements BatchSelectJdbcDao {

    @Override
    public List<String> getCmmSiteList(String apServerUrl) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<String> siteList = new ArrayList<String>();
        sql.append("    select  site_id  as  siteId             ");
        sql.append("      from  cmm_site_master                 ");
        sql.append("     where  active_flag = :flag             ");
        sql.append("       and  ap_server_flag = :apServerUrl   ");

        param.put("apServerUrl", apServerUrl);
        param.put("flag", CommonConstants.CHAR_Y);
        List<PartsBatchBO> returnList = super.queryForList(sql.toString(), param, PartsBatchBO.class);

        for (PartsBatchBO partsBatchBO : returnList) {

            siteList.add(partsBatchBO.getSiteId());
        }


        return siteList;
    }

    @Override
    public String getSystemParDate(String siteId
                                 , String sysParamTypeId) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        String lastDailyBatchDate = "";
        sql.append("    select  parameter_value  as parametervalue            ");
        sql.append("      from  system_parameter          ");
        sql.append("     where  site_id =:siteId  ");
        sql.append("       and  system_parameter_type_id =:sysParamTypeId    ");

        param.put("siteId", siteId);
        param.put("sysParamTypeId", sysParamTypeId);

        List<PartsBatchBO> returnList = super.queryForList(sql.toString(), param, PartsBatchBO.class);

        for (PartsBatchBO partsBatchBO : returnList) {

            lastDailyBatchDate = partsBatchBO.getParametervalue();
        }

        return lastDailyBatchDate;
    }

    @Override
    public String getAcctMonthInfo(String siteId
                                 , String accountMonth) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        String dateTo = "";
        sql.append("    select  to_date as dateto             ");
        sql.append("      from  cmm_mst_acct_month          ");
        sql.append("     where  site_id =:siteId  ");
        sql.append("       and  account_month =:accountMonth    ");

        param.put("siteId", siteId);
        param.put("accountMonth", accountMonth);

        List<PartsBatchBO> returnList = super.queryForList(sql.toString(), param, PartsBatchBO.class);

        for (PartsBatchBO partsBatchBO : returnList) {

            dateTo = partsBatchBO.getDateto();
        }

        return dateTo;
    }

    @Override
    public List<Long> getFacilityList(String siteId,String collectDate) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<Long> facilityList = new ArrayList<Long>();
        sql.append("    select  facility_id  as  facilityid     ");
        sql.append("      from  mst_facility                    ");
        sql.append("     where  site_id =:siteId                ");
        sql.append("       and  from_date <=:collectDate        ");
        sql.append("       and  to_date >=:collectDate          ");
        sql.append("       and  (delete_flag is null or delete_flag != :flag )    ");

        param.put("siteId", siteId);
        param.put("collectDate", collectDate);
        param.put("flag", CommonConstants.CHAR_Y);
        List<PartsBatchBO> returnList = super.queryForList(sql.toString(), param, PartsBatchBO.class);

        for (PartsBatchBO partsBatchBO : returnList) {

            facilityList.add(partsBatchBO.getFacilityid());
        }

        return facilityList;
    }

    @Override
    public Set<String> getProductTypeDEXY(String siteId,Long facilityId){

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        Set<String> facpro = new HashSet<String>();
        sql.append("    select  proabc.facility_id as facilityid                                                        ");
        sql.append("         ,  proabc.product_id as productid                                                          ");
        sql.append("    from     product_abc_info as proabc                                                             ");
        sql.append("    where     exists(select 1 from abc_definition_info as abcdef where proabc.abc_definition_id = abcdef.abc_definition_info_id  ");
        sql.append("                   and abcdef.abc_type in ('D1','D2','D3','E1','E2','E3','XX','YY'))                ");
        sql.append("            and proabc.site_id = :siteId                                                            ");
        if (facilityId != null) {
            sql.append("        and proabc.facility_id = :facilityId                                                    ");
        }

        param.put("siteId", siteId);
        if (facilityId != null) {
            param.put("facilityId",facilityId);
        }

        List<PartsBatchBO> returnList = super.queryForList(sql.toString(), param, PartsBatchBO.class);

        for (PartsBatchBO partsBatchBO : returnList) {
            facpro.add(longToStr(partsBatchBO.getFacilityid())+ CommonConstants.CHAR_COLON + longToStr(partsBatchBO.getProductid()));
        }

        return facpro;
    }


    @Override
    public Set<String> getReorderGuidelineMap(String siteId,Long facilityId){

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        Set<String> facpro = new HashSet<String>();
        sql.append("    select     reordgui.facility_id as facilityid       ");
        sql.append("         ,  reordgui.product_id as productid            ");
        sql.append("    from     reorder_guideline as reordgui              ");
        sql.append("    where     reordgui.site_id = :siteId                ");
        sql.append("      and   reordgui.rop_roq_manual_flag =:manualFlag   ");
        if (facilityId != null) {
            sql.append("    and reordgui.facility_id = :facilityId          ");
        }

        param.put("siteId", siteId);
        param.put("manualFlag", CommonConstants.CHAR_Y);
        if (facilityId != null) {
            param.put("facilityId",facilityId);
        }
        List<PartsBatchBO> returnList = super.queryForList(sql.toString(), param, PartsBatchBO.class);

        for (PartsBatchBO partsBatchBO : returnList) {
            facpro.add(longToStr(partsBatchBO.getFacilityid())+longToStr(partsBatchBO.getProductid()));
        }

        return facpro;
    }

    @Override
    public List<RrDemandForecastMonthItemBatchBO> getDemandForecast(String siteId,Long facilityId){

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();

        sql.append("    select     demforcast.facility_id as facilityid,            ");
        sql.append("            demforcast.product_id as productid,                 ");
        sql.append("            demforcast.to_product_id as toproductid,            ");
        sql.append("            demforcast.demand_qty as demandqty,                 ");
        sql.append("            demforcast.demand_forecast_id as demandforecastid   ");
        sql.append("    from     demand_forecast as demforcast                      ");
        sql.append("    where     demforcast.site_id = :siteId                      ");
        if (facilityId != null) {
            sql.append("    and demforcast.facility_id = :facilityId                ");
        }

        param.put("siteId", siteId);
        if (facilityId != null) {
            param.put("facilityId",facilityId);
        }

        return  super.queryForList(sql.toString(), param, RrDemandForecastMonthItemBatchBO.class);
    }

    @Override
    public Map<String, SiSeasonIndexInfoModelBO> getSeasonIndexInfoMap(String siteId,Long facilityId){

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<SiSeasonIndexInfoModelBO> seasonIndexInfoModelBOs = new ArrayList<SiSeasonIndexInfoModelBO>();
        Map<String, SiSeasonIndexInfoModelBO> returnMap = new HashMap<String, SiSeasonIndexInfoModelBO>();

        sql.append("    SELECT season_index_batch_id as seasonindexid          ");
        sql.append("          ,facility_id as facilityid                       ");
        sql.append("          ,product_category_id as productcategoryid        ");
        sql.append("          ,n_index as nindex                               ");
        sql.append("          ,n_1_index as n1index                            ");
        sql.append("          ,n_2_index as n2index                            ");
        sql.append("          ,n_3_index as n3index                            ");
        sql.append("          ,n_4_index as n4index                            ");
        sql.append("          ,n_5_index as n5index                            ");
        sql.append("          ,n_6_index as n6index                            ");
        sql.append("          ,n_7_index as n7index                            ");
        sql.append("          ,n_8_index as n8index                            ");
        sql.append("          ,n_9_index as n9index                            ");
        sql.append("          ,n_10_index as n10index                          ");
        sql.append("          ,n_11_index as n11index                          ");
        sql.append("    FROM  season_index_batch                               ");
        sql.append("    WHERE site_id = :siteId                                ");
        if (facilityId != null) {
            sql.append("    and facility_id = :facilityId                      ");
        }

        param.put("siteId", siteId);
        if (facilityId != null) {
            param.put("facilityId",facilityId);
        }
        seasonIndexInfoModelBOs = super.queryForList(sql.toString(), param, SiSeasonIndexInfoModelBO.class);

        for (SiSeasonIndexInfoModelBO seasonIndexInfoModelBO : seasonIndexInfoModelBOs) {
            returnMap.put(seasonIndexInfoModelBO.getFacilityid()+CommonConstants.CHAR_COLON+seasonIndexInfoModelBO.getProductcategoryid()
                        , seasonIndexInfoModelBO);
        }

        return returnMap;
    }

    @Override
    public Map<String, SiSeasonIndexInfoModelBO> getSeasonIndexManualInfoMap(String siteId,Long facilityId){

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<SiSeasonIndexInfoModelBO> seasonIndexInfoModelBOs = new ArrayList<SiSeasonIndexInfoModelBO>();
        Map<String, SiSeasonIndexInfoModelBO> returnMap = new HashMap<String, SiSeasonIndexInfoModelBO>();

        sql.append("    SELECT season_index_manual_id as seasonindexid         ");
        sql.append("          ,facility_id as facilityid                       ");
        sql.append("          ,product_category_id as productcategoryid        ");
        sql.append("          ,index_month01 as n01index                       ");
        sql.append("          ,index_month02 as n02index                       ");
        sql.append("          ,index_month03 as n03index                       ");
        sql.append("          ,index_month04 as n04index                       ");
        sql.append("          ,index_month05 as n05index                       ");
        sql.append("          ,index_month06 as n06index                       ");
        sql.append("          ,index_month07 as n07index                       ");
        sql.append("          ,index_month08 as n08index                       ");
        sql.append("          ,index_month09 as n09index                       ");
        sql.append("          ,index_month10 as n10index                       ");
        sql.append("          ,index_month11 as n11index                       ");
        sql.append("          ,index_month12 as n12index                       ");
        sql.append("    FROM  season_index_manual                              ");
        sql.append("    WHERE site_id = :siteId                                ");
        if (facilityId != null) {
            sql.append("    and facility_id = :facilityId                      ");
        }

        param.put("siteId", siteId);
        if (facilityId != null) {
            param.put("facilityId",facilityId);
        }
        seasonIndexInfoModelBOs = super.queryForList(sql.toString(), param, SiSeasonIndexInfoModelBO.class);

        for (SiSeasonIndexInfoModelBO seasonIndexInfoModelBO : seasonIndexInfoModelBOs) {
            returnMap.put(seasonIndexInfoModelBO.getFacilityid()+CommonConstants.CHAR_COLON+seasonIndexInfoModelBO.getProductcategoryid()
                        , seasonIndexInfoModelBO);
        }

        return returnMap;
    }

    @Override
    public void searchFacilityPartsAvgMonthAndTrendIdx(String siteId,
                                                     final Map<String, String> facPrdAvgMonthMap,
                                                     final Map<String, String> facPrdTrendIdxMap){

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<PartsBatchBO> partsBatchBOList = new ArrayList<PartsBatchBO>();
        List<String> typeList = new ArrayList<>();
        typeList.add(RopRoqParameter.KEY_PARTSAVERAGEDEMAND);
        typeList.add(RopRoqParameter.KEY_PARTSTRENDINDEX);
        sql.append("    SELECT  string_value as stringvalue               ");
        sql.append("         ,  facility_id as facilityid                 ");
        sql.append("         ,  product_id as productid                 ");
        sql.append("         ,  ropq_type as ropqtype                     ");
        sql.append("      FROM parts_ropq_monthly                         ");
        sql.append("     WHERE site_id = :siteId                        ");
        sql.append("       and ropq_type in (:typeList)                 ");

        param.put("siteId", siteId);
        param.put("typeList", typeList);
        partsBatchBOList =  super.queryForList(sql.toString(), param, PartsBatchBO.class);

        for (PartsBatchBO partsBatchBO : partsBatchBOList) {

            if(RopRoqParameter.KEY_PARTSAVERAGEDEMAND.equals(partsBatchBO.getRopqtype())) {

                facPrdAvgMonthMap.put(partsBatchBO.getFacilityid()+ CommonConstants.CHAR_COLON+ partsBatchBO.getProductid()
                                    , partsBatchBO.getStringvalue());
            }else {

                facPrdTrendIdxMap.put(partsBatchBO.getFacilityid()+ CommonConstants.CHAR_COLON+ partsBatchBO.getProductid()
                                       , partsBatchBO.getStringvalue());
            }
        }
    }

    @Override
    public List<ProductAbcBatchBO> getAbcType(String siteId,Long facilityId){

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();

        sql.append("    select  proabc.product_id as productid,                                 ");
        sql.append("            proabc.facility_id as facilityid,                               ");
        sql.append("            abcdef.abc_type as abctype,                                     ");
        sql.append("            proabc.product_category_id as productcategoryid                 ");
        sql.append("    from     product_abc_info as proabc                                     ");
        sql.append("            inner join abc_definition_info as abcdef                        ");
        sql.append("            on    proabc.abc_definition_id = abcdef.abc_definition_info_id  ");
        sql.append("    where     proabc.site_id = :siteId                                      ");
        if (facilityId != null) {
            sql.append("    and  proabc.facility_id = :facilityId       ");
        }
        param.put("siteId", siteId);
        if (facilityId != null) {
            param.put("facilityId", facilityId);
        }
        return  super.queryForList(sql.toString(), param, ProductAbcBatchBO.class);
    }

    @Override
    public List<PartsBatchBO> getProductABCCategory(String siteId,Long facilityId){

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();

        sql.append("    select                                                                                  ");
        sql.append("            proabc.facility_id as facilityid,                                               ");
        sql.append("            proabc.product_id as productid,                                                 ");
        sql.append("            proabc.product_category_id as productcategoryid                                 ");
        sql.append("    from     product_abc_info as proabc                                                     ");
        sql.append("            inner join abc_definition_info as abcdef on proabc.abc_definition_id = abcdef.abc_definition_info_id    ");
        sql.append("    where   proabc.site_id = :siteId                                                        ");
        sql.append("        and proabc.product_category_id is not null                                          ");
        sql.append("        and abcdef.abc_type in ('A1','A2','A3','B1','B2','B3','C1','C2','C3')               ");
        if (facilityId != null) {
            sql.append("    and  proabc.facility_id = :facilityId       ");
        }
        param.put("siteId", siteId);
        if (facilityId != null) {
            param.put("facilityId", facilityId);
        }
        return  super.queryForList(sql.toString(), param, PartsBatchBO.class);
    }

    @Override
    public List<String> getSeasonIndex(String siteId,Long facilityId){

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<PartsBatchBO> partsBatchBOList = new ArrayList<PartsBatchBO>();
        List<String> returnList = new ArrayList<String>();

        sql.append("    SELECT season.facility_id as facilityid         ");
        sql.append("         , season.product_category_id as categoryid ");
        sql.append("     from  season_index_manual season               ");
        sql.append("    where  season.site_id = :siteId                 ");
        if (facilityId != null) {
            sql.append("    and  season.facility_id = :facilityId       ");
        }

        param.put("siteId", siteId);
        if (facilityId != null) {
            param.put("facilityId", facilityId);
        }
        partsBatchBOList = super.queryForList(sql.toString(), param, PartsBatchBO.class);

        for (PartsBatchBO partsBatchBO : partsBatchBOList) {

            returnList.add(partsBatchBO.getFacilityid().toString()
                          + CommonConstants.CHAR_COLON
                          + partsBatchBO.getCategoryid().toString());
        }

        return returnList;
    }

    @Override
    public Map<String, String> getTrendIndexUpperAndLowerValues(String siteId){

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<PartsBatchBO> PartsBatchBOList = new ArrayList<PartsBatchBO>();
        List<String> typeList = new ArrayList<String>();
        typeList.add(SystemParameterType.GROWTHLOWER);
        typeList.add(SystemParameterType.GROWTHUPPER);
        Map<String, String> returnMap = new HashMap<String, String>();

        sql.append("    SELECT parameter_value as parametervalue,                       ");
        sql.append("           system_parameter_type_id as systemparametertypeid        ");
        sql.append("     from  system_parameter                                         ");
        sql.append("    where  site_id = :siteId                                        ");
        sql.append("         and  system_parameter_type_id in (:typeList)               ");

        param.put("siteId", siteId);
        param.put("typeList", typeList);
        PartsBatchBOList = super.queryForList(sql.toString(), param, PartsBatchBO.class);

        for (PartsBatchBO partsBatchBO : PartsBatchBOList) {

            returnMap.put(partsBatchBO.getSystemparametertypeid(), partsBatchBO.getParametervalue());
        }

        return returnMap;
    }

    @Override
    public Map<String, SsmProductAbcBatchBO> getPartsRopqMonthlyMap(String siteId,Long facilityId, String typeId){

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<SsmProductAbcBatchBO> ssmProductAbcModelBOList = new ArrayList<SsmProductAbcBatchBO>();
        Map<String, SsmProductAbcBatchBO> returnMap = new HashMap<String, SsmProductAbcBatchBO>();

        sql.append("    SELECT  string_value as stringvalue                         ");
        sql.append("         ,  product_id as productid                             ");
        sql.append("         ,  facility_id as facilityid                           ");
        sql.append("      FROM parts_ropq_monthly                                   ");
        sql.append("     WHERE site_id = :siteId                                    ");
        sql.append("       and ropq_type = :typeId                                  ");
        if (facilityId != null) {
            sql.append("    and facility_id = :facilityId    ");
        }

        param.put("siteId", siteId);
        param.put("typeId", typeId);
        if (facilityId != null) {
            param.put("facilityId", facilityId);
        }
        ssmProductAbcModelBOList = super.queryForList(sql.toString(), param, SsmProductAbcBatchBO.class);

        for (SsmProductAbcBatchBO ssmProductAbcModelBO : ssmProductAbcModelBOList) {

            returnMap.put(ssmProductAbcModelBO.getFacilityid().toString()+ssmProductAbcModelBO.getProductid().toString()
                            , ssmProductAbcModelBO);
        }

        return returnMap;
    }

    @Override
    public Map<String, SsmProductAbcBatchBO> getReorderGuidelineFlagIsOne(String siteId,Long facilityId){

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<SsmProductAbcBatchBO> ssmProductAbcModelBOList = new ArrayList<SsmProductAbcBatchBO>();
        Map<String, SsmProductAbcBatchBO> returnMap = new HashMap<String, SsmProductAbcBatchBO>();

        sql.append("    select reordergui.product_id as productid       ");
        sql.append("         , reordergui.facility_id as facilityid     ");
        sql.append("    from reorder_guideline as reordergui            ");
        sql.append("    where reordergui.site_id = :siteId              ");
        if (facilityId != null) {
            sql.append("    and reordergui.facility_id = :facilityId    ");
        }
        sql.append("  and reordergui.rop_roq_manual_flag = :manualFlag  ");

        param.put("siteId", siteId);
        param.put("manualFlag", CommonConstants.CHAR_Y);
        if (facilityId != null) {
            param.put("facilityId", facilityId);
        }
        ssmProductAbcModelBOList = super.queryForList(sql.toString(), param, SsmProductAbcBatchBO.class);

        for (SsmProductAbcBatchBO ssmProductAbcModelBO : ssmProductAbcModelBOList) {

            returnMap.put(ssmProductAbcModelBO.getFacilityid().toString()+ssmProductAbcModelBO.getProductid().toString()
                        , ssmProductAbcModelBO);
        }

        return returnMap;
    }

    @Override
    public Map<String, SsmProductAbcBatchBO> getReorderGuideline(String siteId, Long facilityId){

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<SsmProductAbcBatchBO> ssmProductAbcModelBOList = new ArrayList<SsmProductAbcBatchBO>();
        Map<String, SsmProductAbcBatchBO> returnMap = new HashMap<String, SsmProductAbcBatchBO>();

        sql.append("    select                                      ");
        sql.append("    reordergui.facility_id as facilityid,       ");
        sql.append("    reordergui.product_id as productid,         ");
        sql.append("    reordergui.parts_leadtime as partsleadtime  ");
        sql.append("    from reorder_guideline as reordergui        ");
        sql.append("    where                                       ");
        sql.append("    reordergui.site_id = :siteId                ");
        if (facilityId != null) {
            sql.append("    and reordergui.facility_id = :facilityId    ");
        }
        sql.append("    and reordergui.rop_roq_manual_flag = :manualFlag    ");

        param.put("siteId", siteId);
        param.put("manualFlag", CommonConstants.CHAR_N);
        if (facilityId != null) {
            param.put("facilityId", facilityId);
        }
        ssmProductAbcModelBOList = super.queryForList(sql.toString(), param, SsmProductAbcBatchBO.class);

        for (SsmProductAbcBatchBO ssmProductAbcModelBO : ssmProductAbcModelBOList) {

            returnMap.put(ssmProductAbcModelBO.getFacilityid().toString()+ssmProductAbcModelBO.getProductid().toString(), ssmProductAbcModelBO);
        }

        return returnMap;
    }

    @Override
    public Map<String, String> getPartsRopqMonthlyMapString(String siteId,Long facilityId, String typeId){

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<PartsBatchBO> PartsBatchBOList = new ArrayList<PartsBatchBO>();
        Map<String, String> returnMap = new HashMap<String, String>();

        sql.append("    SELECT  string_value as stringvalue         ");
        sql.append("         ,  product_id as productid             ");
        sql.append("         ,  facility_id as facilityid           ");
        sql.append("      FROM parts_ropq_monthly                   ");
        sql.append("     WHERE site_id = :siteId                    ");
        if (facilityId != null) {
            sql.append("       and facility_id = :facilityId        ");
        }
        sql.append("       and ropq_type = :typeId                  ");
        param.put("siteId", siteId);
        param.put("typeId", typeId);
        if (facilityId != null) {
            param.put("facilityId", facilityId);
        }
        PartsBatchBOList = super.queryForList(sql.toString(), param, PartsBatchBO.class);

        for (PartsBatchBO partsBatchBOInfo : PartsBatchBOList) {
            returnMap.put( partsBatchBOInfo.getFacilityid()+ CommonConstants.CHAR_COLON+ partsBatchBOInfo.getProductid()
                        , partsBatchBOInfo.getStringvalue());
        }

        return returnMap;
    }

    // in user
    @Override
    public List<ProductAbcBatchBO> getProductProductAbcTypeABCN(String siteId,Long facilityId) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();

        sql.append("  select");
        sql.append("  proabc.product_id as productid,                       ");
        sql.append("  proabc.facility_id as facilityid,                     ");
        sql.append("  proabc.product_category_id as productcategoryid,      ");
        sql.append("  abcdef.abc_type as abctype,                           ");
        sql.append("  abcdef.ssm_upper as ssmupper,                         ");
        sql.append("  abcdef.ssm_lower as ssmlower,                         ");
        sql.append("  abcdef.add_leadtime as addleadtime,                   ");
        sql.append("  abcdef.rop_month as ropmonth,                         ");
        sql.append("  abcdef.roq_month as roqmonth,                         ");
        sql.append("  abcdef.target_supply_rate as targetsupplyrate         ");
        sql.append("  from product_abc_info as proabc                       ");
        sql.append("  inner join abc_definition_info as abcdef on proabc.abc_definition_id = abcdef.abc_definition_info_id");
        sql.append("  where                                                 ");
        sql.append("  proabc.site_id = :siteId                              ");
        if (facilityId != null) {
            sql.append("  and proabc.facility_id = :facilityId              ");
        }
        sql.append("  and abcdef.abc_type in ('A1','A2','A3','B1','B2','B3','C1','C2','C3','N1','N2','N3') ");

        param.put("siteId", siteId);
        if (facilityId != null) {
            param.put("facilityId", facilityId);
        }
        return super.queryForList(sql.toString(), param, ProductAbcBatchBO.class);
    }

    @Override
    public Map<Long,String> getProductCost(String siteId) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<PartsBatchBO> partsBatchBOList = new ArrayList<PartsBatchBO>();
        Map<Long, String> costMap = new HashMap<Long, String>();

        sql.append("    select cost1.cost as cost, cost1.product_id as productid         ");
        sql.append("      from  product_cost cost1                                       ");
        sql.append("     where cost1.cost_type =:costType                                ");
        sql.append("       and cost1.site_id = :siteId                                   ");

        param.put("siteId", siteId);
        param.put("costType", PJConstants.CostType.AVERAGE_COST);
        partsBatchBOList = super.queryForList(sql.toString(), param, PartsBatchBO.class);

        for (PartsBatchBO PartsBatchBOInfo : partsBatchBOList) {

            costMap.put(PartsBatchBOInfo.getProductid(), StringUtils.toString(PartsBatchBOInfo.getCost()));
        }

        return costMap;
    }

    @Override
    public List<PartsBatchBO> getProductsGroupbyFacility(String siteId) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        sql.append("    SELECT  pro.product_id as productid                                 ");
        sql.append("         ,  category.parent_category_id as categoryid                   ");
        sql.append("      FROM mst_product pro                                                  ");
        sql.append(" LEFT JOIN mst_product_category category on pro.product_category_id =  category.product_category_id ");
        sql.append("       and category.site_id = pro.site_id                               ");
        sql.append("     where pro.product_classification = :productClassification          ");
        sql.append("       and pro.site_id = :siteId                                        ");
        sql.append("       and category.parent_category_id is not null                      ");

        param.put("siteId", siteId);
        param.put("productClassification", ProductClsType.PART.getCodeDbid());

        return super.queryForList(sql.toString(), param, PartsBatchBO.class);
    }

    // in user
    @Override
    public  List<CuAbcDefinitionInfoBatchBO> getAbcDefinition(String siteId) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        sql.append("    select abc.abc_definition_info_id as abcid               ");
        sql.append("         , abc.abc_type as abctype                           ");
        sql.append("         , abc.percentage as percentage                       ");
        sql.append("         , abc.cost_from as costfrom                           ");
        sql.append("         , abc.cost_to as costto                               ");
        sql.append("         , abc.product_category_id as productcategoryid     ");
        sql.append("      from abc_definition_info abc                             ");
        sql.append("     where abc.site_id = :siteId                            ");

        param.put("siteId", siteId);

        return super.queryForList(sql.toString(), param, CuAbcDefinitionInfoBatchBO.class);
    }

    @Override
    public Map<Long, List<Long>> getReplaceToProductIdMap(String siteId) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<PartsBatchBO> PartsBatchBOList = new ArrayList<PartsBatchBO>();
        Map<Long, List<Long>> productRelationMap = new HashMap<Long, List<Long>>();
        sql.append("    SELECT  to_product_id as toproductid       ");
        sql.append("         ,  from_product_id as productid       ");
        sql.append("      FROM mst_product_relation                                 ");
        sql.append("     WHERE site_id = :siteId                                    ");
        sql.append("       AND relation_type =:relationClass      ");

        param.put("siteId", siteId);
        param.put("relationClass", PJConstants.ProductRelationClass.SUPERSEDING);
        PartsBatchBOList = super.queryForList(sql.toString(), param, PartsBatchBO.class);

        for (PartsBatchBO PartsBatchBOInfo : PartsBatchBOList) {

            if (productRelationMap.containsKey(PartsBatchBOInfo.getProductid())) {

                productRelationMap.get(PartsBatchBOInfo.getProductid()).add(PartsBatchBOInfo.getToProductid());
              } else {

                  List<Long> newProductIdList = new ArrayList<Long>();
                  newProductIdList.add(PartsBatchBOInfo.getToProductid());
                  productRelationMap.put(PartsBatchBOInfo.getProductid(), newProductIdList);
              }
        }

        return productRelationMap;
    }

    // in user
    @Override
    public Map<String, ProductOrderResultSummaryBatchBO> getProductOrderResultSummaryByTargetYear(String siteId,Long facilityId, String targetYear) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<ProductOrderResultSummaryBatchBO> orderResultSummaryList = new ArrayList<ProductOrderResultSummaryBatchBO>();
        Map<String, ProductOrderResultSummaryBatchBO> returnMap = new HashMap<String, ProductOrderResultSummaryBatchBO>();

        sql.append(" SELECT                                          ");
        sql.append("        product_id            as productid,      ");
        sql.append("        facility_id           as facilityid,     ");
        sql.append("        target_year           as targetyear,     ");
        sql.append("        sum(month01_quantity) as month01quantity,");
        sql.append("        sum(month02_quantity) as month02quantity,");
        sql.append("        sum(month03_quantity) as month03quantity,");
        sql.append("        sum(month04_quantity) as month04quantity,");
        sql.append("        sum(month05_quantity) as month05quantity,");
        sql.append("        sum(month06_quantity) as month06quantity,");
        sql.append("        sum(month07_quantity) as month07quantity,");
        sql.append("        sum(month08_quantity) as month08quantity,");
        sql.append("        sum(month09_quantity) as month09quantity,");
        sql.append("        sum(month10_quantity) as month10quantity,");
        sql.append("        sum(month11_quantity) as month11quantity,");
        sql.append("        sum(month12_quantity) as month12quantity ");
        sql.append(" FROM   product_order_result_summary             ");
        sql.append(" WHERE  target_year = :targetYear                ");
        sql.append(" AND    site_id = :siteId                        ");
        if (facilityId != null) {
            sql.append(" AND    facility_id = :facilityId                        ");
        }
        sql.append(" group by product_id,facility_id,target_year     ");

        param.put("siteId", siteId);
        param.put("targetYear", targetYear);
        if (facilityId != null) {
            param.put("facilityId", facilityId);
        }
        orderResultSummaryList = super.queryForList(sql.toString(), param, ProductOrderResultSummaryBatchBO.class);

        for (ProductOrderResultSummaryBatchBO orderResultSummaryModel : orderResultSummaryList) {
            returnMap.put(orderResultSummaryModel.getProductid()+CommonConstants.CHAR_COLON+orderResultSummaryModel.getFacilityid(), orderResultSummaryModel);
        }

        return returnMap;
    }

    @Override
    public Map<String, Long> getProductOrderResultSummary(String siteId, String targetYear) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<ProductOrderResultSummaryBatchBO> orderResultSummaryList = new ArrayList<ProductOrderResultSummaryBatchBO>();
        Map<String, Long> returnMap = new HashMap<String, Long>();

        sql.append(" SELECT product_id            as productid      ");
        sql.append("       ,facility_id           as facilityid     ");
        sql.append("       , product_order_result_summary_id  as productorderresultsummaryid     ");
        sql.append(" FROM   product_order_result_summary            ");
        sql.append(" WHERE  target_year = :targetYear               ");
        sql.append(" AND    site_id = :siteId                       ");

        param.put("siteId", siteId);
        param.put("targetYear", targetYear);

        orderResultSummaryList = super.queryForList(sql.toString(), param, ProductOrderResultSummaryBatchBO.class);

        for (ProductOrderResultSummaryBatchBO orderResultSummaryModel : orderResultSummaryList) {
            returnMap.put(orderResultSummaryModel.getProductid()+CommonConstants.CHAR_COLON+orderResultSummaryModel.getFacilityid()
                                        , orderResultSummaryModel.getProductorderresultsummaryid());
        }

        return returnMap;
    }

    // in user
    @Override
    public Map<String, String> getPartsRopqParameter(String siteId) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<PartsBatchBO> PartsBatchBOList = new ArrayList<PartsBatchBO>();
        Map<String, String> returnMap = new HashMap<String, String>();
        sql.append("    SELECT  profea.product_id as productid                       ");
        sql.append("         ,  profea.facility_id as facilityid          ");
        sql.append("         ,  profea.first_order_date as firstorderdate             ");
        sql.append("      FROM parts_ropq_parameter profea                             ");
        sql.append("     WHERE profea.site_id = :siteId                             ");
//        sql.append("       and profea.product_id is not null                         ");

        param.put("siteId", siteId);

        PartsBatchBOList = super.queryForList(sql.toString(), param, PartsBatchBO.class);

        for (PartsBatchBO PartsBatchBOInfo : PartsBatchBOList) {
            returnMap.put(PartsBatchBOInfo.getProductid().toString()+CommonConstants.CHAR_COLON+PartsBatchBOInfo.getFacilityid().toString()
                        , PartsBatchBOInfo.getFirstorderdate());
        }

        return returnMap;
    }

    // in user
    @Override
    public List<PartsBatchBO> getAllProductId(String siteId) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        sql.append("    SELECT  product_id  as  productid                            ");
        sql.append("         ,  registration_date as registrationdate                ");
        sql.append("         ,  sales_status_type as salesstatustype                 ");
        sql.append("         ,  site_id as siteId                                    ");
        sql.append("      FROM mst_product                                           ");
        sql.append("     WHERE site_id = :siteId                                     ");
        sql.append("       AND product_classification = :partsClassification         ");

        param.put("siteId", siteId);
        param.put("partsClassification", ProductClsType.PART.getCodeDbid());

        return super.queryForList(sql.toString(), param, PartsBatchBO.class);
    }

    // in user
    @Override
    public Map<Long, Long> getReplaceProductIdMap(String siteId) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<PartsBatchBO> partsBatchBOList = new ArrayList<PartsBatchBO>();
        Map<Long, Long> returnMap = new HashMap<Long, Long>();
        sql.append("    SELECT  from_product_id as fromproductid   ");
        sql.append("         ,  to_product_id as productid         ");
        sql.append("      FROM mst_product_relation                ");
        sql.append("     WHERE site_id = :siteId                   ");
        sql.append("       AND relation_type =:relationType        ");

        param.put("siteId", siteId);
        param.put("relationType", ProductRelationClass.SUPERSEDING);
        partsBatchBOList = super.queryForList(sql.toString(), param, PartsBatchBO.class);

        for (PartsBatchBO partsBatchBOInfo : partsBatchBOList) {
            returnMap.put(partsBatchBOInfo.getProductid(), partsBatchBOInfo.getFromproductid());
        }

        return returnMap;
    }

    @Override
    public List<BSProductStockStatusBO> getProductStockStatus(String siteId
                                                            ,String costType
                                                            ,String classificationId
                                                            ,Long facilityId
                                                            ,List<String> typeIds){

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<BSProductStockStatusBO> productStockStatusList = new ArrayList<BSProductStockStatusBO>();

        sql.append("    SELECT pss.facility_id as facilityid                            ");
        sql.append("          ,pss.product_id as productid                              ");
        sql.append("          ,pco.product_cd as productcd                              ");
        sql.append("          ,pco.sales_description as productnm                       ");
        sql.append("          ,sum(pss.quantity) as sumquantity                         ");
        sql.append("          ,pc.cost as cost                                          ");
        sql.append("     from product_stock_status as pss                               ");
        sql.append("     left join product_cost as pc                                   ");
        sql.append("       on pss.product_id = pc.product_id                            ");
        sql.append("      and pss.site_id = pc.site_id                                  ");
        sql.append("      and pc.cost_type = :costType                                  ");
        sql.append("     left join mst_product as pco                                   ");
        sql.append("       on pss.product_id = pco.product_id                          ");
        sql.append("      and pco.product_classification = :classificationId        ");
        sql.append("    where pss.site_id = :siteId                                     ");
        if (facilityId != null) {
            sql.append("          and pss.facility_id = :facilityId                          ");
        }
        sql.append("          and pss.product_stock_status_type in (:typeIds)           ");
        sql.append("          group by  pss.facility_id,pss.product_id,pco.product_cd,pco.sales_description,pc.cost          ");

        param.put("siteId", siteId);
        param.put("costType", costType);
        if (facilityId != null) {
            param.put("facilityId", facilityId);
        }
        param.put("classificationId", classificationId);
        param.put("typeIds", typeIds);

        productStockStatusList = super.queryForList(sql.toString(), param, BSProductStockStatusBO.class);

        return productStockStatusList;
    }


    @Override
    public List<BSProductStockStatusBO> getSerializdeProduct(String siteId
                                                            ,Long facilityId
                                                            ,List<Long> serializedProductIdS){

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<BSProductStockStatusBO> productStockStatusList = new ArrayList<BSProductStockStatusBO>();

        sql.append("    SELECT serial.facility_id as facilityid                             ");
        sql.append("          ,serial.product_id as productid                               ");
        sql.append("          ,pco.product_cd as productcd                                  ");
        sql.append("          ,pco.sales_description as productnm                           ");
        sql.append("          ,count(serial.serialized_product_id) as sumquantity           ");
        sql.append("          ,0 as cost                                                    ");
        sql.append("     from serialized_product as serial                                  ");
        sql.append("     left join mst_product as pco                                       ");
        sql.append("       on serial.product_id = pco.product_id                            ");
        sql.append("    where serial.site_id = :siteId                                      ");
        if (facilityId != null) {
            sql.append("          and serial.facility_id = :facilityId                          ");
        }
        sql.append("          and serial.serialized_product_id in (:serializedProductIdS)  ");
        sql.append("          group by  serial.facility_id,serial.product_id,pco.product_cd,pco.sales_description          ");

        param.put("siteId", siteId);
        if (facilityId != null) {
            param.put("facilityId", facilityId);
        }
        param.put("serializedProductIdS", serializedProductIdS);

        productStockStatusList = super.queryForList(sql.toString(), param, BSProductStockStatusBO.class);

        return productStockStatusList;
    }

    @Override
    public List<Long> getSerializedProductId(String siteId
                                            ,List<String> statusList){

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<BSProductStockStatusBO> productStockStatusList = new ArrayList<BSProductStockStatusBO>();
        List<Long> serializedProIds = new ArrayList<Long>();
        sql.append("    SELECT serialized_product_id  serializedproid                           ");
        sql.append("     from serialized_product as pss                               ");
        sql.append("    where site_id = :siteId                                     ");
        sql.append("          and stock_status in (:statusList)           ");

        param.put("siteId", siteId);
        param.put("statusList", statusList);

        productStockStatusList = super.queryForList(sql.toString(), param, BSProductStockStatusBO.class);

        for (BSProductStockStatusBO productStockStatus : productStockStatusList) {
            serializedProIds.add(productStockStatus.getSerializedproid());
        }

        return serializedProIds;
    }

    @Override
    public List<ProductOrderResultSummaryBatchBO> getSalesResultProductListSP(String siteId
                                                                             ,String collectDate){

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<ProductOrderResultSummaryBatchBO> resultProductListSp = new ArrayList<ProductOrderResultSummaryBatchBO>();

        sql.append("    SELECT oitm.allocated_product_id as productid                 ");
        sql.append("          , oi.facility_id as deliveryfacility                    ");
//        sql.append("          , oi.order_date as targetyear                     ");
        sql.append("          , sum(oitm.order_qty) as originalquantity               ");
//        sql.append("          , oitm.sales_order_item_id as orderitemid         ");
        sql.append("     from sales_order as oi                                       ");
        sql.append("     left join sales_order_item as oitm                           ");
        sql.append("       on oi.sales_order_id = oitm.sales_order_id                 ");
        sql.append("    where oi.site_id   = :siteId                                  ");
        sql.append("          AND oi.order_date = :collectDate                        ");
        sql.append("          AND oi.product_classification = :productClassification  ");
        sql.append("          AND oi.order_source_type = :ordersourcetype             ");
        sql.append("          AND oi.demand_exception_flag  != 'Y'                    ");
        sql.append("     GROUP BY productid,deliveryfacility                          ");

        param.put("siteId", siteId);
        param.put("collectDate", collectDate);
        param.put("productClassification", ProductClsType.PART.getCodeDbid());
        param.put("ordersourcetype", ProductClsType.PART.getCodeDbid());

        resultProductListSp = super.queryForList(sql.toString(), param, ProductOrderResultSummaryBatchBO.class);

        return resultProductListSp;
    }

    @Override
    public List<ProductOrderResultSummaryBatchBO> getSalesResultProductListSV(String siteId
                                                                             ,String collectDate){

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        List<ProductOrderResultSummaryBatchBO> resultProductListSp = new ArrayList<ProductOrderResultSummaryBatchBO>();

        sql.append("    SELECT oitm.allocated_product_id as productid           ");
        sql.append("          , svoi.facility_id as deliveryfacility                    ");
//        sql.append("          , svoi.settle_date as targetyear                     ");
        sql.append("          , sum(oitm.order_qty) as originalquantity             ");
//        sql.append("          , oitm.sales_order_item_id as orderitemid         ");
        sql.append("     from service_order as svoi                                 ");
        sql.append("     inner join sales_order as oi                      ");
        sql.append("       on svoi.service_order_id = oi.related_sv_order_id                ");
        sql.append("     left join sales_order_item as oitm                      ");
        sql.append("       on oi.sales_order_id = oitm.sales_order_id                ");
        sql.append("    where svoi.site_id   = :siteId                           ");
        sql.append("          AND svoi.settle_date = :collectDate                 ");
        sql.append("          AND oi.product_classification = :productClassification                 ");
        sql.append("          AND oi.order_source_type = :ordersourcetype                 ");
        sql.append("          AND svoi.service_category_id != :servicecategoryid             ");
        sql.append("     GROUP BY productid,deliveryfacility             ");

        param.put("siteId", siteId);
        param.put("collectDate", collectDate);
        param.put("productClassification", ProductClsType.PART.getCodeDbid());
        param.put("ordersourcetype", ProductClsType.SERVICE.getCodeDbid());
        param.put("servicecategoryid", ServiceCategory.CLAIM.getCodeDbid());

        resultProductListSp = super.queryForList(sql.toString(), param, ProductOrderResultSummaryBatchBO.class);

        return resultProductListSp;
    }

    private String longToStr(Long value) {

        String returnValueString = "";
        if (value != null) {

            returnValueString = value.toString();
        }

        return returnValueString;
    }
}
