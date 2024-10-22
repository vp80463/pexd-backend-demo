package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.SysRoleRepositoryCustom;
import com.ymsl.plugins.userauth.entity.UserAuthRoleInfo;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;

@Repository
public interface SysRoleRepository extends JpaExtensionRepository<UserAuthRoleInfo, String>, SysRoleRepositoryCustom {
    @Query(value="select role_id from sys_role_info "
               + "where role_code =:roleCd "
               + "and type =:roleType ", nativeQuery=true)
    Long getIdByRoleCdAndRoleType(@Param("roleCd") String roleCd
                                , @Param("roleType") String roleType);

    @Query(value="select role_id from sys_role_info "
               + "where role_code =:roleCd "
               + "and type in(:roleType) ", nativeQuery=true)
    List<Long> getIdByRoleCdAndRoleType(@Param("roleCd") String roleCd
                                      , @Param("roleType")  List<String> roleType);

    @Query(value="select role_id from sys_role_info "
               + "where role_code in(:roleCd)", nativeQuery=true)
    List<Long> getIdByRoleCdIn(@Param("roleCd") List<String> roleCd);
}
