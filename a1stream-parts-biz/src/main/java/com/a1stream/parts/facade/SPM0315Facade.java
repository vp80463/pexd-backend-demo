package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Component;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.ReceiptSlipStatus;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.domain.bo.batch.PartsStoringReportModelBO;
import com.a1stream.domain.bo.parts.SPM031501BO;
import com.a1stream.domain.entity.QueueData;
import com.a1stream.domain.entity.ReceiptManifest;
import com.a1stream.domain.entity.StoringLine;
import com.a1stream.domain.form.parts.SPM031501Form;
import com.a1stream.domain.repository.QueueDataRepository;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.QueueDataVO;
import com.a1stream.domain.vo.ReceiptManifestVO;
import com.a1stream.domain.vo.ReceiptSlipItemVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.domain.vo.StoringLineItemVO;
import com.a1stream.domain.vo.StoringLineVO;
import com.a1stream.parts.service.PartsStoringReportService;
import com.a1stream.parts.service.SPM0315Service;
import com.ymsl.plugins.userauth.util.JsonUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* mid2287
* 2024年6月13日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/13   Wang Nan      New
*/

@Component
public class SPM0315Facade {

    @Resource
    private SPM0315Service spm0315Service;

    @Resource
    private QueueDataRepository queueDataRepo;

