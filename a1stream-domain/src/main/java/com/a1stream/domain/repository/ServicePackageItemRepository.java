package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ServicePackageItemRepositoryCustom;
import com.a1stream.domain.entity.ServicePackageItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ServicePackageItemRepository extends JpaExtensionRepository<ServicePackageItem, Long>, ServicePackageItemRepositoryCustom {

    @Query(value="select * from service_package_item "
            + "where site_id =:siteId "
            + "and service_package_id =:servicePackageId", nativeQuery=true)
    List<ServicePackageItem> findSvPackageItmes(@Param("siteId") String siteId
                                            , @Param("servicePackageId") Long servicePackageId);

    List<ServicePackageItem> findByServicePackageId(Long servicePackageId);
}
