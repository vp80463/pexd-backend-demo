package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.data.domain.Page;

import com.a1stream.domain.bo.unit.SDM040103BO;
import com.a1stream.domain.bo.unit.SDM050801BO;
import com.a1stream.domain.bo.unit.SDQ010702DetailBO;
import com.a1stream.domain.bo.unit.SDQ011302BO;
import com.a1stream.domain.bo.unit.SVM010401BO;
import com.a1stream.domain.form.service.SVM010702Form;
import com.a1stream.domain.form.service.SVM011001Form;
import com.a1stream.domain.form.unit.SDM050801Form;
import com.a1stream.domain.form.unit.SDQ011301Form;
import com.a1stream.domain.form.unit.SDQ011302Form;
import com.a1stream.domain.form.unit.SVM010401Form;

public interface SerializedProductRepositoryCustom {

    SVM011001Form getModelInfoByFrameNo(SVM011001Form form);

    SVM010702Form getInfoByPlateNo(SVM010702Form form);

    public List<SDQ011302BO> findExportByMotorcycle(SDQ011301Form form);

    public List<SDQ011302BO> findStockInformationInquiryDetail(SDQ011302Form form);

    Page<SVM010401BO> getLocalMcData(SVM010401Form form, String siteId);

    SDM040103BO getMotorCycleInfo(Long serializedProductId);

    List<SDM050801BO> getSpPromoRecList(SDM050801Form form);

    List<SDQ010702DetailBO> getRecManMaintDetail(Long receiptManifestId);
}
