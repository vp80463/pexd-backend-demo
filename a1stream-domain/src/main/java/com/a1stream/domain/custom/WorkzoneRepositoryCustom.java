package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.parts.DIM020501BO;

public interface WorkzoneRepositoryCustom {

    List<DIM020501BO> getWorkzoneMaintenanceInfo(String siteId, Long personId);
}
