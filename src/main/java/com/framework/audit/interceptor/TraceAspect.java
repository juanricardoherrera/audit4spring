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

    @Around("@annotation(com.framework.audit.interceptor.TraceOperation)")
    public Object controllerMethodsAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        TraceOperation annotation = signature.getMethod().getAnnotation(TraceOperation.class);
        HashMap<String, Object> headers = HeadersParams.fillHeadersParams(signature);

        Object proceedResponse = null;

        try {
            proceedResponse = joinPoint.proceed();
            headers.put("Response", "Success");
        } catch (Exception e) {
            headers.put("Response", "Failed");

            log.severe("Failed to complete the operation.");
            e.printStackTrace();

            errorTrace.errorTrace(e, headers);

            if (annotation.overrideException())
                throw new Exception("internal server error");
            throw e;
        } finally {
            auditTrace.auditTrace(startTime, headers);
            messageLoggerTrace.messageLoggerTrace(joinPoint, proceedResponse, headers);
        }

        return proceedResponse;
    }
}
