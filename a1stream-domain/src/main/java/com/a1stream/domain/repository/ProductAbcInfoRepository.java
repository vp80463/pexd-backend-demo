package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.SysUserRepositoryCustom;
import com.ymsl.plugins.userauth.entity.UserAuthUserInfo;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ProductAbcInfoRepository extends JpaExtensionRepository<UserAuthUserInfo, String>, SysUserRepositoryCustom {


}
