package com.a1stream.master.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.master.CMM071601BO;
import com.a1stream.domain.entity.QueueEinvoice;
import com.a1stream.domain.entity.QueueTaxAuthority;
import com.a1stream.domain.form.master.CMM071601Form;
import com.a1stream.domain.repository.QueueEinvoiceRepository;
import com.a1stream.domain.repository.QueueTaxAuthorityRepository;
import com.a1stream.domain.vo.QueueEinvoiceVO;
import com.a1stream.domain.vo.QueueTaxAuthorityVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class CMM0716Service {

    @Resource
    private QueueEinvoiceRepository queueEinvoiceRepo;

    @Resource
    private QueueTaxAuthorityRepository queueTaxAuthorityRepo;

    public List<CMM071601BO> getInvoiceCheckResultByInvoice(CMM071601Form form) {
        return queueEinvoiceRepo.getInvoiceCheckResultByInvoice(form);
    }

    public List<CMM071601BO> getInvoiceCheckResultByTaxAuthority(CMM071601Form form) {
        return queueTaxAuthorityRepo.getInvoiceCheckResultByTaxAuthority(form);
    }

    public List<CMM071601BO> getInvoiceCheckResult(CMM071601Form form) {
        return queueEinvoiceRepo.getInvoiceCheckResult(form);
    }

    public List<QueueEinvoiceVO> getQueueEinvoiceVOList(String siteId, List<Long> relatedInvoiceIds) {
        return BeanMapUtils.mapListTo(queueEinvoiceRepo.findBySiteIdAndRelatedInvoiceIdIn(siteId, relatedInvoiceIds), QueueEinvoiceVO.class);
    }

    public List<QueueTaxAuthorityVO> getQueueTaxAuthorityVOList(String siteId, List<Long> relatedInvoiceIds) {
        return BeanMapUtils.mapListTo(queueTaxAuthorityRepo.findBySiteIdAndRelatedInvoiceIdIn(siteId, relatedInvoiceIds), QueueTaxAuthorityVO.class);
    }

    public void updateQueue(List<QueueEinvoiceVO> queueEinvoiceVOList, List<QueueTaxAuthorityVO> queueTaxAuthorityVOList) {

        queueEinvoiceRepo.saveInBatch(BeanMapUtils.mapListTo(queueEinvoiceVOList, QueueEinvoice.class));

        queueTaxAuthorityRepo.saveInBatch(BeanMapUtils.mapListTo(queueTaxAuthorityVOList, QueueTaxAuthority.class));

    }

}
