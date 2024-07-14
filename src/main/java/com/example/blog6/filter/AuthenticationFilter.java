package com.example.blog6.filter;


import com.example.blog6.service.UserService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter implements Filter {

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 초기화 코드 (필요한 경우)
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("AuthenticationFilter: " + httpRequest.getRequestURI());
        System.out.println("AuthenticationFilter: " + httpRequest.getMethod());

        // URI, Method를 쿠키의 사용자가 이용할 수 있는지 확인한 후 문제가 없다면 chain.doFilter(request, response); 를 실행하고 권한이 없을 경우엔 특정 경로로 redirect한다.

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 정리 코드 (필요한 경우)
    }

    private String validateTokenAndGetUserId(String token) {
        // 토큰 검증 및 사용자 ID 추출 로직 (예: JWT 검증)
        // 유효한 경우 사용자 ID를 반환, 그렇지 않으면 null 반환
        return null;
    }
}
