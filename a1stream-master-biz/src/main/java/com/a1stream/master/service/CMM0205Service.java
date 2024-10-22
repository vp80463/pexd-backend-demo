package com.a1stream.master.service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.FacilityRelationType;
import com.a1stream.common.constants.MstCodeConstants.FacilityRoleType;
import com.a1stream.domain.bo.common.FacilityBO;
import com.a1stream.domain.bo.master.CMM020501BO;
import com.a1stream.domain.bo.master.CMM020502BO;
import com.a1stream.domain.entity.MstFacility;
import com.a1stream.domain.entity.MstFacilityRelation;
import com.a1stream.domain.form.master.CMM020502Form;
import com.a1stream.domain.repository.MstFacilityRelationRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.vo.MstFacilityRelationVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CMM0205Service {

    @Resource
    private MstFacilityRepository mstFacilityRepo;

    @Resource
    private MstFacilityRelationRepository mstFacilityRelationRepo;

    public List<CMM020501BO> findPointList(String siteId) {
        return mstFacilityRepo.findPointListBySiteId(siteId);
    }

    public CMM020502BO getPointDetail(String siteId, CMM020502Form model) {

        Long facilityId = model.getFacilityId();

        CMM020502BO cmm020502BO = BeanMapUtils.mapTo(mstFacilityRepo.findByFacilityId(facilityId), CMM020502BO.class);

        if(StringUtils.equals(FacilityRoleType.KEY_SHOP, cmm020502BO.getFacilityRoleType())) {
            cmm020502BO.setShop(CommonConstants.CHAR_Y);
        }

        if(StringUtils.equals(CommonConstants.CHAR_Y, cmm020502BO.getSpPurchaseFlag())) {
            cmm020502BO.setWsDealerSign(CommonConstants.CHAR_Y);
        }

        List<FacilityBO> deliveryPointList = mstFacilityRelationRepo.findFacilityListByRelationType(siteId
                                                                                                  , facilityId
                                                                                                  , FacilityRelationType.KEY_DELIVERYINCHARGE);

        cmm020502BO.setDeliveryPointList(deliveryPointList);
        return cmm020502BO;
    }

    public MstFacilityVO getPointInfo(Long facilityId) {

        return BeanMapUtils.mapTo(mstFacilityRepo.findByFacilityId(facilityId), MstFacilityVO.class);
    }

    public List<MstFacilityRelationVO> getDeliveryPointList(String siteId
                                                          , Long facilityId) {

        return BeanMapUtils.mapListTo(mstFacilityRelationRepo.findBySiteIdAndFromFacilityId(siteId, facilityId), MstFacilityRelationVO.class);
    }

    public void savePoint(MstFacilityVO mstFacility
                        , List<MstFacilityRelationVO> deleteList
                        , List<MstFacilityRelationVO> insertList) {

        mstFacilityRepo.saveWithVersionCheck(BeanMapUtils.mapTo(mstFacility, MstFacility.class));
        mstFacilityRelationRepo.deleteAllInBatch(BeanMapUtils.mapListTo(deleteList, MstFacilityRelation.class));
        mstFacilityRelationRepo.saveInBatch(BeanMapUtils.mapListTo(insertList, MstFacilityRelation.class));

        return;
    }
}
