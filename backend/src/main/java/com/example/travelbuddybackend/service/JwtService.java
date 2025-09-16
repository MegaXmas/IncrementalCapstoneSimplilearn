package com.example.travelbuddybackend.service;

import com.example.travelbuddybackend.models.AdminUser;
import com.example.travelbuddybackend.models.Client;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Service
public class JwtService {

    // Secret key for signing tokens - this should be strong and unique
    @Value("${jwt.secret:MySecureSecretKeyForJWTTokensThatShouldBeAtLeast256BitsLongForHMACAlgorithms}")
    private String secretKey;

    // Token expiration time in milliseconds
    @Value("${jwt.expiration:172800000}")
    private Long jwtExpiration;

    private static final JWSAlgorithm SIGNATURE_ALGORITHM = JWSAlgorithm.HS256;

    /**
     * Generate a JWT token for a successfully authenticated client
     * @param client The authenticated client for whom to generate the token
     * @return A signed JWT token string that proves the client's identity
     * @throws RuntimeException if token generation fails
     */
    public String generateToken(Client client) {
        try {
            // Step 1: Create the JWT header specifying the signing algorithm
            JWSHeader header = new JWSHeader.Builder(SIGNATURE_ALGORITHM)
                    .type(JOSEObjectType.JWT) // Explicitly mark this as a JWT token
                    .build();

            // Step 2: Calculate the expiration time for this token
            Date now = new Date();
            Date expirationTime = new Date(now.getTime() + jwtExpiration);

            // Step 3: Build the claims set
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    // Standard claims
                    .subject(client.getUsername())
                    .issuer("TravelBuddyApp")
                    .audience("TravelBuddyClients")
                    .issueTime(now)
                    .expirationTime(expirationTime)
                    .jwtID(java.util.UUID.randomUUID().toString())

                    // Custom claims
                    .claim("clientId", client.getId())
                    .claim("email", client.getEmail())
                    .claim("fullName", client.getFullName())
                    .claim("enabled", client.isEnabled())
                    .claim("accountLocked", client.isAccountLocked())

                    .build();

            // Step 4: Create the signed JWT token
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);

            // Step 5: Sign the token with our secret key
            JWSSigner signer = new MACSigner(secretKey.getBytes());
            signedJWT.sign(signer);

            // Step 6: Convert to string format for transmission
            String token = signedJWT.serialize();

            System.out.println("✓ JWT token generated successfully for client: " + client.getUsername());
            return token;

        } catch (JOSEException e) {
            System.err.println("✗ Error generating JWT token: " + e.getMessage());
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    public String generateToken(AdminUser adminUser) {
        try {
            // Step 1: Create the JWT header specifying the signing algorithm
            JWSHeader header = new JWSHeader.Builder(SIGNATURE_ALGORITHM)
                    .type(JOSEObjectType.JWT) // Explicitly mark this as a JWT token
                    .build();

            // Step 2: Calculate the expiration time for this token
            Date now = new Date();
            Date expirationTime = new Date(now.getTime() + jwtExpiration);

            // Step 3: Build the claims set
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    // Standard claims
                    .subject(adminUser.getAdminUsername())
                    .issuer("TravelBuddyApp")
                    .audience("TravelBuddyAdministrators")
                    .issueTime(now)
                    .expirationTime(expirationTime)
                    .jwtID(java.util.UUID.randomUUID().toString())

                    // Custom claims
                    .claim("adminId", adminUser.getId())
                    .claim("enabled", adminUser.isEnabled())
                    .claim("accountLocked", adminUser.isAccountLocked())

                    .build();

            // Step 4: Create the signed JWT token
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);

            // Step 5: Sign the token with our secret key
            JWSSigner signer = new MACSigner(secretKey.getBytes());
            signedJWT.sign(signer);

            // Step 6: Convert to string format for transmission
            String token = signedJWT.serialize();

            System.out.println("✓ JWT token generated successfully for client: " + adminUser.getAdminUsername());
            return token;

        } catch (JOSEException e) {
            System.err.println("✗ Error generating JWT token: " + e.getMessage());
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    /**
     * Extract the username from a JWT token
     * @param token The JWT token to read from
     * @return The username stored in the token's subject field
     * @throws RuntimeException if the token cannot be parsed or is invalid
     */
    public String extractUsername(String token) {
        try {
            JWTClaimsSet claims = extractAllClaims(token);
            return claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract username from token", e);
        }
    }

    /**
     * Extract any user ID from a token (client or admin)
     * @param token The JWT token to read from
     * @return The user ID (client or admin), or null if neither exists
     */
    public Integer extractUserId(String token) {
        try {
            JWTClaimsSet claims = extractAllClaims(token);

            Integer clientId = extractIntegerClaim(claims, "clientId");
            if (clientId != null) {
                return clientId;
            }

            Integer adminId = extractIntegerClaim(claims, "adminId");
            return adminId;

        } catch (Exception e) {
            System.err.println("Failed to extract user ID from token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Helper method to extract and convert integer claims safely
     * @param claims The JWT claims set
     * @param claimName The name of the claim to extract
     * @return The integer value, or null if not found or not convertible
     */
    private Integer extractIntegerClaim(JWTClaimsSet claims, String claimName) {
        try {
            Object claimValue = claims.getClaim(claimName);

            if (claimValue instanceof Integer) {
                return (Integer) claimValue;
            } else if (claimValue instanceof Long) {
                return ((Long) claimValue).intValue();
            } else if (claimValue instanceof String) {
                return Integer.valueOf((String) claimValue);
            } else if (claimValue instanceof Number) {
                return ((Number) claimValue).intValue();
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Determine what type of user this token represents
     * @param token The JWT token to analyze
     * @return "CLIENT", "ADMIN", or "UNKNOWN"
     */
    public String extractUserType(String token) {
        try {
            JWTClaimsSet claims = extractAllClaims(token);

            if (claims.getClaim("clientId") != null) {
                return "CLIENT";
            } else if (claims.getClaim("adminId") != null) {
                return "ADMIN";
            } else {
                return "UNKNOWN";
            }

        } catch (Exception e) {
            System.err.println("Failed to extract user type from token: " + e.getMessage());
            return "UNKNOWN";
        }
    }

    /**
     * Enhanced method to extract user information based on token type
     */
    public UserInfo extractUserInfo(String token) {
        try {
            JWTClaimsSet claims = extractAllClaims(token);

            Integer clientId = extractIntegerClaim(claims, "clientId");
            if (clientId != null) {
                return new UserInfo(clientId, "CLIENT", claims.getSubject());
            }

            Integer adminId = extractIntegerClaim(claims, "adminId");
            if (adminId != null) {
                return new UserInfo(adminId, "ADMIN", claims.getSubject());
            }

            return null;

        } catch (Exception e) {
            System.err.println("Failed to extract user info from token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extract the email address from a JWT token
     * @param token The JWT token to read from
     * @return The email address stored in the token, or null if not present
     */
    public String extractEmail(String token) {
        try {
            JWTClaimsSet claims = extractAllClaims(token);
            return claims.getStringClaim("email");
        } catch (Exception e) {
            System.err.println("Failed to extract email from token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extract the full name from a JWT token
     * @param token The JWT token to read from
     * @return The full name stored in the token, or null if not present
     */
    public String extractFullName(String token) {
        try {
            JWTClaimsSet claims = extractAllClaims(token);
            return claims.getStringClaim("fullName");
        } catch (Exception e) {
            System.err.println("Failed to extract full name from token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extract the expiration date from a JWT token
     * @param token The JWT token to read from
     * @return The expiration date of the token, or null if not present
     */
    public Date extractExpiration(String token) {
        try {
            JWTClaimsSet claims = extractAllClaims(token);
            return claims.getExpirationTime();
        } catch (Exception e) {
            System.err.println("Failed to extract expiration from token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extract all claims from a JWT token
     * @param token The JWT token to parse and validate
     * @return JWTClaimsSet containing all the information from the token
     * @throws RuntimeException if the token is invalid, tampered with, or cannot be parsed
     */
    private JWTClaimsSet extractAllClaims(String token) {
        try {
            // Step 1: Parse the token string into a SignedJWT object
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Step 2: Create a verifier using our secret key
            JWSVerifier verifier = new MACVerifier(secretKey.getBytes());

            // Step 3: Verify the signature
            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("JWT signature verification failed - token may be forged");
            }

            // Step 4: Extract and return the claims
            return signedJWT.getJWTClaimsSet();

        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse JWT token", e);
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to verify JWT token signature", e);
        }
    }

    /**
     * Check if a JWT token has expired
     * @param token The JWT token to check
     * @return true if the token is expired, false if still valid
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            if (expiration == null) {
                return true;
            }
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Validate a JWT token against a specific client
     * @param token The JWT token to validate
     * @param client The client to validate the token against
     * @return true if the token is valid for this client, false otherwise
     */
    public boolean validateToken(String token, Client client) {
        try {
            // Extract the username from the token
            String tokenUsername = extractUsername(token);

            // Check all validation criteria
            return tokenUsername != null &&
                    tokenUsername.equals(client.getUsername()) &&
                    !isTokenExpired(token) &&
                    client.canLogin();

        } catch (Exception e) {
            // If any validation step fails, the token is invalid
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    public boolean validateToken(String token, AdminUser adminUser) {
        try {
            // Extract the username from the token
            String tokenUsername = extractUsername(token);

            // Check all validation criteria
            return tokenUsername != null &&
                    tokenUsername.equals(adminUser.getAdminUsername()) &&
                    !isTokenExpired(token) &&
                    adminUser.canLogin();

        } catch (Exception e) {
            // If any validation step fails, the token is invalid
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if a JWT token is structurally valid
     * @param token The JWT token to validate
     * @return true if the token is structurally valid and not expired
     */
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);

            return !isTokenExpired(token);

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get the remaining time until a token expires
     * @param token The JWT token to check
     * @return Remaining time in milliseconds, or 0 if expired or invalid
     */
    public long getTokenRemainingTime(String token) {
        try {
            Date expiration = extractExpiration(token);
            if (expiration == null) {
                return 0;
            }

            Date now = new Date();
            if (expiration.after(now)) {
                return expiration.getTime() - now.getTime();
            } else {
                return 0; // Token has expired
            }

        } catch (Exception e) {
            return 0; // Invalid token
        }
    }

    /**
     * Extract the token ID (jti claim) from a JWT token
     * @param token The JWT token to read from
     * @return The token ID, or null if not present
     */
    public String extractTokenId(String token) {
        try {
            JWTClaimsSet claims = extractAllClaims(token);
            return claims.getJWTID();
        } catch (Exception e) {
            System.err.println("Failed to extract token ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Check if a client account is enabled according to the token
     * @param token The JWT token to read from
     * @return true if the account was enabled when the token was created
     */
    public boolean isAccountEnabledInToken(String token) {
        try {
            JWTClaimsSet claims = extractAllClaims(token);
            Object enabled = claims.getClaim("enabled");
            return enabled instanceof Boolean ? (Boolean) enabled : true;
        } catch (Exception e) {
            return false; // If we can't determine, assume disabled for security
        }
    }


    /**
     * Data class to hold user information extracted from JWT tokens
     */
        public record UserInfo(Integer id, String userType, String username) {

        public boolean isClient() {
            return "CLIENT".equals(userType);
        }

        public boolean isAdmin() {
            return "ADMIN".equals(userType);
        }

            @Override
            public String toString() {
                return String.format("UserInfo{id=%d, type=%s, username='%s'}", id, userType, username);
            }
        }
}