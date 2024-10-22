package com.a1stream.common.ifs.messageing;

import com.a1stream.common.constants.MessageContent;

/**
 * ListenerExecutionFailedException
 *
 */
public class ListenerExecutionFailedException extends AmazonSQSException {

    private static final long serialVersionUID = -3833634774055328399L;
    private final MessageContent failedMessage;

    public ListenerExecutionFailedException(String msg, Throwable cause, MessageContent failedMessage) {
        super(msg, cause);
        this.failedMessage = failedMessage;
    }

    public MessageContent getFailedMessage() {
        return this.failedMessage;
    }

}
