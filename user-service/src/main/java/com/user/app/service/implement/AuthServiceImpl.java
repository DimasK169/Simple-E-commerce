package com.user.app.service.implement;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.user.app.dto.request.UsersRequest;
import com.user.app.dto.response.RestApiResponse;
import com.user.app.dto.result.UsersLoginResponse;
import com.user.app.entity.Users;
import com.user.app.repository.UsersRepository;
import com.user.app.service.interfacing.AuthService;
import com.user.app.utility.KeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.util.Collections;
import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public RestApiResponse<UsersLoginResponse> authentication(UsersRequest usersRequest) throws Exception {

        System.out.println("Masuk method login");

        Users users = usersRepository.findByEmail(usersRequest.getUserEmail());

        // Jika user tidak ditemukan
        if (users == null) {
            return RestApiResponse.<UsersLoginResponse>builder()
                    .code("401")
                    .message("Email tidak ditemukan")
                    .data(null)
                    .error(Collections.singletonList("User not found"))
                    .build();
        }

        // Jika password salah
        if (!usersRequest.getUserPassword().equals(users.getUserPassword())) {
            return RestApiResponse.<UsersLoginResponse>builder()
                    .code("401")
                    .message("Password salah")
                    .data(null)
                    .error(Collections.singletonList("Invalid password"))
                    .build();
        }

        // Jika login berhasil
        String token = generateToken(users.getUserEmail(), users.getUserRole());
        UsersLoginResponse usersLoginResponse = UsersLoginResponse.builder()
                .userEmail(users.getUserEmail())
                .userToken(token)
                .build();

        return RestApiResponse.<UsersLoginResponse>builder()
                .code("200")
                .message("Login berhasil")
                .data(usersLoginResponse)
                .error(null)
                .build();
    }

    public static String generateToken(String userEmail, String userRole) throws Exception {
        RSAPrivateKey privateKey = KeyUtil.getPrivateKey();
        return JWT.create()
                .withSubject(userEmail)
                .withIssuer("your-auth-server")
                .withClaim("userRole", userRole)
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600 * 1000)) // 1 jam
                .sign(Algorithm.RSA256(null, privateKey));
    }

}
