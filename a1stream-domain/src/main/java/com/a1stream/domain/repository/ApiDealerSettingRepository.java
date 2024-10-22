package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ApiDealerSetting;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ApiDealerSettingRepository extends JpaExtensionRepository<ApiDealerSetting, Long> {

}
