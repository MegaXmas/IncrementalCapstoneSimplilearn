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

/**
 * Professional JWT Service using Nimbus JOSE JWT Library
 *
 * This service represents a significant upgrade in security and functionality compared
 * to basic JWT implementations. Think of Nimbus JOSE JWT as the difference between
 * a simple lock and a sophisticated electronic security system.
 *
 * Key advantages of Nimbus JOSE JWT:
 * 1. **Standards Compliance**: Follows official JOSE (JSON Object Signing and Encryption) standards
 * 2. **Security First**: Built with security best practices and protection against common attacks
 * 3. **Professional Grade**: Used in enterprise applications and banking systems
 * 4. **Flexibility**: Supports multiple algorithms and can be extended for advanced use cases
 * 5. **Clear API**: The code is more readable and maintainable
 *
 * In practical terms, this is like upgrading from a handwritten visitor badge to a
 * professionally printed security card with embedded chips and tamper-proof features.
 */
@Service
public class JwtService {

    // Secret key for signing tokens - this should be strong and unique
    // In production, this should come from environment variables or secure configuration
    @Value("${jwt.secret:MySecureSecretKeyForJWTTokensThatShouldBeAtLeast256BitsLongForHMACAlgorithms}")
    private String secretKey;

    // Token expiration time in milliseconds (48 hours = 172800000 ms)
    // This determines how long a user stays logged in before needing to log in again
    @Value("${jwt.expiration:172800000}")
    private Long jwtExpiration;

    // The signing algorithm we'll use - HMAC SHA-256 is secure and widely supported
    // Think of this as the type of lock mechanism we're using for our security badges
    private static final JWSAlgorithm SIGNATURE_ALGORITHM = JWSAlgorithm.HS256;

    /**
     * Generate a JWT token for a successfully authenticated client
     *
     * This method creates a secure, signed token that serves as a digital proof
     * of the client's identity. The process is similar to issuing an official
     * government ID - we verify the person's identity first, then create a
     * tamper-proof document they can use to prove who they are.
     *
     * The token contains essential information about the client and is signed
     * with our secret key to prevent forgery. Anyone who receives this token
     * can verify its authenticity without contacting the original issuer.
     *
     * @param client The authenticated client for whom to generate the token
     * @return A signed JWT token string that proves the client's identity
     * @throws RuntimeException if token generation fails
     */
    public String generateToken(Client client) {
        try {
            // Step 1: Create the JWT header specifying the signing algorithm
            // This is like specifying what type of security features are on an ID card
            JWSHeader header = new JWSHeader.Builder(SIGNATURE_ALGORITHM)
                    .type(JOSEObjectType.JWT) // Explicitly mark this as a JWT token
                    .build();

            // Step 2: Calculate the expiration time for this token
            Date now = new Date();
            Date expirationTime = new Date(now.getTime() + jwtExpiration);

            // Step 3: Build the claims set (the information stored in the token)
            // Claims are like the fields printed on an ID card - name, ID number, etc.
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    // Standard claims (these are part of the JWT specification)
                    .subject(client.getUsername())           // Primary identifier (like a driver's license number)
                    .issuer("TravelBuddyApp")               // Who issued this token (our application)
                    .audience("TravelBuddyClients")         // Who this token is intended for
                    .issueTime(now)                         // When the token was created
                    .expirationTime(expirationTime)         // When the token expires
                    .jwtID(java.util.UUID.randomUUID().toString()) // Unique token ID (prevents replay attacks)

                    // Custom claims (application-specific information)
                    .claim("clientId", client.getId())           // Database ID for quick lookups
                    .claim("email", client.getEmail())           // Email for communication
                    .claim("fullName", client.getFullName())     // Display name for user interface
                    .claim("enabled", client.isEnabled())        // Account status
                    .claim("accountLocked", client.isAccountLocked()) // Security status

                    .build();

            // Step 4: Create the signed JWT token
            // This is like using an official seal to make the ID card tamper-proof
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);

            // Step 5: Sign the token with our secret key
            // The secret key is like the official government seal - only we can create valid tokens
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
            // This is like specifying what type of security features are on an ID card
            JWSHeader header = new JWSHeader.Builder(SIGNATURE_ALGORITHM)
                    .type(JOSEObjectType.JWT) // Explicitly mark this as a JWT token
                    .build();

            // Step 2: Calculate the expiration time for this token
            Date now = new Date();
            Date expirationTime = new Date(now.getTime() + jwtExpiration);

            // Step 3: Build the claims set (the information stored in the token)
            // Claims are like the fields printed on an ID card - name, ID number, etc.
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    // Standard claims (these are part of the JWT specification)
                    .subject(adminUser.getAdminUsername())           // Primary identifier (like a driver's license number)
                    .issuer("TravelBuddyApp")               // Who issued this token (our application)
                    .audience("TravelBuddyAdministrators")         // Who this token is intended for
                    .issueTime(now)                         // When the token was created
                    .expirationTime(expirationTime)         // When the token expires
                    .jwtID(java.util.UUID.randomUUID().toString()) // Unique token ID (prevents replay attacks)

                    // Custom claims (application-specific information)
                    .claim("adminId", adminUser.getId())           // Database ID for quick lookups// Display name for user interface
                    .claim("enabled", adminUser.isEnabled())        // Account status
                    .claim("accountLocked", adminUser.isAccountLocked()) // Security status

                    .build();

            // Step 4: Create the signed JWT token
            // This is like using an official seal to make the ID card tamper-proof
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);

