package com.framework.audit.util;

import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;

public class HeadersParams {

    public static HashMap<String, Object> fillHeadersParams(MethodSignature signature) {
        HashMap<String, Object> headers = new HashMap<>();
        headers.put("API", System.getProperty("spring.application.name"));
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

            if (!request.getQueryString().isBlank())
                httpHeaders.put("QueryString", request.getQueryString());

            headers.put("HttpHeaders", httpHeaders);

        } catch (Exception e) {
            // If fails this is not a HttpServletRequest
            // So ignore exception
        }
    }
}
