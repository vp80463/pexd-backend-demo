package com.a1stream.service.facade;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.a1stream.common.logic.ValidateLogic;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.service.SVQ010401BO;
import com.a1stream.domain.form.service.SVQ010401Form;
import com.a1stream.service.service.SVQ0104Service;
import com.ymsl.solid.base.exception.BusinessCodedException;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

/**
 * 功能描述:Service Order明细画面
 *
 * @author mid1341
 */
@Component
public class SVQ0104Facade {

	@Resource
	private SVQ0104Service svq0104Ser;
	
	@Resource
	private ValidateLogic validLogic;

	public Page<SVQ010401BO> page0KmServiceOrder(SVQ010401Form model, String siteId) {

		validQueryParams(model);

		return svq0104Ser.page0KmServiceOrder(model, siteId);
	}

	private void validQueryParams(SVQ010401Form model) {

		// pointId不存在时，报错
		validLogic.validateEntityNotExist(model.getPoint(), model.getPointId(), ComUtil.t("label.point"));

		// model不为空时，modelId不存在，报错
		validLogic.validateEntityNotExist(model.getModel(), model.getModelId(), ComUtil.t("label.model"));

		// mechanic不为空时，mechanicId不存在，报错
		validLogic.validateEntityNotExist(model.getMechanic(), model.getMechanicId(), ComUtil.t("label.employee"));

		// serviceJob不为空时，serviceJobId不存在，报错
		validLogic.validateEntityNotExist(model.getServiceJob(), model.getServiceJobId(), ComUtil.t("label.serviceJob"));

		// 验证日期: 3个月以内
		validLogic.validateDateRange(model.getDateFrom(), model.getDateTo(), 3);

		// 当orderNo全为空时，则DateFrom/To至少有一个不为空
		if (StringUtils.isBlank(model.getOrderNo())
				&& Objects.isNull(model.getDateFrom()) && Objects.isNull(model.getDateTo())) {

			throw new BusinessCodedException(ComUtil.t("M.E.10326", new String[] { ComUtil.t("label.orderDate") }));
		}
	}
}
