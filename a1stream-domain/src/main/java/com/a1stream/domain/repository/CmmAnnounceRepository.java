package com.a1stream.domain.repository;

import com.a1stream.domain.entity.CmmAnnounce;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CmmAnnounceRepository extends JpaExtensionRepository<CmmAnnounce, Long> {

    @Query(value = "SELECT * FROM cmm_announce"
                 + " WHERE site_id = :siteId"
                 + "   AND notify_type_id IN ( :notifyTypeId )"
                 + "   AND :nowDate BETWEEN from_date AND to_date"
            , nativeQuery = true)
    List<CmmAnnounce> findByNotifyTypeAndDate(@Param("siteId")String siteId, @Param("notifyTypeId")List<String> notifyTypeId, @Param("nowDate")String nowDate);
}
