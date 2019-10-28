package com.framework.audit.interceptor;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.audit.service.trace.AuditTrace;
import com.framework.audit.service.trace.ErrorTrace;
import com.framework.audit.service.trace.MessageLoggerTrace;
import com.framework.audit.util.HeadersParams;
import lombok.extern.java.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.Future;


@Aspect
@Log
@Component
public class TraceAspect {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    AuditTrace auditTrace;

    @Autowired
    ErrorTrace errorTrace;

    @Autowired
    MessageLoggerTrace messageLoggerTrace;

    @Autowired
    HeadersParams headersParams;

    @Around("@annotation(com.framework.audit.interceptor.TraceOperation)")
    public Object controllerMethodsAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        TraceOperation annotation = signature.getMethod().getAnnotation(TraceOperation.class);
        HashMap<String, Object> headers = headersParams.fillHeadersParams(signature);

        Object proceedResponse = null;
        try {
            proceedResponse = joinPoint.proceed();
            headers.put("Response", "Success");
        } catch (Exception e) {
            headers.put("Response", "Failed");

            log.severe("Failed to complete the operation: " + headers);

            // Eliminated to release log operation use error trace instead if you want to print
            //e.printStackTrace();

            errorTrace.errorTrace(e, headers, annotation);

            if (annotation.overrideException())
                throw new Exception("internal server error");

            throw e;
        } finally {
            auditTrace.auditTrace(startTime, headers, annotation);
            messageLoggerTrace.messageLoggerTrace(joinPoint, proceedResponse, headers, annotation);
        }

        return proceedResponse;
    }
}
