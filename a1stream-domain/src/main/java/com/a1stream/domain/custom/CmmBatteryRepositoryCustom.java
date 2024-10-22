package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.service.BatteryBO;

public interface CmmBatteryRepositoryCustom {

    List<BatteryBO> listServiceBatteryByMotorId(Long serializedProId);
}
