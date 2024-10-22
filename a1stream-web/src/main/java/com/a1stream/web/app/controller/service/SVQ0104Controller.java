package com.a1stream.web.app.controller.service;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.service.SVQ010401BO;
import com.a1stream.domain.form.service.SVQ010401Form;
import com.a1stream.service.facade.SVQ0104Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("service/svq0104")
public class SVQ0104Controller implements RestProcessAware {

	@Resource
	private SVQ0104Facade svq0104Facade;

	@PostMapping(value = "/searchServiceOrderList.json")
	public Page<SVQ010401BO> searchServiceOrderList(@RequestBody final SVQ010401Form model,
			@AuthenticationPrincipal final PJUserDetails uc) {

		return svq0104Facade.page0KmServiceOrder(model, uc.getDealerCode());
	}
}