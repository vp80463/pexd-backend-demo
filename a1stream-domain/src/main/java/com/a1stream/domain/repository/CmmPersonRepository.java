package com.a1stream.domain.repository;

import com.a1stream.domain.custom.CmmPersonRepositoryCustom;
import com.a1stream.domain.entity.CmmPerson;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CmmPersonRepository extends JpaExtensionRepository<CmmPerson, Long>, CmmPersonRepositoryCustom  {

    List<CmmPerson> findByUserId(String userId);

    CmmPerson findFirstByUserId(String userId);

    boolean existsBySiteIdAndPersonCd(String siteId, String personCd);

    boolean existsBySiteIdAndPersonCdAndPersonIdNot(String siteId, String personCd, Long personId);

    CmmPerson findByPersonId(Long personId);

    CmmPerson findBySiteIdAndPersonCd(String siteId, String personCd);
}
