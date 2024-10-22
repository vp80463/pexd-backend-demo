package com.a1stream.service.facade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.RemindType;
import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.common.logic.ConstantsLogic;
import com.a1stream.common.logic.ServiceLogic;
import com.a1stream.common.model.ConstantsBO;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.vo.CmmSerializedProductFscRemindVO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmServiceDemandVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.RemindScheduleVO;
import com.a1stream.domain.vo.RemindSettingVO;
import com.a1stream.service.service.ServiceCallbackService;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

@Component
public class ServiceCallbackFacade {

    @Resource
    private ServiceCallbackService serviceCallbackService;
    @Resource
    private ConstantsLogic constantsLogic;
    @Resource
    ServiceLogic serviceLogic;

    public void generateServiceRemind() {

        //1.提前3天，获取待处理的车辆FscRemind表数据
        List<CmmSerializedProductFscRemindVO> mcFscRemindList = serviceCallbackService.findMcFscRemind(ComUtil.date2str(LocalDate.now().plusDays(-3)), ComUtil.date2str(LocalDate.now()));

        if (mcFscRemindList.isEmpty()) {return;}

        //这里仅生成ROUTINEINSPECTION，也就是FSC的remind
        List<RemindSettingVO> remindSettingList = serviceCallbackService.findRemindSettingByType(RemindType.ROUTINEINSPECTION.getCodeDbid());
        List<CmmServiceDemandVO> svDemandList = serviceCallbackService.findCmmServiceDemandVOList();
        Map<Long, CmmServiceDemandVO> svDemandMap = svDemandList.stream().collect(Collectors.toMap(CmmServiceDemandVO::getServiceDemandId, Function.identity()));
        Map<String, ConstantsBO> svCategoryMap = constantsLogic.getConstantsMap(ServiceCategory.class.getDeclaredFields());
        Map<Long, CmmSerializedProductVO> mcMap = serviceCallbackService.findMcByIds(mcFscRemindList.stream().map(CmmSerializedProductFscRemindVO::getSerializedProductId).collect(Collectors.toSet()))
                                                                        .stream().collect(Collectors.toMap(CmmSerializedProductVO::getSerializedProductId, Function.identity()));
        Map<Long, MstProductVO> modelMap = serviceCallbackService.findModelByIds(mcMap.values().stream().map(CmmSerializedProductVO::getProductId).collect(Collectors.toSet()))
                                                                 .stream().collect(Collectors.toMap(MstProductVO::getProductId, Function.identity()));

        List<RemindScheduleVO> remindScheduleForSave = new ArrayList<>();

        //2.生成serviceRemind数据
        RemindScheduleVO remindSchedule;
        CmmServiceDemandVO serviceDemand;
        CmmSerializedProductVO mcInfo;
        MstProductVO modelInfo;
        String eventStartDate;
        String eventEndDate;
        String remindDueDate;

        for (CmmSerializedProductFscRemindVO fscRemind : mcFscRemindList) {

            serviceDemand = svDemandMap.get(fscRemind.getNextServiceDemandId());
            mcInfo = mcMap.get(fscRemind.getSerializedProductId());

            if (Objects.isNull(serviceDemand) || Objects.isNull(mcInfo) || StringUtils.isBlank(mcInfo.getStuDate())) {continue;}

            modelInfo = modelMap.get(mcInfo.getProductId());

            //根据demand获取事件的日期范围
            eventStartDate = ComUtil.date2str(ComUtil.str2date(mcInfo.getStuDate()).plusMonths(serviceDemand.getBaseDateAfter()));
            eventEndDate = ComUtil.date2str(ComUtil.str2date(mcInfo.getStuDate()).plusMonths((long)serviceDemand.getBaseDateAfter() + (long)serviceDemand.getDuePeriod()).plusDays(-1));

            //如果事件开始日早于今天，则不生成remind数据
            if (ComUtil.str2date(eventStartDate).compareTo(LocalDate.now()) < 0) {continue;}

            for (RemindSettingVO remindSetting : remindSettingList) {

                remindDueDate = serviceLogic.calculateRemindDueDate(remindSetting.getBaseDateType(), remindSetting.getDays().longValue(), eventStartDate, eventEndDate);

                //如果事件提醒日早于今天，则不生成remind数据
                if (ComUtil.str2date(remindDueDate).compareTo(LocalDate.now()) < 0) {continue;}

                remindSchedule = new RemindScheduleVO();

                remindSchedule.setSiteId(fscRemind.getSiteId());
                remindSchedule.setFacilityId(fscRemind.getFacilityId());
                remindSchedule.setActivityFlag(CommonConstants.CHAR_N);
                remindSchedule.setRemindDueDate(remindDueDate);
                remindSchedule.setExpireDate(ComUtil.date2str(ComUtil.str2date(remindDueDate).plusDays(remindSetting.getContinueDays().longValue())));
                remindSchedule.setRemindType(RemindType.ROUTINEINSPECTION.getCodeDbid());
                remindSchedule.setProductCd(Objects.isNull(modelInfo) ? null : modelInfo.getProductCd());
                remindSchedule.setSerializedProductId(fscRemind.getSerializedProductId());
                remindSchedule.setRemindSettingId(remindSetting.getRemindSettingId());
                remindSchedule.setRemindContents(this.generateRemindContents(svCategoryMap.get(serviceDemand.getServiceCategory()).getCodeData1(), serviceDemand.getDescription(), remindSetting.getStepContent()));
                remindSchedule.setEventStartDate(eventStartDate);
                remindSchedule.setEventEndDate(eventEndDate);
                remindSchedule.setServiceDemandId(serviceDemand.getServiceDemandId());
                remindSchedule.setRoleList(remindSetting.getRoleList());

                remindScheduleForSave.add(remindSchedule);
            }
        }

        //3.更新车辆FscRemind表数据至下一枚提示
        List<CmmSerializedProductFscRemindVO> fscRemindForSave = new ArrayList<>();

        for (CmmSerializedProductFscRemindVO fscRemind : mcFscRemindList) {

            mcInfo = mcMap.get(fscRemind.getSerializedProductId());

            if (Objects.isNull(mcInfo) || StringUtils.isBlank(mcInfo.getStuDate())) {continue;}

            CmmServiceDemandVO nextServiceDemand = serviceLogic.getNextServiceDemand(svDemandList, mcInfo.getStuDate(), null);

            //若没有符合条件的下次demand，不再生成
            if (Objects.isNull(nextServiceDemand)) {return;}

            fscRemind.setNextServiceDemandId(nextServiceDemand.getServiceDemandId());
            fscRemind.setNextRemindGenDate(serviceLogic.calculateNextRemindGenDate(nextServiceDemand, remindSettingList));

            fscRemindForSave.add(fscRemind);
        }


    }

    private String generateRemindContents(String serviceCategory, String serviceDemandDes, String remindSettingStepContent) {

        return new StringBuilder().append(StringUtils.isBlank(serviceCategory) ? CommonConstants.CHAR_BLANK : serviceCategory + CommonConstants.CHAR_COMMA)
                                  .append(StringUtils.isBlank(serviceDemandDes) ? CommonConstants.CHAR_BLANK : serviceDemandDes + CommonConstants.CHAR_COMMA)
                                  .append(remindSettingStepContent)
                                  .toString();
    }
}
