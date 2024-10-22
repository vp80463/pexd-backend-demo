package com.a1stream.ifs.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.ServiceRequestEditHistoryVO;
import com.a1stream.domain.vo.ServiceRequestJobVO;
import com.a1stream.domain.vo.ServiceRequestPartsVO;
import com.a1stream.domain.vo.ServiceRequestVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvClaimJudgeResultParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long svReqId;
    private String siteId;
    private String requestType;
    // 更新数据对象
    private List<ServiceRequestVO> svRequestInfos = new ArrayList<>();
    private List<ServiceRequestJobVO> updSvReqJobList = new ArrayList<>();
    private List<ServiceRequestPartsVO> updSvReqPartList = new ArrayList<>();
    private List<ServiceRequestEditHistoryVO> updSvReqEditHistList = new ArrayList<>();
    // 事前准备的参数
    private Long symptomId; // L1
    private Long conditionId; // C1
    private Map<String, Long> productMap;
    private Map<String, MstFacilityVO> facilityMap;
    private Map<String, List<SvClaimJudgeResultBO>> siteDataList;

    private Map<String, String> actJudgeMap = new HashedMap<String, String>() {
        private static final long serialVersionUID = 1L;
        {
            put("JUDGE_STATUS_APPROVED", "ACTION_JUDGEMENT_APPROVED");
            put("JUDGE_STATUS_DENIED", "ACTION_JUDGEMENT_DENIED");
            put("JUDGE_STATUS_ERROR", "ACTION_JUDGEMENT_ERROR");
            put("JUDGE_STATUS_HOLD_FOR_PART", "ACTION_HOLDING_FOR_PARTS");
            put("JUDGE_STATUS_CANCELLED", "ACTION_CANCELLED");
            put("JUDGE_STATUS_DROPPED", "ACTION_DROPPED");
            put("JUDGE_STATUS_SUBMITTED", "ACTION_SUBMITTED");
            put("JUDGE_STATUS_ACCOUNT_CONFIRMED", "ACTION_PAYMENTDOCRECEIVED");
            put("JUDGE_STATUS_HOLD_FOR_REVIEW", "ACTION_HOLD_FOR_REVIEW");
        }
    };
    private Map<String, String> calStatusMap = new HashedMap<String, String>() {
        private static final long serialVersionUID = 1L;
        {
            put("ACTION_CREATE", "S026NEW");
            put("ACTION_ISSUE", "S026ISSUED");
            put("ACTION_JUDGEMENT_APPROVED", "S026APPROVED");
            put("ACTION_JUDGEMENT_ERROR", "S026ERROR");
            put("ACTION_JUDGEMENT_DENIED", "S026REJECTED");
            put("ACTION_PAYMENTDOCRECEIVED", "S026ACCOUNTCONFIRMED");
            put("ACTION_HOLDING_FOR_PARTS", "S026HOLDINGFORPARTS");
            put("ACTION_CANCELLED", "S026CANCELLED");
            put("ACTION_DROPPED", "S026DROPPED");
            put("ACTION_SUBMITTED", "S026SUBMITTED");
            put("ACTION_HOLD_FOR_REVIEW", "S026HOLDFORREVIEW");
        }
    };
}
