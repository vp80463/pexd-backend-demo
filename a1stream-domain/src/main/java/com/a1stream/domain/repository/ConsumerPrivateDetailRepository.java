package com.a1stream.domain.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ConsumerPrivateDetailRepositoryCustom;
import com.a1stream.domain.entity.ConsumerPrivateDetail;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ConsumerPrivateDetailRepository extends JpaExtensionRepository<ConsumerPrivateDetail, Long>, ConsumerPrivateDetailRepositoryCustom{

    ConsumerPrivateDetail findFirstByConsumerIdAndSiteId(Long consumerId, String siteId);

    @Query(value="SELECT * FROM consumer_private_detail "
               + "WHERE consumer_retrieve = :consumerRetrieve "
               + "LIMIT 1 ", nativeQuery = true)
    ConsumerPrivateDetail findByConsumerRetrieve(@Param("consumerRetrieve") String consumerRetrieve);
}