            // Step 5: Sign the token with our secret key
            // The secret key is like the official government seal - only we can create valid tokens
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
     *
     * This method reads the subject field from the token, which contains the username.
     * It's like reading the name from an ID card - we parse the token and extract
     * the specific piece of information we need.
     *
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
     * Enhanced ID extraction methods for handling both Client and Admin tokens
     *
     * These methods provide a clean way to extract IDs from JWT tokens while
     * maintaining clarity about what type of user the token represents.
     */

    /**
     * Extract any user ID from a token (client or admin)
     *
     * This is a more elegant version of your approach. It checks for both
     * clientId and adminId claims and returns whichever one exists.
     *
     * @param token The JWT token to read from
     * @return The user ID (client or admin), or null if neither exists
     */
    public Integer extractUserId(String token) {
        try {
            JWTClaimsSet claims = extractAllClaims(token);

            // First, try to get clientId
            Integer clientId = extractIntegerClaim(claims, "clientId");
            if (clientId != null) {
                return clientId;
            }

            // If no clientId, try adminId
            Integer adminId = extractIntegerClaim(claims, "adminId");
            if (adminId != null) {
                return adminId;
            }

            return null; // Neither clientId nor adminId found

        } catch (Exception e) {
            System.err.println("Failed to extract user ID from token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Helper method to extract and convert integer claims safely
     *
     * This handles the different ways integers might be stored in JWT claims
     * (as Integer, Long, or String) and converts them consistently.
     *
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
     *
     * This is useful when you need to know whether a token belongs to
     * a client or an admin user.
     *
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
     *
     * This returns a UserInfo object that contains both the ID and the type,
     * making it easy to handle both client and admin tokens in your controllers.
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
     *
     * This reads our custom email claim from the token. Having the email
     * readily available in the token is useful for user interface elements
     * and communication features.
     *
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
     *
     * This is useful for personalizing the user interface without needing
     * to make additional database queries.
     *
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
     *
     * This tells us when the token expires, which is useful for determining
     * if we need to refresh the token or ask the user to log in again.
     *
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
     *
     * This is the core method that parses and validates a JWT token.
     * It performs several critical security checks:
     * 1. Verifies the token structure is valid
     * 2. Checks the digital signature to ensure the token wasn't tampered with
     * 3. Extracts all the claims (information) stored in the token
     *
     * Think of this as the process of carefully examining an ID card with
     * special equipment to verify it's authentic and reading all the information on it.
     *
     * @param token The JWT token to parse and validate
     * @return JWTClaimsSet containing all the information from the token
     * @throws RuntimeException if the token is invalid, tampered with, or cannot be parsed
     */
    private JWTClaimsSet extractAllClaims(String token) {
        try {
            // Step 1: Parse the token string into a SignedJWT object
            // This is like putting the ID card into a special reader machine
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Step 2: Create a verifier using our secret key
            // This is like having the official verification tool that can check the security features
            JWSVerifier verifier = new MACVerifier(secretKey.getBytes());

            // Step 3: Verify the signature
            // This checks if the token was created by us and hasn't been tampered with
            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("JWT signature verification failed - token may be forged");
            }

            // Step 4: Extract and return the claims
            // Now that we know the token is authentic, we can safely read the information from it
            return signedJWT.getJWTClaimsSet();

        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse JWT token", e);
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to verify JWT token signature", e);
        }
    }

    /**
     * Check if a JWT token has expired
     *
     * This method checks if the token is past its expiration date.
     * Expired tokens should not be accepted for authentication,
     * just like you wouldn't accept an expired driver's license as valid ID.
     *
     * @param token The JWT token to check
     * @return true if the token is expired, false if still valid
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            if (expiration == null) {
                return true; // If we can't determine expiration, consider it expired for safety
            }
            return expiration.before(new Date());
        } catch (Exception e) {
            // If we can't check the expiration due to any error, consider it expired for security
            return true;
        }
    }

    /**
     * Validate a JWT token against a specific client
     *
     * This performs comprehensive validation of a token:
     * 1. Checks if the token is structurally valid and hasn't been tampered with
     * 2. Verifies the token belongs to the specified client
     * 3. Ensures the token hasn't expired
     * 4. Confirms the client account is still active
     *
     * This is like checking if an ID card belongs to a specific person,
     * is still valid, and the person is still authorized to use our services.
     *
     * @param token The JWT token to validate
     * @param client The client to validate the token against
     * @return true if the token is valid for this client, false otherwise
     */
    public boolean validateToken(String token, Client client) {
        try {
            // Extract the username from the token
            String tokenUsername = extractUsername(token);

            // Check all validation criteria
            return tokenUsername != null &&                    // Token contains a username
                    tokenUsername.equals(client.getUsername()) && // Token belongs to this client
                    !isTokenExpired(token) &&                   // Token hasn't expired
                    client.canLogin();                           // Client account is still active

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
            return tokenUsername != null &&                    // Token contains a username
                    tokenUsername.equals(adminUser.getAdminUsername()) && // Token belongs to this admin
                    !isTokenExpired(token) &&                   // Token hasn't expired
                    adminUser.canLogin();                           // Admin account is still active

        } catch (Exception e) {
            // If any validation step fails, the token is invalid
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if a JWT token is structurally valid
     *
     * This performs basic validation without checking against a specific client.
     * It's useful for general token validation, like checking if a token is
     * worth processing before looking up the associated client.
     *
     * @param token The JWT token to validate
     * @return true if the token is structurally valid and not expired
     */
    public boolean isTokenValid(String token) {
        try {
            // Try to extract claims (this will fail if the token is malformed or signature is invalid)
            extractAllClaims(token);

            // Check if token is expired
            return !isTokenExpired(token);

        } catch (Exception e) {
            // If anything fails, the token is invalid
            return false;
        }
    }

    /**
     * Get the remaining time until a token expires
     *
     * This calculates how much time is left before the token expires.
     * It's useful for displaying "session expires in X minutes" messages
     * to users or deciding when to automatically refresh tokens.
     *
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
     *
     * The JWT ID is a unique identifier for each token. This can be useful
     * for token blacklisting (tracking revoked tokens) or audit logging.
     *
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
     *
     * This reads the account status from the token itself. Note that this
     * reflects the status when the token was created - for real-time status,
     * you should check the database.
     *
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
     *
     * This makes it easy to pass around user information without
     * having to make multiple method calls to extract different pieces.
     */
    public static class UserInfo {
        private final Integer id;
        private final String userType;
        private final String username;

        public UserInfo(Integer id, String userType, String username) {
            this.id = id;
            this.userType = userType;
            this.username = username;
        }

        public Integer getId() { return id; }
        public String getUserType() { return userType; }
        public String getUsername() { return username; }

        public boolean isClient() { return "CLIENT".equals(userType); }
        public boolean isAdmin() { return "ADMIN".equals(userType); }

        @Override
        public String toString() {
            return String.format("UserInfo{id=%d, type=%s, username='%s'}", id, userType, username);
        }
    }
}