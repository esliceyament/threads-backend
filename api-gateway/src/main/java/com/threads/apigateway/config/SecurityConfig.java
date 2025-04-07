//package com.threads.apigateway.config;
//
//import com.threads.apigateway.filter.JwtAuthenticationFilter;
//import com.threads.apigateway.service.JwtUtil;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//
//@Configuration
//@EnableWebFluxSecurity
//public class SecurityConfig {
//    @Bean
//    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
//        return http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
//                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
//                .authorizeExchange(exchanges -> exchanges
//                        .pathMatchers("/authentication/login", "/authentication/register").permitAll()
//                        .pathMatchers(HttpMethod.POST, "/profile/create-profile").hasRole("USER")
//                        //.pathMatchers("/profile/get-all").hasRole("ADMIN")
//                        .anyExchange().authenticated()
//                )
//                .build();
//    }
//}
//
//
//
//
//
