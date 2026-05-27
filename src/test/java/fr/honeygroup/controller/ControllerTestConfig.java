package fr.honeygroup.controller;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;

import fr.honeygroup.security.JwtAuthenticationFilter;
import fr.honeygroup.security.JwtService;

@TestConfiguration
public class ControllerTestConfig {

    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public JwtService jwtService() {
        return Mockito.mock(JwtService.class);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return Mockito.mock(UserDetailsService.class);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        JwtAuthenticationFilter filter = Mockito.mock(JwtAuthenticationFilter.class);
        try {
            Mockito.doAnswer(invocation -> {
                jakarta.servlet.http.HttpServletRequest request = invocation.getArgument(0);
                jakarta.servlet.http.HttpServletResponse response = invocation.getArgument(1);
                jakarta.servlet.FilterChain chain = invocation.getArgument(2);
                chain.doFilter(request, response);
                return null;
            }).when(filter).doFilter(Mockito.any(), Mockito.any(), Mockito.any());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return filter;
    }
}
