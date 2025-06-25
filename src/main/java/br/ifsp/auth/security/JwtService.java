package br.ifsp.auth.security;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import br.ifsp.auth.model.User;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;

    public JwtService(JwtEncoder encoder) {
        this.jwtEncoder = encoder;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        long expire = 3600L;

        // Mapeia os nomes das roles como strings
        List<String> authorities = user.getRoles().stream()
                .map(role -> role.getRoleName().name()) // Ex: "ROLE_ADMIN"
                .collect(Collectors.toList());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("spring-security")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expire))
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("authorities", authorities) // Adiciona a claim de roles
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
