package com.a1stream.ifs.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.vo.CmmServiceGroupItemVO;
import com.a1stream.domain.vo.CmmServiceGroupJobManhourVO;
import com.a1stream.domain.vo.CmmServiceGroupVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.ifs.bo.SvJobFlatRateBO;
import com.a1stream.ifs.bo.SvJobFlatRateDetailBO;
import com.a1stream.ifs.bo.SvJobFlatRateModelTypeBO;
import com.a1stream.ifs.bo.SvJobFlatRateParam;
import com.a1stream.ifs.service.SvJobFlatRateService;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SvJobFlatRateFacade {

    @Resource
    private SvJobFlatRateService jobFlatRateSer;

    public final static String SITE_666N = CommonConstants.CHAR_DEFAULT_SITE_ID;

    /**
     * IX_svJobFlatrate
     * MdmServiceProductManagerImpl importJobFlatrateData
     */
    public void importJobFlatRateData(List<SvJobFlatRateBO> dataList) {

        if(dataList ==null || dataList.isEmpty()) { return; }

        SvJobFlatRateParam param = new SvJobFlatRateParam();

        Set<String> jobCds = new HashSet<>();
        Set<String> groupCds = dataList.stream().map(SvJobFlatRateBO::getFlatrateGroupCode).collect(Collectors.toSet());
        for (SvJobFlatRateBO item : dataList) {
            List<SvJobFlatRateDetailBO> jobs = item.getJobs();
            jobCds.addAll(jobs.stream().map(SvJobFlatRateDetailBO::getJobCode).collect(Collectors.toSet()));
        }

        jobFlatRateSer.getMstProductMap(jobCds, param);
        jobFlatRateSer.prepareExistDataMap(groupCds, param);

        Map<String, MstProductVO> mstProductMap = param.getMstProductMap();
        Map<String, CmmServiceGroupVO> svGroupMap = param.getSvGroupMap();
        Map<Long, List<CmmServiceGroupItemVO>> svGroupItemMap = param.getSvGroupItemMap();
        Map<Long, List<CmmServiceGroupJobManhourVO>> svGroupJobMap = param.getSvGroupJobMap();

        // logNotExistData
        Set<String> dbJobCds = mstProductMap.keySet();
        if(jobCds.size() > dbJobCds.size()) {
            // 使用流和过滤操作移除A中与B相同的元素
            Set<String> notExists = jobCds.stream().filter(element -> !dbJobCds.contains(element)).collect(Collectors.toSet());
            if (!notExists.isEmpty()) {
                log.warn("Processing job flatrate imports, following CmmProduct'productCode is not found:" +
                        StringUtils.joinString(notExists.toArray(new String[notExists.size()]), ";", 10000, null));
            }
        }

        // processJobFlatRateDataList
        for (SvJobFlatRateBO data : dataList) {
            // cmm_service_group
            prepareGroupData(data, svGroupMap, param);
            Long groupId = param.getSvGroupId();

            List<CmmServiceGroupItemVO> groupItems = svGroupItemMap.containsKey(groupId)? svGroupItemMap.get(groupId) : new ArrayList<>();
            List<CmmServiceGroupJobManhourVO> groupJobs = svGroupJobMap.containsKey(groupId)? svGroupJobMap.get(groupId) : new ArrayList<>();

            Map<String, CmmServiceGroupItemVO> itemsMapByProdCd = groupItems.stream().collect(Collectors.toMap(CmmServiceGroupItemVO::getProdCd, Function.identity()));
            Map<Long, CmmServiceGroupJobManhourVO> jobsMapByJobId = groupJobs.stream().collect(Collectors.toMap(CmmServiceGroupJobManhourVO::getCmmServiceJobId, Function.identity()));

            // cmm_service_group_item
            for (SvJobFlatRateModelTypeBO modelType : data.getModelTypes()) {
                String modelTypeCd = modelType.getModelTypeCode();
                prepareItemData(param, groupItems, itemsMapByProdCd, modelTypeCd);
            }

            // cmm_service_group_job_manhour
            for (SvJobFlatRateDetailBO job : data.getJobs()) {
                String jobCd = job.getJobCode();
                if (mstProductMap.containsKey(jobCd)) {
                    Long jobId = mstProductMap.get(jobCd).getProductId();
                    prepareJobData(param, groupJobs, jobsMapByJobId, jobId, job.getFlatrate());
                }
            }

            // delete remain records exist in database
            if (!groupItems.isEmpty()) {
                Set<Long> delGroupItems = groupItems.stream().map(CmmServiceGroupItemVO::getServiceGroupItemId).collect(Collectors.toSet());
                param.setDelGroupItems(delGroupItems);
            }
            if (!groupJobs.isEmpty()) {
                Set<Long> delGroupJobs = groupJobs.stream().map(CmmServiceGroupJobManhourVO::getServiceGroupJobManhourId).collect(Collectors.toSet());
                param.setDelGroupJobs(delGroupJobs);
            }
        }

        jobFlatRateSer.maintainData(param);
    }

    private void prepareJobData(SvJobFlatRateParam param
                                , List<CmmServiceGroupJobManhourVO> groupJobs
                                , Map<Long, CmmServiceGroupJobManhourVO> groupJobsMap
                                , Long jobId
                                , BigDecimal manhour) {

        if (groupJobsMap.containsKey(jobId)) {
            groupJobs.remove(groupJobsMap.get(jobId));
        } else {
            CmmServiceGroupJobManhourVO item = new CmmServiceGroupJobManhourVO();

            item.setSiteId(SITE_666N);
            item.setCmmServiceJobId(jobId);
            item.setServiceGroupId(param.getSvGroupId());
            item.setManHours(manhour);

            param.getUpdSvGroupJobs().add(item);
        }
    }

    private void prepareItemData(SvJobFlatRateParam param
                                , List<CmmServiceGroupItemVO> groupItems
                                , Map<String, CmmServiceGroupItemVO> groupItemsMap
                                , String modelTypeCd) {

        if (groupItemsMap.containsKey(modelTypeCd)) {
            groupItems.remove(groupItemsMap.get(modelTypeCd));
        } else {
            CmmServiceGroupItemVO item = new CmmServiceGroupItemVO();

            item.setSiteId(SITE_666N);
            item.setProdCd(modelTypeCd);
            item.setServiceGroupId(param.getSvGroupId());

            param.getUpdSvGroupItems().add(item);
        }
    }

    private void prepareGroupData(SvJobFlatRateBO item, Map<String, CmmServiceGroupVO> svGroupMap, SvJobFlatRateParam param) {

        CmmServiceGroupVO group;
        String groupCd = item.getFlatrateGroupCode();
        if (svGroupMap.containsKey(groupCd)) {
            group = svGroupMap.get(groupCd);
        } else {
            group = CmmServiceGroupVO.create();

            group.setSiteId(SITE_666N);
            group.setServiceGroupCd(groupCd);
            group.setDescription(item.getFlatrateGroupName());
        }
        param.setSvGroupId(group.getServiceGroupId());
        param.getUpdSvGroups().add(group);
    }
}
