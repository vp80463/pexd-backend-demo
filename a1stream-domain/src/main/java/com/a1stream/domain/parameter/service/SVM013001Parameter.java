package com.a1stream.domain.parameter.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.bo.service.JobDetailBO;
import com.a1stream.domain.bo.service.PartDetailBO;
import com.a1stream.domain.bo.service.SituationBO;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmServiceHistoryVO;
import com.a1stream.domain.vo.CmmSpecialClaimSerialProVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.domain.vo.ServiceOrderBatteryVO;
import com.a1stream.domain.vo.ServiceOrderFaultVO;
import com.a1stream.domain.vo.ServiceOrderJobVO;
import com.a1stream.domain.vo.ServiceOrderVO;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述:Service Order 0KM明细画面
*
* @author mid1341
*/
@Getter
@Setter
public class SVM013001Parameter implements Serializable {

    private static final long serialVersionUID = 1L;

    private ServiceOrderVO serviceOrder;
    private SalesOrderVO salesOrder;
    private CmmSerializedProductVO cmmSerializedProduct;

    private List<ServiceOrderFaultVO> situationListForSave = new ArrayList<>();
    private List<Long> situationListForDelete = new ArrayList<>();
    private List<ServiceOrderJobVO> jobListForSave = new ArrayList<>();
    private List<Long> jobListForDelete = new ArrayList<>();
    private List<SalesOrderItemVO> partListForSave = new ArrayList<>();//part没有删除，始终保留Qty=0的数据
    private List<ServiceOrderBatteryVO> batteryList = new ArrayList<>();

    private List<CmmSpecialClaimSerialProVO> cmmSpecialClaimSerialProList = new ArrayList<>();
    private Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = new HashMap<>();

    private String doFlag;

    private List<SituationBO> situationListCheckList = new ArrayList<>();
    private List<JobDetailBO> jobListCheckList = new ArrayList<>();
    private List<PartDetailBO> partListCheckList = new ArrayList<>();

    private SerializedProductVO serializedProduct;
    private CmmServiceHistoryVO cmmServiceHistory;
    private List<BatteryVO> batteryMstList = new ArrayList<>();
    private List<CmmBatteryVO> cmmBatteryMstList = new ArrayList<>();
}
