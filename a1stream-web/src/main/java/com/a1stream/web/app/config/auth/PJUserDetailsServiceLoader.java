package com.a1stream.web.app.config.auth;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.SystemParameterType;
import com.a1stream.common.constants.PJConstants.UserHabitConstant;
import com.a1stream.domain.entity.CmmPerson;
import com.a1stream.domain.entity.CmmSiteMaster;
import com.a1stream.domain.entity.MstFacility;
import com.a1stream.domain.entity.SystemParameter;
import com.a1stream.domain.entity.UserHabit;
import com.a1stream.domain.repository.CmmPersonRepository;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.repository.UserHabitRepository;
import com.ymsl.plugins.userauth.entity.UserAuthUserInfo;
import com.ymsl.plugins.userauth.repository.UserManageRepository;
import com.ymsl.solid.base.constants.BaseConstants;
import com.ymsl.solid.base.json.JsonUtils;
import com.ymsl.solid.base.json.exception.JsonOperationException;
import com.ymsl.solid.base.util.CollectionUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.context.auth.BaseUserDetailsService;
import com.ymsl.solid.oauth2.client.service.CommonOidcUserService;

import jakarta.annotation.Resource;

/**
 * User details service for project.
 * Load user details from database or other sources.
 * Generate temp user details for password validation via {@link #loadUserPwdByUsername(String)},
 * and create complete user details via {@link #loadUserByUsername(String)}.
 */
public class PJUserDetailsServiceLoader extends CommonOidcUserService implements BaseUserDetailsService {

    @Resource
    private UserManageRepository userManageRepository;

    @Resource
    private CmmPersonRepository cmmPersonRepository;

    @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepository;

    @Resource
    private UserHabitRepository userHabitRepository;

    @Resource
    private SystemParameterRepository systemParameterRepository;

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    // add method for creating PJUserDetails
    @Override
    public OidcUser createUserDetails(OidcUser oidcUser, OidcUserRequest userRequest) {
      PJUserDetails user = new PJUserDetails(oidcUser.getName(), oidcUser.getAuthorities(), oidcUser.getIdToken(), userRequest, oidcUser.getClaims(), oidcUser.getUserInfo(), oidcUser.getAttributes());
      user.setUserCode(oidcUser.getName());
      //user.setCompanyCode("0000");
      user.setAttributes(oidcUser.getAttributes());
      user.setClaims(oidcUser.getClaims());
      List<UserAuthUserInfo> userInfoList = userManageRepository.findByUserCode(oidcUser.getName());
      if(CollectionUtils.isNotEmpty(userInfoList)) {
          UserAuthUserInfo userInfo = userInfoList.get(0);
          return setUc(user, userInfo.getSiteId(), userInfo.getUserId(), userInfo.getUserCode());
      }else {
          return null;
      }
    }

    @Override
    public UserDetails loadUserByUsername(String userLoginInfo) throws UsernameNotFoundException {

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");

        // reserve the userLoginInfo in case you want to use jwttoken or remember-me service
        PJUserDetails uc = new PJUserDetails(userLoginInfo, "", authorities);

        try {
          Map<String, String> loginInfo = JsonUtils.toMap(userLoginInfo, String.class, String.class);
          // put real userCode in other field
          uc.setUserCode(loginInfo.get(BaseConstants.UserInfo.USER_NAME));
        } catch (JsonOperationException e) {
          // userLoginInfo is not json, so it can be used as user code
            uc.setUserCode(userLoginInfo);
        }

        //这边获取uc需要的信息
        //uc.setAppLocale(Locale.ENGLISH);
        //获取用户登录信息
        List<UserAuthUserInfo> userInfoList = userManageRepository.findByUserCode(uc.getUserCode());
        UserAuthUserInfo userInfo = userInfoList.get(0);
        return setUc(uc, userInfo.getSiteId(), userInfo.getUserId(), userInfo.getUserCode());
    }

    @Override
    public UserDetails loadUserPwdByUsername(String userLoginInfo) throws UsernameNotFoundException {

        String userName = "";

        try {
          Map<String, String> loginInfo = JsonUtils.toMap(userLoginInfo, String.class, String.class);
          if (loginInfo == null || StringUtils.isEmpty(loginInfo.get("username"))) {
            throw new UsernameNotFoundException("no user info");
          }
          userName = loginInfo.get(BaseConstants.UserInfo.USER_NAME);
        } catch (JsonOperationException e) {
          // userLoginInfo is not json, so it can be used as user code
          if (StringUtils.isEmpty(userLoginInfo)) {
            throw new UsernameNotFoundException("no user info");
          }
          userName = userLoginInfo;
        }

        // encoded for user
        List<UserAuthUserInfo> userInfoList = userManageRepository.findByUserCode(userName);
        UserAuthUserInfo userInfo = userInfoList.get(0);
        String passwordFromDb = userInfo.getPassword();
        PJUserDetails uc = new PJUserDetails(userName, passwordFromDb, AuthorityUtils.NO_AUTHORITIES);

        return setUc(uc, userInfo.getSiteId(), userInfo.getUserId(), userInfo.getUserCode());
    }

