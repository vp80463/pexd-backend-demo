package com.a1stream.domain.repository;

import com.a1stream.domain.custom.UserHabitRepositoryCustom;
import com.a1stream.domain.entity.UserHabit;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserHabitRepository extends JpaExtensionRepository<UserHabit, Long>, UserHabitRepositoryCustom {

    List<UserHabit> findByUserId(String userId);

    List<UserHabit> findByUserIdAndSiteId(String userId, String dealerCode);

    UserHabit findByUserIdAndSiteIdAndUserHabitTypeId(String userId, String siteId, String userHabitTypeId);
}
