package com.a1stream.domain.custom;

import com.a1stream.domain.bo.unit.SDM050101BO;
import com.a1stream.domain.entity.SysUserAuthority;

public interface SysUserAuthorityRepositoryCustom {

    public SysUserAuthority findUserBySDAndAccount(String userId);

    public SDM050101BO findDetailAndAddFlag(String userId);
}
