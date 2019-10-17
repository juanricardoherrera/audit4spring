package com.framework.audit.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TraceOperation {
    public boolean overrideException() default true;
    public boolean subscribeAudit() default true;
    public boolean subscribeError() default true;
    public boolean subscribeMessageLogger() default true;
}
