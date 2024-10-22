package com.a1stream.common.facade;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntSupplier;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.a1stream.common.bo.HomePageAnnounceBO;
import com.a1stream.common.bo.HomePageCmmMenuBO;
import com.a1stream.common.bo.HomePageDataBO;
import com.a1stream.common.bo.HomePageRemindMessageBO;
import com.a1stream.common.bo.HomePageReservationRemindBO;
import com.a1stream.common.bo.HomePageToDoBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.RedisFormatConstants;
import com.a1stream.common.model.HomePageForm;
import com.a1stream.common.model.HomePageMessageBO;
import com.a1stream.common.model.HomePageMessageItemBO;
import com.a1stream.common.model.HomePagePointBO;
import com.a1stream.common.service.HomePageService;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.common.utils.TimeChangeUtils;
import com.a1stream.domain.vo.CmmAnnounceVO;
import com.a1stream.domain.vo.CmmMenuClickSituationVO;
import com.a1stream.domain.vo.CmmMessageRemindVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.ServiceScheduleVO;
import com.a1stream.domain.vo.SysMenuVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ymsl.plugins.userauth.util.ListSortUtils;
import com.ymsl.solid.base.util.DateUtils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dong zhen
 */
@Slf4j
@Component
public class HomePageFacade {

    @Resource
    private HomePageService homePageService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public HomePageDataBO getHomePageData(HomePageForm form) {

        HomePageDataBO result = new HomePageDataBO();

        //carousel chat url list
        result.setImgUrlList(this.getImgUrlList(form));
        result.setNotifyEmergentFlag(form.getNotifyEmergentFlag());
        log.info("提取轮播图成功");

        //common menu list
        result.setCommonMenuList(this.getCommonMenuList(form));
        log.info("提取常用菜单成功");

        //to do list
        result.setToDoList(this.getToDoList(form));
        log.info("提取代办事项成功");

        //reservation remind list
        result.setReservationRemindBO(this.getReservationRemindBO(form));
        log.info("提取预订提醒成功");

        //important reminder list
        result.setImportantRemindList(this.getImportantRemindList(form));
        log.info("提取重要提醒成功");
        return result;
    }

    /**
     * 获取公告图片URL列表
     * 此方法从主页服务获取公告列表，将其转换成适合前端展示的格式，
     * 并区分紧急公告与其他普通公告。
     *
     * @param form 公告查询表单，可能包含查询条件。
     * @return 返回一个包含公告封面URL及标题的列表。
     */
    private List<HomePageAnnounceBO> getImgUrlList(HomePageForm form) {

        List<HomePageAnnounceBO> imgUrlList = new ArrayList<>();
        List<CmmAnnounceVO> cmmAnnounceVOList;

        try {
            cmmAnnounceVOList = homePageService.getAnnounceList(form);
            if (!CollectionUtils.isEmpty(cmmAnnounceVOList)) {
                imgUrlList.addAll(cmmAnnounceVOList.stream()
                        .map(member -> convertToHomePageAnnounceBO(member, form))
                        .toList());
            }
        } catch (Exception e) {
            log.error("获取公告列表失败", e);
        }
        return imgUrlList;
    }

    /**
     * 将 CmmAnnounceVO 转换为 HomePageAnnounceBO
     *
     * @param member 公告详情
     * @param form 公告查询表单
     * @return 转换后的公告对象
     */
    private HomePageAnnounceBO convertToHomePageAnnounceBO(CmmAnnounceVO member, HomePageForm form) {
        HomePageAnnounceBO homePageAnnounceBO = new HomePageAnnounceBO();
        homePageAnnounceBO.setCoverUrl(member.getCoverUrl());
        homePageAnnounceBO.setTitle(member.getTitle());

        // 如果是紧急公告，设置标志
        if (PJConstants.AnnounceType.ANNOUNCE_EMERGENCY.equals(member.getNotifyTypeId())) {
            form.setNotifyEmergentFlag(false);
        }

        return homePageAnnounceBO;
    }

