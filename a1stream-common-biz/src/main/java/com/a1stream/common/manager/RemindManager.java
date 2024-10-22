package com.a1stream.common.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.RemindType;
import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.common.logic.ServiceLogic;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.entity.CmmSerializedProductFscRemind;
import com.a1stream.domain.entity.RemindSchedule;
import com.a1stream.domain.repository.CmmSerializedProductFscRemindRepository;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.CmmServiceDemandRepository;
import com.a1stream.domain.repository.RemindScheduleRepository;
import com.a1stream.domain.repository.RemindSettingRepository;
import com.a1stream.domain.repository.ServiceOrderJobRepository;
import com.a1stream.domain.vo.CmmSerializedProductFscRemindVO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmServiceDemandVO;
import com.a1stream.domain.vo.RemindScheduleVO;
import com.a1stream.domain.vo.RemindSettingVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.ServiceOrderJobVO;
import com.a1stream.domain.vo.ServiceOrderVO;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;
import jodd.util.StringUtil;

@Component
public class RemindManager {

    @Resource
    RemindSettingRepository remindSettingRepo;
    @Resource
    ServiceOrderJobRepository serviceOrderJobRepo;
    @Resource
    CmmSerializedProductFscRemindRepository cmmSerializedProductFscRemindRepo;
    @Resource
    CmmSerializedProductRepository cmmSerializedProductRepo;
    @Resource
    CmmServiceDemandRepository cmmServiceDemandRepo;
    @Resource
    RemindScheduleRepository remindScheduleRepo;
    @Resource
    ServiceLogic serviceLogic;

    /**
     * Service settle后调用，生成serviceFollowUp
     */
    public List<RemindScheduleVO> generateServiceFollowUpRemind(ServiceOrderVO serviceOrder) {

        List<RemindScheduleVO> result = new ArrayList<>();

        //如果是quickService，不做售后回访
        if (StringUtils.equals(serviceOrder.getServiceCategoryId(), ServiceCategory.QUICKSERVICE.getCodeDbid())) {return result;}

        //获取Service Follow Up相关的提醒信息
        List<RemindSettingVO> serviceFollowUpRemindSettingList = BeanMapUtils.mapListTo(remindSettingRepo.findByRemindType(RemindType.SERVICEFOLLOWUP.getCodeDbid()), RemindSettingVO.class);

        RemindScheduleVO remindSchedule ;
        String remindDueDate;

        //根据提醒master生成remindSchedule
        for (RemindSettingVO remindSetting : serviceFollowUpRemindSettingList) {

            remindDueDate = serviceLogic.calculateRemindDueDate(remindSetting.getBaseDateType(), remindSetting.getDays().longValue(), serviceOrder.getSettleDate(), serviceOrder.getSettleDate());
            //只有当提醒日>=当前日期时，才生成remind数据
            if (ComUtil.str2date(remindDueDate).compareTo(LocalDate.now()) < 0) {continue;}

            remindSchedule = new RemindScheduleVO();

            remindSchedule.setSiteId(serviceOrder.getSiteId());
            remindSchedule.setFacilityId(serviceOrder.getFacilityId());
            remindSchedule.setRemindDueDate(remindDueDate);
            remindSchedule.setExpireDate(serviceLogic.calculateRemindExpireDate(remindSetting.getBaseDateType(), remindSetting.getDays().longValue(), remindSetting.getContinueDays().longValue(), serviceOrder.getSettleDate(), serviceOrder.getSettleDate()));
            remindSchedule.setRemindType(remindSetting.getRemindType());
            remindSchedule.setProductCd(serviceOrder.getModelCd());
            remindSchedule.setSerializedProductId(serviceOrder.getCmmSerializedProductId());
            remindSchedule.setRemindSettingId(remindSetting.getRemindSettingId());
            remindSchedule.setRemindContents(this.generateServiceFollowupRemindContents(serviceOrder));
            remindSchedule.setEventStartDate(serviceOrder.getSettleDate());
            remindSchedule.setEventEndDate(serviceOrder.getSettleDate());
            remindSchedule.setRelatedOrderId(serviceOrder.getServiceOrderId());
            remindSchedule.setServiceDemandId(serviceOrder.getServiceDemandId());
            remindSchedule.setRoleList(remindSetting.getRoleList());

            result.add(remindSchedule);
        }

        return result;
    }

