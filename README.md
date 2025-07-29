# Spring Security 6 WebSocket SpEL AuthorizationManager

## 📖 About

Spring Security 6 removed the built-in MessageExpressionAuthorizationManager used in WebSocket authorization.
The official documentation recommends creating your own implementation to support SpEL expressions, but offers only partial examples.

This library provides a ready-to-use implementation of an AuthorizationManager for WebSocket messages that evaluates SpEL expressions, fully compatible with Spring Security 6.x.

It simplifies migration from older Spring Security versions and removes the need to manually adapt the code.

---

## 💡 Why this library?

- ✅ Restores SpEL-based access control for WebSocket messages.
- ✅ Uses Spring Security's official APIs (AuthorizationManager, SecurityExpressionHandler).
- ✅ ✅ Saves time by providing a tested implementation that follows Spring's recommended migration path. 
- ✅ No external dependencies beyond Spring Security and Spring Messaging.

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