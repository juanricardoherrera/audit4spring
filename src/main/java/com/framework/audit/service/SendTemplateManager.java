package com.framework.audit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.audit.model.Level;
import com.framework.audit.model.SenderBeanTemplateReference;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@Service
@Log
public class SendTemplateManager {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    SenderBeanTemplateReference senderBeanTemplateReference;

    @Async(value = "auditThreadPoolExecutor")
    public void sendMessage(HashMap<String, Object> message) {
        sendMessage(Level.CUSTOM, message);
    }

    /**
     * Clase que envía mensajes a una cola, para luego ser almacenados en ElasticSearch
     *
     * @param level   level from Level.class
     * @param message message to be sended
     */
    @SneakyThrows
    public void sendMessage(Level level, HashMap<String, Object> message) {
        message.put("Level", level.name());

        message.put("EventTime", ZonedDateTime.now().toString());

        senderBeanTemplateReference.sendEvent(message);
    }
}
