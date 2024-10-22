package com.blank.test.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.a1stream.common.logic.ConsumerLogic;
import com.a1stream.common.manager.ConsumerManager;
import com.a1stream.common.model.BaseConsumerForm;
import com.a1stream.domain.entity.CmmConsumer;
import com.a1stream.domain.entity.ConsumerPrivateDetail;
import com.a1stream.domain.repository.CmmConsumerRepository;
import com.a1stream.domain.repository.ConsumerPrivateDetailRepository;
import com.a1stream.domain.vo.CmmConsumerVO;
import com.a1stream.domain.vo.ConsumerPrivateDetailVO;
import com.blank.DomainTestConfiguration;


@SpringBootTest(classes = { DomainTestConfiguration.class })
class ConsumerSaveOrUpdateTestCase {

    @InjectMocks
    private ConsumerManager consumerManager;

    @Mock
    private ConsumerPrivateDetailRepository consumerPrivateDetailRepository;

    @Mock
    private ConsumerLogic consumerLogic;

    @Mock
    private CmmConsumerRepository cmmConsumerRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 初始化测试数据
        ConsumerPrivateDetail consumerPrivateDetail = new ConsumerPrivateDetail();
        ConsumerPrivateDetailVO consumerPrivateDetailVO = new ConsumerPrivateDetailVO();
        consumerPrivateDetailVO.setConsumerId(Long.valueOf(12345678));
        consumerPrivateDetail.setConsumerId(Long.valueOf(12345678));
        CmmConsumerVO cmmConsumerVO = new CmmConsumerVO();
        cmmConsumerVO.setConsumerId(Long.valueOf(12345678));
        CmmConsumer cmmConsumer = new CmmConsumer();
        cmmConsumer.setConsumerId(Long.valueOf(12345678));

        when(consumerPrivateDetailRepository.findByConsumerRetrieve(anyString())).thenReturn(consumerPrivateDetail);
        when(cmmConsumerRepository.findByConsumerId(any())).thenReturn(cmmConsumer);
    }

    // case1 如果所有名称与电话都为空则直接return
    @Test
    void consumerManagerTestCase1() {
        // 模拟前台传送的数据
        BaseConsumerForm form = new BaseConsumerForm();
        form.setAddress("addressTest");
        consumerManager.saveOrUpdateConsumer(form);

        // Assert
        Assertions.assertTrue(true);
    }

    //case2 ConsumerId为空则与consumerPrivateDetail为空的情况
    @Test
    void consumerManagerTestCase2() {
        BaseConsumerForm form = new BaseConsumerForm();
        form.setConsumerId(null); // 初始为空
        form.setFirstNm("firstName");
        form.setMiddleNm("middleName");
        form.setLastNm("lastName");
        form.setMobilePhone("18888888888");

        // 模拟 form 的 getConsumerId() 方法返回空
        when(consumerLogic.getConsumerRetrieve(any(),any(),any(),any()))
                          .thenReturn(getConsumerRetrieve(form.getLastNm()
                                                         ,form.getMiddleNm()
                                                         ,form.getFirstNm()
                                                         ,form.getMobilePhone()));

        //调用被测方法
        consumerManager.saveOrUpdateConsumer(form);

        // 验证form.getConsumerId()是否被设置（假设在buildCmmConsumerVO中设置了ID）
        assertNotNull(form.getConsumerId().toString(), "Consumer ID should be set for a new consumer");
    }

    //case3 ConsumerId为空则与consumerPrivateDetail不为空的情况
    @Test
    void consumerManagerTestCase3() {
        BaseConsumerForm form = new BaseConsumerForm();
        form.setConsumerId(null); // 初始为空
        form.setFirstNm("firstName");
        form.setMiddleNm("middleName");
        form.setLastNm("lastName");
        form.setMobilePhone("18888888888");

        when(consumerLogic.getConsumerRetrieve(any(),any(),any(),any())).thenReturn("RHS1382222222");

        //调用被测方法
        consumerManager.saveOrUpdateConsumer(form);

        // Assert
        assertEquals(Long.valueOf(12345678), form.getConsumerId());
    }

    private String getConsumerRetrieve(String lastNm, String middleNm, String firstNm, String mobilePhone) {

        return new StringBuilder()
                  .append(lastNm)
                  .append(middleNm)
                  .append(firstNm)
                  .append(mobilePhone)
                  .toString()
                  .replace(" ", "")
                  .toUpperCase();
    }
}


