package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.SysUserAuthorityRepositoryCustom;
import com.ymsl.plugins.userauth.entity.UserAuthUserAuthority;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;

@Repository
public interface SysUserAuthorityRepository extends JpaExtensionRepository<UserAuthUserAuthority, String> ,SysUserAuthorityRepositoryCustom {

    public UserAuthUserAuthority findFirstByUserId(String userId);

    public UserAuthUserAuthority findFirstBySiteIdAndUserId(String siteId, String userId);
}
