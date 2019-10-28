package com.framework.audit.service.trace;


import com.framework.audit.interceptor.TraceOperation;
import com.framework.audit.model.Level;
import com.framework.audit.service.SendTemplateManager;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuditTrace {

    @Autowired
    SendTemplateManager sendTemplateManager;

    @SneakyThrows
    @Async(value = "auditThreadPoolExecutor")
    public void auditTrace(long startTime, HashMap<String, Object> headers, TraceOperation annotation) {
        if (!annotation.subscribeAudit())
            return;
        HashMap<String, Object> message = new HashMap<>();

        message.putAll(headers);

        long endtime = System.currentTimeMillis();

        message.put("Action", "Invoked");
        message.put("DurationMS", endtime - startTime);

        sendTemplateManager.sendMessage(Level.AUDIT, message);
    }
}
