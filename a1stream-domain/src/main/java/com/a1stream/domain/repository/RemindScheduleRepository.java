package com.a1stream.domain.repository;

import com.a1stream.domain.custom.RemindScheduleRepositoryCustom;
import com.a1stream.domain.entity.RemindSchedule;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RemindScheduleRepository extends JpaExtensionRepository<RemindSchedule, Long>, RemindScheduleRepositoryCustom {

    @Query(value="SELECT * "
               + " FROM remind_schedule "
               + " WHERE remind_type = :remindType "
               + " AND remind_due_date >= :today "
               + " AND serialized_product_id = :serializedProId ", nativeQuery=true)
    List<RemindSchedule> findFutureScheduleByMcId( @Param("remindType") String remindType
                                                 , @Param("today") String today
                                                 , @Param("serializedProId") Long serializedProId);

    @Query(value="SELECT * "
            + " FROM remind_schedule "
            + " WHERE remind_type = :remindType "
            + " AND service_demand_id = :demandId "
            + " AND serialized_product_id = :serializedProId ", nativeQuery=true)
    List<RemindSchedule> findByDemandAndMcId( @Param("remindType") String remindType
                                            , @Param("demandId") Long demandId
                                            , @Param("serializedProId") Long serializedProId);

    @Query(value="SELECT * "
            + " FROM remind_schedule "
            + " WHERE remind_type = :remindType "
            + " AND serialized_product_id = :serializedProId ", nativeQuery=true)
    List<RemindSchedule> findByMcId( @Param("remindType") String remindType
                                   , @Param("serializedProId") Long serializedProId);
}
