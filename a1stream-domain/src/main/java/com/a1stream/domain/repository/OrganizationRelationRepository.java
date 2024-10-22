package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.OrganizationRelationRepositoryCustom;
import com.a1stream.domain.entity.OrganizationRelation;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface OrganizationRelationRepository extends JpaExtensionRepository<OrganizationRelation, Long>, OrganizationRelationRepositoryCustom{

    List<OrganizationRelation> findBySiteIdAndRelationType(String siteId, String relationType);
    List<OrganizationRelation> findBySiteIdAndRelationTypeAndProductClassification(String siteId, String relationType,String productClassification);

    OrganizationRelation findByRelationTypeAndSiteId(String relationType, String siteId);

    List<OrganizationRelation> findBySiteIdInAndRelationType(Set<String> siteIdSet, String relationType);
}
