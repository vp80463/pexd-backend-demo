package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.MstCodeInfoRepositoryCustom;
import com.a1stream.domain.entity.MstCodeInfo;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface MstCodeInfoRepository extends JpaExtensionRepository<MstCodeInfo, String>, MstCodeInfoRepositoryCustom {

    List<MstCodeInfo> findByCodeId(String codeId);

    List<MstCodeInfo> findByCodeIdIn(Set<String> codeId);

    List<MstCodeInfo> findBySiteIdAndCodeIdIn(String siteId, Set<String> codeId);

    List<MstCodeInfo> findByCodeIdAndCodeData2(String codeId, String codeData2);

    List<MstCodeInfo> findByCodeIdAndSiteId(String codeId, String siteId);

    List<MstCodeInfo> findByCodeIdAndSiteIdOrderByKey1(String codeId, String siteId);

    MstCodeInfo findFirstByCodeIdAndCodeData1AndAndSiteId(String codeId, String codeData1, String siteId);

    List<MstCodeInfo> findByCodeIdAndCodeData1In(String codeId, Set<String> codeData1);
}
