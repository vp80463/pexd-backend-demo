package com.a1stream.unit.facade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.domain.bo.unit.SDQ011401BO;
import com.a1stream.domain.bo.unit.SDQ011402BO;
import com.a1stream.domain.form.unit.SDQ011401Form;
import com.a1stream.domain.form.unit.SDQ011402Form;
import com.a1stream.domain.vo.CmmConsumerVO;
import com.a1stream.domain.vo.CmmMstOrganizationVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.unit.service.SDQ0114Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
* 功能描述: Stock In Out History Inquiry
*
* mid2287
* 2024年7月25日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/07/25   Wang Nan      New
*/
@Component
public class SDQ0114Facade {

    @Resource
    private SDQ0114Service sdq0114Service;

    @Resource
    private HelperFacade helperFacade;

    private static final List<String> TRANSFER_SALESTOCK_LIST = Arrays.asList(InventoryTransactionType.TRANSFERIN.getCodeDbid(),
                                                                              InventoryTransactionType.TRANSFEROUT.getCodeDbid(),
                                                                              InventoryTransactionType.SALESTOCKOUT.getCodeDbid());

    private static final List<String> TRANSFER_LIST = Arrays.asList(InventoryTransactionType.TRANSFERIN.getCodeDbid(),
                                                                    InventoryTransactionType.TRANSFEROUT.getCodeDbid());

    public Page<SDQ011401BO> getStockInOutHistoryList(SDQ011401Form form) {
        this.check(form);
        return this.dataEdit(sdq0114Service.getStockInOutHistoryList(form));
    }

    private Page<SDQ011401BO> dataEdit(Page<SDQ011401BO> list) {

        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(InventoryTransactionType.CODE_ID);

        List<SDQ011401BO> filterList = list.stream().filter(item -> TRANSFER_SALESTOCK_LIST.contains(item.getTransactionTypeCd())).toList();

        Map<Long, String> fromFacilityMap = new HashMap<>();
        Map<Long, String> toFacilityMap = new HashMap<>();

        if (!filterList.isEmpty()) {

            Set<Long> fromFacilityIds = filterList.stream().map(SDQ011401BO::getFromFacilityId).collect(Collectors.toSet());
            Set<Long> toFacilityIds = filterList.stream().map(SDQ011401BO::getToFacilityId).collect(Collectors.toSet());

            fromFacilityMap = this.getMstFacilityMap(fromFacilityIds);
            toFacilityMap = this.getMstFacilityMap(toFacilityIds);
        }

        List<Long> toOrganizationIds = list.stream().filter(item -> !Nulls.isNull(item.getToOrganizationId()))
                .map(SDQ011401BO::getToOrganizationId).toList();
        List<Long> fromOrganizationIds = list.stream().filter(item -> !Nulls.isNull(item.getFromOrganizationId()))
                .map(SDQ011401BO::getFromOrganizationId).toList();
        Map<Long, Long> saleStockOutMap = this.getSaleStockOutMap(list);
        Map<Long, String> cmmConsumerMap = this.getCmmConsumerMap(saleStockOutMap);
        Map<Long, String> organizationMap = this.getCmmMstOrganizationMap(toOrganizationIds);
        Map<Long, String> fromOrganizationMap = this.getCmmMstOrganizationMap(fromOrganizationIds);

        for (SDQ011401BO bo : list) {

            String transactionTypeCd = bo.getTransactionTypeCd();

            //当inventory_transaction_type = S027TRANSFERIN/S027TRANSFEROUT
            if (TRANSFER_LIST.contains(transactionTypeCd)) {

                //From为对应from_facility_id的facilityNm
                if (fromFacilityMap.containsKey(bo.getFromFacilityId())) {
                    bo.setFrom(fromFacilityMap.get(bo.getFromFacilityId()));
                }

                //To为对应to_facility_id的facilityNm
                if (toFacilityMap.containsKey(bo.getToFacilityId())) {
                    bo.setTo(toFacilityMap.get(bo.getToFacilityId()));
                }

            }

            //当inventory_transaction_type = S027SALESTOCKOUT && to_organization_id == null
            if (StringUtils.equals(InventoryTransactionType.SALESTOCKOUT.getCodeDbid(), transactionTypeCd) && Nulls.isNull(bo.getToOrganizationId())) {

                //from为from_facility_id对应facilityNm
                bo.setFrom(fromFacilityMap.get(bo.getFromFacilityId()));

                //to为对应consumer的名称
                if (saleStockOutMap.containsKey(bo.getRelatedSlipId()) && cmmConsumerMap.containsKey(saleStockOutMap.get(bo.getRelatedSlipId()))) {

                    //根据to_consumerId查询cmm_consumer获取顾客信息
                    bo.setTo(cmmConsumerMap.get(saleStockOutMap.get(bo.getRelatedSlipId())));
                }

            } else {

                //to为to_organization_id对应cmm_mst_organization的organization_nm
                if (fromOrganizationMap.containsKey(bo.getFromOrganizationId())) {
                    bo.setFrom(fromOrganizationMap.get(bo.getFromOrganizationId()));
                }

                if (organizationMap.containsKey(bo.getToOrganizationId())) {
                    bo.setTo(organizationMap.get(bo.getToOrganizationId()));
                }
            }

            //TransactionTypeCd -> TransactionTypeNm
            bo.setTransactionTypeNm(codeMap.get(bo.getTransactionTypeCd()));

        }
        return list;
    }

