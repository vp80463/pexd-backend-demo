package com.a1stream.common.constants;

import java.util.List;

import lombok.Data;
import software.amazon.awssdk.services.sqs.model.Message;

@Data
public class MessageContent {

	private String queueName;

	private List<Message> messages;

}
