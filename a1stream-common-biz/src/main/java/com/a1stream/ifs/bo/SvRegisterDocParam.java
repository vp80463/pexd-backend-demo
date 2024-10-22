package com.a1stream.ifs.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmConsumerVO;
import com.a1stream.domain.vo.CmmGeorgaphyVO;
import com.a1stream.domain.vo.CmmRegistrationDocumentVO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.ConsumerPrivateDetailVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.SerializedProductVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvRegisterDocParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<BatteryVO> updBatteryList = new ArrayList<>();
    private List<CmmBatteryVO> updCmmBatteryList = new ArrayList<>();

    private List<CmmConsumerVO> updCmmConsumerList = new ArrayList<>();
    private List<ConsumerPrivateDetailVO> updConsumerPrivateList = new ArrayList<>();
    private List<CmmRegistrationDocumentVO> updRegistDocList = new ArrayList<>();

    // param
    private Map<String, CmmBatteryVO> cmmBatteryVOMap;
    private Map<String, BatteryVO> batteryVOMap;
    private Map<Long, CmmRegistrationDocumentVO> registDocBatteryMap;
    private Map<String, CmmGeorgaphyVO> cityGeographyMap;
    private Map<String, MstFacilityVO> facilityMap;
    private Map<String, CmmSerializedProductVO> serialProductMap;
    private Map<Long, SerializedProductVO> productMap;
    private Map<Long, CmmRegistrationDocumentVO> serialProductRegistDocMap;

    private Long consumerId;
}