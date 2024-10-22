package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.InvoiceSerializedItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface InvoiceSerializedItemRepository extends JpaExtensionRepository<InvoiceSerializedItem, Long> {

}
