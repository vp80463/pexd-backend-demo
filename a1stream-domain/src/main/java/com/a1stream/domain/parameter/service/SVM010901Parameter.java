package com.a1stream.domain.parameter.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.model.BaseConsumerForm;
import com.a1stream.domain.bo.service.SituationBO;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmRegistrationDocumentVO;
import com.a1stream.domain.vo.ConsumerPrivacyPolicyResultVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.ServiceOrderBatteryVO;
import com.a1stream.domain.vo.ServiceOrderFaultVO;
import com.a1stream.domain.vo.ServiceOrderVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM010901Parameter implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long pointId;
	private String siteId;
	private String action;

	private ServiceOrderVO serviceOrderVO;
	private SalesOrderVO salesOrderVO;
	private List<SalesOrderItemVO> salesOrderItemVO;
	private ServiceOrderBatteryVO serviceBatteryVO;

    private List<BatteryVO> batteryList = new ArrayList<>();
    private List<CmmBatteryVO> cmmBatteryList = new ArrayList<>();
    private CmmRegistrationDocumentVO cmmRegistDocument;

	private List<Long> removeFaultIds;
	private List<ServiceOrderFaultVO> saveFaultList;
	private List<SituationBO> situationCheckedList;

    private BaseConsumerForm consumerBaseInfo;
	private ConsumerPrivacyPolicyResultVO policyResultVO;

    private Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = new HashMap<>();
}
