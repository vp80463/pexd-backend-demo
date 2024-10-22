/**********************************0********************************************/
/* SYSTEM     : Stream                                                        */
/*                                                                            */
/* SUBSYSTEM  : Xm03                                                          */
/******************************************************************************/
package com.a1stream.domain.batchdao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.batchdao.BaseJdbcDao;
import com.a1stream.domain.batchdao.BatchUpdateJdbcDao;
@Repository
public class BatchUpdateJdbcDaoImpl extends BaseJdbcDao implements BatchUpdateJdbcDao {

	final static String SQL_INS_PRO_FAC_FEA =
                                            " INSERT INTO " +
                                                "         parts_ropq_monthly " +
                                                "        (parts_ropq_monthly_id " +
                                                "       , site_id " +
                                                "       , facility_id " +
                                                "       , product_id " +
                                                "       , ropq_type " +
                                                "       , string_value " +
                                                "       , last_updated_by " +
                                                "       , last_updated " +
                                                "       , created_by " +
                                                "       , date_created " +
                                                "       , update_count " +
                                                "       , update_program) " +
                                                " VALUES (? " +
                                                "       , ? " +
                                                "       , ? " +
                                                "       , ? " +
                                                "       , ? " +
                                                "       , ? " +
                                                "       , ? " +
                                                "       , current_timestamp " +
                                                "       , ? " +
                                                "       , current_timestamp " +
                                                "       , 0 " +
                                                "       , ? )";
    //In use
    @Override
    public void insertPartsRopqMonthlyData(List<Object[]> batchArgs) {


        this.batchUpdate(SQL_INS_PRO_FAC_FEA, batchArgs);
	}

    //In use
    final static  String SQL_INS_DEMA_FC_TMP        =
                                                      " INSERT INTO " +
                                                      "         demand_forecast_temp " +
                                                      "        (demand_forecast_temp_id " +
                                                      "       , site_id " +
                                                      "       , facility_id " +
                                                      "       , product_id " +
                                                      "       , n1_quantity " +
                                                      "       , n2_quantity " +
                                                      "       , n3_quantity " +
                                                      "       , n4_quantity " +
                                                      "       , n5_quantity " +
                                                      "       , n6_quantity " +
                                                      "       , n7_quantity " +
                                                      "       , n8_quantity " +
                                                      "       , n9_quantity " +
                                                      "       , n10_quantity " +
                                                      "       , n11_quantity " +
                                                      "       , n12_quantity " +
                                                      "       , register_date " +
                                                      "       , first_order_date " +
                                                      "       , j1 " +
                                                      "       , j2 ) " +
                                                      " VALUES (? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? )";

    @Override
    public void insertDemandForecastTempData(List<Object[]> batchArgs) {

        this.batchUpdate(SQL_INS_DEMA_FC_TMP, batchArgs);
    }

    // insert ProductAbc
    final static String SQL_INS_PROD_ABC = "INSERT INTO product_abc_info ( "
                                        + " product_abc_id ,"
                                        + " site_id, "
                                        + " facility_id , "
                                        + " product_id , "
                                        + " product_category_id, "
                                        + " abc_definition_id ,"
                                        + " abc_type ,"
                                        + " last_updated_by, "
                                        + " last_updated, "
                                        + " created_by, "
                                        + " date_created, "
                                        + " update_count, "
                                        + " update_program )"
                                        + " VALUES (? " +
                                        "         , ? " +
                                        "         , ? " +
                                        "         , ? " +
                                        "         , ? " +
                                        "         , ? " +
                                        "         , ? " +
                                        "         , ? " +
                                        "         , current_timestamp " +
                                        "         , ? " +
                                        "         , current_timestamp " +
                                        "         , 0 " +
                                        "         , ? )";
    @Override
    public void insertProductAbc (List<Object[]> batchArgs) {

        this.batchUpdate(SQL_INS_PROD_ABC, batchArgs);
    }

    //In use
    final static String deletePartsRopqMonthlyDataSql =
				        " DELETE " +
				        "   FROM  parts_ropq_monthly " +
				        "  WHERE  site_id = ? " +
				        "    and  ropq_type = ? ";

    //In use
    final static String deletePartsRopqMonthlyDataAndFacilitySql =
                        " DELETE " +
                        "   FROM  parts_ropq_monthly " +
                        "  WHERE  site_id = ? " +
                        "    and  facility_id = ? " +
                        "    and  ropq_type = ? ";

	//In use
	@Override
    public void deletePartsRopqMonthlyData(String siteId, String productFeatureCategoryId) {

		this.update(deletePartsRopqMonthlyDataSql, siteId, productFeatureCategoryId);
	}

