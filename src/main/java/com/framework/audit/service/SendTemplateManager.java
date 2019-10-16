package com.framework.audit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.audit.model.Level;
import com.framework.audit.model.SenderBeanTemplateReference;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Service
@Log
public class SendTemplateManager {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    SenderBeanTemplateReference senderBeanTemplateReference;


    private MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

    public SendTemplateManager() throws NoSuchAlgorithmException {
    }


    public void sendMessage(HashMap<String, Object> message) {
        sendMessage(Level.CUSTOM, message);
    }

    /**
     * Clase que envía mensajes a una cola, para luego ser almacenados en ElasticSearch
     *
     * @param level level from Level.class
     * @param message message to be sended
     */
    @SneakyThrows
    public void sendMessage(Level level, HashMap<String, Object> message) {
        CompletableFuture future = CompletableFuture.supplyAsync(() -> {

            message.put("Level", level.name());
            message.put("EventTime", LocalDateTime.now().toString());

            String validation = Base64.getEncoder().encodeToString(sha1.digest(message.toString().getBytes()));
            message.put("ValidationEncode", validation);
            senderBeanTemplateReference.sendEvent(message);
            return "OK";
        });
    }
}
