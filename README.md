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
