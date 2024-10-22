package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ApiSvFaultItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ApiSvFaultItemRepository extends JpaExtensionRepository<ApiSvFaultItem, Long> {

}