    /**
     * 获取首页通用菜单列表。
     * 根据前端页面传递的请求信息，通过调用服务获取相应的菜单列表，并将其转换为首页通用菜单对象的列表。
     * 这样做的目的是为了根据前端的需要，组装出适合首页展示的菜单结构。
     *
     * @param form 首页请求表单，包含用于获取菜单列表的必要信息。
     * @return 首页通用菜单对象的列表，每个对象包含菜单的名称、ID、代码和图片等信息。
     */
    private List<HomePageCmmMenuBO> getCommonMenuList(HomePageForm form) {

        List<String> menuCdlist = this.getMenuCdList(form);
        List<SysMenuVO> sysMenuVOList = homePageService.getSysMenuVOList(menuCdlist);
        Map<String, HomePageCmmMenuBO> menuMap = new LinkedHashMap<>();

        sysMenuVOList.forEach(member -> {
            HomePageCmmMenuBO homePageCmmMenuBO = new HomePageCmmMenuBO();
            homePageCmmMenuBO.setMenuNm(member.getMenuName());
            homePageCmmMenuBO.setMenuId(member.getMenuId());
            homePageCmmMenuBO.setMenuCd(member.getMenuCode());
            homePageCmmMenuBO.setMenuPic(member.getMenuPic());
            menuMap.put(member.getMenuCode(), homePageCmmMenuBO);
        });

        return menuCdlist.stream().map(menuMap::get).filter(Objects::nonNull).toList();
    }

    /**
     * 根据用户首页配置表，获取菜单代码列表。
     * 该方法首先通过查询所有菜单的点击情况，然后根据手动设置的优先级和点击次数对菜单进行排序，
     * 最终根据排序结果筛选出前6个菜单的代码。
     * 如果手动设置的菜单数量超过6个，则只取前6个；
     * 如果手动设置的菜单数量不足6个，则将非手动设置的菜单按点击次数补充至6个。
     *
     * @param form 用户首页表单，用于查询菜单点击情况。
     * @return 菜单代码列表。
     */
    private List<String> getMenuCdList(HomePageForm form) {
        List<String> menuCdList = new ArrayList<>();

        try {
            List<CmmMenuClickSituationVO> cmmMenuClickSituationVOList = homePageService.getMenuClickSituationVOList(form);
            List<String> userMenuCdSet = this.processManualMenus(cmmMenuClickSituationVOList);

            List<String> redisUserMenuCdSet = this.processNonManualMenus(form);

            menuCdList = this.combineAndLimitMenus(userMenuCdSet, redisUserMenuCdSet);
        } catch (Exception e) {
            log.error("获取常用菜单失败: ", e);
        }

        return menuCdList;
    }

    private List<String> processManualMenus(List<CmmMenuClickSituationVO> voList) {
        return voList.stream()
                .sorted(Comparator.comparingInt(CmmMenuClickSituationVO::getPriority))
                .map(CmmMenuClickSituationVO::getMenuCd)
                .toList();
    }

    /**
     * 处理非手动配置的菜单。
     * 该方法通过从Redis中获取用户的菜单配置，根据菜单的点击次数进行排序，然后返回排名前六的菜单代码。
     * 如果菜单数量不足六个，则返回所有菜单代码,这样做的目的是为了提供一个动态的、基于用户行为的菜单列表，以优化用户体验。
     *
     * @param form 包含用户信息和站点信息的首页表单对象。
     * @return 返回一个包含菜单代码的列表，这些菜单代码是根据用户的点击数据动态确定的。
     */
    private List<String> processNonManualMenus(HomePageForm form) {
        List<String> redisUserMenuCdSet = new ArrayList<>();
        String key = String.format(RedisFormatConstants.USER_MENU_COUNT, form.getRealSiteId(), form.getUserId());

        String menuJsonStr = stringRedisTemplate.opsForValue().get(key);

        if (StringUtils.isNotBlank(menuJsonStr)) {
            JSONObject menuJson = JSON.parseObject(menuJsonStr);
            List<Map.Entry<String, Object>> menuEntryList = new ArrayList<>(menuJson.entrySet());

            menuEntryList.sort((o1, o2) -> (NumberUtil.toInteger(o2.getValue()) - NumberUtil.toInteger(o1.getValue())));

            int limit = Math.min(menuEntryList.size(), 6);
            for (int i = 0; i < limit; i++) {
                redisUserMenuCdSet.add(menuEntryList.get(i).getKey());
            }
        }

        return redisUserMenuCdSet;
    }

    private List<String> combineAndLimitMenus(List<String> userMenuCdSet, List<String> redisUserMenuCdSet) {
        List<String> combinedList = new ArrayList<>(userMenuCdSet);
        int remainingSlots = 6 - combinedList.size();
        if (remainingSlots > 0) {
            combinedList.addAll(redisUserMenuCdSet.stream().limit(remainingSlots).toList());
        }
        return combinedList.stream().limit(6).toList();
    }

