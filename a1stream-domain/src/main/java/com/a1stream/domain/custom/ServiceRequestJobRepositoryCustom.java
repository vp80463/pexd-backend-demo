package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.service.SVM020102BO;
import com.a1stream.domain.form.service.SVM020102Form;

public interface ServiceRequestJobRepositoryCustom {

    List<SVM020102BO> getRepairJobDetailList(SVM020102Form form);
}