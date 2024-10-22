package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmConsumerRepositoryCustom;
import com.a1stream.domain.entity.CmmConsumer;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmConsumerRepository extends JpaExtensionRepository<CmmConsumer, Long>, CmmConsumerRepositoryCustom{

    CmmConsumer findByConsumerId(Long consumerId);

    List<CmmConsumer> findByConsumerIdIn(List<Long> consumerId);

}