    /**
     * 获取待办事项列表。
     * 根据给定的首页表单，查询并构建不同类型的待办事项对象，包括采购单、销售单、服务单等。
     *
     * @param form 首页表单，包含实际站点ID等信息，用于查询待办事项的数量。
     * @return 待办事项列表，每个待办事项包含计数、名称和菜单代码等信息。
     */
    private List<HomePageToDoBO> getToDoList(HomePageForm form) {

        List<HomePageToDoBO> toDoList = new ArrayList<>();

        try {
            String menuCd = "cmm0001_01";
            //purchase order
            toDoList.add(createToDoItem("label.tablePurchaseOrder", menuCd,
                    () -> homePageService.getPurchaseOrderSize(form.getRealSiteId(),
                            MstCodeConstants.PurchaseOrderStatus.SPWAITINGISSUE),
                    PJConstants.OrderType.PURCHASEORDER.getCodeDbid()));

            //sales order for sp
            toDoList.add(createToDoItem("label.salesOrderForSP", menuCd,
                    () -> homePageService.getSalesOrderCountForSp(form.getRealSiteId(), List.of(
                            MstCodeConstants.SalesOrderStatus.SP_WAITINGPICKING,
                            MstCodeConstants.SalesOrderStatus.SP_ONPICKING)),
                    PJConstants.OrderType.SALESORDER.getCodeDbid()));

            //sales order for sd
            toDoList.add(createToDoItem("common.label.unitSales", menuCd,
                    () -> homePageService.getSalesOrderCountForSd(form.getRealSiteId(),
                            MstCodeConstants.SalesOrderStatus.WAITING_SHIPPING),
                    CommonConstants.CHAR_BLANK));

            //service order
            toDoList.add(createToDoItem("common.label.serviceOrder", menuCd,
                    () -> homePageService.getServiceOrderCount(form.getRealSiteId(),
                            MstCodeConstants.ServiceOrderStatus.WAIT_FOR_SETTLE,
                            CommonConstants.CHAR_Y,
                            PJConstants.ServiceCategory.CLAIMBATTERY.getCodeDbid()),
                    PJConstants.OrderType.SERVICEORDER.getCodeDbid()));

            //0km service order
            toDoList.add(createToDoItem("title.serviceFor0KMMC_01", menuCd,
                    () -> homePageService.getZeroKmServiceOrderCount(form.getRealSiteId(),
                            MstCodeConstants.ServiceOrderStatus.WAIT_FOR_SETTLE,
                            CommonConstants.CHAR_Y),
                    PJConstants.OrderType.SERVICEFOR0KMMC.getCodeDbid()));

            //claim battery
            toDoList.add(createToDoItem("title.claimForBattery", menuCd,
                    () -> homePageService.getClaimBatteryServiceOrderCount(form.getRealSiteId(),
                            MstCodeConstants.ServiceOrderStatus.WAIT_FOR_SETTLE,
                            PJConstants.ServiceCategory.CLAIMBATTERY.getCodeDbid()),
                    PJConstants.OrderType.BATTERYCLAIM.getCodeDbid()));

        } catch (Exception e) {
            log.error("获取待办事项失败", e);
        }

        return toDoList;
    }

    /**
     * 创建待办事项项。
     *
     * @param name       待办事项名称
     * @param menuCd     菜单代码
     * @param countSupplier 待办事项计数的供应商，使用lambda表达式来延迟执行
     * @param orderType  订单类型
     * @return 创建的待办事项对象
     */
    private HomePageToDoBO createToDoItem(String name, String menuCd, IntSupplier countSupplier, String orderType) {
        HomePageToDoBO toDoItem = new HomePageToDoBO();
        toDoItem.setName(name);
        toDoItem.setMenuCd(menuCd);
        toDoItem.setOrderType(orderType);
        toDoItem.setCount(countSupplier.getAsInt());
        return toDoItem;
    }

