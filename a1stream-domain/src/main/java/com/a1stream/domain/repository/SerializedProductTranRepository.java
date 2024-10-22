package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.SerializedProductTranRepositoryCustom;
import com.a1stream.domain.entity.SerializedProductTran;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface SerializedProductTranRepository extends JpaExtensionRepository<SerializedProductTran, Long>, SerializedProductTranRepositoryCustom{

    List<SerializedProductTran> findBySerializedProductIdIn(Set<Long> serializedProductIds);
}
