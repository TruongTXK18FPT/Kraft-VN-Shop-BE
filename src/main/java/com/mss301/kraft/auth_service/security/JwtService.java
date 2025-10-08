package com.mss301.kraft.auth_service.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtService {

    private final byte[] secret;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationMs) {
        this.secret = secret.getBytes();
        this.expirationMs = expirationMs;
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                .subject(subject)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusMillis(expirationMs)));
        if (claims != null) {
            claims.forEach(claimsBuilder::claim);
        }
        JWTClaimsSet claimsSet = claimsBuilder.build();
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            JWSSigner signer = new MACSigner(secret);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to sign JWT", e);
        }
    }

    // refresh tokens removed

    public boolean isTokenValid(String token, String expectedSubject) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(secret);
            if (!signedJWT.verify(verifier)) {
                return false;
            }
            Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();
            String sub = signedJWT.getJWTClaimsSet().getSubject();
            return exp != null && exp.after(new Date()) && sub != null && sub.equals(expectedSubject);
        } catch (ParseException | JOSEException e) {
            return false;
        }
    }

    public String extractSubject(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            return null;
        }
    }
}
