package com.a1stream.domain.repository;

import com.a1stream.domain.custom.SysMenuRepositoryCustom;
import com.a1stream.domain.entity.SysMenu;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author dong zhen
 */
@Repository
public interface SysMenuRepository extends JpaExtensionRepository<SysMenu, String>, SysMenuRepositoryCustom {

    List<SysMenu> findByMenuIdIn(List<String> menuIdlist);

    List<SysMenu> findByMenuCodeIn(List<String> menuCdlist);
}
