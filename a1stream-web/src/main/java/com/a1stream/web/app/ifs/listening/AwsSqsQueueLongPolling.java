package com.a1stream.web.app.ifs.listening;

//@Component
//@Slf4j
//public class AwsSqsQueueLongPolling {
//
//    @Value("${ifs.listen.consumerIp}")
//    public String CONSUMER_IP;
//
//    @Autowired
//    private InterfManager interfManager;
//
//    public static final String CONSUMER_TYPE = ConsumerType.CMM.toString();
//
//    public void receiveMessage() {
//
//        SqsClient sqsClient = SqsClient.builder()
//                                       .region(Region.AP_SOUTHEAST_1)
//                                       .build();
//
//        setLongPoll(sqsClient);
//        sqsClient.close();
//    }
//
//    public void setLongPoll(SqsClient sqsClient) {
//        // Enable long polling when creating a queue.
//        HashMap<QueueAttributeName, String> attributes = new HashMap<QueueAttributeName, String>();
//        attributes.put(QueueAttributeName.RECEIVE_MESSAGE_WAIT_TIME_SECONDS, "20");
//
//        try {
//            //遍历获取所有SQS队列的队列名
//            ListQueuesResponse listQueues = sqsClient.listQueues();
//            Optional<Object> queueUrlListOpt = listQueues.getValueForField(AwsSqs.QUEUE_URLS, Object.class);
//            if(queueUrlListOpt.isPresent() && queueUrlListOpt.get() instanceof List<?>) {
//                Object queueUrlListObj = queueUrlListOpt.get();
//                List<String> queueUrlList = JSONArray.parseArray(JSONArray.toJSONString(queueUrlListObj), String.class);
//                if(CollectionUtils.isNotEmpty(queueUrlList)) {
//                    for (String queueUrl : queueUrlList) {
//                        String[] parts = queueUrl.split(CommonConstants.CHAR_SLASH);
//                        String queueName = parts[parts.length - 1];
//                        System.out.println("Queue Name: " + queueName);
//                        //每个队列进行消息接收
//                        doMessageReceive(attributes, sqsClient, queueName);
//                    }
//                }
//            }
//
//        } catch (SqsException e) {
//            //System.err.println(e.awsErrorDetails().errorMessage());
//            log.error("AWS SQS get queue name list error", e);
//        }
//    }
//
//    //接收队列消息
//    private void doMessageReceive(HashMap<QueueAttributeName, String> attributes, SqsClient sqsClient, String queueName) {
//        // TODO Auto-generated method stub
//        try {
//
//            GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
//                    .queueName(queueName)
//                    .build();
//
//            String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();
//            SetQueueAttributesRequest setAttrsRequest = SetQueueAttributesRequest.builder()
//                    .queueUrl(queueUrl)
//                    .attributes(attributes)
//                    .build();
//
//            sqsClient.setQueueAttributes(setAttrsRequest);
//
//            // Enable long polling on a message receipt.
//            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
//                    .queueUrl(queueUrl)
//                    .waitTimeSeconds(20)
//                    .build();
//
//            List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();
//            //这边对获取的消息进行处理
//            for (Message message : messages) {
//                Optional<String> stringBody = message.getValueForField(AwsSqs.MESSAGE_BODY, String.class);
//                if(stringBody.isPresent()) {
//                    String body = stringBody.get();
//                    InterfProcessRequest request = JSON.parseObject(body,InterfProcessRequest.class);
//                    messageProcess(request, queueName, message, sqsClient, queueUrl);
//                }
//            }
//        } catch (SqsException e) {
//            // TODO: handle exception
//            //System.err.println(e.awsErrorDetails().errorMessage());
//            log.error("AWS SQS receive message error", e);
//        }
//    }
//
//    //处理接收的消息
//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    private void messageProcess(InterfProcessRequest request, String queueName, Message message, SqsClient sqsClient, String queueUrl) {
//        try {
//            //保存队列名
//            request.setQueueName(queueName);
//            request.setStartTime(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMDHMS));
//            if (StringUtils.isNotBlankText(request.getUpdateProgram())) {
//                PJAuditableDetail pjAuditableDetail = ThreadLocalPJAuditableDetailAccessor.getValue();
//                pjAuditableDetail.setUpdateProgram(request.getUpdateProgram());
//                pjAuditableDetail.setIfsUser(request.getIfsUser());
//            }
//
//            //判断是否是指定的消费者IP，不是则重发
//            if(checkConsumerIp(request.getConsumerIp())){
//                String interfCode = request.getInterfCode();
//                InterfProcessResponse response = new InterfProcessResponse();
//                try{
//                    if(interfManager.constainKey(CONSUMER_TYPE,interfCode)) {
//                        InterfProcessor process = ApplicationContextHolder.get(interfManager.findByCode(CONSUMER_TYPE,interfCode).getClass());
//                        response = process.execute(request);
//                        //这边用来保存消费日志
//                        /*if (response == null) {
//                            consumerSendService.sendErrorForProducer(request,"consumer response layout error, response: " + response);
//                        } else {
//                            consumerSendService.sendToProducer(response, request);
//                        }*/
//                    }else{
//                        //consumerSendService.sendErrorForProducer(request,"consumer listen not exist interfcode: " + interfCode);
//                    }
//                }catch(Exception e){
//                    log.error("ConsumerDirectReceiver, interfCode:" + interfCode, e);
//                    //consumerSendService.sendErrorForProducer(request, e.toString());
//                }
//            }else {
//
//            }
//
//        } catch (Exception e) {
//            // TODO: handle exception
//            log.error("message process error", e);
//        }finally {
//
//            /*最后不管成没成功，都需要将接收的消息，进行删除，避免重复进行接收，
//              至于接收处理失败的消息，采用系统的重发机制，进行消息的重发然后重新进行消费*/
//            Optional<String> receiptHandleOpt = message.getValueForField(AwsSqs.RECEIPT_HANDLE, String.class);
//            if(receiptHandleOpt.isPresent()) {
//                DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
//                                                           .queueUrl(queueUrl)
//                                                           .receiptHandle(receiptHandleOpt.get()).build();
//                sqsClient.deleteMessage(deleteMessageRequest);
//            }
//        }
//    }
//
//    private boolean checkConsumerIp(String consumerIp){
//        boolean flag = true;
//        if(StringUtils.isNotBlankText(consumerIp)){
//            if(!StringUtils.equals(consumerIp, CONSUMER_IP)){
//                flag = false;
//            }
//        }
//        return flag;
//    }
//}
