package com.a1stream.domain.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmWarrantyModelPartRepositoryCustom;
import com.a1stream.domain.entity.CmmWarrantyModelPart;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;

@Repository
public interface CmmWarrantyModelPartRepository extends JpaExtensionRepository<CmmWarrantyModelPart, Long>, CmmWarrantyModelPartRepositoryCustom {

    @Modifying
    @Query("DELETE FROM CmmWarrantyModelPart e ")
    void deleteAllWarrantyModelPart();
}
