package com.a1stream.master.facade;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.UserStatusSub;
import com.a1stream.common.logic.ValidateLogic;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.common.RoleBO;
import com.a1stream.domain.bo.master.CMQ070101BO;
import com.a1stream.domain.bo.master.CMQ070102BO;
import com.a1stream.domain.form.master.CMQ070101Form;
import com.a1stream.domain.form.master.CMQ070102Form;
import com.a1stream.domain.vo.CmmPersonVO;
import com.a1stream.domain.vo.SysUserAuthorityVO;
import com.a1stream.domain.vo.SysUserVO;
import com.a1stream.master.service.CMQ0701Service;
import com.ymsl.plugins.userauth.util.JsonUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

@Component
public class CMQ0701Facade {

    @Resource
    private CMQ0701Service cmq0701Service;
    @Resource
    private ValidateLogic validateLogic;

    public List<CMQ070101BO> findUserList(CMQ070101Form model, String siteId) {

        return cmq0701Service.findUserList(model, siteId);
    }

    public CMQ070102BO getUserDetail(String userId) {

         return cmq0701Service.getUserDetail(userId);
    }

    public void resetUserPassword(CMQ070102Form model, String siteId){

        //密码重置时，事务可以拆开。更改现有user数据后，再触发user pool的重构
        this.saveOrUpdateUser(model, siteId);
        cmq0701Service.resetUserPassword(model.getUserCd(), model.getEmail(), model.getDateFrom(), model.getDateTo(), StringUtils.equals(model.getStatus(), CommonConstants.CHAR_Y) ? UserStatusSub.ACTIVE.getCodeDbid() : UserStatusSub.INACTIVE.getCodeDbid());
    }

    public void saveOrUpdateUser(CMQ070102Form model, String siteId) {

        //更新前验证
        this.validateNewOrModifyUserInfo(model);

        SysUserVO sysUserVO = this.buildSysUserVO(model, siteId);

        SysUserAuthorityVO sysUserAuthorityVO = this.buildSysUserAuthorityVO(model, sysUserVO);

        List<CmmPersonVO> updCmmPersonVOList = this.buildCmmPersonVO(model, sysUserVO);

        if (StringUtils.isEmpty(model.getUserId())) {
            cmq0701Service.saveUser(sysUserVO, sysUserAuthorityVO, updCmmPersonVOList);
        }
        else {
            cmq0701Service.updateUser(sysUserVO, sysUserAuthorityVO, updCmmPersonVOList);
        }
    }

