package io.github.khaledabushamat.discount.shared.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public final class TestTokens {

    private static final String SECRET =
            "local-development-secret-at-least-32-bytes-long";

    public static String forCustomer(String externalId) {
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(externalId)
                    .issueTime(Date.from(Instant.now()))
                    .expirationTime(Date.from(Instant.now().plus(365, ChronoUnit.DAYS)))
                    .build();

            SignedJWT jwt = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256), claims);
            jwt.sign(new MACSigner(SECRET.getBytes()));

            return jwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to mint test token", e);
        }
    }

    public static void main(String[] args) {
        for (String id : List.of("emp-001", "aff-001", "loyal-001", "new-001")) {
            System.out.println(id + ": " + forCustomer(id));
        }
    }
}