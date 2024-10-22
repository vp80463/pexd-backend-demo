package com.a1stream.web.app.controller.common;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.facade.HomePageFacade;
import com.a1stream.common.facade.MenuCheckFacade;
import com.a1stream.common.facade.MenuFacade;
import com.a1stream.common.model.BaseResult;
import com.a1stream.common.model.HomePageForm;
import com.a1stream.common.model.MenuCheckBO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;
import com.ymsl.solid.web.usercontext.UserDetailsAccessor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author dong zhen
 */
@RestController
@RequestMapping("common/homePage")
@FunctionId("homePage")
@Slf4j
public class HomePageController implements RestProcessAware {

    @Resource
    private HomePageFacade homePageFacade;

    @Resource
    private MenuCheckFacade menuCheckFac;

    @Resource
    private MenuFacade menuFacade;

    /**
     * 处理首页数据请求的控制器方法。
     * 通过该方法获取首页展示所需的数据。
     *
     * @param form 首页数据请求表单，包含用户请求数据的详细信息。
     * @param uc 已认证用户的详细信息，包括用户ID、公司代码和经销商代码等。
     * @return 包含首页数据的结果对象。
     */
    @PostMapping(value = "/homePageData.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult homePageData(@RequestBody final HomePageForm form, @AuthenticationPrincipal final PJUserDetails uc) {

        BaseResult result = new BaseResult();
        form.setBaseSiteId(uc.getCompanyCode());
        form.setRealSiteId(uc.getDealerCode());
        form.setUserId(uc.getUserId());
        form.setPointId(uc.getDefaultPointId());
        form.setUserHabitTypeId(PJConstants.UserHabitConstant.QUICKMENUITEM);
        form.getNotifyTypeIdList().add(PJConstants.AnnounceType.ANNOUNCE_NORMAL);
        form.getNotifyTypeIdList().add(PJConstants.AnnounceType.ANNOUNCE_EMERGENCY);
        form.setNowDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
        result.setData(homePageFacade.getHomePageData(form));
        return result;
    }

    /**
     * 通过用户认证信息获取用户角色菜单列表。
     * <p>
     * 该方法针对特定的URL路径和媒体类型配置，旨在处理JSON格式的POST请求，返回用户角色相关的菜单列表。
     * 使用@AuthenticationPrincipal注解，以便将当前认证的用户详情作为方法参数，无需直接从安全上下文中提取。
     * </p>
     *
     * @param uc 当前认证的用户详情，包含用户ID等信息。
     * @return 用户角色对应的菜单列表，以JSON字符串数组的形式返回。
     */
    @PostMapping(value = "/menuList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object[] findUserRoleList(@AuthenticationPrincipal final PJUserDetails uc) {

        return menuFacade.getMenuJsonStrByUserId(uc.getUserId());
    }

    /**
     * 处理获取用户上次登录时间的请求。
     */
    @PostMapping(value = "/lastLoginTime.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult lastLoginTime(@RequestBody final HomePageForm form, @AuthenticationPrincipal final PJUserDetails uc) {

        BaseResult result = new BaseResult();
        if (StringUtils.isNotEmpty(uc.getLastLoginDateTime())){

            String lastLoginDateTimeStr = uc.getLastLoginDateTime();
            SimpleDateFormat originalFormat = new SimpleDateFormat(DateUtils.FORMAT_YMDHMS);
            Date lastLoginDateTime;
            try {
                lastLoginDateTime = originalFormat.parse(lastLoginDateTimeStr);
                SimpleDateFormat newFormat = new SimpleDateFormat(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS);
                String formattedDateTime = newFormat.format(lastLoginDateTime);
                result.setData(formattedDateTime);
            } catch (ParseException e) {
                log.error("时间转换失败", e);
                result.setData("Acquisition failed");
            }
        }
        return result;
    }

    /**
     * 获取用户默认据点信息
     */
    @PostMapping(value = "/defaultPoint.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult defaultPoint(@RequestBody final HomePageForm form, @AuthenticationPrincipal final PJUserDetails uc) {

        BaseResult result = new BaseResult();
        HashMap<String, Object> pointMap = new HashMap<>();
        pointMap.put("pointCd", uc.getDefaultPointCd());
        pointMap.put("pointId", uc.getDefaultPointId());
        pointMap.put("pointNm", uc.getDefaultPointNm());
        result.setData(pointMap);
        return result;
    }

    /**
     * 获取用户可选择的所有据点信息接口。
     */
    @PostMapping(value = "/pointList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult pointList(@RequestBody final HomePageForm form, @AuthenticationPrincipal final PJUserDetails uc) {

        BaseResult result = new BaseResult();
        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        form.setBaseSiteId(uc.getCompanyCode());
        result.setData(homePageFacade.getPointList(form));
        return result;
    }

    /**
     * 重置用户据点信息接口。
     * 通过此接口，用户可以重新设置他们的默认据点，同时更新与据点相关的用户习惯信息。
     *
     * @param form 包含用户选择的新默认据点信息的请求体。
     * @return 返回一个包含操作结果和新默认据点详细信息的基本结果对象。
     */
    @PostMapping(value = "/resetPointInformation.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult resetPointInformation(@RequestBody final HomePageForm form) {

        BaseResult result = new BaseResult();
        // 取得当前用户
        PJUserDetails uc = UserDetailsAccessor.DEFAULT.get();

        // 设置UC的字段
        MstFacilityVO mstFacilityVO = homePageFacade.getMstFacilityVO(form.getPointId());
        uc.setDefaultPointCd(mstFacilityVO.getFacilityCd());
        uc.setDefaultPointId(mstFacilityVO.getFacilityId());
        uc.setDefaultPointNm(mstFacilityVO.getFacilityNm());

        // 构造 Authentication
        Authentication newAuth = new UsernamePasswordAuthenticationToken(uc,null,uc.getAuthorities());

        // 反向设置到 session 里
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        RequestContextHolder.currentRequestAttributes().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext(), RequestAttributes.SCOPE_SESSION);

