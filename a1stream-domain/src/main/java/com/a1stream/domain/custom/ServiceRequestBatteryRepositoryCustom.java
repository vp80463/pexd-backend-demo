package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.service.SVM020102BO;
import com.a1stream.domain.form.service.SVM020102Form;

public interface ServiceRequestBatteryRepositoryCustom {

    List<SVM020102BO> getRepairBatteryDetailList(SVM020102Form form);
}