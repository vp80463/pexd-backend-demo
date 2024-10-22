package com.a1stream.master.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.PJConstants.UserStatusSub;
import com.a1stream.common.manager.CognitoManager;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.common.RoleBO;
import com.a1stream.domain.bo.master.CMQ070101BO;
import com.a1stream.domain.bo.master.CMQ070102BO;
import com.a1stream.domain.entity.CmmPerson;
import com.a1stream.domain.form.master.CMQ070101Form;
import com.a1stream.domain.repository.CmmPersonRepository;
import com.a1stream.domain.repository.SysUserAuthorityRepository;
import com.a1stream.domain.repository.SysUserRepository;
import com.a1stream.domain.vo.CmmPersonVO;
import com.a1stream.domain.vo.SysUserAuthorityVO;
import com.a1stream.domain.vo.SysUserVO;
import com.ymsl.plugins.userauth.entity.UserAuthUserAuthority;
import com.ymsl.plugins.userauth.entity.UserAuthUserInfo;
import com.ymsl.plugins.userauth.repository.UserAuthorityRepository;
import com.ymsl.plugins.userauth.repository.UserManageRepository;
import com.ymsl.plugins.userauth.util.JsonUtils;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

@Service
public class CMQ0701Service {

    @Resource
    private SysUserRepository sysUserRepo;

    @Resource
    private UserManageRepository userManageRepo;

    @Resource
    private UserAuthorityRepository userAuthorityRepo;

    @Resource
    private SysUserAuthorityRepository sysUserAuthorityRepo;

    @Resource
    private CmmPersonRepository cmmPersonRepo;

    @Resource
    private CognitoManager cognitoMgr;

    public List<CMQ070101BO> findUserList(CMQ070101Form model, String siteId) {

        return sysUserRepo.findUserList(model, siteId);
    }

    public CMQ070102BO getUserDetail(String userId) {

        CMQ070102BO userInfo = sysUserRepo.getUserDetail(userId);

        SysUserAuthorityVO userAuthority = BeanMapUtils.mapTo(sysUserAuthorityRepo.findFirstByUserId(userId), SysUserAuthorityVO.class);

        if (userAuthority != null) {

            userInfo.setRoleList(JsonUtils.toBeanList(userAuthority.getRoleList(), RoleBO.class));
        }

        return userInfo;
    }

    public SysUserAuthorityVO findUserAuthorityByUserId(String userId) {

        return BeanMapUtils.mapTo(sysUserAuthorityRepo.findFirstByUserId(userId), SysUserAuthorityVO.class);
    }

    public SysUserVO findUserById(String userId) {

        return BeanMapUtils.mapTo(sysUserRepo.findById(userId), SysUserVO.class);
    }

    public boolean findUserExistByCode(String userCd) {

        return sysUserRepo.existsByUserCode(userCd);
    }

    public CmmPersonVO findPersonById(Long personId) {

        return BeanMapUtils.mapTo(cmmPersonRepo.findById(personId), CmmPersonVO.class);
    }

    public List<CmmPersonVO> findPersonByUserId(String userId) {

        return BeanMapUtils.mapListTo(cmmPersonRepo.findByUserId(userId), CmmPersonVO.class);
    }

    public void saveUser(SysUserVO sysUserVO, SysUserAuthorityVO sysUserAuthorityVO, List<CmmPersonVO> cmmPersonVOList) {

        this.saveBaseUserInfo(sysUserVO, sysUserAuthorityVO, cmmPersonVOList);
        //新增用户时，创建cognito用户池，并根据具体值失效处理
        cognitoMgr.cognitoRegisterUser(sysUserVO.getUserCode(), sysUserVO.getMail());
        //根据生失效日，使用户池失效
        String currentDate = ComUtil.nowDate();
        if (ComUtil.date2str(sysUserVO.getEffectiveDate()).compareTo(currentDate) > 0
                || ComUtil.date2str(sysUserVO.getExpiredDate()).compareTo(currentDate) < 0
                || !StringUtils.equals(sysUserVO.getType(), UserStatusSub.ACTIVE.getCodeDbid())) {
            cognitoMgr.cognitoDisableUser(sysUserVO.getUserCode());
        }
    }

    public void updateUser(SysUserVO sysUserVO, SysUserAuthorityVO sysUserAuthorityVO, List<CmmPersonVO> cmmPersonVOList) {

        this.saveBaseUserInfo(sysUserVO, sysUserAuthorityVO, cmmPersonVOList);
        //根据生效日，使用户失效/生效
        String currentDate = ComUtil.nowDate();
        if (ComUtil.date2str(sysUserVO.getEffectiveDate()).compareTo(currentDate) > 0
                || ComUtil.date2str(sysUserVO.getExpiredDate()).compareTo(currentDate) < 0
                || !StringUtils.equals(sysUserVO.getType(), UserStatusSub.ACTIVE.getCodeDbid())) {
            cognitoMgr.cognitoDisableUser(sysUserVO.getUserCode());
        }
        else {
            cognitoMgr.cognitoEnsableUser(sysUserVO.getUserCode());
        }
    }

    private void saveBaseUserInfo(SysUserVO sysUserVO, SysUserAuthorityVO sysUserAuthorityVO, List<CmmPersonVO> cmmPersonVOList) {

        userManageRepo.save(BeanMapUtils.mapTo(sysUserVO, UserAuthUserInfo.class));

        if (sysUserAuthorityVO != null) {
            userAuthorityRepo.save(BeanMapUtils.mapTo(sysUserAuthorityVO, UserAuthUserAuthority.class));
        }

        if (cmmPersonVOList != null && !cmmPersonVOList.isEmpty()) {
            cmmPersonRepo.saveInBatch(BeanMapUtils.mapListTo(cmmPersonVOList, CmmPerson.class));
        }
    }

    public void resetUserPassword(String userCode, String email, String effectiveDate, String expiredDate, String activeType) {
        cognitoMgr.cognitoResetUser(userCode, email);
        //根据生失效日，使用户池失效
        String currentDate = ComUtil.nowDate();
        if (effectiveDate.compareTo(currentDate) > 0
                || expiredDate.compareTo(currentDate) < 0
                || !StringUtils.equals(activeType, UserStatusSub.ACTIVE.getCodeDbid())) {
            cognitoMgr.cognitoDisableUser(userCode);
        }
    }
}
