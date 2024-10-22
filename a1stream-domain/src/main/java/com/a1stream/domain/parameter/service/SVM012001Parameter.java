package com.a1stream.domain.parameter.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.a1stream.common.model.BaseConsumerForm;
import com.a1stream.domain.bo.service.SVM012001JobBO;
import com.a1stream.domain.bo.service.SVM012001PartBO;
import com.a1stream.domain.vo.ConsumerPrivacyPolicyResultVO;
import com.a1stream.domain.vo.ServiceOrderItemOtherBrandVO;
import com.a1stream.domain.vo.ServiceOrderVO;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述:Service Order Other Brand明细画面
*
* @author mid1341
*/
@Getter
@Setter
public class SVM012001Parameter implements Serializable {

    private static final long serialVersionUID = 1L;

    private ServiceOrderVO serviceOrder;
    private ConsumerPrivacyPolicyResultVO consumerPrivacyPolicyResult;

    private List<ServiceOrderItemOtherBrandVO> serviceDetailForSave = new ArrayList<>();
    private List<Long> serviceDetailForDelete = new ArrayList<>();

    private String doFlag;

    private List<SVM012001JobBO> jobListCheckList = new ArrayList<>();
    private List<SVM012001PartBO> partListCheckList = new ArrayList<>();

    private BaseConsumerForm consumerBaseInfo;
}