    private Map<Long, Long> getSaleStockOutMap(Page<SDQ011401BO> list) {

        List<SDQ011401BO> saleStockOutList = list.stream()
                .filter(item -> StringUtils.equals(
                        InventoryTransactionType.SALESTOCKOUT.getCodeDbid(),
                        item.getTransactionTypeCd()) && Nulls.isNull(item.getToOrganizationId()))
                .toList();

        if (saleStockOutList.isEmpty()) {
            return Collections.emptyMap();
        }

        //获取relatedSlipIds
        List<Long> relatedSlipIds = saleStockOutList.stream().map(SDQ011401BO::getRelatedSlipId).toList();

        //获取deliveryOrderVOList
        List<DeliveryOrderVO> deliveryOrderVOList = sdq0114Service.getDeliveryOrderVOList(relatedSlipIds);

        return deliveryOrderVOList.stream().collect(Collectors.toMap(DeliveryOrderVO::getDeliveryOrderId,
                                                                     DeliveryOrderVO::getToConsumerId));

    }

    private Map<Long, String> getCmmConsumerMap(Map<Long, Long> saleStockOutMap) {

        if (saleStockOutMap.isEmpty()) {
            return Collections.emptyMap();
        }

        //获取consumerIds
        List<Long> consumerIds = saleStockOutMap.values().stream().toList();

        //获取cmmConsumerVOList
        List<CmmConsumerVO> cmmConsumerVOList = sdq0114Service.getCmmConsumerVOList(consumerIds);

        if (cmmConsumerVOList.isEmpty()) {
            return Collections.emptyMap();
        }

        return cmmConsumerVOList.stream().collect(Collectors.toMap(CmmConsumerVO::getConsumerId,
                                                                   CmmConsumerVO::getConsumerFullNm));
    }

    private Map<Long, String> getCmmMstOrganizationMap(List<Long> organizationIds) {

        if (organizationIds.isEmpty()) {
            return Collections.emptyMap();
        }

        //获取cmmMstOrganizationVOList
        List<CmmMstOrganizationVO> cmmMstOrganizationVOList = sdq0114Service.getCmmMstOrganizationVOList(organizationIds);

        return cmmMstOrganizationVOList.stream().collect(Collectors.toMap(CmmMstOrganizationVO::getOrganizationId,
                                                                          CmmMstOrganizationVO::getOrganizationNm));

    }

    private Map<Long, String> getMstFacilityMap(Set<Long> facilityIds) {

        List<MstFacilityVO> mstFacilityVOList = sdq0114Service.getMstFacilityVOList(facilityIds);

        if (mstFacilityVOList.isEmpty()) {
            return Collections.emptyMap();
        }

        return mstFacilityVOList.stream().collect(Collectors.toMap(MstFacilityVO::getFacilityId,
                                                                   MstFacilityVO::getFacilityNm));
    }

    private void check(SDQ011401Form form) {

        //必入力check(Date)
        if (StringUtils.isBlank(form.getDateFrom())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("errors.required", new String[]{
                                             CodedMessageUtils.getMessage("label.fromDate")}));
        }

        if (StringUtils.isBlank(form.getDateTo())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("errors.required", new String[]{
                                             CodedMessageUtils.getMessage("label.toDate")}));
        }

        //日期Check
        if (StringUtils.isNotBlank(form.getDateFrom()) && StringUtils.isNotBlank(form.getDateTo())) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

            LocalDate dateFrom = LocalDate.parse(form.getDateFrom(), formatter);
            LocalDate dateTo = LocalDate.parse(form.getDateTo(), formatter);

            //dateFrom 不能大于 dateTo
            if (dateFrom.isAfter(dateTo)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00205", new String[] {
                                                    CodedMessageUtils.getMessage("label.toDate"),
                                                    CodedMessageUtils.getMessage("label.fromDate")}));
            }

            //dateTo不能大于当前系统时间的日期
            if (dateTo.isAfter(LocalDate.now())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00205", new String[] {
                                                 CodedMessageUtils.getMessage("label.toDate"),
                                                 CodedMessageUtils.getMessage("label.sysDate")}));

            }

            //dateFrom到dateTo的间隔不能大于一个月
            if (dateTo.isAfter(dateFrom.plus(CommonConstants.INTEGER_ONE, ChronoUnit.MONTHS))) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00355", new String[] {
                                                 CodedMessageUtils.getMessage("label.fromDate"),
                                                 CodedMessageUtils.getMessage("label.toDate")}));
            }
        }

        if(StringUtils.isNotBlank(form.getModel()) && ObjectUtils.isEmpty(form.getModelId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.model"), form.getModel(), CodedMessageUtils.getMessage("label.productInformation")}));
        }
    }

    public List<SDQ011402BO> getStockHistoryDetail(SDQ011402Form form) {
        return sdq0114Service.getStockHistoryDetail(form);
    }

}
