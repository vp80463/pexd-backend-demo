package com.a1stream.common.listener.model;

public final class ThreadLocalPJAuditableDetailAccessor {

    private static final ThreadLocal<PJAuditableDetail> HOLDER = new ThreadLocal<PJAuditableDetail>() {
        @Override
        protected PJAuditableDetail initialValue() {
            return new PJAuditableDetail();
        }
    };

    private ThreadLocalPJAuditableDetailAccessor() {
    }

    public static PJAuditableDetail getValue() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
