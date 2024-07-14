package com.example.blog6.config;

import com.example.blog6.filter.AuthenticationFilter;
import com.example.blog6.service.UserService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {


    @Bean
    public FilterRegistrationBean<AuthenticationFilter> authenticationFilter(UserService userService) {
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setUserService(userService); // UserService 주입
        registrationBean.setFilter(authenticationFilter);
        registrationBean.addUrlPatterns("/*"); // 모든 경로에 필터 적용
        registrationBean.setOrder(1); // 필터 실행 순서 설정 (낮을수록 먼저 실행)
        return registrationBean;
    }
}

