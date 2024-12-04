package com.ecom.config;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {
        // Get the roles of the authenticated user
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Set<String> roles = AuthorityUtils.authorityListToSet(authorities);

        // Redirect based on roles
        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/admin/");
        } else if (roles.contains("ROLE_USER")) {
            response.sendRedirect(request.getContextPath() + "/user/");
        } else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}
