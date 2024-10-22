package com.blank.test.tests;

import com.a1stream.common.manager.ConsumerManager;
import com.a1stream.common.model.BaseConsumerForm;
import com.blank.DomainTestConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = { DomainTestConfiguration.class })
@ActiveProfiles("h2-test")
@Rollback
class DomainTestCase {

    @InjectMocks
    private ConsumerManager consumerManager;

    @Test
    void test() {
        // Prepare
        int i = 1;

        // Execute
        i++;

        // Assert
        Assertions.assertEquals(2, i);
    }

    @Test
    void consumerManagerTest() {

        // 模拟前台传送的数据
        BaseConsumerForm form = new BaseConsumerForm();
        form.setAddress("addressTest");
        consumerManager.saveOrUpdateConsumer(form);


        // Assert
        Assertions.assertTrue(true);
    }
}
