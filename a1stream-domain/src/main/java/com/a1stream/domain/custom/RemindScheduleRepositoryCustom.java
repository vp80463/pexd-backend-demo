package com.a1stream.domain.custom;

import com.a1stream.domain.bo.service.SVM010601BO;
import com.a1stream.domain.form.service.SVM010601Form;

import java.util.List;

/**
 * @author dong zhen
 */
public interface RemindScheduleRepositoryCustom {

    List<SVM010601BO> findServiceRemindList(SVM010601Form form);
}
