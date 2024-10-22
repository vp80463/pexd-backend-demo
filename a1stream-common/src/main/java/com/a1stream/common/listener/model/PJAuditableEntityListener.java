package com.a1stream.common.listener.model;

import org.apache.commons.lang3.StringUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseEntity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class PJAuditableEntityListener {

    @PrePersist
    public void auditForCreate(final Object target) {
        audit(target, true);
    }

    @PreUpdate
    public void auditForUpdate(final Object target) {
        audit(target, false);
    }

    boolean audit(final Object target, final boolean isNew) {

        if (!(target instanceof BaseEntity)) {
            return false;
        }

        final BaseEntity entity = (BaseEntity) target;
        final PJAuditableDetail pjAuditableDetail = getPJAuditableDetail();
        if (isNew) {
            if (StringUtils.isEmpty(entity.getUpdateProgram())) {
                entity.setUpdateProgram(pjAuditableDetail.getUpdateProgram());
            }
            if (StringUtils.isNotEmpty(pjAuditableDetail.getBatchUser())) {
                entity.setCreatedBy(pjAuditableDetail.getBatchUser());
                entity.setLastUpdatedBy(pjAuditableDetail.getBatchUser());
            }
            if (StringUtils.isNotEmpty(pjAuditableDetail.getIfsUser())) {
                entity.setCreatedBy(pjAuditableDetail.getIfsUser());
                entity.setLastUpdatedBy(pjAuditableDetail.getIfsUser());
            }
            entity.setUpdateCount(CommonConstants.INTEGER_ZERO);
//            entity.setDateCreated(LocalDateTime.now());
//            entity.setLastUpdated(LocalDateTime.now());
            writeForCreate(entity);
        } else {
            if (StringUtils.isNotEmpty(pjAuditableDetail.getUpdateProgram())) {
                entity.setUpdateProgram(pjAuditableDetail.getUpdateProgram());
            }
            if (StringUtils.isNotEmpty(pjAuditableDetail.getBatchUser())) {
                entity.setLastUpdatedBy(pjAuditableDetail.getBatchUser());
            }
            if (StringUtils.isNotEmpty(pjAuditableDetail.getIfsUser())) {
                entity.setLastUpdatedBy(pjAuditableDetail.getIfsUser());
            }
//            entity.setLastUpdated(LocalDateTime.now());
            writeForUpdate(entity);
        }

        return true;
    }

    protected void writeForCreate(final BaseEntity entity) {
    }

    protected void writeForUpdate(final BaseEntity entity) {
    }

    protected PJAuditableDetail getPJAuditableDetail() {
        return ThreadLocalPJAuditableDetailAccessor.getValue();
    }
}