	@Override
    public void deletePartsRopqMonthlyDataAndFacility(String siteId, Long facility, String productFeatureCategoryId ) {

        this.update(deletePartsRopqMonthlyDataAndFacilitySql, siteId, facility, productFeatureCategoryId);
    }

    //In use
    final static String SQL_DEL_TMP_PRD_FACI_FEA =
                                                  " DELETE " +
                                                  " FROM   tmp_product_facility_feature " +
                                                  " WHERE  site_id = ? ";

  	@Override
    public void deleteTempProductFacilityFeatureData(String siteId) {

  		this.update(SQL_DEL_TMP_PRD_FACI_FEA, siteId);
  	}

    //In use
    final static String SQL_DEL_SEA_IDX =
                                      	" DELETE " +
                                      	" FROM   season_index_batch " +
                                      	" WHERE  site_id = ? " +
                                      	" and manual_flag = 'N'";
    @Override
    public void deleteSeasonIndexInfo(String siteId) {
        this.update(SQL_DEL_SEA_IDX, siteId);
    }

    //In use
    final static String SQL_DEL_SEA_IDX_FAC =
                                        " DELETE " +
                                        " FROM   season_index_batch " +
                                        " WHERE  site_id = ? " +
                                        " and facility_id = ? " +
                                        " and manual_flag = 'N'";
    @Override
    public void deleteSeasonIndexInfoForFacility(String siteId,Long facilityId) {
        this.update(SQL_DEL_SEA_IDX_FAC, siteId, facilityId);
    }

    //In use
    final static String SQL_INS_SEAS_IDX = "INSERT INTO season_index_batch ( "
                                              + " season_index_batch_id ,"
                                              + " site_id ,"
                                              + " facility_id ,"
                                              + " product_category_id , "
                                              + " n_index , "
                                              + " n_1_index , "
                                              + " n_2_index , "
                                              + " n_3_index , "
                                              + " n_4_index , "
                                              + " n_5_index , "
                                              + " n_6_index , "
                                              + " n_7_index , "
                                              + " n_8_index , "
                                              + " n_9_index , "
                                              + " n_10_index , "
                                              + " n_11_index , "
                                              + " last_updated_by, "
                                              + " last_updated, "
                                              + " created_by, "
                                              + " date_created, "
                                              + " update_count, "
                                              + " update_program )"
                                              + " VALUES (? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , current_timestamp " +
                                              "       , ? " +
                                              "       , current_timestamp " +
                                              "       , 0 " +
                                              "       , ? )";

    @Override
    public void insertSeasonIndexInfo (List<Object[]> batchArgs) {
        this.batchUpdate(SQL_INS_SEAS_IDX, batchArgs);
    }

  //In use
    final static String SQL_INS_INV_TRANSACTION = "INSERT INTO inventory_transaction ( "
                                              + " inventory_transaction_id ,"
                                              + " site_id ,"
                                              + " physical_transaction_date ,"
                                              + " physical_transaction_time , "
                                              + " product_id , "
                                              + " product_cd , "
                                              + " product_nm , "
                                              + " target_facility_id , "
                                              + " current_qty , "
                                              + " current_average_cost , "
                                              + " inventory_transaction_type , "
                                              + " inventory_transaction_nm , "
                                              + " product_classification,"
                                              + " last_updated_by, "
                                              + " last_updated, "
                                              + " created_by, "
                                              + " date_created, "
                                              + " update_count, "
                                              + " update_program )"
                                              + " VALUES (? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , ? " +
                                              "       , current_timestamp " +
                                              "       , ? " +
                                              "       , current_timestamp " +
                                              "       , 0 " +
                                              "       , ? )";

    @Override
    public void insertInvTransaction (List<Object[]> batchArgs) {
        this.batchUpdate(SQL_INS_INV_TRANSACTION, batchArgs);
    }

    //In use
    final static String SQL_INS_DFC =
                      " INSERT INTO " +
                          "         demand_forecast " +
                          "        (demand_forecast_id " +
                          "       , site_id " +
                          "       , facility_id " +
                          "       , target_month " +
                          "       , to_product_id " +
                          "       , product_id " +
                          "       , product_category_id " +
                          "       , demand_qty  " +
                          "       , last_updated_by " +
                          "       , last_updated " +
                          "       , created_by " +
                          "       , date_created " +
                          "       , update_count " +
                          "       , update_program) " +
                          " VALUES (? " +
                          "       , ? " +
                          "       , ? " +
                          "       , ? " +
                          "       , ? " +
                          "       , ? " +
                          "       , ? " +
                          "       , ? " +
                          "       , ? " +
                          "       , current_timestamp " +
                          "       , ? " +
                          "       , current_timestamp " +
                          "       , 0 " +
                          "       , ? )";