    /**
     * 整车销售单结束后调用，生成salesFollowUp
     */
    public List<RemindScheduleVO> generateSalesFollowUpRemind(SalesOrderVO salesOrder) {

        List<RemindScheduleVO> result = new ArrayList<>();

        //获取Sales Follow Up相关的提醒信息
        List<RemindSettingVO> salesFollowUpRemindSettingList = BeanMapUtils.mapListTo(remindSettingRepo.findByRemindType(RemindType.SALESFOLLOWUP.getCodeDbid()), RemindSettingVO.class);

        RemindScheduleVO remindSchedule ;
        String remindDueDate;

        //根据提醒master生成remindSchedule
        for (RemindSettingVO remindSetting : salesFollowUpRemindSettingList) {

            remindDueDate = serviceLogic.calculateRemindDueDate(remindSetting.getBaseDateType(), remindSetting.getDays().longValue(), salesOrder.getOrderDate(), salesOrder.getOrderDate());
            //只有当提醒日>=当前日期时，才生成remind数据
            if (ComUtil.str2date(remindDueDate).compareTo(LocalDate.now()) < 0) {continue;}

            remindSchedule = new RemindScheduleVO();

            remindSchedule.setSiteId(salesOrder.getSiteId());
            remindSchedule.setFacilityId(salesOrder.getFacilityId());
            remindSchedule.setRemindDueDate(remindDueDate);
            remindSchedule.setExpireDate(serviceLogic.calculateRemindExpireDate(remindSetting.getBaseDateType(), remindSetting.getDays().longValue(), remindSetting.getContinueDays().longValue(), salesOrder.getOrderDate(), salesOrder.getOrderDate()));
            remindSchedule.setRemindType(remindSetting.getRemindType());
            remindSchedule.setProductCd(salesOrder.getModelCd());
            remindSchedule.setSerializedProductId(salesOrder.getSerializedProductId());
            remindSchedule.setRemindSettingId(remindSetting.getRemindSettingId());
            remindSchedule.setRemindContents(remindSetting.getStepContent());
            remindSchedule.setEventStartDate(salesOrder.getOrderDate());
            remindSchedule.setEventEndDate(salesOrder.getOrderDate());
            remindSchedule.setRelatedOrderId(salesOrder.getSalesOrderId());
            remindSchedule.setRoleList(remindSetting.getRoleList());

            result.add(remindSchedule);
        }

        return result;
    }

    /**
     * Service和车辆Sales订单结束后调用，修改或生成车辆FscRemind标志表
     */
    public void setupSerializedProFscRemind(String siteId
                                          , Long facilityId
                                          , Long relatedOrderId
                                          , Long cmmSerializedProductId
                                          , String stuDate
                                          , Long serviceDemandId) {

        if (Objects.isNull(cmmSerializedProductId)) {return;}
        //获取当前车辆的FscRemind信息
        CmmSerializedProductFscRemindVO cmmSerializedProFscRemind = BeanMapUtils.mapTo(cmmSerializedProductFscRemindRepo.findById(cmmSerializedProductId), CmmSerializedProductFscRemindVO.class);

        if(Objects.isNull(cmmSerializedProFscRemind)) {
            //如果不存在，则创建FscRemind
            this.registerSerializedProFscRemind(siteId, facilityId, relatedOrderId, cmmSerializedProductId, stuDate, serviceDemandId);

        }else {
            //如果存在，则将FscRemind + 已生成的remindSchedule的归属site改为当前经销商
            this.reassignSerializedProFscRemind(siteId, facilityId, cmmSerializedProductId, cmmSerializedProFscRemind);
        }
    }

