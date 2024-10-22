package com.a1stream.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class MstProductVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long productId;

    private String productClassification;

    private Long productCategoryId;

    private String productCd;

    private String allParentId;

    private String allPath;

    private String allNm;

    private String registrationDate;

    private String englishDescription;

    private String localDescription;

    private String salesDescription;

    private Serializable productProperty;

    private String productRetrieve;

    private BigDecimal stdRetailPrice = BigDecimal.ZERO;

    private BigDecimal stdWsPrice = BigDecimal.ZERO;

    private String stdPriceUpdateDate;

    private BigDecimal salLotSize = BigDecimal.ZERO;

    private BigDecimal purLotSize = BigDecimal.ZERO;

    private BigDecimal minSalQty = BigDecimal.ZERO;

    private BigDecimal minPurQty = BigDecimal.ZERO;

    private String salesStatusType;

    private Long brandId;

    private String brandCd;

    private String imageUrl;

    private String batteryFlag;

    private String doDescription;

    private String colorCd;

    private String colorNm;

    private Integer displacement = CommonConstants.INTEGER_ZERO;

    private Integer productLevel = CommonConstants.INTEGER_ZERO;

    private Long toProductId;

    private String modelType;

    private String expiredDate;

    public static MstProductVO create() {
        MstProductVO entity = new MstProductVO();
        entity.setProductId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }

}
