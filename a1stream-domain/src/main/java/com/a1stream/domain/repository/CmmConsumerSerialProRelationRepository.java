package com.a1stream.domain.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.custom.CmmConsumerSerialProRelationRepositoryCustom;
import com.a1stream.domain.entity.CmmConsumerSerialProRelation;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmConsumerSerialProRelationRepository extends JpaExtensionRepository<CmmConsumerSerialProRelation, Long>, CmmConsumerSerialProRelationRepositoryCustom {

    CmmConsumerSerialProRelation findFirstBySerializedProductIdAndConsumerId(Long serializedProductId, Long consumerId);

    @Query(value="select * from cmm_consumer_serial_pro_relation "
            + "where serialized_product_id =:serializedProductId "
            + "and owner_flag = '" + CommonConstants.CHAR_Y + "' "
            + "limit 1  ", nativeQuery=true)
    CmmConsumerSerialProRelation findOwnerBySerializedProductId(@Param("serializedProductId") Long serializedProductId);
}