    /**
     * RegisterDoc变更stuDate时调用，根据新的stu重新计算FscRemind
     */
    public void resetSerializedProFscRemind(Long cmmSerializedProductId, String stuDate) {

        if (Objects.isNull(cmmSerializedProductId)) {return;}
        //获取当前车辆的FscRemind信息
        CmmSerializedProductFscRemindVO cmmSerializedProFscRemind = BeanMapUtils.mapTo(cmmSerializedProductFscRemindRepo.findById(cmmSerializedProductId), CmmSerializedProductFscRemindVO.class);
        //当没有获取到目标数据时，不做处理
        if (Objects.isNull(cmmSerializedProFscRemind)) {return;}

        //补偿获取stuDate, 若依然没有StuDate，不做提醒处理
        stuDate = StringUtils.isBlank(stuDate) ? BeanMapUtils.mapTo(cmmSerializedProductRepo.findById(cmmSerializedProductId), CmmSerializedProductVO.class).getStuDate() : stuDate;
        if (StringUtil.isBlank(stuDate)){return;}

        //获取下一次有效的demand
        CmmServiceDemandVO nextServiceDemand = this.getNextServiceDemand(stuDate, null);

        //若没有符合条件的下次demand，不做提醒
        if (Objects.isNull(nextServiceDemand)) {return;}

        cmmSerializedProFscRemind.setNextServiceDemandId(nextServiceDemand.getServiceDemandId());
        cmmSerializedProFscRemind.setNextRemindGenDate(this.calculateNextRemindGenDate(nextServiceDemand));

        cmmSerializedProductFscRemindRepo.save(BeanMapUtils.mapTo(cmmSerializedProFscRemind, CmmSerializedProductFscRemind.class));
    }

    /**
     * Service（仅FreeCoupon）结束后，关闭对应demand的RemindSchedule
     */
    public List<RemindScheduleVO> getWaitingCloseRemindSchedule(ServiceOrderVO serviceOrder) {

        //仅当freeCoupon时，才更新remindSchedule
        if (!StringUtils.equals(serviceOrder.getServiceCategoryId(), ServiceCategory.FREECOUPON.getCodeDbid())) {return new ArrayList<>();}

        //获取关闭当前车辆对应demand的那条remindSchedule,将activityFlag改为Y
        List<RemindScheduleVO> result = BeanMapUtils.mapListTo(remindScheduleRepo.findByDemandAndMcId(RemindType.ROUTINEINSPECTION.getCodeDbid(), serviceOrder.getServiceDemandId(), serviceOrder.getCmmSerializedProductId()), RemindScheduleVO.class);

        result.forEach(remind -> remind.setActivityFlag(CommonConstants.CHAR_Y));

        return result;
    }

    /**
     * RegisterDoc变更stuDate时调用
     */
    public void removeRemindScheduleByMcId(Long cmmSerializedProId) {

        if (Objects.isNull(cmmSerializedProId)) {return;}

        List<RemindScheduleVO> scheduleList = BeanMapUtils.mapListTo(remindScheduleRepo.findByMcId(RemindType.ROUTINEINSPECTION.getCodeDbid(), cmmSerializedProId), RemindScheduleVO.class);

        if (!scheduleList.isEmpty()) {remindScheduleRepo.deleteAllInBatch(BeanMapUtils.mapListTo(scheduleList, RemindSchedule.class));}
    }

    private void reassignSerializedProFscRemind(String siteId
                                              , Long facilityId
                                              , Long cmmSerializedProductId
                                              , CmmSerializedProductFscRemindVO cmmSerializedProFscRemind) {

        //更新FscRemind的所属siteId和facilityId
        cmmSerializedProFscRemind.setSiteId(siteId);
        cmmSerializedProFscRemind.setFacilityId(facilityId);

        //更新生效期在未来的remindSchdule
        List<RemindScheduleVO> remindScheduleList = BeanMapUtils.mapListTo(remindScheduleRepo.findFutureScheduleByMcId(RemindType.ROUTINEINSPECTION.getCodeDbid(), ComUtil.date2str(LocalDate.now()), cmmSerializedProductId), RemindScheduleVO.class);

        for (RemindScheduleVO remindSchedule: remindScheduleList) {

            remindSchedule.setSiteId(siteId);
            remindSchedule.setFacilityId(facilityId);
        }

        //更新修改信息
        cmmSerializedProductFscRemindRepo.save(BeanMapUtils.mapTo(cmmSerializedProFscRemind, CmmSerializedProductFscRemind.class));
        remindScheduleRepo.saveInBatch(BeanMapUtils.mapListTo(remindScheduleList, RemindSchedule.class));
    }

