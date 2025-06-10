package com.example.useradmin.security;

import io.jsonwebtoken.*;
// import io.jsonwebtoken.security.Keys; // Not available in 0.9.1
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

// import javax.crypto.SecretKey; // Can use java.security.Key or byte[] for HS256
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Base64; // For decoding secret string if it's Base64 encoded
import java.nio.charset.StandardCharsets; // For converting string to bytes

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret-key}")
    private String jwtSecretString;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    private Key jwtSigningKey;

    @jakarta.annotation.PostConstruct
    protected void init() {
        // For JJWT 0.9.1, the key is typically byte array.
        // Ensure the secret string is treated appropriately.
        // If your secret key is Base64 encoded (common practice):
        // byte[] keyBytes = Base64.getDecoder().decode(jwtSecretString);
        // If it's a plain string:
        byte[] keyBytes = jwtSecretString.getBytes(StandardCharsets.UTF_8);

        // Ensure the key length is appropriate for HS256 if that's the intended algorithm
        // For HS256, any length is technically usable but longer is better.
        // JJWT 0.9.1 doesn't have strong opinions on key length for HS256 like later versions.
        if (keyBytes.length == 0) {
             logger.warn("JWT secret key is not configured. Using a default insecure key. PLEASE CONFIGURE a strong app.jwt.secret-key.");
             // Generate a default key for placeholder - THIS IS INSECURE FOR PRODUCTION
             keyBytes = "DefaultInsecureSecretKeyForTestingPurposesOnly12345".getBytes(StandardCharsets.UTF_8);
        }
        this.jwtSigningKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateTokenFromUsername(userPrincipal.getUsername());
    }

    public String generateTokenFromUsername(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, jwtSigningKey) // Algorithm first in 0.9.1
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSigningKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // getClaimFromToken and getAllClaimsFromToken are essentially replaced by direct parsing
    // If you need a specific claim, you parse then get it from Claims object.

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSigningKey).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) { // Note: In 0.9.1, this was io.jsonwebtoken.SignatureException
            logger.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }
}
