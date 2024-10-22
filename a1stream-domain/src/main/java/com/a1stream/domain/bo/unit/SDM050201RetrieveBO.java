package com.a1stream.domain.bo.unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.a1stream.domain.vo.CmmUnitPromotionListVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM050201RetrieveBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private CmmUnitPromotionListVO header = new CmmUnitPromotionListVO();
    private List<SDM050201BO> detailList = new ArrayList<>();

}
