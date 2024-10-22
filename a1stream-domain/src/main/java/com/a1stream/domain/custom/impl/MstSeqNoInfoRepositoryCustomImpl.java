package com.a1stream.domain.custom.impl;

import java.util.List;

import com.a1stream.domain.custom.MstSeqNoInfoRepositoryCustom;
import com.a1stream.domain.entity.MstSeqNoInfo;
import com.a1stream.domain.vo.MstSeqNoInfoVO;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

public class MstSeqNoInfoRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements MstSeqNoInfoRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public MstSeqNoInfoVO getBySeqNoType(String seqNoType, String siteId, Long facilityId) {

        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT m FROM MstSeqNoInfo m ")
            .append(" WHERE seqNoType = :seqNoType  ")
            .append("   AND (TRIM(:siteId) = '' OR siteId = :siteId) ")
            .append("   AND (:facilityId is null OR facilityId = :facilityId) ")
            .append("   ORDER BY siteId, facilityId ")
            .append(" ");

        Query query = entityManager.createQuery(sql.toString());
        query.setParameter("seqNoType", seqNoType);
        query.setParameter("siteId", siteId);
        query.setParameter("facilityId", facilityId);

        @SuppressWarnings("unchecked")
        List<MstSeqNoInfo> results = query.getResultList();

        return results.size() > 0? BeanMapUtils.mapTo(results.get(0), MstSeqNoInfoVO.class) : null;
    }

    @Override
    public MstSeqNoInfoVO getBySeqNoType(String seqNoType) {

        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT m FROM MstSeqNoInfo m ")
            .append(" WHERE seqNoType = :seqNoType  ")
            .append("   ORDER BY dateCreated DESC ")
            .append("   LIMIT 1 ")
            .append(" ");

        Query query = entityManager.createQuery(sql.toString());
        query.setParameter("seqNoType", seqNoType);

        @SuppressWarnings("unchecked")
        List<MstSeqNoInfo> results = query.getResultList();

        return results.size() > 0? BeanMapUtils.mapTo(results.get(0), MstSeqNoInfoVO.class) : null;
    }
}
