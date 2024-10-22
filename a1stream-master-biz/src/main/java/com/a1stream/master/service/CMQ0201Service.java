package com.a1stream.master.service;

import org.springframework.stereotype.Service;

import com.a1stream.domain.entity.UploadAccCatalog;
import com.a1stream.domain.repository.SysRoleRepository;
import com.a1stream.domain.repository.SysUserAuthorityRepository;
import com.a1stream.domain.repository.UploadAccCatalogRepository;
import com.a1stream.domain.vo.SysUserAuthorityVO;
import com.a1stream.domain.vo.UploadAccCatalogVO;
import com.ymsl.solid.base.util.BeanMapUtils;
import java.util.List;
import jakarta.annotation.Resource;


@Service
public class CMQ0201Service {

    @Resource
    private UploadAccCatalogRepository uploadAccCatalogRepository;

    @Resource
    private SysUserAuthorityRepository sysUserAuthorityRepository;

    @Resource
    private SysRoleRepository sysRoleRepository;
    
    public SysUserAuthorityVO getSysUserAuthority(String siteId, String userId) {
        return BeanMapUtils.mapTo(sysUserAuthorityRepository.findFirstBySiteIdAndUserId(siteId,userId),SysUserAuthorityVO.class);
    }

    public List<Long> getRoleIds(String roleCode,List<String> roleType) {
        return sysRoleRepository.getIdByRoleCdAndRoleType(roleCode, roleType);
    }

    public void saveUploadAccCatalog(UploadAccCatalogVO vo) {
        uploadAccCatalogRepository.save(BeanMapUtils.mapTo(vo, UploadAccCatalog.class));
    }

    public UploadAccCatalogVO getUploadAccCatalog(String id) {
        return BeanMapUtils.mapTo(uploadAccCatalogRepository.findByParameterTypeId(id),UploadAccCatalogVO.class);
    }
}
