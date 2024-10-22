package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.parts.SPM020901BO;
import com.a1stream.domain.bo.parts.SPM021101BO;
import com.a1stream.domain.bo.parts.SPM030602BO;
import com.a1stream.domain.form.parts.SPM020901Form;
import com.a1stream.domain.form.parts.SPM030602Form;
import com.a1stream.domain.vo.DeliveryOrderVO;

public interface DeliveryOrderRepositoryCustom {

    List<SPM030602BO> getPartsPointTransferReceiptList(SPM030602Form form);

    List<SPM021101BO> findRePurchaseAndRetail(String siteId
                                            , Long facilityId
                                            , String deliveryStatus
                                            , Long fromOrgnazationId
                                            , Long toOrgnazationId
                                            , String duNo);

    List<SPM021101BO> findReturnAndTransfer(String siteId
                                          , Long facilityId
                                          , String deliveryStatus
                                          , String inventoryTransactionType
                                          , String duNo);

    public List<SPM020901BO> findPickingDiscEntryList(SPM020901Form form);

    public DeliveryOrderVO findDeliveryOrder(String siteId, Long facilityId, String seqNo);

}