        // 重置用户习惯
        form.setUserId(uc.getUserId());
        form.setSiteId(uc.getDealerCode());
        form.setUserHabitTypeId(PJConstants.UserHabitConstant.DEFAULTPOINT);
        homePageFacade.resetUserHabit(form);

        HashMap<String, Object> pointMap = new HashMap<>();
        pointMap.put("pointCd", uc.getDefaultPointCd());
        pointMap.put("pointId", uc.getDefaultPointId());
        pointMap.put("pointNm", uc.getDefaultPointNm());
        result.setData(pointMap);
        result.setMessage(BaseResult.SUCCESS_MESSAGE);
        return result;
    }

    @PostMapping(value = "/changeLanguage.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void changeLanguage(@RequestBody final HomePageForm form) {

        // 取得当前用户
        PJUserDetails uc = UserDetailsAccessor.DEFAULT.get();
        uc.setAppLocale(new Locale(form.getLanguage()));

        // 构造 Authentication
        Authentication newAuth = new UsernamePasswordAuthenticationToken(uc,null,uc.getAuthorities());

        // 反向设置到 session 里
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        RequestContextHolder.currentRequestAttributes().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext(), RequestAttributes.SCOPE_SESSION);

        // 重置用户习惯
        form.setUserId(uc.getUserId());
        form.setSiteId(uc.getDealerCode());
        form.setUserHabitTypeId(PJConstants.UserHabitConstant.LANGUAGE);
        homePageFacade.resetUserHabit(form);
    }

    /**
     * 获取状态栏消息数据接口。
     */
    @PostMapping(value = "/statusBarMessageData.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult statusBarMessageData(@RequestBody final HomePageForm form, @AuthenticationPrincipal final PJUserDetails uc) {

        BaseResult result = new BaseResult();
        form.setSiteId(uc.getDealerCode());
        result.setData(homePageFacade.getStatusBarMessageData(form));
        return result;
    }

    /**
     * 获取消息列表数据接口（跳转到消息一览页面的时候使用的）。
     */
    @PostMapping(value = "/listMessageData.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult listMessageData(@RequestBody final HomePageForm form, @AuthenticationPrincipal final PJUserDetails uc) {

        BaseResult result = new BaseResult();
        form.setSiteId(uc.getDealerCode());
        result.setData(homePageFacade.getMessageList(form));
        return result;
    }

    /**
     * 消息阅读（跳转到消息一览页面的时候使用的）。
     */
    @PostMapping(value = "/messageRead.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult messageRead(@RequestBody final HomePageForm form, @AuthenticationPrincipal final PJUserDetails uc) {

        BaseResult result = new BaseResult();
        form.setSiteId(uc.getDealerCode());
        form.setUserId(uc.getUserId());
        homePageFacade.messageRead(form);
        result.setMessage(BaseResult.SUCCESS_MESSAGE);
        return result;
    }

    /**
     * 处理菜单变更的请求。
     * 当用户在前端请求改变菜单时，这个方法会被调用。它主要负责根据用户的当前状态和请求的数据来更新用户的菜单配置。
     * 仅当请求的菜单代码不等于"home"时，才进行后续处理。
     *
     * @param form 包含菜单变更相关数据的表单对象，其中包含了菜单代码等信息。
     * @param uc 当前登录的用户详情，包含了用户的标识符以及所属公司和经销商的代码。
     */
    @PostMapping(value = "/menuChange.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void menuChange(@RequestBody final HomePageForm form, @AuthenticationPrincipal final PJUserDetails uc) {

        if (!"home".equals(form.getMenuCd())){
            form.setUserId(uc.getUserId());
            form.setBaseSiteId(uc.getCompanyCode());
            form.setRealSiteId(uc.getDealerCode());
            homePageFacade.menuChange(form);
        }
    }

    /**
     * 菜单检查：盘点、夜间作业等
     *
     */
    @PostMapping(value = "/menuCheck.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public MenuCheckBO menuCheck(@RequestBody final HomePageForm form, @AuthenticationPrincipal final PJUserDetails uc) {

        MenuCheckBO menuCheck = new MenuCheckBO();
        if (!"home".equals(form.getMenuCd())) {
            // 夜间时段
            menuCheckFac.isMenuAccessTimePeriod(menuCheck);
            // 菜单检查：盘点状态
            menuCheckFac.isPartsStockTaking(menuCheck, uc.getDealerCode(), form.getMenuCd(), uc.getDefaultPointId());
        }

        return menuCheck;
    }

    /**
     * 登录时，更新用户上次登录时间。
     */
    @PostMapping(value = "/logInChangeLastLoginTime.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void logInChangeLastLoginTime(@RequestBody final HomePageForm form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setUserId(uc.getUserId());
        form.setSiteId(uc.getDealerCode());
        form.setUserHabitTypeId(PJConstants.UserHabitConstant.LASTLOGINDATETIME);
        homePageFacade.logInChangeLastLoginTime(form);
    }
}