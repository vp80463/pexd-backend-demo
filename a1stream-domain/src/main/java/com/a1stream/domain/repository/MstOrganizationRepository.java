package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.MstOrganizationRepositoryCustom;
import com.a1stream.domain.entity.MstOrganization;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface MstOrganizationRepository extends JpaExtensionRepository<MstOrganization, Long>, MstOrganizationRepositoryCustom {

    MstOrganization findBySiteIdAndOrganizationCd(String siteId, String orgCd);

    List<MstOrganization> findByOrganizationIdIn(Set<Long> organztionIds);

    List<MstOrganization> findByOrganizationCdIn(Set<String> organztionCdList);

    List<MstOrganization> findBySiteIdInAndOrganizationCd(Set<String> siteId, String orgCd);

    MstOrganization findFirstByOrganizationId(Long organizationId);

    @Query(value = " SELECT organization_id AS customerId "+
                     " FROM mst_organization mo " +
                     " WHERE organization_id in(select to_organization_id from organization_relation or2 where site_id = ?1 and relation_type = ?2)"
                     , nativeQuery = true )
    public Long getCustomerId(String siteId, String relationType);

    @Query(value = " SELECT * "+
                    " FROM mst_organization mo " +
                    " WHERE organization_id in(select to_organization_id from organization_relation or2 where site_id = ?1 and relation_type = ?2)"
                    , nativeQuery = true )
    public MstOrganization getCustomer(String siteId, String relationType);
}
