package com.a1stream.web.app.controller.service;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.service.SVQ010201BO;
import com.a1stream.domain.form.service.SVQ010201Form;
import com.a1stream.service.facade.SVQ0102Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("service/svq0102")
public class SVQ0102Controller implements RestProcessAware {

	@Resource
	private SVQ0102Facade svq0102Facade;

	@Resource
	private ExcelFileExporter exporter;

	@PostMapping(value = "/searchServiceOrderList.json")
	public Page<SVQ010201BO> searchServiceOrderList(@RequestBody final SVQ010201Form model,
			@AuthenticationPrincipal final PJUserDetails uc) {

		return svq0102Facade.searchServiceOrderList(model, uc.getDealerCode());
	}

	@PostMapping(value = "/exportServiceOrderList.json", produces = MediaType.APPLICATION_JSON_VALUE)
	public DownloadResponseView downloadExcel(@RequestBody final SVQ010201Form model,
			@AuthenticationPrincipal final PJUserDetails uc) {

		DownloadResponse excel = exporter.generate(FileConstants.EXCEL_TEMPLATE_SVQ0102_01,
				svq0102Facade.exportServiceOrderList(model, uc.getDealerCode()), FileConstants.EXCEL_EXPORT_SVQ0102_01);
		return new DownloadResponseView(excel);
	}
}