package com.a1stream.domain.repository;

import com.a1stream.domain.custom.CmmMessageRepositoryCustom;
import com.a1stream.domain.entity.CmmMessage;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author dong zhen
 */
@Repository
public interface CmmMessageRepository extends JpaExtensionRepository<CmmMessage, Long>, CmmMessageRepositoryCustom {

    @Query(value="select * from cmm_message " 
               + "where site_id <> :siteId", nativeQuery=true)
    List<CmmMessage> findBySiteIdNotIn( @Param("siteId") String siteId);

}
