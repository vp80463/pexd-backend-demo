package com.a1stream.service.facade;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ConsumerPrivacyAgreementType;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.common.logic.ValidateLogic;
import com.a1stream.common.manager.ConsumerManager;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.domain.bo.service.SVQ010201BO;
import com.a1stream.domain.bo.service.SVQ010201ExportBO;
import com.a1stream.domain.form.service.SVQ010201Form;
import com.a1stream.service.service.SVQ0102Service;
import com.ymsl.solid.base.exception.BusinessCodedException;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

/**
 * 功能描述:Service Order明细画面
 *
 * @author mid1341
 */
@Component
public class SVQ0102Facade {

	@Resource
	private SVQ0102Service svq0102Ser;

    @Resource
    private ConsumerManager consumerMgr;

    @Resource
    private HelperFacade helperFacade;

	@Resource
	private ValidateLogic validLogic;

	public Page<SVQ010201BO> searchServiceOrderList(SVQ010201Form model, String siteId) {

		validQueryParams(model);
		Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(ConsumerPrivacyAgreementType.CODE_ID);
		Page<SVQ010201BO> pageData = svq0102Ser.pageServiceOrder(model, siteId);
		for(SVQ010201BO item : pageData) {
		    String policyFlag = consumerMgr.getConsumerPolicyInfo(siteId, item.getLastNm(), item.getMiddleNm(), item.getFirstNm(), item.getMobilePhone());
		    item.setPolicyResult(codeMap.containsKey(policyFlag)? codeMap.get(policyFlag) : null);
		}

		return pageData;
	}

	public List<SVQ010201ExportBO> exportServiceOrderList(SVQ010201Form model, String siteId) {

		List<SVQ010201ExportBO> result = svq0102Ser.listServiceOrder(model, siteId);
		Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(ConsumerPrivacyAgreementType.CODE_ID);

		BigDecimal serviceAmountTotal = CommonConstants.BIGDECIMAL_ZERO;
		BigDecimal partsAmountTotal = CommonConstants.BIGDECIMAL_ZERO;
		BigDecimal workTimeTotal = CommonConstants.BIGDECIMAL_ZERO;

		for (SVQ010201ExportBO item : result) {

            String policyFlag = consumerMgr.getConsumerPolicyInfo(siteId, item.getLastNm(), item.getMiddleNm(), item.getFirstNm(), item.getMobilePhone());
            item.setPolicyResult(codeMap.containsKey(policyFlag)? codeMap.get(policyFlag) : null);
			this.formateForExport(item);

			serviceAmountTotal = NumberUtil.add(serviceAmountTotal, item.getServiceAmount());
			partsAmountTotal = NumberUtil.add(partsAmountTotal, item.getPartsAmount());
			BigDecimal workTime = StringUtils.isBlank(item.getWorkTime()) ? CommonConstants.BIGDECIMAL_ZERO : new BigDecimal(item.getWorkTime());

			workTimeTotal = NumberUtil.add(workTimeTotal, workTime);
		}

		// 追加合计行
		result.add(SVQ010201ExportBO.createtTotalRow(serviceAmountTotal, partsAmountTotal, workTimeTotal.toString()));

		return result;
	}

	private void formateForExport(SVQ010201ExportBO exportDetail) {

		DateTimeFormatter DATETIME_FORMAT = CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS_LOC;
		// 日期格式
		exportDetail.setOrderDate(ComUtil.changeFormat(exportDetail.getOrderDate()));
		exportDetail.setSettleDate(ComUtil.changeFormat(exportDetail.getSettleDate()));
		exportDetail.setSoldDate(ComUtil.changeFormat(exportDetail.getSoldDate()));
		exportDetail.setStartTimeStr(Objects.isNull(exportDetail.getStartTime()) ? CommonConstants.CHAR_BLANK : exportDetail.getStartTime().format(DATETIME_FORMAT));
		exportDetail.setFinishTimeStr(Objects.isNull(exportDetail.getFinishTime()) ? CommonConstants.CHAR_BLANK : exportDetail.getFinishTime().format(DATETIME_FORMAT));

		// 计算work time
		String workTime = (Objects.isNull(exportDetail.getStartTime()) || Objects.isNull(exportDetail.getFinishTime())) ?
						CommonConstants.CHAR_BLANK
						: String.format("%.2f", (double) Duration.between(exportDetail.getStartTime(), exportDetail.getFinishTime()).toHours());
		exportDetail.setWorkTime(workTime);
	}

	private void validQueryParams(SVQ010201Form model) {

		// pointId不存在时，报错
		validLogic.validateEntityNotExist(model.getPoint(), model.getPointId(), ComUtil.t("label.point"));

		// model不为空时，modelId不存在，报错
		validLogic.validateEntityNotExist(model.getModel(), model.getModelId(), ComUtil.t("label.model"));

		// consumer不为空时，consumerId不存在，报错
		validLogic.validateEntityNotExist(model.getConsumer(), model.getConsumerId(), ComUtil.t("label.consumer"));

		// receptionPic不为空时，receptionPicId不存在，报错
		validLogic.validateEntityNotExist(model.getReceptionPic(), model.getReceptionPicId(), ComUtil.t("label.employee"));

		// mechanic不为空时，mechanicId不存在，报错
		validLogic.validateEntityNotExist(model.getMechanic(), model.getMechanicId(), ComUtil.t("label.employee"));

		// serviceJob不为空时，serviceJobId不存在，报错
		validLogic.validateEntityNotExist(model.getServiceJob(), model.getServiceJobId(), ComUtil.t("label.serviceJob"));

		// 验证日期: 6个月以内
		validLogic.validateDateRange(model.getDateFrom(), model.getDateTo(), 6);

		// 当plateNo/batteryNo/orderNo全为空时，则DateFrom/To至少有一个不为空
		if (StringUtils.isBlank(model.getPlateNo()) && StringUtils.isBlank(model.getBatteryNo()) && StringUtils.isBlank(model.getOrderNo())
				&& Objects.isNull(model.getDateFrom()) && Objects.isNull(model.getDateTo())) {

			throw new BusinessCodedException(ComUtil.t("M.E.10326", new String[] { ComUtil.t("label.orderDate") }));
		}
	}
}
