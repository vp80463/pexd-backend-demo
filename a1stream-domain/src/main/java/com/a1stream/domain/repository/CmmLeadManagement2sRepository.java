package com.a1stream.domain.repository;

import com.a1stream.domain.custom.CmmLeadManagement2sRepositoryCustom;
import com.a1stream.domain.entity.CmmLeadManagement2s;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


/**
 * @author dong zhen
 */
@Repository
public interface CmmLeadManagement2sRepository extends JpaExtensionRepository<CmmLeadManagement2s, Long>, CmmLeadManagement2sRepositoryCustom {

    CmmLeadManagement2s findByLeadManagementResultId(Long leadManagementResultId);

    List<CmmLeadManagement2s> findByLeadManagementResultIdIn(Set<Long> leadManagementResultIdList);

    List<CmmLeadManagement2s> findByMobilePhoneInAndTimeStampInAndOilFlagInAndFrameNoIn(List<String> phoneList, List<String> timeStampList, List<String> categoryList, List<String> frameNoList);
}
