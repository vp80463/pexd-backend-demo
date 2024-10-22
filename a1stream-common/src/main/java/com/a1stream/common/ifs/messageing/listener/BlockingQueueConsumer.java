package com.a1stream.common.ifs.messageing.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.a1stream.common.constants.MessageContent;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

/**
 *  Amazon SQS 消费者
 *
 */
@Slf4j
public class BlockingQueueConsumer {

    private final BlockingQueue<List<Message>> queue;

    private final SqsClient amazonSQS;

    private final String queueName;
    private final int waitTimeSeconds;
    private final int maxNumberOfMessages;

    private final Executor taskExecutor;

    volatile Thread thread;

    volatile boolean declaring;

    public BlockingQueueConsumer(SqsClient amazonSQS, Executor taskExecutor, String queueName, int waitTimeSeconds, int maxNumberOfMessages, int prefetchCount) {
        this.amazonSQS = amazonSQS;
        this.taskExecutor = taskExecutor;
        this.queueName = queueName;
        this.waitTimeSeconds = waitTimeSeconds;
        this.maxNumberOfMessages = maxNumberOfMessages;
        this.queue = new LinkedBlockingQueue<>(prefetchCount);
    }

    public Executor getTaskExecutor() {
        return taskExecutor;
    }

    public void start() {
        this.thread = Thread.currentThread();
        this.declaring = true;
        consumeFromQueue(queueName);
    }

    public synchronized void stop() {
        this.queue.clear(); // in case we still have a client thread blocked
    }

    private void consumeFromQueue(String queueName) {
        InternalConsumer consumer = new InternalConsumer(this.amazonSQS, queueName);
        getTaskExecutor().execute(consumer);
    }

    public MessageContent nextMessage() throws InterruptedException {
        return handle(this.queue.take());
    }

    public MessageContent nextMessage(long timeout) throws InterruptedException {
        MessageContent message = handle(this.queue.poll(timeout, TimeUnit.MILLISECONDS));
        return message;
    }

    protected boolean hasDelivery() {
        return !this.queue.isEmpty();
    }

    private MessageContent handle(List<Message> messages) {
        if(messages == null || messages.isEmpty()) {
            return null;
        }
        MessageContent item = new MessageContent();
        item.setQueueName(this.queueName);
        item.setMessages(messages);
        return item;
    }

    private final class InternalConsumer implements Runnable {

        private final SqsClient amazonSQS;

        private final String queueName;

        public InternalConsumer(SqsClient amazonSQS, String queueName) {
            this.amazonSQS = amazonSQS;
            this.queueName = queueName;
        }

        @Override
        public void run() {
            while(true){
                try {
                    if(BlockingQueueConsumer.this.queue.size() <= 0){

                        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                                .queueUrl(queueName)
                                .waitTimeSeconds(waitTimeSeconds)
                                .maxNumberOfMessages(maxNumberOfMessages)
                                .build();
                        List<Message> messages = amazonSQS.receiveMessage(receiveMessageRequest).messages();


                        List<DeleteMessageBatchRequestEntry> entries = new ArrayList<>();
                        for (Message message : messages) {
                            entries.add(DeleteMessageBatchRequestEntry.builder()
                                    .id(message.messageId())
                                    .receiptHandle(message.receiptHandle())
                                    .build());
                        }
                        if(!messages.isEmpty()) {

                            try {
                                BlockingQueueConsumer.this.queue.put(messages);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            } finally {
                                DeleteMessageBatchRequest deleteMessageBatchRequest = DeleteMessageBatchRequest.builder()
                                        .queueUrl(queueName)
                                        .entries(entries)
                                        .build();
                                amazonSQS.deleteMessageBatch(deleteMessageBatchRequest);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn(this.queueName + ": amazonSQS.receiveMessage error, sleep 120s try again!!");
                    try {
                        Thread.sleep(120000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

}
