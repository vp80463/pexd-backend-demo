package com.a1stream.domain.custom;

import com.a1stream.domain.bo.service.SVM010604BO;
import com.a1stream.domain.form.service.SVM010604Form;

import java.util.List;

/**
 * @author dong zhen
 */
public interface CmmLeadManagement2sRepositoryCustom {

    List<SVM010604BO> retrieveSalesLeadList(SVM010604Form form);
}
