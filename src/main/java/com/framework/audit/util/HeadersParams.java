package com.framework.audit.util;

import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.Future;

@Component
public class HeadersParams {
    @Autowired
    private Environment env;

    public HashMap<String, Object> fillHeadersParams(MethodSignature signature) {
        HashMap<String, Object> headers = new HashMap<>();

        headers.put("API", env.getRequiredProperty("spring.application.name"));
        headers.put("Service", signature.getMethod().getDeclaringClass().toGenericString());
        headers.put("Operation", signature.getMethod().getName());

        addHTTPRequestParams(headers);
        return headers;
    }

    private static void addHTTPRequestParams(HashMap<String, Object> headers) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            Enumeration<String> headersNames = request.getHeaderNames();

            HashMap<String, Object> httpHeaders = new HashMap<>();
            while (headersNames.hasMoreElements()) {
                String arg = headersNames.nextElement();
                httpHeaders.put(arg, request.getHeader(arg));
            }

            httpHeaders.put("RequestURI", request.getRequestURI());

            if (request.getQueryString()!=null && !request.getQueryString().isBlank())
                httpHeaders.put("QueryString", request.getQueryString());

            headers.put("HttpHeaders", httpHeaders);

        } catch (Exception e) {
            // If fails this is not a HttpServletRequest
            // So ignore exception
        }
    }
}
