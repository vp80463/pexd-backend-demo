package com.a1stream.master.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.QueueStatus;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.domain.bo.master.CMM071601BO;
import com.a1stream.domain.form.master.CMM071601Form;
import com.a1stream.domain.vo.QueueEinvoiceVO;
import com.a1stream.domain.vo.QueueTaxAuthorityVO;
import com.a1stream.master.service.CMM0716Service;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

@Component
public class CMM0716Facade {

    @Resource
    private CMM0716Service cmm0716Service;

    @Resource
    private HelperFacade helperFacade;

    /**
     * 检索逻辑
     */
    public List<CMM071601BO> getInvoiceCheckResultList(CMM071601Form form) {

        List<CMM071601BO> returnList = new ArrayList<>();

        if (form.getType().size() == 1) {

            String type = form.getType().get(0);

            //当Type = Invoice时检索
            if (StringUtils.equals(CommonConstants.CHAR_INVOICE, type)) {

                returnList = cmm0716Service.getInvoiceCheckResultByInvoice(form);
            }

            //当Type = TaxAuthority时检索
            if (StringUtils.equals(CommonConstants.CHAR_TAXAUTHORITY, type)) {

                returnList = cmm0716Service.getInvoiceCheckResultByTaxAuthority(form);
            }

        } else {

            returnList = cmm0716Service.getInvoiceCheckResult(form);

        }

        return this.codeConvert(returnList);
    }

    private List<CMM071601BO> codeConvert(List<CMM071601BO> list) {

        Map<String, String> queueStatusMap = helperFacade.getMstCodeInfoMap(QueueStatus.CODE_ID);
        Map<String, String> productClsTypeMap = helperFacade.getMstCodeInfoMap(ProductClsType.CODE_ID);

        if (!list.isEmpty()) {

            list.forEach(item -> {
                item.setStatus(queueStatusMap.get(item.getStatus()));
                item.setInterfaceType(productClsTypeMap.get(item.getInterfaceType()));
            });

        }
        return list;
    }


    /**
     * resend逻辑
     */
    public void doResend(CMM071601Form form) {

        List<CMM071601BO> list = form.getGridData();

        if (list.isEmpty()) {
            return;
        }

        //创建用于保存的list
        List<QueueEinvoiceVO> saveQueueEinvoiceList = new ArrayList<>();
        List<QueueTaxAuthorityVO> saveQueueTaxAuthorityList = new ArrayList<>();

        //获取relatedInvoiceId
        List<Long> relatedInvoiceIds = list.stream().map(CMM071601BO::getRelatedInvoiceId).toList();

        //根据siteId和获取relatedInvoiceId获取QueueEinvoice
        List<QueueEinvoiceVO> queueEinvoiceList = cmm0716Service.getQueueEinvoiceVOList(form.getSiteId(), relatedInvoiceIds);
        Map<Long, List<QueueEinvoiceVO>> einvoiceMap = queueEinvoiceList.stream().collect(Collectors.groupingBy(QueueEinvoiceVO::getRelatedInvoiceId));

        //根据siteId和获取relatedInvoiceId获取QueueTaxAuthority
        List<QueueTaxAuthorityVO> queueTaxAuthorityList = cmm0716Service.getQueueTaxAuthorityVOList(form.getSiteId(), relatedInvoiceIds);
        Map<Long, List<QueueTaxAuthorityVO>> taxAuthorityMap = queueTaxAuthorityList.stream().collect(Collectors.groupingBy(QueueTaxAuthorityVO::getRelatedInvoiceId));

        for(CMM071601BO bo : list) {

            //先判断statusChangeFlag是否为N
            if (StringUtils.equals(CommonConstants.CHAR_N, bo.getStatusChangeFlag())) {

                //判断type是否为Invoice, 若符合则更新表queue_einvoice
                if (StringUtils.equals(CommonConstants.CHAR_INVOICE, bo.getType()) && einvoiceMap.containsKey(bo.getRelatedInvoiceId())) {

                    List<QueueEinvoiceVO> einvoiceList = einvoiceMap.getOrDefault(bo.getRelatedInvoiceId(), new ArrayList<>());

                    einvoiceList.forEach(item -> {

                        //更新status
                        item.setStatus(QueueStatus.WAITINGSEND.getCodeDbid());

                        //更新send_times
                        if (!Nulls.isNull(item.getSendTimes())) {
                            item.setSendTimes(item.getSendTimes() + CommonConstants.INTEGER_ONE);
                        }
                        saveQueueEinvoiceList.add(item);
                    });
                }

                //判断type是否为TaxAuthority, 若符合则更新表queue_tax_authority
                if (StringUtils.equals(CommonConstants.CHAR_TAXAUTHORITY, bo.getType()) && taxAuthorityMap.containsKey(bo.getRelatedInvoiceId())) {

                    List<QueueTaxAuthorityVO> taxAuthorityList = taxAuthorityMap.getOrDefault(bo.getRelatedInvoiceId(), new ArrayList<>());

                    taxAuthorityList.forEach(item -> {

                        //更新status
                        item.setStatus(QueueStatus.WAITINGSEND.getCodeDbid());

                        //更新send_times
                        if (!Nulls.isNull(item.getSendTimes())) {
                            item.setSendTimes(item.getSendTimes() + CommonConstants.INTEGER_ONE);
                        }
                        saveQueueTaxAuthorityList.add(item);
                    });
                }
            }
        }

        //更新表queue_einvoice和queue_tax_authority
        cmm0716Service.updateQueue(queueEinvoiceList, queueTaxAuthorityList);
    }
}
