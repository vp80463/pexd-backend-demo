package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmServiceGroupItemRepositoryCustom;
import com.a1stream.domain.entity.CmmServiceGroupItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmServiceGroupItemRepository extends JpaExtensionRepository<CmmServiceGroupItem, Long>, CmmServiceGroupItemRepositoryCustom {

    List<CmmServiceGroupItem> findByServiceGroupIdIn(Set<Long> serviceGroupIds);

    @Modifying
    @Query("DELETE FROM CmmServiceGroupItem e WHERE e.serviceGroupItemId in :deleteIds")
    void deleteSvGroupItem(@Param("deleteIds") Set<Long> deleteIds);
}
