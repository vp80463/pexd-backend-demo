package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.ReceiptSlipStatus;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.domain.bo.parts.SPM030301BO;
import com.a1stream.domain.form.parts.SPM030301Form;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.ReceiptSlipItemVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.domain.vo.StoringLineItemVO;
import com.a1stream.domain.vo.StoringLineVO;
import com.a1stream.parts.service.SPM0303Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
 *
* 功能描述: Parts Stock Register
*
* mid2287
* 2024年6月11日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/11   Wang Nan      New
 */
@Component
public class SPM0303Facade {

    @Resource
    private SPM0303Service spm0303Service;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_H_M_S);

    public StoringLineVO getStoringlineVO(SPM030301Form form) {

        //检查point
        if (StringUtils.isNotBlank(form.getPoint()) && Nulls.isNull(form.getPointId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.point"),
                                             form.getPoint(),
                                             CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }

        StoringLineVO storingLineVO = spm0303Service.getStoringLineVO(form);
        if (!Nulls.isNull(storingLineVO) && StringUtils.isBlank(storingLineVO.getCompletedDate())) {
            storingLineVO.setStoredQty(BigDecimal.ZERO);
            storingLineVO.setFrozenQty(BigDecimal.ZERO);
        }
        return storingLineVO;
     }

    public List<SPM030301BO> getStoringLineItemList(SPM030301Form form) {
        return spm0303Service.getStoringLineItemVOList(form);
    }

    public void confirmPartsStockRegister(SPM030301Form form, PJUserDetails uc) {

        //更新前Check
        this.valid(form);

        //更新逻辑
        this.confirm(form, uc);
    }

    private void confirm(SPM030301Form form, PJUserDetails uc) {

        List<StoringLineItemVO> storingLineItemVOList = new ArrayList<>();
        List<ReceiptSlipItemVO> receiptSlipItemVOList = new ArrayList<>();

        List<SPM030301BO> addList = form.getInsertData();
        List<SPM030301BO> updList = form.getUpdateData();

        //获取storing_line
        StoringLineVO storingLineVO = spm0303Service.getStoringLineVO(form.getStoringLineId());

        //乐观锁Check(Storing_line)
        Optional<Integer> slUpdateCountOpt = updList.stream()
                                                    .map(SPM030301BO::getSlUpdateCount)
                                                    .findFirst();

        slUpdateCountOpt.ifPresent(slUpdateCount -> {

            //乐观锁Check(Storing_line)
            if (Nulls.isNull(storingLineVO) || !slUpdateCount.equals(storingLineVO.getUpdateCount())) {

                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10449"));

            }
        });

        ReceiptSlipVO receiptSlipVO = new ReceiptSlipVO();

        //storing_line_item新增
        for (SPM030301BO bo: addList) {

            StoringLineItemVO newstoringLineItemVO = new StoringLineItemVO();
            newstoringLineItemVO.setSiteId(form.getSiteId());
            newstoringLineItemVO.setFacilityId(form.getPointId());
            newstoringLineItemVO.setStoringLineId(form.getStoringLineId());
            newstoringLineItemVO.setLocationId(bo.getLocationId());
            newstoringLineItemVO.setLocationCd(bo.getLocation());
            newstoringLineItemVO.setInstuctionQty(BigDecimal.ZERO);
            newstoringLineItemVO.setStoredQty(bo.getQty());
            newstoringLineItemVO.setCompletedDate(ComUtil.date2str(LocalDate.now()));
            newstoringLineItemVO.setCompletedTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_H_M_S)));
            storingLineItemVOList.add(newstoringLineItemVO);

        }

        if (!updList.isEmpty()) {

            //获取页面每一行的storingLineItemId
            List<Long> storingLineItemIds = updList.stream().map(SPM030301BO::getStoringLineItemId).toList();

            //获取storing_line_item的集合
            List<StoringLineItemVO> sliList = spm0303Service.getStoringLineItemVOList(storingLineItemIds);

            //根据storingLineItemId分组
            Map<Long, StoringLineItemVO> sliMap = sliList.stream()
                                                         .collect(Collectors.toMap(StoringLineItemVO::getStoringLineItemId,
                                                                                   storingLineItemVO -> storingLineItemVO));

            //storing_line_item更新
            for (SPM030301BO bo: updList) {

                //乐观锁Check(storing_line_item)
                StoringLineItemVO sliVO = sliMap.get(bo.getStoringLineItemId());
                if (Nulls.isNull(sliVO) || !bo.getSliUpdateCount().equals(sliVO.getUpdateCount())) {

                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10449"));

                } else {

                    sliVO.setLocationId(bo.getLocationId());
                    sliVO.setLocationCd(bo.getLocation());
                    sliVO.setStoredQty(bo.getQty());
                    sliVO.setCompletedDate(ComUtil.date2str(LocalDate.now()));
                    sliVO.setCompletedTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_H_M_S)));
                    storingLineItemVOList.add(sliVO);

                }
            }

            //receipt_slip_item更新
            Optional<SPM030301BO> updOptional = updList.stream().findFirst();

            if (updOptional.isPresent()) {

                SPM030301BO optional = updOptional.get();

                Long receiptSlipItemId = optional.getReceiptSlipItemId();
                Integer rsiUpdateCount = optional.getRsiUpdateCount();

                //获取ReceiptSlipItem
                ReceiptSlipItemVO receiptSlipItemVO = spm0303Service.getReceiptSlipItemVO(receiptSlipItemId);

                //乐观锁Check(receipt_slip_item)
                if (Nulls.isNull(receiptSlipItemVO) || !rsiUpdateCount.equals(receiptSlipItemVO.getUpdateCount())) {

                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10449"));

                } else {

                    receiptSlipItemVO.setReceiptQty(form.getRegisterQty());
                    receiptSlipItemVO.setFrozenQty(form.getOnFrozenQty());
                    receiptSlipItemVOList.add(receiptSlipItemVO);

                }

                //receipt_slip更新
                Long receiptSlipId = optional.getReceiptSlipId();
                Integer rsUpdateCount = optional.getRsUpdateCount();

                //获取ReceiptSlip
                receiptSlipVO = spm0303Service.getReceiptSlipVO(receiptSlipId);

                //乐观锁Check(receipt_slip)
                if (Nulls.isNull(receiptSlipVO) || !rsUpdateCount.equals(receiptSlipVO.getUpdateCount())) {

                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10449"));

                } else {

                    receiptSlipVO.setReceiptSlipStatus(ReceiptSlipStatus.STORED.getCodeDbid());
                    receiptSlipVO.setStoringPicId(uc.getUserId());
                    receiptSlipVO.setStoringPicNm(uc.getUsername());
                    receiptSlipVO.setStoringEndDate(LocalDateTime.now().format(DATE_FORMATTER));
                    receiptSlipVO.setStoringEndTime(LocalDateTime.now().format(TIME_FORMATTER));

                }
            }
        }

        spm0303Service.saveOrUpdateData(storingLineVO,
                                        storingLineItemVOList,
                                        receiptSlipItemVOList,
                                        receiptSlipVO,
                                        form.getSiteId(),
                                        form.getPointId());

    }


    private void valid(SPM030301Form form) {

        List<SPM030301BO> addList = form.getInsertData();
        List<SPM030301BO> updList = form.getUpdateData();

        List<SPM030301BO> list = Stream.concat(addList.stream(), updList.stream()).toList();

        if (list.isEmpty()) {
            return;
        }

        List<String> locationNoList = list.stream().map(o -> o.getLocation()).collect(Collectors.toList());

        for (SPM030301BO bo : list) {

            //location必入力check
            if (StringUtils.isBlank(bo.getLocation())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("errors.required", new String[]{
                                                 CodedMessageUtils.getMessage("label.location")}));
            }

            //若location Type ID = S006SERVICE时报错
            if (StringUtils.equals(PJConstants.LocationType.SERVICE.getCodeDbid(), bo.getLocationTypeCd())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10337"));
            }

            //若location重复报错
            if (locationNoList.indexOf(bo.getLocation()) != locationNoList.lastIndexOf(bo.getLocation())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00301", new String[]{ CodedMessageUtils.getMessage("label.locationCode")}));
            }

            // //Qty > 0
            // if (Nulls.isNull(bo.getQty()) || NumberUtil.le(bo.getQty(), BigDecimal.ZERO)) {
            //     throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00200", new String[]{
            //                                      CodedMessageUtils.getMessage("label.quantity"),
            //                                      CommonConstants.CHAR_ZERO}));
            // }
        }

        //Receipt Qty = Register Qty + On Frozen Qty
        BigDecimal receiptQty = NumberUtil.add(form.getRegisterQty(), form.getOnFrozenQty());
        if (!NumberUtil.equals(form.getReceiptQty(), receiptQty)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10285", new String[]{
                CodedMessageUtils.getMessage("label.registerQty"),
                CodedMessageUtils.getMessage("label.onFrozenQty"),
                StringUtils.EMPTY,
                CodedMessageUtils.getMessage("label.receiptedQuantity"),
            }));
        }

        //location存在性check
        this.existLocation(form.getSiteId(), form.getPointId(), list);
    }

    private void existLocation(String siteId, Long facilityId, List<SPM030301BO> list) {

        List<String> locationCds = list.stream().map(SPM030301BO::getLocation).toList();

        //Location存在性check
        List<LocationVO> locationVOList = spm0303Service.findLocationVOList(siteId,
                                                                            facilityId,
                                                                            locationCds);

        List<String> locationCdList = locationVOList.stream().map(LocationVO::getLocationCd).toList();

        for (String locationCd: locationCds) {

            if (!locationCdList.contains(locationCd)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[]{
                                                 CodedMessageUtils.getMessage("label.location"),
                                                 locationCd,
                                                 CodedMessageUtils.getMessage("label.tableLocationInfo")}));
            }

        }
    }


}
