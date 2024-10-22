package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.CmmPersonFacility;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmPersonFacilityRepository extends JpaExtensionRepository<CmmPersonFacility, Long> {

    List<CmmPersonFacility> findBySiteIdAndPersonId(String siteId, Long personId);

}
