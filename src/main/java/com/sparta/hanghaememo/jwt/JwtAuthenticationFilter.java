package com.sparta.hanghaememo.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.hanghaememo.dto.exception.ErrorResponseDto;
import com.sparta.hanghaememo.entity.Token;
import com.sparta.hanghaememo.entity.Users;
import com.sparta.hanghaememo.repository.TokenRepository;
import com.sparta.hanghaememo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accesstoken = jwtUtil.resolveToken(request, jwtUtil.ACCESS_HEADER);

        if(accesstoken != null) {
            if(jwtUtil.validateToken(accesstoken)) {
                String username = jwtUtil.getUserInfoFromToken(accesstoken);
                log.info("access키 유효함");
                setAuthentication(username);
            }
            else if (tokenRepository.existsByAccessToken(request.getHeader("ACCESS_HEADER"))) {
                Token refreshtoken = tokenRepository.findByAccessToken(request.getHeader("ACCESS_HEADER"));

                if(!jwtUtil.validateToken(refreshtoken.getRefreshToken().substring(7))){
                    log.info("access 키도 refresh 키도 모두 유효하지 않음 : 인증 불가");
                    jwtExceptionHandler(response, "Token Error", HttpStatus.UNAUTHORIZED.name(), HttpStatus.UNAUTHORIZED.value());
                } else{
                    log.info("access 키 유효하지 않지만 refresh 토큰 살아있음 : access키 재발급");
                    Users user = userRepository.findByUsername(refreshtoken.getUsername()).get();
                    String newAccessToken = jwtUtil.createToken(user.getUsername(), user.getRole(), "ACCESS_HEADER");
                    String newRefreshToken = jwtUtil.createToken(user.getUsername(), user.getRole(), "REFRESH_HEADER");
                    response.addHeader(JwtUtil.ACCESS_HEADER, newAccessToken);
                    refreshtoken.update(new Token(user.getUsername(),newAccessToken,newRefreshToken));
                    tokenRepository.saveAndFlush(refreshtoken);
                    setAuthentication(user.getUsername());
                }
            }
            else
                log.info("access키 유효하지 않음 & tokenrepository에 없음");
        }
        filterChain.doFilter(request,response);
    }

    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtUtil.createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    public void jwtExceptionHandler(HttpServletResponse response, String error, String msg, int statusCode) {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        try {
            String json = new ObjectMapper().writeValueAsString(new ErrorResponseDto(statusCode, error, msg));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
