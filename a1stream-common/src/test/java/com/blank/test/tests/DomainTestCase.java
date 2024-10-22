package com.blank.test.tests;

import com.blank.DomainTestConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = { DomainTestConfiguration.class })
@ActiveProfiles("h2-test")
@Rollback
class DomainTestCase {

    @Test
    void test() {
        // Prepare
        int i = 1;

        // Execute
        i++;

        // Assert
        Assertions.assertEquals(2, i);
    }
}