    public void deleteUser(CMQ070101Form model) {

        SysUserVO sysUserVO = cmq0701Service.findUserById(model.getUserId());

        // 将 LocalDate 转换为 Date
        sysUserVO.setExpiredDate(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        sysUserVO.setType(UserStatusSub.INACTIVE.getCodeDbid());

        cmq0701Service.updateUser(sysUserVO, null, null);
    }

    private void validateNewOrModifyUserInfo(CMQ070102Form model) {

        //新建时---UserCode在DB中不重复（跨dealer也不能重复）
        if (StringUtils.isEmpty(model.getUserId()) && cmq0701Service.findUserExistByCode(model.getUserCd())) {
        	throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00309", new String[] { ComUtil.t("label.userCode"), model.getUserCd(), ComUtil.t("label.tablePerson")}));
        }
        //employeeId在DB不存在时，报错
        CmmPersonVO cmmPersonVO = cmq0701Service.findPersonById(model.getEmployeeId());
        if (cmmPersonVO == null) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.employee"), model.getEmployee(), CodedMessageUtils.getMessage("title.employeeInfoList_01")}));
        }
        //employeeId在DB存在，且已有关联user，且user和当前userID不同时，报错
        if (!StringUtils.isEmpty(cmmPersonVO.getUserId()) && !StringUtils.equals(cmmPersonVO.getUserId(), model.getUserId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00309", new String[] { ComUtil.t("label.userCode"), model.getUserCd(), ComUtil.t("label.tablePerson")}));
        }
        //检查email格式
        validateLogic.validateEmail(model.getEmail());
    }

    private List<CmmPersonVO> buildCmmPersonVO(CMQ070102Form model, SysUserVO sysUserVO) {

        List<CmmPersonVO> result = new ArrayList<>();

        //将旧职工的user关联字段设置为null
        List<CmmPersonVO> oldPersonList = cmq0701Service.findPersonByUserId(model.getUserId())
                                                        .stream()
                                                        .map(person -> {person.setUserId(null); return person;})
                                                        .toList();

        result.addAll(oldPersonList);

        //设置当前职工的user关联字段
        CmmPersonVO cmmPersonVO = cmq0701Service.findPersonById(model.getEmployeeId());

        if (cmmPersonVO != null) {

            cmmPersonVO.setUserId(sysUserVO.getUserId());
            result.add(cmmPersonVO);
        }

        return result;
    }

    private SysUserAuthorityVO buildSysUserAuthorityVO(CMQ070102Form model, SysUserVO sysUserVO) {

        SysUserAuthorityVO sysUserAuthorityVO = !StringUtils.isEmpty(model.getUserId()) ? cmq0701Service.findUserAuthorityByUserId(model.getUserId()) : new SysUserAuthorityVO();

        sysUserAuthorityVO.setUserId(sysUserVO.getUserId());
        sysUserAuthorityVO.setSiteId(sysUserVO.getSiteId());

        //插件系表需手动设值
        sysUserAuthorityVO.setCreatedBy(StringUtils.isNotBlank(model.getUserId()) ? sysUserAuthorityVO.getCreatedBy() : CommonConstants.CHAR_ADMIN);
        sysUserAuthorityVO.setDateCreated(StringUtils.isNotBlank(model.getUserId()) ? sysUserAuthorityVO.getDateCreated() : LocalDateTime.now());
        sysUserAuthorityVO.setLastUpdatedBy(CommonConstants.CHAR_ADMIN);
        sysUserAuthorityVO.setLastUpdated(LocalDateTime.now());

        sysUserAuthorityVO.setRoleList(model.getRoleList().isEmpty() ? null : JsonUtils.toString(model.getRoleList().stream()
                                                                                    .collect(Collectors.toMap(RoleBO::getRoleId, roleBO -> roleBO, (existing, replacement) -> existing))
                                                                                    .values().stream().toList()));

        return sysUserAuthorityVO;
    }

    private SysUserVO buildSysUserVO(CMQ070102Form model, String siteId) {

        SysUserVO sysUserVO = !StringUtils.isEmpty(model.getUserId()) ? cmq0701Service.findUserById(model.getUserId()) : SysUserVO.create();

        sysUserVO.setSiteId(siteId);
        sysUserVO.setPersonId(model.getEmployeeId().toString());
        sysUserVO.setUserCode(StringUtils.upperCase(model.getUserCd()));
        sysUserVO.setMail(model.getEmail());
        sysUserVO.setPassword(CommonConstants.CHAR_ASTERISK);//系统表密码非空限制

        sysUserVO.setEffectiveDate(ComUtil.str2Date(model.getDateFrom()));
        sysUserVO.setExpiredDate(ComUtil.str2Date(model.getDateTo()));
        sysUserVO.setType(StringUtils.equals(model.getStatus(), CommonConstants.CHAR_Y) ? UserStatusSub.ACTIVE.getCodeDbid() : UserStatusSub.INACTIVE.getCodeDbid());

        //插件系表需手动设值
        sysUserVO.setCreatedBy(StringUtils.isNotBlank(model.getUserId()) ? sysUserVO.getCreatedBy() : CommonConstants.CHAR_ADMIN);
        sysUserVO.setDateCreated(StringUtils.isNotBlank(model.getUserId()) ? sysUserVO.getDateCreated() : LocalDateTime.now());
        sysUserVO.setLastUpdatedBy(CommonConstants.CHAR_ADMIN);
        sysUserVO.setLastUpdated(LocalDateTime.now());

        return sysUserVO;
    }
}
