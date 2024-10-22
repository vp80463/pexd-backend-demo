package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.PickingListRepositoryCustom;
import com.a1stream.domain.entity.PickingList;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface PickingListRepository extends JpaExtensionRepository<PickingList, Long>, PickingListRepositoryCustom {

    List<PickingList> findByPickingListIdIn(Set<Long> pickingListIds);

    PickingList findByPickingListId(Long pickingListId);
}
