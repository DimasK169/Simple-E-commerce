package com.cart.service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cart.service.exception.UnauthorizedException;
import com.cart.service.utility.KeyUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");

            try {
                RSAPublicKey publicKey = KeyUtil.getPublicKey();
                DecodedJWT jwt = JWT.require(Algorithm.RSA256(publicKey, null))
                        .withIssuer("your-auth-server")
                        .build()
                        .verify(token);

                String userEmail = jwt.getSubject();
                String userRole = jwt.getClaim("userRole").asString();

                request.setAttribute("userRole", userRole);
                request.setAttribute("userEmail", userEmail);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userEmail, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token tidak valid: " + e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}
