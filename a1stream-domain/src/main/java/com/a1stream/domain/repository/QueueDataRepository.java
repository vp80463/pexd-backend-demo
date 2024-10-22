package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.QueueData;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface QueueDataRepository extends JpaExtensionRepository<QueueData, Long> {

}