    /**
     * 根据首页表单信息，获取首页预订提醒信息。
     *
     * @param form 首页表单，包含当前日期、实际场地ID等信息。
     * @return HomePageReservationRemindBO 预订提醒业务对象，包含今天和明天的确认和待确认预订数量。
     */
    private HomePageReservationRemindBO getReservationRemindBO(HomePageForm form) {

        HomePageReservationRemindBO reservationRemindBO = new HomePageReservationRemindBO();
        String tomorrowDate = getTomorrowDateString();

        List<String> dateList = List.of(tomorrowDate, form.getNowDate());
        List<String> orderStatusList = List.of(PJConstants.ReservationStatus.CONFIRMED.getCodeDbid(), PJConstants.ReservationStatus.WAITCONFIRM.getCodeDbid());

        try {
            List<ServiceScheduleVO> serviceScheduleVOList = homePageService.getServiceScheduleList(form.getRealSiteId(),form.getPointId(), dateList, orderStatusList);

            if (serviceScheduleVOList == null) {
                serviceScheduleVOList = List.of();
            }

            int todayConfirmed = (int) serviceScheduleVOList.stream()
                    .filter(member -> form.getNowDate().equals(member.getScheduleDate()) && isConfirmed(member))
                    .count();

            int tomorrowConfirmed = (int) serviceScheduleVOList.stream()
                    .filter(member -> tomorrowDate.equals(member.getScheduleDate()) && isConfirmed(member))
                    .count();

            int todayWaitConfirmed = (int) serviceScheduleVOList.stream()
                    .filter(member -> form.getNowDate().equals(member.getScheduleDate()) && isWaitConfirm(member))
                    .count();

            int tomorrowWaitConfirmed = (int) serviceScheduleVOList.stream()
                    .filter(member -> tomorrowDate.equals(member.getScheduleDate()) && isWaitConfirm(member))
                    .count();

            reservationRemindBO.setTodayConfirmed(todayConfirmed);
            reservationRemindBO.setTomorrowConfirmed(tomorrowConfirmed);
            reservationRemindBO.setTodayWaitConfirmed(todayWaitConfirmed);
            reservationRemindBO.setTomorrowWaitConfirmed(tomorrowWaitConfirmed);

        } catch (Exception e) {
            log.error("提取预订提醒时出错: ", e);
            reservationRemindBO.setTodayConfirmed(0);
            reservationRemindBO.setTomorrowConfirmed(0);
            reservationRemindBO.setTodayWaitConfirmed(0);
            reservationRemindBO.setTomorrowWaitConfirmed(0);
        }
        reservationRemindBO.setToday(form.getNowDate());
        reservationRemindBO.setTomorrow(tomorrowDate);
        reservationRemindBO.setWaitType(PJConstants.ReservationStatus.WAITCONFIRM.getCodeDbid());
        reservationRemindBO.setConfirmType(PJConstants.ReservationStatus.CONFIRMED.getCodeDbid());
        return reservationRemindBO;
    }

