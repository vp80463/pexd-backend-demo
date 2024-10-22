package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmMstOrganizationRepositoryCustom;
import com.a1stream.domain.entity.CmmMstOrganization;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmMstOrganizationRepository extends JpaExtensionRepository<CmmMstOrganization, Long>, CmmMstOrganizationRepositoryCustom {

    CmmMstOrganization findByOrganizationId(Long organizationId);

    List<CmmMstOrganization> findByOrganizationIdIn(List<Long> organizationId);

    List<CmmMstOrganization> findByOrganizationCdIn(Set<String> organizationCdSet);

    List<CmmMstOrganization> findByOrganizationCdNotIn(List<String> organizationCdList);

    CmmMstOrganization findBySiteIdAndOrganizationCd(String siteId, String orgCd);

    @Query(value="SELECT * FROM cmm_mst_organization "
            + "WHERE site_id = :siteId "
            + "AND organization_type = :organizationType "
            + "AND from_date <= :sysDate "
            + "AND to_date >= :sysDate", nativeQuery=true)
    List<CmmMstOrganization> getCmmMstOrganization(@Param("siteId") String siteId,
                                             @Param("organizationType") String organizationType,
                                             @Param("sysDate") String sysDate);
}
