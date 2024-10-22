package com.a1stream.domain.bo.batch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeaderBO {

    private String requestId;
    private String messageType;
    private String senderCode;
    private String receiverCode;
    private String createDateTime;
}
