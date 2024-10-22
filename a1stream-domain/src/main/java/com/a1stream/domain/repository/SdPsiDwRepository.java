package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.SdPsiDw;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface SdPsiDwRepository extends JpaExtensionRepository<SdPsiDw, Long> {

}
