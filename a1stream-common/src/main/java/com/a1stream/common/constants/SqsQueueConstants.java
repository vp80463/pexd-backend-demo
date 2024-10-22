package com.a1stream.common.constants;

public class SqsQueueConstants {

    public static final String ID   = "id";
    public static final String NAME = "name";

    public static class Queue {
        public static final String Q_IFS_CONSUMER_RROCESS          = "q_ifs_consumer_process.fifo";//IFS监听消费
        public static final String Q_IFS_INTERF_CC_QUEUE           = "q_ifs_interf_cc_queue.fifo";//IFS调用消费队列
        public static final String Q_IFS_LOG_PROCESS               = "q_ifs_log_process.fifo";//操作日志
        public static final String Q_IFS_BATCH_BUFFER              = "q_ifs_batch_buffer.fifo";
        public static final String Q_IFS_MESSAGE_PUSH_QUEUE        = "q_ifs_message_push_queue.fifo";//消息推送

        public static final String Q_IFS_THROW_LOG_QUEUE           = "q_ifs_throw_log_queue.fifo";//异常日志队列

        public static final String Q_IFS_CONSUMER_RESULT_CATCH             = "q_ifs_consumer_result_catch.fifo";//日志返回队列(重试三次)
        public static final String Q_IFS_CONSUMER_DEAD_COOL_QUEUE          = "q_ifs_consumer_dead_cool_queue.fifo";//消费死信冷却队列(五分钟)
        public static final String Q_IFS_CONSUMER_DEAD_RETRY_QUEUE         = "q_ifs_consumer_dead_retry_queue.fifo";//消费冷却重试队列(三次)
        public static final String Q_IFS_CONSUMER_DEAD_QUEUE               = "q_ifs_consumer_dead_queue.fifo";//消费最终死信队列

        public static final String Q_IFS_THROW_INTERF_CC_QUEUE             = "q_ifs_throw_interf_cc_queue.fifo";//异常接口调用消费队列

        public static final String Q_IFS_FILE_INTERF_QUEUE                 = "q_ifs_file_interf_queue.fifo";//文件写入队列

        public static final String YMVN_DMS_WEBMC1_QUEUE                   = "YMVN-DMS-WEBMC1.fifo";

    }
}