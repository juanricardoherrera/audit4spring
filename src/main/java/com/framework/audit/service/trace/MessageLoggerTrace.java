package com.framework.audit.service.trace;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.audit.interceptor.TraceOperation;
import com.framework.audit.model.Level;
import com.framework.audit.service.SendTemplateManager;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MessageLoggerTrace {

    @Autowired
    SendTemplateManager sendTemplateManager;
    @Autowired
    ObjectMapper objectMapper;

    @SneakyThrows
    @Async(value = "auditThreadPoolExecutor")
    public void messageLoggerTrace(ProceedingJoinPoint joinPoint, Object proceedResponse, HashMap<String, Object> headers, TraceOperation annotation) {
        if (!annotation.subscribeMessageLogger())
            return;

        HashMap<String, Object> message = new HashMap<>();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        message.putAll(headers);

        HashMap<String, Object> input = new HashMap<>();
        Object argsValue[] = joinPoint.getArgs();
        String[] argsName = signature.getParameterNames();

        for (int argIndex = 0; argIndex < argsValue.length; argIndex++) {
            input.put(argsName[argIndex], (Object) argsValue[argIndex]);
        }

        String inputString = objectMapper.writeValueAsString(input);
        try {
            message.put("Input", objectMapper.readValue(inputString, Map.class));
        } catch (Exception e) {
            message.put("Input", inputString);
        }

        String outputString = objectMapper.writeValueAsString(proceedResponse);
        try {
            message.put("Output", objectMapper.readValue(outputString, Map.class));
        } catch (Exception e) {
            message.put("Output", outputString);
        }

        sendTemplateManager.sendMessage(Level.MESSAGE_LOGGER, message);
    }
}
