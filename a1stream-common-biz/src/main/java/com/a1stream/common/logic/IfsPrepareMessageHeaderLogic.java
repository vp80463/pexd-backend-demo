package com.a1stream.common.logic;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.batch.HeaderBO;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.IdUtils;
import com.ymsl.solid.base.util.uuid.SnowflakeIdWorker;

@Component
public class IfsPrepareMessageHeaderLogic {

    public HeaderBO setHeaderBo(String interfCode) {

        SnowflakeIdWorker snowflakeIdWorker = IdUtils.getSnowflakeIdWorker();

        String currentDateString = DateUtils.getCurrentDateString(DateUtils.FORMAT_YMDHMSS_HYPHEN);
        HeaderBO headerBO = new HeaderBO();
        headerBO.setRequestId(snowflakeIdWorker.nextIdStr());
        headerBO.setMessageType(interfCode);
        headerBO.setSenderCode(CommonConstants.CHAR_DMS);
        headerBO.setReceiverCode(CommonConstants.CHAR_YAMAHA);
        headerBO.setCreateDateTime(currentDateString);

        return headerBO;
    }
}
