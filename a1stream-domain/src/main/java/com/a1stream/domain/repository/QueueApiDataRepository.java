package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.QueueApiData;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface QueueApiDataRepository extends JpaExtensionRepository<QueueApiData, Long> {

}
