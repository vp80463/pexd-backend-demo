package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ApiProformaorder;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ApiProformaorderRepository extends JpaExtensionRepository<ApiProformaorder, Long> {

}
