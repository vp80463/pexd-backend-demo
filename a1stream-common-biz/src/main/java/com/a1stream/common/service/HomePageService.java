package com.a1stream.common.service;

import com.a1stream.common.bo.HomePageRemindMessageBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.UserHabitConstant;
import com.a1stream.common.manager.MessageSendManager;
import com.a1stream.common.model.HomePageForm;
import com.a1stream.common.model.HomePagePointBO;
import com.a1stream.domain.entity.*;
import com.a1stream.domain.repository.*;
import com.a1stream.domain.vo.*;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dong zhen
 */
@Service
public class HomePageService {

    @Resource
    private UserHabitRepository userHabitRepository;

    @Resource
    private SysMenuRepository sysMenuRepository;

    @Resource
    private CmmAnnounceRepository cmmAnnounceRepository;

    @Resource
    private PurchaseOrderRepository purchaseOrderRepository;

    @Resource
    private SalesOrderRepository salesOrderRepository;

    @Resource
    private ServiceOrderRepository serviceOrderRepository;

    @Resource
    private ServiceScheduleRepository serviceScheduleRepository;

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    @Resource
    private CmmMenuClickSituationRepository cmmMenuClickSituationRepository;

    @Resource
    private CmmMessageRemindRepository cmmMessageRemindRepository;

    @Resource
    private MessageSendManager messagesManager;

    @Resource
    private CmmMessageRepository cmmMessageRepository;

    public List<SysMenuVO> getSysMenuVOList(List<String> menuCdlist) {

        List<SysMenu> sysMenus = sysMenuRepository.findByMenuCodeIn(menuCdlist);
        return BeanMapUtils.mapListTo(sysMenus, SysMenuVO.class);
    }

    public List<CmmAnnounceVO> getAnnounceList(HomePageForm form) {

        List<CmmAnnounce> cmmAnnounces = cmmAnnounceRepository
                .findByNotifyTypeAndDate(CommonConstants.CHAR_DEFAULT_SITE_ID, form.getNotifyTypeIdList(), form.getNowDate());
        return BeanMapUtils.mapListTo(cmmAnnounces, CmmAnnounceVO.class);
    }

    public Integer getPurchaseOrderSize(String siteId, String orderStatus) {

        return purchaseOrderRepository.getPurchaseOrderSize(siteId, orderStatus);
    }

    public Integer getSalesOrderCountForSp(String siteId, List<String> orderStatusList) {

        return salesOrderRepository.getSalesOrderCountForSp(siteId, orderStatusList);
    }

    public Integer getSalesOrderCountForSd(String siteId, String orderStatus) {

        return salesOrderRepository.getSalesOrderCountForSd(siteId, orderStatus);
    }

    public Integer getServiceOrderCount(String siteId, String orderStatus, String zeroKmFlag, String serviceCategoryId) {

        return serviceOrderRepository.getServiceOrderCount(siteId, orderStatus, zeroKmFlag, serviceCategoryId);
    }

    public Integer getZeroKmServiceOrderCount(String siteId, String orderStatus, String zeroKmFlag) {

        return serviceOrderRepository.getZeroKmServiceOrderCount(siteId, orderStatus, zeroKmFlag);
    }

    public Integer getClaimBatteryServiceOrderCount(String siteId, String orderStatus, String serviceCategoryId) {

        return serviceOrderRepository.getClaimBatteryServiceOrderCount(siteId, orderStatus, serviceCategoryId);
    }

    public List<ServiceScheduleVO> getServiceScheduleList(String siteId, Long pointId, List<String> dateList, List<String> orderStatusList) {

        List<ServiceSchedule> serviceScheList = serviceScheduleRepository.findBySiteIdAndFacilityIdAndScheduleDateInAndReservationStatusIn(siteId, pointId, dateList, orderStatusList);
        return BeanMapUtils.mapListTo(serviceScheList, ServiceScheduleVO.class);
    }

    public List<HomePagePointBO> getPointList(HomePageForm form) {

        return mstFacilityRepository.findPointListForHome(form);
    }

    public MstFacilityVO getMstFacilityById(Long pointId) {

        MstFacility mstFacility = mstFacilityRepository.findByFacilityId(pointId);
        return BeanMapUtils.mapTo(mstFacility, MstFacilityVO.class);
    }

