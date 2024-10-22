package com.a1stream.domain.custom.impl;

import java.util.List;

import com.a1stream.domain.custom.MstSeqNoSettingRepositoryCustom;
import com.a1stream.domain.entity.MstSeqNoSetting;
import com.a1stream.domain.vo.MstSeqNoSettingVO;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

public class MstSeqNoSettingRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements MstSeqNoSettingRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public MstSeqNoSettingVO getUsableSeqNoSetting(String seqNoType, String siteId, Long facilityId) {

        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT m FROM MstSeqNoSetting m ")
            .append(" WHERE seqNoType = :seqNoType  ")
            .append("   AND (TRIM(:siteId) = '' OR TRIM(siteId) = '' OR siteId = :siteId) ")
            .append("   AND (:facilityId is null OR facilityId is null OR facilityId = :facilityId) ")
            .append("   ORDER BY siteId, facilityId ")
            .append(" ");

        Query query = entityManager.createQuery(sql.toString());
        query.setParameter("seqNoType", seqNoType);
        query.setParameter("siteId", siteId);
        query.setParameter("facilityId", facilityId);

        @SuppressWarnings("unchecked")
        List<MstSeqNoSetting> results = query.getResultList();

        return results.size() > 0? BeanMapUtils.mapTo(results.get(0), MstSeqNoSettingVO.class) : null;
    }
}
