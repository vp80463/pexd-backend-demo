package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ApiSvPartItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ApiSvPartItemRepository extends JpaExtensionRepository<ApiSvPartItem, Long> {

}
