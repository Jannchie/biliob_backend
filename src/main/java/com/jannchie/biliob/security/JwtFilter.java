package com.jannchie.biliob.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Jannchie
 */
public class JwtFilter extends BasicAuthenticationFilter {

    private static final Logger logger = LogManager.getLogger();

    public JwtFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    public JwtFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint) {
        super(authenticationManager, authenticationEntryPoint);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = request.getHeader("token");
        try {

            if (token != null) {
                DecodedJWT jwt = JWT.decode(token);
                String name = jwt.getClaim("name").asString();
                String role = jwt.getClaim("role").asString();
                String password = jwt.getClaim("password").asString();
                Collection<GrantedAuthority> authorityCollection = new ArrayList<>();
                authorityCollection.add(new SimpleGrantedAuthority(role));
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(name, password, authorityCollection));
            }
        } catch (Exception e) {
            logger.info("验证token失败");
        }
        chain.doFilter(request, response);
    }
}