    private void registerSerializedProFscRemind(String siteId
                                              , Long facilityId
                                              , Long relatedOrderId
                                              , Long cmmSerializedProductId
                                              , String stuDate
                                              , Long serviceDemandId) {

        CmmSerializedProductVO cmmSerializedProduct = BeanMapUtils.mapTo(cmmSerializedProductRepo.findById(cmmSerializedProductId), CmmSerializedProductVO.class);

        //如果车辆不存在 或 车辆为BigBike时，不做提醒处理
        if(Objects.isNull(cmmSerializedProduct) || cmmSerializedProductRepo.isMcBigBike(cmmSerializedProductId)) {return;}

        //补偿获取stuDate, 若依然没有StuDate，不做提醒处理
        stuDate = StringUtils.isBlank(stuDate) ? cmmSerializedProduct.getStuDate() : stuDate;
        if (StringUtil.isBlank(stuDate)){return;}

        //获取下一次有效的demand
        CmmServiceDemandVO nextServiceDemand = this.getNextServiceDemand(stuDate, serviceDemandId);

        //若没有符合条件的下次demand，不做提醒
        if (Objects.isNull(nextServiceDemand)) {return;}

        CmmSerializedProductFscRemindVO cmmSerializedProFscRemind = new CmmSerializedProductFscRemindVO();

        cmmSerializedProFscRemind.setSiteId(siteId);
        cmmSerializedProFscRemind.setFacilityId(facilityId);
        cmmSerializedProFscRemind.setOrderId(relatedOrderId);
        cmmSerializedProFscRemind.setNextServiceDemandId(nextServiceDemand.getServiceDemandId());
        cmmSerializedProFscRemind.setNextRemindGenDate(this.calculateNextRemindGenDate(nextServiceDemand));

        cmmSerializedProductFscRemindRepo.save(BeanMapUtils.mapTo(cmmSerializedProFscRemind, CmmSerializedProductFscRemind.class));
    }

    private CmmServiceDemandVO getNextServiceDemand(String stuDate, Long serviceDemandId) {

        List<CmmServiceDemandVO> demandList= BeanMapUtils.mapListTo(cmmServiceDemandRepo.findAllByOrderByBaseDateAfter(), CmmServiceDemandVO.class);

        return serviceLogic.getNextServiceDemand(demandList, stuDate, serviceDemandId);
    }

    private String calculateNextRemindGenDate(CmmServiceDemandVO nextServiceDemand) {

        //获取Routine Inspection相关的提醒信息
        List<RemindSettingVO> routineInspectionRemindSettingList = BeanMapUtils.mapListTo(remindSettingRepo.findByRemindType(RemindType.ROUTINEINSPECTION.getCodeDbid()), RemindSettingVO.class);
        //获取最早的RemindDueDate
        return serviceLogic.calculateNextRemindGenDate(nextServiceDemand, routineInspectionRemindSettingList);
    }

    private String generateServiceFollowupRemindContents(ServiceOrderVO serviceOrder) {

        List<ServiceOrderJobVO> jobList = BeanMapUtils.mapListTo(serviceOrderJobRepo.findByServiceOrderId(serviceOrder.getServiceOrderId()), ServiceOrderJobVO.class);

        return new StringBuilder().append(serviceOrder.getServiceCategoryContent())
                .append(CommonConstants.CHAR_COMMA)
                .append(StringUtils.isBlank(serviceOrder.getServiceDemandContent())
                            ? CommonConstants.CHAR_BLANK
                            : serviceOrder.getServiceDemandContent() + CommonConstants.CHAR_COMMA)
                .append(jobList.stream().map(ServiceOrderJobVO::getJobNm).collect(Collectors.joining(": ")))
                .toString();
    }
}