    private PJUserDetails setUc(PJUserDetails uc, String siteId, String userId, String userCode) {

        uc.setUserCode(userCode);
        uc.setUserId(userId);
        uc.setCompanyCode(siteId);

        //设置dealerCd
        UserHabit userHabit = userHabitRepository.findByUserIdAndSiteIdAndUserHabitTypeId(userId, siteId, UserHabitConstant.ISCHANGEDEALER);
        if (userHabit != null && StringUtils.isNotEmpty(userHabit.getHabitContent())) {
            uc.setDealerCode(userHabit.getHabitContent());
        } else {
            uc.setDealerCode((StringUtils.equals(siteId, CommonConstants.CHAR_DEFAULT_SITE_ID)) || (StringUtils.equals(siteId, CommonConstants.CHAR_YMSLX_SITE_ID)) ? CommonConstants.CHAR_DEFAULT_SITE_ID : siteId);
        }

        //获取员工信息
        CmmPerson personInfo = cmmPersonRepository.findFirstByUserId(userId);
        String personCode = ObjectUtils.isNotEmpty(personInfo) ? personInfo.getPersonCd() : CommonConstants.CHAR_BLANK;
        String personName = ObjectUtils.isNotEmpty(personInfo) ? personInfo.getPersonNm() : CommonConstants.CHAR_BLANK;
        uc.setPersonCode(personCode);
        uc.setPersonName(personName);
        uc.setPersonId(ObjectUtils.isNotEmpty(personInfo) ? personInfo.getPersonId() : null);

        //获取直营店信息
        CmmSiteMaster siteMasterInfo = cmmSiteMasterRepository.findFirstBySiteId(siteId);
        String doFlag = ObjectUtils.isNotEmpty(siteMasterInfo) ? siteMasterInfo.getDoFlag() : CommonConstants.CHAR_N;
        uc.setDoFlag(doFlag);

        //设置本地主机端口
        SystemParameter systemParameter2 = systemParameterRepository.findBySystemParameterTypeId(SystemParameterType.WEBSOCKETPORT);
        uc.setPort(systemParameter2 != null? systemParameter2.getParameterValue() : "");

        //获取userHabit信息
        List<UserHabit> userHabitList = userHabitRepository.findByUserIdAndSiteId(userId, uc.getDealerCode());

        Map<String, UserHabit> userHabitMap = userHabitList.stream().collect(Collectors.toMap(UserHabit::getUserHabitTypeId, e->e));

        //设置登录时间信息
        uc.setLoginDateTime(DateUtils.getCurrentDateString(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS));
        uc.setLastLoginDateTime(ObjectUtils.isNotEmpty(userHabitMap.get(UserHabitConstant.LASTLOGINDATETIME)) ?
                userHabitMap.get(UserHabitConstant.LASTLOGINDATETIME).getHabitContent() : CommonConstants.CHAR_BLANK);

        //设置facility信息
        if(ObjectUtils.isNotEmpty(userHabitMap.get(UserHabitConstant.DEFAULTPOINT))) {
            Long pointId = Long.valueOf(userHabitMap.get(UserHabitConstant.DEFAULTPOINT).getHabitContent());
            MstFacility facilityInfo = mstFacilityRepository.findByFacilityId(pointId);
            String pointCode = ObjectUtils.isNotEmpty(facilityInfo) ? facilityInfo.getFacilityCd() : CommonConstants.CHAR_BLANK;
            String pointName = ObjectUtils.isNotEmpty(facilityInfo) ? facilityInfo.getFacilityNm() : CommonConstants.CHAR_BLANK;
            uc.setDefaultPointCd(pointCode);
            uc.setDefaultPointNm(pointName);
            uc.setDefaultPointId(pointId);
            uc.setDefaultPoint(pointCode + CommonConstants.CHAR_SPACE + pointName);
        }else {
            uc.setDefaultPointCd(CommonConstants.CHAR_BLANK);
            uc.setDefaultPointNm(CommonConstants.CHAR_BLANK);
            uc.setDefaultPoint(CommonConstants.CHAR_BLANK);
        }

        // 语言
        if(userHabitMap.containsKey(UserHabitConstant.LANGUAGE)) {
            String language = userHabitMap.get(UserHabitConstant.LANGUAGE).getHabitContent();
            try {
                uc.setAppLocale(new Locale(language));
            } catch(Exception e) {
                uc.setAppLocale(new Locale(CommonConstants.LANGUAGE_EN));
            }
        }

        //设置税率的使用时间
        // SystemParameter systemParameter = systemParameterRepository.findBySystemParameterTypeId(SystemParameterType.TAXPERIOD);
        SystemParameter systemParameter = systemParameterRepository.findSystemParameterBySiteIdAndSystemParameterTypeId(CommonConstants.CHAR_DEFAULT_SITE_ID, SystemParameterType.TAXPERIOD);
        uc.setTaxPeriod(systemParameter != null? systemParameter.getParameterValue() : "");

        return uc;
    }
}
