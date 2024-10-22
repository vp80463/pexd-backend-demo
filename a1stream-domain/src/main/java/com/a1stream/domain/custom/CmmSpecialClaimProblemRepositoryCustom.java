package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.master.CMQ060302BO;
import com.a1stream.domain.form.master.CMQ060302Form;

public interface CmmSpecialClaimProblemRepositoryCustom {

    List<CMQ060302BO> findProblemDetailList(CMQ060302Form form, String problemCategory);
}