    @Override
    public void insertDemandForecast(List<Object[]> batchArgs){

        this.batchUpdate(SQL_INS_DFC, batchArgs);
    }

    //In use
    final static String SQL_INS_REORDER_GUIDELINE = "INSERT INTO reorder_guideline ( "
                                                      + " reorder_guideline_id ,"
                                                      + " site_id ,"
                                                      + " facility_id , "
                                                      + " product_id , "
                                                      + " reorder_point , "
                                                      + " reorder_qty , "
                                                      + " rop_roq_manual_flag , "
                                                      + " last_updated_by, "
                                                      + " last_updated, "
                                                      + " created_by, "
                                                      + " date_created, "
                                                      + " update_count ,"
                                                      + " update_program )"
                                                      + " VALUES (? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , ? " +
                                                      "       , current_timestamp " +
                                                      "       , ? " +
                                                      "       , current_timestamp " +
                                                      "       , 0 " +
                                                      "       , ? )";
    /* (non-Javadoc)
     * @see jp.co.yamaha_motor.xm03.nonserializeditem.batch.dao.IRopRoqBatchDao#insertReorderGuideLineInfo(java.util.List)
     */
    @Override
    public void insertReorderGuideLineInfo (List<Object[]> batchArgs) {
        this.batchUpdate(SQL_INS_REORDER_GUIDELINE, batchArgs);
    }

    @Override
    public void insertSpecParameterValue(String siteId
                                        , Long systemParameterId
                                        , String sysParamTypeId
                                        , String parameterValue
                                        , String updateProgram) {

        String sql =
        " INSERT INTO" +
        "         system_parameter " +
        "        (system_parameter_id " +
        "       , site_id " +
        "       , parameter_value " +
        "       , system_parameter_type_id " +
        "       , last_updated_by " +
        "       , last_updated " +
        "       , created_by " +
        "       , date_created " +
        "       , update_count " +
        "       , update_program) " +
        " VALUES ('" + systemParameterId + "' " +
        "       , '" + siteId + "' " +
        "       , '" + parameterValue + "' " +
        "       , '" + sysParamTypeId + "' " +
        "       , '" + updateProgram + "' " +
        "       , current_timestamp " +
        "       , '" + updateProgram + "' " +
        "       , current_timestamp " +
        "       , 0 " +
        "       , '" + updateProgram + "') ";

        this.update(sql);
    }

    // In use
    final static String SQL_UPDATESYSTEMPARAMETER =
                " UPDATE  system_parameter " +
                " SET     parameter_value = ?" +
                "       , last_updated = current_timestamp " +
                "       , update_program = ? " +
                "       , update_count = update_count + 1 " +
                " WHERE   system_parameter_type_id = ?" +
                " AND     site_id = ?";

    @Override
    public int setSpecParameterValue(String siteId
                                   , String sysParamTypeId
                                   , String parameterValue) {

        int count = this.update(SQL_UPDATESYSTEMPARAMETER, parameterValue,CommonConstants.CHAR_BATCH_USER_ID, sysParamTypeId, siteId);

        return count;
    }

    final static String SQL_UPDATESITEMST =
                            " UPDATE  cmm_site_master " +
                            " SET     active_flag = ?" +
                            "       , last_updated = current_timestamp " +
                            "       , update_program = ? " +
                            "       , update_count = update_count + 1 " +
                            " WHERE   site_id = ?";

    @Override
    public void updateSiteMst(String siteId) {

        this.update(SQL_UPDATESITEMST,CommonConstants.CHAR_Y , CommonConstants.CHAR_BATCH_USER_ID ,siteId);
    }

 // In use
    final static String SQL_UPDATEFACILITY =
                " UPDATE  mst_facility " +
                " SET     new_flag = ?" +
                "       , last_updated = current_timestamp " +
                "       , update_program = ? " +
                "       , update_count = update_count + 1 " +
                " WHERE   site_id = ?" +
                " AND     facility_id = ?";

    @Override
    public void updateNewPointSign(String siteId
                                 , Long facilityId) {

        this.update(SQL_UPDATEFACILITY,CommonConstants.CHAR_N , CommonConstants.CHAR_BATCH_USER_ID ,siteId, facilityId);
    }