    /**
     * 获取明天的日期字符串。
     *
     * @return 明天的日期字符串，格式为yyyyMMdd。
     */
    private String getTomorrowDateString() {
        return LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern(DateUtils.FORMAT_YMD_NODELIMITER));
    }

    private boolean isConfirmed(ServiceScheduleVO member) {
        return PJConstants.ReservationStatus.CONFIRMED.getCodeDbid().equals(member.getReservationStatus());
    }

    private boolean isWaitConfirm(ServiceScheduleVO member) {
        return PJConstants.ReservationStatus.WAITCONFIRM.getCodeDbid().equals(member.getReservationStatus());
    }

    private List<HomePageRemindMessageBO> getImportantRemindList(HomePageForm form) {

        List<String> siteIds = new ArrayList<>(Arrays.asList(form.getRealSiteId(), CommonConstants.CHAR_DEFAULT_SITE_ID));
        return homePageService.getImportantRemindList(siteIds, form.getUserId());
    }

    public List<HomePagePointBO> getPointList(HomePageForm form) {

        return homePageService.getPointList(form);
    }

    public MstFacilityVO getMstFacilityVO(Long pointId) {

        return homePageService.getMstFacilityById(pointId);
    }

    public void resetUserHabit(HomePageForm form) {

        homePageService.resetUserHabit(form);
    }

    /**
     * 更新用户菜单访问次数。
     * 该方法用于根据用户访问的菜单，更新其在Redis中的访问计数。如果菜单访问计数已存在，则增加计数；
     * 如果不存在，则初始化计数为1。通过这种方式，可以实时统计不同菜单的访问热度。
     *
     * @param form 包含用户ID和菜单代码的表单对象，用于识别是哪个用户访问了哪个菜单。
     */
    public void menuChange(HomePageForm form) {

        String key = String.format(RedisFormatConstants.USER_MENU_COUNT, form.getRealSiteId(), form.getUserId());

        String menuJsonStr = stringRedisTemplate.opsForValue().get(key);

        JSONObject menuJson = new JSONObject();

        if (StringUtils.isNotBlank(menuJsonStr)) {
            menuJson = JSON.parseObject(menuJsonStr);
            if (menuJson.containsKey(form.getMenuCd())) {
                menuJson.put(form.getMenuCd(), menuJson.getInteger(form.getMenuCd()) + 1);
            } else {
                menuJson.put(form.getMenuCd(), 1);
            }
        } else {
            menuJson.put(form.getMenuCd(), 1);
        }

        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(menuJson));
    }

    /**
     * 获取首页状态栏消息数据。
     * 根据给定的首页表单对象，从服务中获取未读消息的提醒信息，并转换为首页消息展示对象。
     *
     * @param form 首页表单对象，包含站点ID等信息。
     * @return 首页消息数据列表，每个消息包含一个标题、时间和描述。
     */
    public List<HomePageMessageBO> getStatusBarMessageData(HomePageForm form) {

        List<CmmMessageRemindVO> messageRemindVOList = homePageService.getMessageRemindVOList(form.getSiteId(), PJConstants.ReadType.UNREAD);
        ListSortUtils.sort(messageRemindVOList, new String[]{"createDate"}, new boolean[]{false});

        List<HomePageMessageBO> messageList = new ArrayList<>();
        HomePageMessageBO messageBO = new HomePageMessageBO();
        messageBO.setKey(PJConstants.NoticeType.MESSAGE);
        messageBO.setName(PJConstants.NoticeType.MESSAGE);

        List<HomePageMessageItemBO> messageItemList = new ArrayList<>();
        for (CmmMessageRemindVO member : messageRemindVOList){
            HomePageMessageItemBO messageItemBO = new HomePageMessageItemBO();

            messageItemBO.setTitle(member.getBusinessType());
            if (member.getCreateDate() != null) {
                messageItemBO.setDatetime(TimeChangeUtils.changeDateFormat(member.getCreateDate().format(DateTimeFormatter.ofPattern(DateUtils.FORMAT_YMDHMS)), DateUtils.FORMAT_YMDHMS, CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS));
            }
            messageItemBO.setDescription(member.getMessage());
            messageItemList.add(messageItemBO);
        }
        messageBO.setList(messageItemList);

        messageList.add(messageBO);
        return messageList;
    }

    public List<HomePageMessageItemBO> getMessageList(HomePageForm form) {

        List<CmmMessageRemindVO> messageRemindVOList = homePageService.getMessageRemindVOList(form.getSiteId(), PJConstants.ReadType.UNREAD);
        ListSortUtils.sort(messageRemindVOList, new String[]{"createDate"}, new boolean[]{false});

        List<HomePageMessageItemBO> messageItemList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(messageRemindVOList)) {
            for (CmmMessageRemindVO member : messageRemindVOList) {
                HomePageMessageItemBO messageItemBO = new HomePageMessageItemBO();
                messageItemBO.setReadType(member.getReadType());
                messageItemBO.setDatetime(member.getCreateDate().format(DateTimeFormatter.ofPattern(DateUtils.FORMAT_YMDHMS)));
                messageItemBO.setDescription(member.getMessage());
                messageItemBO.setMessageRemindId(member.getMessageRemindId());
                messageItemBO.setReadCategoryType(member.getReadCategoryType());
                messageItemList.add(messageItemBO);
            }
        }
        return messageItemList;
    }

    public void messageRead(HomePageForm form) {

        if (!CollectionUtils.isEmpty(form.getMessageRemindIdList())) {

            List<CmmMessageRemindVO> messageRemindVOList = homePageService.getMessageRemindByIdVOList(form.getMessageRemindIdList());
            messageRemindVOList.forEach(member -> {
                member.setReadType(PJConstants.ReadType.READ);
                member.setReadDate(LocalDateTime.now());
                member.setReadUser(form.getUserId());
            });
            homePageService.messageRead(messageRemindVOList, form.getSiteId());
        }
    }

    public void logInChangeLastLoginTime(HomePageForm form) {

        homePageService.logInChangeLastLoginTime(form);
    }
}
