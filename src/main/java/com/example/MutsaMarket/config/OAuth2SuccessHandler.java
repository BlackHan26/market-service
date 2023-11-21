package com.example.MutsaMarket.config;

import com.example.MutsaMarket.jwt.JwtTokenUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2SuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenUtils tokenUtils;

    public OAuth2SuccessHandler(JwtTokenUtils tokenUtils) {
        this.tokenUtils = tokenUtils;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        OAuth2User oAuth2User
                = (OAuth2User) authentication.getPrincipal();
        String jwt
                = tokenUtils
                .generateToken(User.withUsername(oAuth2User.getName())
                        .password(oAuth2User.getAttribute("id").toString())
                        .build());

        String targetUrl = String.format(
                "http://localhost:8080/token/val?token=%s", jwt);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}