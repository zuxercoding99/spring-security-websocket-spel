package org.zuxercoding99.security;

import java.util.function.Supplier;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.messaging.Message;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.ExpressionAuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.messaging.access.expression.DefaultMessageSecurityExpressionHandler;
import org.springframework.security.messaging.access.intercept.MessageAuthorizationContext;
import org.springframework.util.Assert;

/**
 * AuthorizationManager genérico para evaluar expresiones SpEL en mensajes de
 * WebSocket.
 */
public final class MessageExpressionAuthorizationManager
        implements AuthorizationManager<MessageAuthorizationContext<?>> {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private final SecurityExpressionHandler<Message<?>> expressionHandler = new DefaultMessageSecurityExpressionHandler();
    private final Expression expression;

    public MessageExpressionAuthorizationManager(String expressionString) {
        Assert.hasText(expressionString, "expressionString cannot be empty");
        this.expression = this.expressionHandler.getExpressionParser().parseExpression(expressionString);
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication,
            MessageAuthorizationContext<?> context) {
        EvaluationContext ctx = this.expressionHandler.createEvaluationContext(authentication, context.getMessage());
        boolean granted = ExpressionUtils.evaluateAsBoolean(this.expression, ctx);
        return new ExpressionAuthorizationDecision(granted, this.expression);
    }
}