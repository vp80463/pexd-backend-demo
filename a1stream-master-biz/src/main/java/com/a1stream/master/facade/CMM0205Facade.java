package com.a1stream.master.facade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.FacilityRelationType;
import com.a1stream.common.constants.MstCodeConstants.FacilityRoleType;
import com.a1stream.common.model.BaseTableData;
import com.a1stream.domain.bo.common.FacilityBO;
import com.a1stream.domain.bo.master.CMM020501BO;
import com.a1stream.domain.bo.master.CMM020502BO;
import com.a1stream.domain.form.master.CMM020502Form;
import com.a1stream.domain.vo.MstFacilityRelationVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.master.service.CMM0205Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Component
public class CMM0205Facade {

    @Resource
    private CMM0205Service cmm0205Service;

    public List<CMM020501BO> findPointList(String siteId) {

        return cmm0205Service.findPointList(siteId);
    }

    public CMM020502BO getPointDetail(String siteId, CMM020502Form model) {

        return cmm0205Service.getPointDetail(siteId, model);
    }

    public void updatePoint(String siteId, CMM020502Form model) {

        // 更新前验证
        this.validateUpdatePointInfo(model);

        // 待更新的 mstFacility
        MstFacilityVO mstFacility = this.buildMstFacility(model);

        // 待更新的 deliveryPoint table
        List<MstFacilityRelationVO> deleteList = new ArrayList<>();
        List<MstFacilityRelationVO> insertList = new ArrayList<>();
        this.getDeleteAndInsertList(siteId
                                  , model
                                  , deleteList
                                  , insertList);

        cmm0205Service.savePoint(mstFacility
                               , deleteList
                               , insertList);
    }

    /**
     * @param model
     */
    private void validateUpdatePointInfo(CMM020502Form model) {
        // delivery point grid ，point 存在性校验

        // 获取Grid变更内容 Insert / Remove
        BaseTableData<FacilityBO> changeTableObj = model.getDeliveryPointTable();
        List<FacilityBO> tableInsertList = changeTableObj.getInsertRecords();

        for (FacilityBO tableInsertModel : tableInsertList) {

            if( StringUtils.isNotEmpty(tableInsertModel.getFacilityCd()) && tableInsertModel.getFacilityId() == null) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303"
                                               , new String[] {CodedMessageUtils.getMessage("label.point")
                                               , tableInsertModel.getFacilityCd()
                                               , CodedMessageUtils.getMessage("title.pointInfoList_01")}));
            }

        }

    }

    /**
     * @param model
     * @param deleteList
     * @param insertList
     */
    private void getDeleteAndInsertList(String siteId
                                      , CMM020502Form model
                                      , List<MstFacilityRelationVO> deleteList
                                      , List<MstFacilityRelationVO> insertList) {
        List<MstFacilityRelationVO> dbFacilityRelationList = cmm0205Service.getDeliveryPointList(siteId,model.getFacilityId());

        Map<Long, MstFacilityRelationVO> dbFacilityRelationMap = dbFacilityRelationList.stream()
                                                                                       .collect(Collectors.toMap(MstFacilityRelationVO::getToFacilityId
                                                                                                               , mstFacilityRelationVO -> mstFacilityRelationVO
                                                                                                               , (c1,c2)-> c1));

        // 获取Grid变更内容 Insert / Remove
        BaseTableData<FacilityBO> changeTableObj = model.getDeliveryPointTable();
        List<FacilityBO> tableDeleteList = changeTableObj.getRemoveRecords();
        List<FacilityBO> tableInsertList = changeTableObj.getInsertRecords();

        Long toFacilityId = null;
        // 获取deleteList
        for (FacilityBO tableDeleteModel : tableDeleteList) {
            toFacilityId = tableDeleteModel.getFacilityId();
            if (!dbFacilityRelationMap.containsKey(toFacilityId)) continue;

            deleteList.add(dbFacilityRelationMap.get(toFacilityId));
            dbFacilityRelationMap.remove(toFacilityId);
        }
        // 获取insertList
        Set<Long> toFacilityIdSet = new HashSet<>();
        for (FacilityBO tableInsertModel : tableInsertList) {
            toFacilityId = tableInsertModel.getFacilityId();
            // 数据跳过处理 ： 明细输入facility未在Db存在
            if(toFacilityId == null) continue;
            // 数据跳过处理 ： 明细已在DB存在
            if(dbFacilityRelationMap.containsKey(toFacilityId)) continue;
            // 数据跳过处理 ： 画面明细新增数据重复
            if(toFacilityIdSet.contains(toFacilityId)) continue;
            MstFacilityRelationVO mstFacilityRelationVO = this.buildMstFacilityRelation(siteId
                                                                                      , model.getFacilityId()
                                                                                      , toFacilityId);

            insertList.add(mstFacilityRelationVO);
            toFacilityIdSet.add(toFacilityId);
        }

        return;
    }

    /**
     * @param model
     * @return
     */
    private MstFacilityRelationVO buildMstFacilityRelation(String siteId
                                                         , Long formFacilityId
                                                         , Long toFacilityId) {

        MstFacilityRelationVO mstFacilityRelationVO = new MstFacilityRelationVO();

        mstFacilityRelationVO.setSiteId(siteId);
        mstFacilityRelationVO.setFromFacilityId(formFacilityId);
        mstFacilityRelationVO.setToFacilityId(toFacilityId);
        mstFacilityRelationVO.setFacilityRelationTypeId(FacilityRelationType.KEY_DELIVERYINCHARGE);

        return mstFacilityRelationVO;
    }

    /**
     * @param facilityId
     * @return
     */
    private MstFacilityVO buildMstFacility(CMM020502Form model) {

        MstFacilityVO mstFacility = cmm0205Service.getPointInfo(model.getFacilityId());

        if (mstFacility == null) return mstFacility;

        mstFacility.setFacilityNm(model.getFacilityNm());
        mstFacility.setProvinceId(model.getProvinceId());
        mstFacility.setProvinceNm(model.getProvinceNm());
        mstFacility.setCityId(model.getCityId());
        mstFacility.setCityNm(model.getCityNm());
        mstFacility.setAddress1(model.getAddress1());
        mstFacility.setAddress2(model.getAddress2());
        mstFacility.setPostCode(model.getPostCode());
        mstFacility.setContactTel(model.getContactTel());
        mstFacility.setContactFax(model.getContactFax());
        mstFacility.setFacilityRoleType(StringUtils.equals(CommonConstants.CHAR_Y
                                                         , model.getShop())
                                      ? FacilityRoleType.KEY_SHOP
                                      : CommonConstants.CHAR_BLANK);

        return mstFacility;
    }
}
