package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.StoringList;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface StoringListRepository extends JpaExtensionRepository<StoringList, Long> {

    StoringList findByStoringListId(Long storingListId);

    List<StoringList> findByStoringListIdIn(List<Long> storingListIds);
}
