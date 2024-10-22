package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.SysAdditionalSetting;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


/**
 * @author Liu Chaoran
 */
@Repository
public interface SysAdditionalSettingRepository extends JpaExtensionRepository<SysAdditionalSetting, Long> {

    List<SysAdditionalSetting> findBySiteId(String siteId);

    List<SysAdditionalSetting> findBySiteIdAndMenuCodeLike(String siteId, String menuCode);

    @Modifying
    @Query("DELETE FROM SysAdditionalSetting e WHERE e.siteId = :siteId")
    void deleteAdditionalSetting(@Param("siteId") String siteId);

}
