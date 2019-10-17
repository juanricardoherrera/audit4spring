# Audit4Spring

Audit for spring is a audit framework that encapsulate all business logic to create a unique and centraliced audit flow, that means you only have to put a label on the bean you need to track, it includes all services methods, configuration methods, component method, even restcontroller operations.

## Using with SystemOut
Add dependency to your project [https://search.maven.org/artifact/io.github.kamiloj/audit4spring/1.0/pom](https://search.maven.org/artifact/io.github.kamiloj/audit4spring/1.0/pom)

![Alt text](/img/import.png?raw=true "import image")

Create later a config class to recieve messages from audit framework.
```
import com.framework.audit.model.SenderBeanTemplateReference;  
import org.springframework.context.annotation.ComponentScan;  
import org.springframework.context.annotation.Configuration;  
  
import java.util.HashMap;  
  
@Configuration  
@ComponentScan(basePackages = "com.framework.audit")  
public class AuditConfig implements SenderBeanTemplateReference {  

    @Override  
  public void sendEvent(HashMap<String, Object> message) {  
        System.out.println(message);   
  }  
}
```

At last you can tag any bean method to perform audit annotation used is @TraceOperation

![Alt text](/img/tag.png?raw=true "import image")

## Using with RabbitMQ
Add dependency to your project [https://search.maven.org/artifact/io.github.kamiloj/audit4spring/1.0/pom](https://search.maven.org/artifact/io.github.kamiloj/audit4spring/1.0/pom)

![Alt text](/img/importrabbit.png?raw=true "import image")

Add config code for Rabbitmq

```
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RabbitMQConfig {

    @Autowired
    private Environment env;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setUsername(env.getRequiredProperty("rabbit.username"));
        factory.setPassword(env.getRequiredProperty("rabbit.password"));
        factory.setHost(env.getRequiredProperty("rabbit.hostname"));
        factory.setPort(Integer.parseInt(env.getRequiredProperty("rabbit.portnumber")));
        return factory;
    }

}
```

Create a config class to recieve messages from audit framework and send to rabbitmq
```
import com.framework.audit.model.SenderBeanTemplateReference;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;

@Configuration
@ComponentScan(basePackages = "com.framework.audit")
public class AuditConfig implements SenderBeanTemplateReference {

    @Autowired
    private Environment env;

    @Autowired
    private RabbitTemplate template;

    @Value("${audit.eventqueue:AuditEventQueue}")
    private String eventQueueName;

    @Bean
    public Queue auditQueue() {
        return new Queue(eventQueueName);
    }

    @Override
    public void sendEvent(HashMap<String, Object> message) {
        //System.out.println(message);
        template.convertAndSend(eventQueueName, "", message);
    }
}
```

dont forget to put your env variables to access RabbitMQ

```
rabbit:
  hostname: ${RABBIT_HOSTNAME:localhost}
  portnumber: ${RABBIT_PORT:5672}
  username: ${RABBIT_USERNAME:guest}
  password: ${RABBIT_PASSWORD:guest}
```


At last you can tag any bean method to perform audit annotation used is @TraceOperation

![Alt text](/img/tag.png?raw=true "import image")
