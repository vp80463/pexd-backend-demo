package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.MstProductRelationRepositoryCustom;
import com.a1stream.domain.entity.MstProductRelation;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface MstProductRelationRepository extends JpaExtensionRepository<MstProductRelation, Long>, MstProductRelationRepositoryCustom {

    List<MstProductRelation> findByToProductId(Long toProductId);

    List<MstProductRelation> findByRelationTypeAndToProductIdIn(String relationClass, Set<Long> toProductIds);

    @Query(value="select * from mst_product_relation "
            + "where site_id =:siteId "
            + "and relation_type =:relationClass "
            + "and to_product_id in :toProductIds "
            + "and to_date >= :todate "
            + "and from_date <=:fromDate", nativeQuery=true)
    List<MstProductRelation> findByValidRelationType( @Param("siteId") String siteId
                                            , @Param("relationClass") String relationClass
                                            , @Param("toProductIds") Set<Long> toProductIds
                                            , @Param("fromDate") String fromDate
                                            , @Param("todate") String todate
                                            );
}