    public void resetUserHabit(HomePageForm form) {

        UserHabit userHabit = userHabitRepository.findByUserIdAndSiteIdAndUserHabitTypeId(form.getUserId(), form.getSiteId(), form.getUserHabitTypeId());
        if (userHabit == null) {
            userHabit = new UserHabit();
            userHabit.setUserId(form.getUserId());
            userHabit.setSiteId(form.getSiteId());
            userHabit.setUserHabitTypeId(form.getUserHabitTypeId());
            if (StringUtils.equals(form.getUserHabitTypeId(), UserHabitConstant.DEFAULTPOINT)) {
                userHabit.setHabitContent(form.getPointId().toString());
            } else if (StringUtils.equals(form.getUserHabitTypeId(), UserHabitConstant.LANGUAGE)) {
                userHabit.setHabitContent(form.getLanguage());
            }
        } else {
            if (StringUtils.equals(form.getUserHabitTypeId(), UserHabitConstant.DEFAULTPOINT)) {
                if (!userHabit.getHabitContent().equals(form.getPointId().toString())) {
                    userHabit.setHabitContent(form.getPointId().toString());
                }
            } else if (StringUtils.equals(form.getUserHabitTypeId(), UserHabitConstant.LANGUAGE)) {
                if (!userHabit.getHabitContent().equals(form.getLanguage().toString())) {
                    userHabit.setHabitContent(form.getLanguage().toString());
                }
            }
        }
        userHabitRepository.save(userHabit);
    }

    public List<CmmMenuClickSituationVO> getMenuClickSituationVOList(HomePageForm form) {

        List<CmmMenuClickSituation> menuClickSituationVOList = cmmMenuClickSituationRepository.findByUserIdAndSiteIdAndCustomStatus(form.getUserId(), form.getRealSiteId(), CommonConstants.CHAR_Y);
        return BeanMapUtils.mapListTo(menuClickSituationVOList, CmmMenuClickSituationVO.class);
    }

    public List<CmmMessageRemindVO> getMessageRemindVOList(String siteId, String readType) {

        List<CmmMessageRemind> chatMessageRemind = cmmMessageRemindRepository.findBySiteIdAndReadType(siteId, readType);
        return BeanMapUtils.mapListTo(chatMessageRemind, CmmMessageRemindVO.class);
    }

    public List<CmmMessageRemindVO> getMessageRemindByIdVOList(List<Long> messageRemindIdList) {

        List<CmmMessageRemind> chatMessageReminds = cmmMessageRemindRepository.findByMessageRemindIdIn(messageRemindIdList);
        return BeanMapUtils.mapListTo(chatMessageReminds, CmmMessageRemindVO.class);
    }

    public void messageRead(List<CmmMessageRemindVO> messageRemindVOList, String siteId) {

        List<CmmMessageRemind> messageReminds = BeanMapUtils.mapListTo(messageRemindVOList, CmmMessageRemind.class);
        cmmMessageRemindRepository.saveInBatch(messageReminds);

        messagesManager.sendMessage(siteId);
    }

    public void logInChangeLastLoginTime(HomePageForm form) {

        UserHabit userHabit = userHabitRepository.findByUserIdAndSiteIdAndUserHabitTypeId(form.getUserId(), form.getSiteId(), form.getUserHabitTypeId());
        if (userHabit != null) {
            userHabit.setHabitContent(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMDHMS));
        } else {
            userHabit = new UserHabit();
            userHabit.setUserId(form.getUserId());
            userHabit.setSiteId(form.getSiteId());
            userHabit.setUserHabitTypeId(form.getUserHabitTypeId());
            userHabit.setHabitContent(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMDHMS));
            userHabit.setSeqNo(CommonConstants.CHAR_ONE);
        }
        userHabitRepository.save(userHabit);
    }

    public List<HomePageRemindMessageBO> getImportantRemindList(List<String> siteIds, String userId) {

        List<CmmMessage> cmmMessages = cmmMessageRepository.getImportantRemindList(siteIds, userId);
        return BeanMapUtils.mapListTo(cmmMessages, HomePageRemindMessageBO.class);
    }
}
