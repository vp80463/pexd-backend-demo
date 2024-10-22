package com.a1stream.domain.bo.master;

import java.util.List;

import com.a1stream.common.model.BaseTableData;
import com.a1stream.domain.bo.common.FacilityBO;
import com.a1stream.domain.vo.MstFacilityVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM020502BO extends MstFacilityVO {

    private static final long serialVersionUID = 1L;

    private String wsDealerSign;

    private String shop;

    private List<FacilityBO> deliveryPointList; // 自定义List接收传入参数

    private BaseTableData<FacilityBO> deliveryPointTable; // 接收有变更的Table记录
}
