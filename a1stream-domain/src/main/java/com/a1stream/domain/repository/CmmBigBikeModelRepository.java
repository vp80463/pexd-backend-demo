package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.CmmBigBikeModel;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmBigBikeModelRepository extends JpaExtensionRepository<CmmBigBikeModel, Long> {

    Boolean existsByModelCd(String modelCd);

    List<CmmBigBikeModel> findBySiteId(String siteId);

    @Modifying
    @Query("DELETE FROM CmmBigBikeModel e WHERE e.bigBikeModelId in :deleteIds")
    void deleteOriBigBike(@Param("deleteIds") Set<Long> deleteIds);
}
