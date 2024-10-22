package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.service.SVM010701BO;
import com.a1stream.domain.form.service.SVM010701Form;
import com.a1stream.domain.form.service.SVM010702Form;

public interface ServiceScheduleRepositoryCustom {

    List<SVM010701BO> findServiceReservationList(SVM010701Form form);

    Integer getServiceScheduleRowCount(SVM010702Form form);
}