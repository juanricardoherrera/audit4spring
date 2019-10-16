package com.framework.audit.service.trace;

import com.framework.audit.model.Level;
import com.framework.audit.service.SendTemplateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

@Service
public class ErrorTrace {

    @Autowired
    SendTemplateManager sendTemplateManager;

    public void errorTrace(Exception generalException, HashMap headers) {
        HashMap<String, Object> message = new HashMap<>();

        message.putAll(headers);

        message.put("ErrorMessage", generalException.getMessage());

        //Stacktrace to String
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        generalException.printStackTrace(pw);
        message.put("StackTrace", sw.toString());

        sendTemplateManager.sendMessage(Level.ERROR, message);
    }
}
