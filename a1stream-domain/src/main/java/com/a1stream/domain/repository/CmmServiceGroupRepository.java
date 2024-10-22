package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.CmmServiceGroup;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmServiceGroupRepository extends JpaExtensionRepository<CmmServiceGroup, Long> {

    List<CmmServiceGroup> findByServiceGroupCdIn(Set<String> serviceGroupCds);
}
