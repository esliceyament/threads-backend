package com.threads.apigateway.filter;

import com.threads.apigateway.service.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestPath = request.getPath().toString();

        // 🔹 1. Allow Public Endpoints (No Authentication Required)
        if (requestPath.startsWith("/authentication/login") || requestPath.startsWith("/authentication/register")) {
            System.out.println("----------------------------");
            return chain.filter(exchange);
        }

        // 🔹 2. Check if Authorization header is present
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            System.out.println("00000000000000000000");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 🔹 3. Extract Bearer token
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("1111111111111111111111");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        // 🔹 4. Validate JWT Token
        if (!jwtUtil.validateToken(token)) {
            System.out.println("222222222222222222222222222222");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 🔹 5. Extract User Role (for Authorization)
        String userRole = jwtUtil.extractRole(token);

        // 🔹 6. Restrict access based on roles
        if (requestPath.startsWith("/profile/create-profile") && !userRole.equals("USER")) {
            System.out.println("33333333333333333333333333");
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
        System.out.println("Final Request Path: " + request.getURI());
        System.out.println(userRole);
        System.out.println(authHeader);
        // 🔹 7. Forward request to microservice with Authorization header
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .build();
        System.out.println(authHeader);
        System.out.println("Forwarding request with Authorization: " + modifiedRequest.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
