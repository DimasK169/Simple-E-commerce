package com.user.app.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.user.app.utility.KeyUtil;
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

import static com.user.app.service.implement.AuthServiceImpl.generateToken;

public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        System.out.println("Request Path: " + request.getServletPath());

        List<String> publicPaths = List.of("/users/login", "/users/save", "/users/me", "/users/refresh");

        if (publicPaths.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");

            try {
                RSAPublicKey publicKey = KeyUtil.getPublicKey();
                DecodedJWT jwt = JWT.require(Algorithm.RSA256(publicKey, null))
                        .withIssuer("your-auth-server")
                        .build()
                        .verify(token);

                String userEmail = jwt.getSubject();
                String userRole = jwt.getClaim("userRole").toString();
                String userName = jwt.getClaim("userName").asString();

                long currentTimeMillis = System.currentTimeMillis();
                long expiryTime = jwt.getExpiresAt().getTime();

                if (expiryTime - currentTimeMillis <= 5000) {  // 5000 ms = 5 detik
                    String refreshedToken = generateToken(userEmail, userRole, userName);
                    response.setHeader("Refresh-Token", refreshedToken); // Mengirimkan refresh token
                }

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userEmail, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}
