# Spring Security 6 WebSocket SpEL AuthorizationManager

This library restores support for SpEL-based access authorization in Spring Security 6 with WebSocket.  

Spring Security 6 removed direct SpEL support, but it includes tools to bring it back, though implementation can be complex.  
This library simplifies the process, as there were no clear guides or GitHub repositories providing a straightforward solution.

---

## ✨ Features

- ✅ Evaluate SpEL expressions for WebSocket messages (`/topic`, `/user`, etc.)  
- ✅ Compatible with any Spring Security 6.x version  
- ✅ Zero external dependencies beyond Spring Security and Spring Messaging  

---

## 📦 Installation

### Prerequisites
Ensure you have the following dependencies in your project:

```gradle
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.springframework.security:spring-security-messaging'
```

### Gradle

Add the following to your `build.gradle`:

```gradle
dependencies {
    implementation 'io.github.zuxercoding99:spring-security-websocket-spel:1.0.0'
}
```

### Maven

Add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.zuxercoding99</groupId>
    <artifactId>spring-security-websocket-spel</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## 🚀 Usage

Below is an example of how to configure WebSocket security with SpEL-based authorization:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.zuxercoding99.security.MessageExpressionAuthorizationManager;


@Configuration
public class WebSocketSecurityConfig {

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(
            MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
                .simpTypeMatchers(SimpMessageType.HEARTBEAT).permitAll()
                .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.DISCONNECT).authenticated()
                .simpDestMatchers("/app/**").authenticated()
                .simpSubscribeDestMatchers("/topic/group/{groupId}")
                .access(new MessageExpressionAuthorizationManager(
                        "isAuthenticated() and @groupService.isMember(principal.name, #groupId)"
                ))
                .anyMessage().denyAll();
        return messages.build();
    }
}
```