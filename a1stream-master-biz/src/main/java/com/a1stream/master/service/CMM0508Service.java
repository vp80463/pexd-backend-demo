package com.a1stream.master.service;
import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.domain.entity.MstFacility;
import com.a1stream.domain.entity.SeasonIndexBatch;
import com.a1stream.domain.entity.SeasonIndexManual;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstProductCategoryRepository;
import com.a1stream.domain.repository.SeasonIndexBatchRepository;
import com.a1stream.domain.repository.SeasonIndexManualRepository;
import com.a1stream.domain.vo.MstProductCategoryVO;
import com.a1stream.domain.vo.SeasonIndexBatchVO;
import com.a1stream.domain.vo.SeasonIndexManualVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class CMM0508Service {

    @Resource
    private SeasonIndexManualRepository seasonIndexManualRepository;

    @Resource
    private SeasonIndexBatchRepository seasonIndexBatchRepository;

    @Resource
    private MstProductCategoryRepository mstProductCategoryRepository;

    @Resource
    private MstFacilityRepository facilityRepository;

    public List<MstProductCategoryVO> findProductCategoryLargeList() {

        return BeanMapUtils.mapListTo(mstProductCategoryRepository.findBySiteIdAndCategoryType(CommonConstants.CHAR_DEFAULT_SITE_ID,PJConstants.PartsCategory.LARGEGROUP),MstProductCategoryVO.class);
    }

    public List<SeasonIndexManualVO> findSeasonIndexManualList(String siteId,List<Long> productCategoryIdList,Long facilityId) {

        return BeanMapUtils.mapListTo(seasonIndexManualRepository.findBySiteIdAndProductCategoryIdInAndFacilityId(siteId,productCategoryIdList,facilityId),SeasonIndexManualVO.class);
    }

    public List<SeasonIndexBatchVO> findSeasonIndexBatchList(String siteId, List<Long> productCategoryIdList,Long facilityId) {

        return BeanMapUtils.mapListTo(seasonIndexBatchRepository.findBySiteIdAndProductCategoryIdInAndFacilityId(siteId,productCategoryIdList,facilityId),SeasonIndexBatchVO.class);
    }

    public MstProductCategoryVO findProductCategoryLargeById(Long productCategoryId) {

        return BeanMapUtils.mapTo(mstProductCategoryRepository.findBySiteIdAndProductCategoryIdAndCategoryType(CommonConstants.CHAR_DEFAULT_SITE_ID,productCategoryId,PJConstants.PartsCategory.LARGEGROUP),MstProductCategoryVO.class);
    }
    public SeasonIndexBatchVO findSeasonIndexBatch(String siteId, Long productCategoryId, Long pointId) {

        return BeanMapUtils.mapTo(seasonIndexBatchRepository.findBySiteIdAndProductCategoryIdAndFacilityId(siteId,productCategoryId,pointId),SeasonIndexBatchVO.class);
    }
    public SeasonIndexManualVO findSeasonIndexManual(String siteId, Long productCategoryId, Long pointId) {

        return BeanMapUtils.mapTo(seasonIndexManualRepository.findBySiteIdAndProductCategoryIdAndFacilityId(siteId, productCategoryId, pointId),SeasonIndexManualVO.class);
    }

    public void saveManualAndDeleteBatch(SeasonIndexManual seasonIndexManualNew, SeasonIndexBatch seasonIndexBatch) {

        seasonIndexManualRepository.save(seasonIndexManualNew);
        seasonIndexBatchRepository.delete(seasonIndexBatch);
    }

    public void saveOrUpdateManual(SeasonIndexManual seasonIndexManual) {

        seasonIndexManualRepository.save(seasonIndexManual);
    }
    public void deleteSeasonIndexBatch(SeasonIndexBatch seasonIndexBatch) {

        seasonIndexBatchRepository.delete(seasonIndexBatch);
    }

    public void deleteSeasonIndexManual(SeasonIndexManual seasonIndexManual) {

        seasonIndexManualRepository.delete(seasonIndexManual);
    }

    /**
     * @param pointId
     * @return
     */
    public MstFacility findFacility(Long pointId) {

        return facilityRepository.findByFacilityId(pointId);
    }
}

