package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.Calendar;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CalendarRepository extends JpaExtensionRepository<Calendar, Long> {

}
