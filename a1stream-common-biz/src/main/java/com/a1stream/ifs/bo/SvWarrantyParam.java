package com.a1stream.ifs.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import com.a1stream.common.constants.PJConstants.WarrantyPolicyType;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmServiceDemandDetailVO;
import com.a1stream.domain.vo.CmmWarrantyBatteryVO;
import com.a1stream.domain.vo.CmmWarrantySerializedProductVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvWarrantyParam implements Serializable {

    private static final long serialVersionUID = 1L;
    // 更新对象
    private List<CmmServiceDemandDetailVO> updSvDemandDtl = new ArrayList<>();
    private List<CmmWarrantySerializedProductVO> updWarrantySerialProd = new ArrayList<>();
    private List<CmmWarrantyBatteryVO> updWarrantyBattery = new ArrayList<>();

    // 参数
    private Map<Long, String> svDemandMap;
    private Map<Long, CmmSerializedProductVO> prodIdAndSerProdMap = new HashMap<>();
    private Map<Long, SvWarrantyBO> prodIdAndBOMap = new HashMap<>();
    private Map<Long, List<SvWarrantyItemBO>> prodIdAndItemBOMap = new HashMap<>();
    private Map<Long, CmmWarrantySerializedProductVO> warrantyPolicyMap;
    private Map<Long, CmmWarrantyBatteryVO> warrantyBatteryMap;
    private Map<Long, List<CmmServiceDemandDetailVO>> svDemandDtlMap;

    private Map<String, String> warrantyTypeMap = new HashedMap<String, String>() {
        private static final long serialVersionUID = 1L;
        {
            put("COUP_EXTENDED_WARRANTY", WarrantyPolicyType.NEW.getCodeDbid());
            put("FULL_WARRANTY", WarrantyPolicyType.OLD.getCodeDbid());
            put("BIGBIKE_WARRANTY", WarrantyPolicyType.BIGBIKE.getCodeDbid());
            put("EV_WARRANTY", WarrantyPolicyType.EV.getCodeDbid());
            put("BATTERY_WARRANTY", WarrantyPolicyType.BATTERY.getCodeDbid());
        }
    };
}