    @Resource
    private PartsStoringReportService partsStoringReportService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_H_M_S);

    public List<SPM031501BO> getPartsStockRegisterList(SPM031501Form form) {

        //检查point
        if (StringUtils.isNotBlank(form.getPoint()) && Nulls.isNull(form.getPointId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.point"),
                                             form.getPoint(),
                                             CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }

        List<SPM031501BO> list = spm0315Service.getPartsStockRegisterList(form);

        //过滤出completedDate值为空的数据
        if (!list.isEmpty()) {
            list = list.stream().filter(item -> StringUtils.isBlank(item.getCompletedDate())).toList();
        }

        //当MainLocationSign<>Y, 且location_type = S006NORMAL时可编辑
        for (SPM031501BO bo : list) {

            if (!StringUtils.equals(bo.getMainLocationSign(), CommonConstants.CHAR_Y) && StringUtils.equals(PJConstants.LocationType.NORMAL.getCodeDbid(), bo.getLocationTypeId())) {
                bo.setSetAsMainLocationFlg(CommonConstants.FALSE_CODE);
            }

        }
        return list;
    }

    public void confirmPartsStockRegister(SPM031501Form form, PJUserDetails uc) {

        //更新前Check
        this.valid(form);

        //更新逻辑
        this.update(form, uc);

    }

    private void update(SPM031501Form form, PJUserDetails uc) {

        //获取页面数据
        List<SPM031501BO> list = form.getGridDataList();

        //获取storing_line
        Set<Long> storingLineIds = list.stream().map(SPM031501BO::getStoringLineId).collect(Collectors.toSet());
        List<StoringLineVO> storingLineVOs = spm0315Service.getStoringLineVOList(storingLineIds);

        //根据storingLineId对storingLineVOs进行分组
        Map<Long, StoringLineVO> slMap = storingLineVOs.stream()
                                                       .collect(Collectors.toMap(StoringLineVO::getStoringLineId,
                                                                                 storingLineVO -> storingLineVO));

        //根据storingLineId对list进行分组
        Map<Long, List<SPM031501BO>> map = list.stream().collect(Collectors.groupingBy(SPM031501BO::getStoringLineId));

        for (Map.Entry<Long, StoringLineVO> entry : slMap.entrySet()) {

            Long storingLineId = entry.getKey();

            Optional<Integer> slUpdateCountOpt = list.stream()
                                                     .filter(item -> item.getStoringLineId().equals(storingLineId))
                                                     .map(SPM031501BO::getSlUpdateCount)
                                                     .findFirst();

            slUpdateCountOpt.ifPresent(slUpdateCount -> {

                StoringLineVO slVO = slMap.get(storingLineId);

                //乐观锁Check(Storing_line)
                if (Nulls.isNull(slVO) || !slUpdateCount.equals(slVO.getUpdateCount())) {

                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10449"));

                }
            });
        }

        //storing_line_item更新
        List<StoringLineItemVO> updStoringLineItemVOList = new ArrayList<>();

        //获取storing_line_item
        List<Long> storingLineItemIds = list.stream().map(SPM031501BO::getStoringLineItemId).toList();
        List<StoringLineItemVO> sliList = spm0315Service.getStoringLineItemVOList(storingLineItemIds);

        //通过storingLineItemId对storing_line_item进行分组
        Map<Long, StoringLineItemVO> sliMap = sliList.stream()
                                                     .collect(Collectors.toMap(StoringLineItemVO::getStoringLineItemId,
                                                                               storingLineItemVO -> storingLineItemVO));

        for (SPM031501BO bo: list) {

            //乐观锁Check(storing_line_item)
            StoringLineItemVO sliVO = sliMap.get(bo.getStoringLineItemId());
            if (Nulls.isNull(sliVO) || !bo.getSliUpdateCount().equals(sliVO.getUpdateCount())) {

                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10449"));

            } else {

                sliVO.setLocationId(bo.getLocationId());
                sliVO.setLocationCd(bo.getLocation());
                sliVO.setStoredQty(bo.getRegisterQty());
                sliVO.setCompletedDate(LocalDateTime.now().format(DATE_FORMATTER));
                sliVO.setCompletedTime(LocalDateTime.now().format(TIME_FORMATTER));
                updStoringLineItemVOList.add(sliVO);

            }
        }

        //receipt_slip_item更新
        List<ReceiptSlipItemVO> updReceiptSlipItemVOList = new ArrayList<>();
        for (Map.Entry<Long, List<SPM031501BO>> entry : map.entrySet()) {

            List<SPM031501BO> boList = map.get(entry.getKey());

            //frozenQty总和(location_type = S006FROZEN)
            BigDecimal frozenQty = boList.stream().filter(item -> StringUtils.equals(PJConstants.LocationType.FROZEN.getCodeDbid(), item.getLocationTypeId()))
                                                  .map(SPM031501BO::getRegisterQty)
                                                  .reduce(BigDecimal.ZERO, BigDecimal::add);

            //获取receipt_slip_item
            Set<Long> receiptSlipItemIds = boList.stream().map(SPM031501BO::getReceiptSlipItemId).collect(Collectors.toSet());
            List<ReceiptSlipItemVO> receiptSlipItemVOList = spm0315Service.getReceiptSlipItemVOList(receiptSlipItemIds);

            //根据receiptSlipItemI对receipt_slip_item进行分组
            Map<Long, ReceiptSlipItemVO> rsiMap = receiptSlipItemVOList.stream()
                                                                        .collect(Collectors.toMap(ReceiptSlipItemVO::getReceiptSlipItemId,
                                                                                                  receiptSlipItemVO -> receiptSlipItemVO));

            //乐观锁Check(receipt_slip_item)
            for(SPM031501BO bo : boList) {
                ReceiptSlipItemVO rsiVO = rsiMap.get(bo.getReceiptSlipItemId());
                if (Nulls.isNull(rsiVO) || !bo.getRsiUpdateCount().equals(rsiVO.getUpdateCount())) {

                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10449"));

                }
            }

            for (ReceiptSlipItemVO rsi: receiptSlipItemVOList) {

                BigDecimal res = NumberUtil.subtract(rsi.getReceiptQty(), frozenQty);
                if (NumberUtil.ge(res, BigDecimal.ZERO)) {
                    rsi.setReceiptQty(res);
                }
                rsi.setFrozenQty(frozenQty);
                updReceiptSlipItemVOList.add(rsi);
            }
        }

        //receipt_slip更新
        List<ReceiptSlipVO> updReceiptSlipVOList = new ArrayList<>();

        //获取receipt_slip
        Set<Long> receiptSlipIds = list.stream().map(SPM031501BO::getReceiptSlipItemId).collect(Collectors.toSet());
        List<ReceiptSlipVO> receiptSlipVOList = spm0315Service.getReceiptSlipVOList(receiptSlipIds);

        //通过receiptSlipId对receiptSlipVOList进行分组
        Map<Long, ReceiptSlipVO> rsMap = receiptSlipVOList.stream()
                                                          .collect(Collectors.toMap(ReceiptSlipVO::getReceiptSlipId,
                                                                                    receiptSlipVO -> receiptSlipVO));

        //receipt_slip更新
        for (Map.Entry<Long, ReceiptSlipVO> entry : rsMap.entrySet()) {

            Long receiptSlipId = entry.getKey();

            //获取画面receipt_slip的UpdateCount
            Optional<Integer> rsUpdateCountOpt = list.stream()
                                                     .filter(item -> item.getReceiptSlipId().equals(receiptSlipId))
                                                     .map(SPM031501BO::getRsUpdateCount)
                                                     .findFirst();

            rsUpdateCountOpt.ifPresent(rsUpdateCount -> {

                ReceiptSlipVO rsVO = rsMap.get(receiptSlipId);

                //乐观锁Check(receipt_slip)
                if (Nulls.isNull(rsVO) || !rsUpdateCount.equals(rsVO.getUpdateCount())) {

                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10449"));

                } else {

                    rsVO.setReceiptSlipStatus(ReceiptSlipStatus.STORED.getCodeDbid());
                    rsVO.setStoringPicId(uc.getUserId());
                    rsVO.setStoringPicNm(uc.getUsername());
                    rsVO.setStoringEndDate(LocalDateTime.now().format(DATE_FORMATTER));
                    rsVO.setStoringEndTime(LocalDateTime.now().format(TIME_FORMATTER));
                    updReceiptSlipVOList.add(rsVO);

                }
            });
        }

        spm0315Service.saveOrUpdateData(storingLineVOs,
                                        updStoringLineItemVOList,
                                        updReceiptSlipItemVOList, 
                                        updReceiptSlipVOList,
                                        form.getSiteId(),
                                        form.getPointId(),
                                        list);

        ReceiptManifestVO receiptManifestVO = spm0315Service.findFirstBySiteIdAndSupplierInvoiceNo(form.getSiteId(), updReceiptSlipItemVOList.get(0).getSupplierInvoiceNo());
        List<PartsStoringReportModelBO> storageReportComplete = this.storageReportComplete(storingLineVOs,updReceiptSlipItemVOList.get(0).getSupplierInvoiceNo(), receiptManifestVO.getSupplierShipmentNo(), uc.getDealerCode(), form.getPoint());

        if (!storageReportComplete.isEmpty()) {queueDataRepo.save(BeanMapUtils.mapTo(QueueDataVO.create(storageReportComplete.get(0).getDealerCode(), InterfCode.OX_SPSTORINGREPORT, "receipt_manifest", receiptManifestVO.getReceiptManifestId(), JsonUtils.toString(storageReportComplete.stream().toList())), QueueData.class));}
    }

    // storage Report Complete
	private List<PartsStoringReportModelBO> storageReportComplete(List<StoringLineVO> storingLineVOs, String invoiceSeqNo, String shipmentNo, String dealerCode, String facilityCd) {

        List<PartsStoringReportModelBO> partsStoringReportModelBOs = new ArrayList<>();
    	PartsStoringReportModelBO partsStoringReportModelBO = new PartsStoringReportModelBO();


		if (StringUtil.isBlank(shipmentNo) && StringUtil.isBlank(invoiceSeqNo)) {
			return partsStoringReportModelBOs;
		}

    	BigDecimal storageTotal = new BigDecimal(0);
    	BigDecimal receiveTotal = new BigDecimal(0);

    	for (StoringLineVO storingLineVO : storingLineVOs) {

    		receiveTotal = receiveTotal.add(storingLineVO.getFrozenQty()
    		                           .add(storingLineVO.getStoredQty()));

    		storageTotal = storageTotal.add(storingLineVO.getInstuctionQty());
		}

    	if (!NumberUtil.equals(receiveTotal, BigDecimal.ZERO) && NumberUtil.equals(receiveTotal, storageTotal)) {

    		partsStoringReportModelBO.setDealerCode(dealerCode);
    		partsStoringReportModelBO.setInvoiceSeqNo(invoiceSeqNo);
    		partsStoringReportModelBO.setConsigneeCode(facilityCd);
    		partsStoringReportModelBO.setStorageDate(storingLineVOs.get(0).getCompletedDate());
            partsStoringReportModelBO.setStorageTime(storingLineVOs.get(0).getCompletedTime());

    		partsStoringReportModelBO.setShipmentNo(shipmentNo);
    		partsStoringReportModelBOs.add(partsStoringReportModelBO);
    	}

    	return partsStoringReportModelBOs;
    }

    private void valid(SPM031501Form form) {

        List<SPM031501BO> list = form.getGridDataList();

        if (list.isEmpty()) {
            return;
        }

        for (SPM031501BO bo : list) {

            //location必入力check
            if (StringUtils.isBlank(bo.getLocation())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("errors.required", new String[]{
                                                 CodedMessageUtils.getMessage("label.location")}));
            }

            //Register Qty = Detail.Instruction Qty
            if (!NumberUtil.equals(bo.getRegisterQty(), bo.getInstructionQty())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00204", new String[]{
                                                 CodedMessageUtils.getMessage("label.registerQty"),
                                                 CodedMessageUtils.getMessage("label.instructionQty")}));
            }

            //若location Type ID = S006SERVICE时报错
            if (StringUtils.equals(PJConstants.LocationType.SERVICE.getCodeDbid(), bo.getLocationType())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10337"));
            }

        }

        //location存在性check
        this.existLocation(form.getSiteId(), form.getPointId(), list);
    }

    private void existLocation(String siteId, Long facilityId, List<SPM031501BO> list) {

        List<String> locationCds = list.stream().map(SPM031501BO::getLocation).toList();

        //Location存在性check
        List<LocationVO> locationVOList = spm0315Service.findLocationVOList(siteId, 
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
