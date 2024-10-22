package com.a1stream.common.bo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.LocationVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PickingResultItemBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long partsId;

    private BigDecimal pickingQuantity;

    private LocationVO pickingLocation;

    private DeliveryOrderItemVO bindEntity;
}