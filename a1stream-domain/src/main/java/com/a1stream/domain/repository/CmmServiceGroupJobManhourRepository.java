package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.CmmServiceGroupJobManhour;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmServiceGroupJobManhourRepository extends JpaExtensionRepository<CmmServiceGroupJobManhour, Long> {

    List<CmmServiceGroupJobManhour> findByServiceGroupIdIn(Set<Long> serviceGroupIds);

    @Modifying
    @Query("DELETE FROM CmmServiceGroupJobManhour e WHERE e.serviceGroupJobManhourId in :deleteIds")
    void deleteSvGroupJob(@Param("deleteIds") Set<Long> deleteIds);
}
