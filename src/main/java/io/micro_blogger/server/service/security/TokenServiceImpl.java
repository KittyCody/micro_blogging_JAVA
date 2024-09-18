package io.micro_blogger.server.service.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.micro_blogger.server.model.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
public class TokenServiceImpl implements TokenService {

    @Value("${security.jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${security.jwt.issuer}")
    private String jwtIssuer;

    @Override
    public String generate(Account user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(7 * 24 * 60 * 60))
                .subject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("roles", List.of("ROLE_USER"))
                .build();

        JWKSource<SecurityContext> secret = new ImmutableSecret<>(jwtSecretKey.getBytes());
        JwtEncoder encoder = new NimbusJwtEncoder(secret);

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        JwtEncoderParameters parameters = JwtEncoderParameters.from(header, claims);

        return encoder.encode(parameters).getTokenValue();
    }
}