    @Override
    public void insertPartsRopqParameter(List<Object[]> batchArgs) {

        String sql =
        " INSERT INTO" +
        "         parts_ropq_parameter " +
        "        (parts_ropq_parameter_id " +
        "       , site_id " +
        "       , facility_id " +
        "       , product_id " +
        "       , first_order_date " +
        "       , last_updated_by " +
        "       , last_updated " +
        "       , created_by " +
        "       , date_created " +
        "       , update_count " +
        "       , update_program) " +
        " VALUES (? " +
        "       , ? " +
        "       , ? " +
        "       , ? " +
        "       , ? " +
        "       , ? " +
        "       , current_timestamp " +
        "       , ? " +
        "       , current_timestamp " +
        "       , 0 " +
        "       , ? )";

        this.batchUpdate(sql,batchArgs);
    }

    //In use
    final static String SQL_UPDATEROPQPARAMETER =
                " UPDATE  parts_ropq_parameter " +
                " SET     first_order_date = ? " +
                "       , last_updated = current_timestamp " +
                "       , update_program = ? " +
                " WHERE   product_id = ? " +
                " AND     facility_id = ? " +
                " AND     site_id = ? ";
    @Override
    public void updatePartsRopqParameter(List<Object[]> batchArgs) {
        this.batchUpdate(SQL_UPDATEROPQPARAMETER,batchArgs);
    }

    @Override
    public void insertProductOrderResultSummaryData(String collectMonth, List<Object[]> batchArgs) {

        String SQL_INS_ORDER_SUMMARY =
                " INSERT INTO " +
                "         product_order_result_summary " +
                "        (product_order_result_summary_id " +
                "       , site_id " +
                "       , target_year " +
                "       , facility_id " +
                "       , product_id " +
                "       , month"+collectMonth+"_quantity " +
                "       , last_updated_by " +
                "       , last_updated " +
                "       , created_by " +
                "       , date_created " +
                "       , update_count " +
                "       , update_program) " +
                " VALUES (? " +
                "       , ? " +
                "       , ? " +
                "       , ? " +
                "       , ? " +
                "       , ? " +
                "       , ? " +
                "       , current_timestamp " +
                "       , ? " +
                "       , current_timestamp " +
                "       , 0 " +
                "       , ? )";

        this.batchUpdate(SQL_INS_ORDER_SUMMARY, batchArgs);
    }

    //In use
    @Override
    public void updateProductOrderResultSummaryData(String collectMonth, List<Object[]> batchArgs) {

        String sql =
            " UPDATE  product_order_result_summary " +
            " SET     month" + collectMonth + "_quantity = " +
            "         month" + collectMonth + "_quantity + ? " +
            "       , last_updated = current_timestamp " +
            "       , update_program = ? " +
            " WHERE   product_order_result_summary_id = ? ";

        this.batchUpdate(sql, batchArgs);
    }

    final static String SQL_DEMANDFORECASE = " delete from demand_forecast where site_id = ? " ;
    @Override
    public void deleteTableDemandforecast(String siteId) {

        this.update(SQL_DEMANDFORECASE, siteId);
    }

    final static String SQL_DEMANDFORECASEFACILITY = " delete from demand_forecast where site_id = ? and facility_id = ? " ;
    @Override
    public void deleteTableDemandforecastFacility(String siteId,Long facilityId) {

        this.update(SQL_DEMANDFORECASEFACILITY, siteId, facilityId);
    }

    final static String SQL_PRODUCTFACILITY = " delete from parts_ropq_monthly where site_id = ? ";
    @Override
    public void deleteTablePartsRopqMonthly(String siteId) {

        this.update(SQL_PRODUCTFACILITY, siteId);
    }

    final static String SQL_PRODUCTABC = " delete from product_abc_info where site_id = ? " ;
    @Override
    public void deleteTableProductAbc(String siteId) {

        this.update(SQL_PRODUCTABC,siteId);
    }

    final static String SQL_PRODUCTABCBYFACILITY = " delete from product_abc_info where site_id = ? and facility_id = ?" ;
    @Override
    public void deleteTableProductAbcByFacility(String siteId,Long facilityId) {

        this.update(SQL_PRODUCTABCBYFACILITY,siteId,facilityId);
    }

    final static String SQL_REORDERGUIDELING = " delete from reorder_guideline where rop_roq_manual_flag = ? and site_id = ? " ;
    @Override
    public void deleteTableReorderGuideline(String siteId) {

        this.update(SQL_REORDERGUIDELING,CommonConstants.CHAR_N,siteId);
    }

    final static String SQL_REORDERGUIDELINGFACILITY = " delete from reorder_guideline where rop_roq_manual_flag = ? and site_id = ? and facility_id = ? " ;
    @Override
    public void deleteTableReorderGuidelineFacility(String siteId,Long facilityId) {

        this.update(SQL_REORDERGUIDELINGFACILITY,CommonConstants.CHAR_N,siteId,facilityId);
    }
}