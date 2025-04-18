package com.flash_sale.app.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.flash_sale.app.utility.KeyUtil;
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

        System.out.println("jwt auth");

        String path = request.getServletPath();
        List<String> publicPaths = List.of("/flash-sale/get");

        if (publicPaths.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

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

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userEmail, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);

                System.out.println("auth " + auth);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token tidak valid: " + e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}
