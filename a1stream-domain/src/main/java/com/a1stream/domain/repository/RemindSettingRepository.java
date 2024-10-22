package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.RemindSetting;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;

@Repository
public interface RemindSettingRepository extends JpaExtensionRepository<RemindSetting, Long> {

    List<RemindSetting> findByRemindType(String remindType);
}
