package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class MessageSendBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5225780325472224537L;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 消息
     */
    private Object message;
}