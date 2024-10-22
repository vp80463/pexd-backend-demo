package com.a1stream.master.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.entity.SysAdditionalSetting;
import com.a1stream.domain.repository.SysAdditionalSettingRepository;
import com.a1stream.domain.repository.SysMenuRepository;

import jakarta.annotation.Resource;

@Service
public class CMM0704Service {

    @Resource
    private SysMenuRepository sysMenuRepo;

    @Resource
    private SysAdditionalSettingRepository sysAddSetRepo;

    public String getMenuJson() {

        return sysMenuRepo.findMenuJson();
    }

    public List<SysAdditionalSetting> findBySiteId(String siteId) {

        return sysAddSetRepo.findBySiteId(siteId);
    }

    public void saveSysAdditionalSettings(List<SysAdditionalSetting> saveAdditionalSettings, String siteId) {

        sysAddSetRepo.deleteAdditionalSetting(siteId);

        sysAddSetRepo.saveInBatch(saveAdditionalSettings);
    }
}
