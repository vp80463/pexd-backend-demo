package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.QueueOrderBkList;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface QueueOrderBkListRepository extends JpaExtensionRepository<QueueOrderBkList, Long> {

}
