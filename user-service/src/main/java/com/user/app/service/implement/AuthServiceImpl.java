package com.user.app.service.implement;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.user.app.dto.request.UsersRequest;
import com.user.app.dto.response.RestApiError;
import com.user.app.dto.response.RestApiResponse;
import com.user.app.dto.result.UsersLoginResponse;
import com.user.app.entity.Users;
import com.user.app.exceptions.UnauthorizedException;
import com.user.app.repository.UsersRepository;
import com.user.app.service.interfacing.AuthService;
import com.user.app.utility.KeyUtil;
import com.user.app.utility.jwtUtility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.util.Collections;
import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private jwtUtility jwtUtil;

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public RestApiResponse<UsersLoginResponse> authentication(UsersRequest usersRequest) throws Exception {

        System.out.println("Masuk method login");

        Users users = usersRepository.findByEmail(usersRequest.getUserEmail());

        if (users == null) {
            throw new UnauthorizedException("Email tidak ditemukan");
        }

        if (!usersRequest.getUserPassword().equals(users.getUserPassword())) {
            throw new UnauthorizedException("Password salah");
        }

        String token = generateToken(users.getUserEmail(), users.getUserRole(), users.getUserName());

        UsersLoginResponse usersLoginResponse = UsersLoginResponse.builder()
                .userEmail(users.getUserEmail())
                .userName(users.getUserName())
                .userRole(users.getUserRole())
                .userToken(token)
                .build();

        return RestApiResponse.<UsersLoginResponse>builder()
                .code("200")
                .message("Login berhasil")
                .data(usersLoginResponse)
                .error(null)
                .build();
    }

    public static String generateToken(String userEmail, String userRole, String userName) throws Exception {
        RSAPrivateKey privateKey = KeyUtil.getPrivateKey();
        return JWT.create()
                .withSubject(userEmail)
                .withIssuer("your-auth-server")
                .withClaim("userRole", userRole)
                .withClaim("userName", userName)
                .withExpiresAt(new Date(System.currentTimeMillis() + 18000 * 1000)) // 1 jam
                .sign(Algorithm.RSA256(null, privateKey));
    }

    @Override
    public ResponseEntity<RestApiResponse<UsersLoginResponse>> getCurrentUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Missing or malformed Authorization header.");
            return buildErrorResponse(RestApiError.ACCOUNT_NOT_FOUND);
        }

        String token = authHeader.substring(7);
        System.out.println("Extracted Token: " + token);

        if (!jwtUtil.validateToken(token)) {
            System.out.println("Token failed validation.");
            return buildErrorResponse(RestApiError.ACCOUNT_NOT_FOUND);
        }

        String username = jwtUtil.extractUsername(token);

        System.out.println("Extracted Username: " + username);

        //TODO askgfdasdkfm
        UsersLoginResponse user = UsersLoginResponse.builder()
                .userName(username)
                .userEmail(username + "@example.com")
                .userRole("USER")
                .build();

        RestApiResponse<UsersLoginResponse> response = RestApiResponse.<UsersLoginResponse>builder()
                .code(String.valueOf(HttpStatus.OK.value()))
                .message("SUCCESS")
                .data(user)
                .error(Collections.emptyList())
                .dataAll(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @Override
    public RestApiResponse<UsersLoginResponse> refreshToken(String token){
        String currentToken = token.replace("Bearer ", "");

        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.RSA256(KeyUtil.getPublicKey(), null))
                    .withIssuer("your-auth-server")
                    .build()
                    .verify(currentToken);

            String userEmail = decodedJWT.getSubject();
            String userRole = decodedJWT.getClaim("userRole").toString();
            String userName = decodedJWT.getClaim("userName").asString();
            String newToken = generateToken(userEmail, userRole, userName);

            UsersLoginResponse response = UsersLoginResponse.builder()
                    .userToken(newToken)
                    .build();

            return RestApiResponse.<UsersLoginResponse>builder()
                    .code("200")
                    .message("Token refreshed successfully")
                    .data(response)
                    .error(null)
                    .build();

        } catch (JWTVerificationException e) {
            if (e.getMessage().contains("expired")) {
                throw new UnauthorizedException("Token has expired and cannot be refreshed. Please login again");
            }
            throw new UnauthorizedException("Invalid refresh token");
        } catch (Exception e) {
            throw new UnauthorizedException("An error occurred while refreshing the token.");
        }
    }

    private ResponseEntity<RestApiResponse<UsersLoginResponse>> buildErrorResponse(RestApiError error) {
        RestApiResponse<UsersLoginResponse> response = RestApiResponse.<UsersLoginResponse>builder()
                .code(String.valueOf(error.getCode()))
                .message(error.getMessage())
                .data(null)
                .error(Collections.singletonList(error.getMessage()))
                .dataAll(null)
                .build();

        return ResponseEntity.status(error.getCode()).body(response);
    }

}
