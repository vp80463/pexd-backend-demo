package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ServiceScheduleVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceScheduleId;

    private String serviceScheduleNo;

    private String scheduleDate;

    private String scheduleTime;

    private String picNm;

    private Long facilityId;

    private Long consumerId;

    private String consumerNm;

    private String mobilePhone;

    private Long serializedProductId;

    private String plateNo;

    private Long productId;

    private String productCd;

    private String productNm;

    private String memo;

    private Long serviceOrderId;

    private String orderNo;

    private String reservationStatus;

    private String serviceContents;

    private String bookingMethod;

    private String estimateFinishTime;

    private String serviceOrderSettledFlag;

    public static ServiceScheduleVO create() {
        ServiceScheduleVO entity = new ServiceScheduleVO();
        entity.setServiceScheduleId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }

}
