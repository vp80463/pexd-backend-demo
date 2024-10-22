package com.a1stream.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.a1stream.common.constants.CommonConstants;

/**
* 功能描述: update-product自动获取注解
*
* @author mid1955
*/
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FunctionId {

    String value() default CommonConstants.AUDIT_DFAULT_FUNC_ID;
}
