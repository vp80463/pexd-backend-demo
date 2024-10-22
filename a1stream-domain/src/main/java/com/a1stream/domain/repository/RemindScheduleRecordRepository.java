package com.a1stream.domain.repository;

import com.a1stream.domain.entity.RemindScheduleRecord;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RemindScheduleRecordRepository extends JpaExtensionRepository<RemindScheduleRecord, Long> {

    List<RemindScheduleRecord> findBySiteIdAndRemindScheduleId(String siteId, Long remindScheduleId);

    @Query(value = " SELECT * "+
                     " FROM remind_schedule_record " +
                    " WHERE site_id = :siteId " +
                      " AND remind_schedule_id IN ( :remindScheduleIdSet ) " +
                 " ORDER BY date_created DESC "
            , nativeQuery = true )
    List<RemindScheduleRecord> findTopByRemindScheduleIdInOrderByDateCreatedDesc(@Param("siteId") String siteId, @Param("remindScheduleIdSet") Set<Long> remindScheduleIdSet);
}
