package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.service.SVM011001BO;
import com.a1stream.domain.form.service.SVM011001Form;

public interface CmmPdiRepositoryCustom {

    List<SVM011001BO> getPdiInfoListByProductCategoryId(SVM011001Form form);

    List<SVM011001BO> getPdiInfoList(SVM011001Form form);
}
