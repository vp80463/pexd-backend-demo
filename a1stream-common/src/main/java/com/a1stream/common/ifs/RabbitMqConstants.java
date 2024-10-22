package com.a1stream.common.ifs;

public class RabbitMqConstants {

    public static class Exchange {
        public static final String DIRECT_EXCHANGE   = "direct_exchange";
        public static final String TOPIC_EXCHANGE    = "topic_exchange";
        public static final String DEAD_EXCHANGE     = "dead_exchange";
    }

    public static class Queue {
        public static final String Q_IFS_CONSUMER_RROCESS          = "q_ifs_consumer_process";//ifs监听消费
        public static final String Q_IFS_LOG_PROCESS               = "q_ifs_log_process";//操作日志
        public static final String Q_IFS_BATCH_BUFFER              = "q_ifs_batch_buffer";
        public static final String Q_IFS_MESSAGE_PUSH_QUEUE        = "q_ifs_message_push_queue";//消息推送

        public static final String Q_IFS_THROW_LOG_QUEUE           = "q_ifs_throw_log_queue";//异常日志队列
//        public static final String Q_IFS_INTERF_LOG_QUEUE          = "q_ifs_interf_log_queue";//日志保存队列

        public static final String Q_IFS_CONSUMER_RESULT_CATCH             = "q_ifs_consumer_result_catch";//日志返回队列(重试三次)
        public static final String Q_IFS_CONSUMER_DEAD_COOL_QUEUE          = "q_ifs_consumer_dead_cool_queue";//消费死信冷却队列(五分钟)
        public static final String Q_IFS_CONSUMER_DEAD_RETRY_QUEUE         = "q_ifs_consumer_dead_retry_queue";//消费冷却重试队列(三次)
        public static final String Q_IFS_CONSUMER_DEAD_QUEUE               = "q_ifs_consumer_dead_queue";//消费最终死信队列

        public static final String Q_IFS_THROW_INTERF_CC_QUEUE             = "q_ifs_throw_interf_cc_queue";//异常接口调用消费队列

        public static final String Q_IFS_FILE_INTERF_QUEUE                 = "q_ifs_file_interf_queue";//文件写入队列
    }
}

