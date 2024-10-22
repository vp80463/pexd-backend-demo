package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.MstBrand;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface MstBrandRepository extends JpaExtensionRepository<MstBrand, Long> {

    public MstBrand findBySiteIdAndDefaultFlag(String siteId, String dfaultFlag);

    @Query(value="select * from mst_brand "
            + "where site_id =:siteId "
            + "order by brand_id"
            , nativeQuery=true)
    List<MstBrand> findBySiteIdOrderByBrandId(@Param("siteId") String siteId);

    @Query(value="select * from mst_brand "
            + "where site_id =:siteId "
            + "and default_flag !=:defaultFlag "
            , nativeQuery=true)
    List<MstBrand> findBySiteIdAndFlag ( @Param("siteId") String siteId
                                       , @Param("defaultFlag") String defaultFlag);

    MstBrand findByBrandCd(String brandCd);
}
