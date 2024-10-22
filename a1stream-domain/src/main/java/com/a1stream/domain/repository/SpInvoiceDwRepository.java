package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.SpInvoiceDw;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface SpInvoiceDwRepository extends JpaExtensionRepository<SpInvoiceDw, String> {

}
