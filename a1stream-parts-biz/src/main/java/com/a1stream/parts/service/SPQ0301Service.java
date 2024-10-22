package com.a1stream.parts.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.a1stream.domain.bo.parts.SPQ030101BO;
import com.a1stream.domain.form.parts.SPQ030101Form;
import com.a1stream.domain.repository.ReceiptPoItemRelationRepository;
import com.a1stream.domain.repository.ReceiptSlipRepository;
import com.a1stream.domain.repository.StoringLineItemRepository;
import com.a1stream.domain.vo.ReceiptPoItemRelationVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
 *
* 功能描述:Parts Receive And Register Inquiry
*
* mid2287
* 2024年6月6日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/06   Wang Nan      New
 */
@Service
public class SPQ0301Service {

    @Resource
    private ReceiptSlipRepository receiptSlipRepository;

    @Resource
    private StoringLineItemRepository storingLineItemRepository;

    @Resource
    private ReceiptPoItemRelationRepository receiptPoItemRelationRepository;

    public List<SPQ030101BO> getReceiptSlipList(SPQ030101Form form, String siteId) {
        return receiptSlipRepository.getReceiptSlipList(form, siteId);
    }

    public List<SPQ030101BO> getPartsReceiveListDetail(@RequestBody final SPQ030101Form form, String siteId) {
        return storingLineItemRepository.getPartsReceiveListDetail(form, siteId);
    }

    public List<ReceiptPoItemRelationVO> findReceiptPoItemRelationVOList(String purchaseOrderNo, String siteId) {
        return BeanMapUtils.mapListTo(receiptPoItemRelationRepository.findByPurchaseOrderNoAndSiteId(purchaseOrderNo, siteId), ReceiptPoItemRelationVO.class);
    }

    public List<SPQ030101BO> getReceiptSlipListByReceiptSlipItemId(List<Long> list, String siteId) {
        return receiptSlipRepository.getReceiptSlipListByReceiptSlipItemId(list, siteId);
    }

    public List<SPQ030101BO> getPartsReceiveAndRegisterPrintList(SPQ030101Form form, String siteId) {
        return receiptSlipRepository.getPartsReceiveAndRegisterPrintList(form, siteId);
    }
}
