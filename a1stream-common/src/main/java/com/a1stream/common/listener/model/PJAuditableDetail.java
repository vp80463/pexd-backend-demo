package com.a1stream.common.listener.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class PJAuditableDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    private String siteId = "---";
    private String updateProgram = "---";
    private String batchUser = "";
    private String ifsUser = "";

    public String getSiteId() {
        return this.siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getUpdateProgram() {
        return this.updateProgram;
    }

    public void setUpdateProgram(String updateProgram) {
        this.updateProgram = StringUtils.upperCase(updateProgram);
    }

    public String getBatchUser() {
        return this.batchUser;
    }

    public void setBatchUser(String batchUser) {
        this.batchUser = batchUser;
    }

    public String getIfsUser() {
        return this.ifsUser;
    }

    public void setIfsUser(String ifsUser) {
        this.ifsUser = ifsUser;
    }
}
