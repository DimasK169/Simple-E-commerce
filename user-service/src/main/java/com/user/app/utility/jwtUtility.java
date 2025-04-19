package com.user.app.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;

@Component
public class jwtUtility {

    private RSAPublicKey publicKey;

    public jwtUtility() {
        try {
            this.publicKey = KeyUtil.getPublicKey();
        } catch (Exception e) {
            System.out.println("Failed to load public key: " + e.getMessage());
            e.printStackTrace();
            this.publicKey = null;
        }
    }

    public String extractUsername(String token) {
        DecodedJWT jwt = getDecodedJWT(token);
        return jwt != null ? jwt.getSubject() : null;
    }

    public boolean validateToken(String token) {
        try {
            return getDecodedJWT(token) != null;
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("keys/public_key.pem");
            System.out.println("InputStream = " + inputStream);
            return false;
        }
    }

    private DecodedJWT getDecodedJWT(String token) {
        if (publicKey == null) {
            throw new IllegalStateException("Public key not loaded");
        }
        Algorithm algorithm = Algorithm.RSA256(publicKey, null);
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

}