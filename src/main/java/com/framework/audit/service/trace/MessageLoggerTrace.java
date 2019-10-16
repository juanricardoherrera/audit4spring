package com.framework.audit.service.trace;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.audit.model.Level;
import com.framework.audit.service.SendTemplateManager;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class MessageLoggerTrace {

    @Autowired
    SendTemplateManager sendTemplateManager;
    @Autowired
    ObjectMapper objectMapper;

    @SneakyThrows
    public void messageLoggerTrace(ProceedingJoinPoint joinPoint, Object proceedResponse, HashMap<String, Object> headers) {
        HashMap<String, Object> message = new HashMap<>();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        message.putAll(headers);

        HashMap<String, Object> input = new HashMap<>();
        Object argsValue[] = joinPoint.getArgs();
        String[] argsName = signature.getParameterNames();



        for (int argIndex = 0; argIndex < argsValue.length; argIndex++) {
            input.put(argsName[argIndex], objectMapper.writeValueAsString(argsValue[argIndex]) );
        }

        message.put("Input", input);

        message.put("Output", objectMapper.writeValueAsString(proceedResponse));

        sendTemplateManager.sendMessage(Level.MESSAGE_LOGGER, message);
    }
}
