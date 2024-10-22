package com.a1stream.web.app.config.auth;

import java.lang.reflect.Method;

import javax.annotation.Nonnull;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.listener.model.ThreadLocalPJAuditableDetailAccessor;
import com.ymsl.solid.base.audit.ThreadLocalFunctionNameAccessor;
import com.ymsl.solid.web.AbstractHandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
* 功能描述:
*
* @author mid1955
*/
public class FunctionNameCaptureInterceptor extends AbstractHandlerInterceptor {

    @Override
    protected boolean executePreHandle(final HttpServletRequest request, final HttpServletResponse response,
            final HandlerMethod handler) throws Exception {

        final String values = getFunctionNames(handler.getMethod());
        ThreadLocalPJAuditableDetailAccessor.getValue().setUpdateProgram(values);
        return true;
    }

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
            final ModelAndView modelAndView) throws Exception {
    }

    @Override
    protected void executeAfterCompletion(final HttpServletRequest request, final HttpServletResponse response,
            final HandlerMethod handler, final Exception ex) throws Exception {
        clearThreadLocal();
    }

    @Override
    protected void executeConcurrentHandlingStarted(final HttpServletRequest request,
            final HttpServletResponse response, final HandlerMethod handler) throws Exception {
        clearThreadLocal();
    }

    private void clearThreadLocal() {
        ThreadLocalFunctionNameAccessor.clear();
    }

    @Nonnull
    protected String getFunctionNames(@Nonnull final Method m) {

        //From method
        FunctionId[] methodFunId =  m.getAnnotationsByType(FunctionId.class);
        if(methodFunId != null && methodFunId.length>0) {
         return methodFunId[0].value();
        }

        // From class
         FunctionId[] clzzFunId =  m.getDeclaringClass().getAnnotationsByType(FunctionId.class);
         if(clzzFunId != null && clzzFunId.length>0) {
             return  clzzFunId[0].value();
         }

         return CommonConstants.AUDIT_DFAULT_FUNC_ID;
    }
}
