package com.bankofapis.util;

import com.bankofapis.constant.BrandConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class JSONWebTokenUtils {
    private static final Env ENV = Env.getInstance();
    
    public static JwtBuilder generateSignedJWTForDynamicRegistrationPostCall() throws Exception {
        // Get required vars from properties file
        String ssa = ENV.getSSA();

        // Claims mappings
        Map<String, Object> claims = new HashMap<>();
        claims.put("software_statement", ssa);
        claims.put("token_endpoint_auth_method", "private_key_jwt");

        // Generate and return signed JWT
        return generateJWTForClaims(claims, "PS256");
    }

    public static JwtBuilder generateSignedJWTWithAdditionalScopes(List<String> additionalScopesList) throws Exception {
        String additionalScopes = "";
        if (additionalScopesList != null) {
            additionalScopes = String.join(" ", additionalScopesList);
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", ENV.getClientId());
        claims.put("iss", ENV.getClientId());
        claims.put("openbanking-intent-id", "consentid");
        claims.put("login_hint_token", "loginhinttoken");
        claims.put("scope", String.join(" ","openid", additionalScopes, ENV.getScope()));
        return generateJWTForClaims(claims, "RS256");
    }

    public static JwtBuilder generateJWTForClaims(Map<String, Object> claims, String alg) throws Exception {
        String kid = ENV.getSigningKid();
        if (kid == null) {
            String ssa = ENV.getSSA();
            kid = getKeyIdFromSSAForJWTHeader(ssa);
        }
        return generateJsonWebToken(kid, claims, alg);
    }

    private static String getKeyIdFromSSAForJWTHeader(String ssa) throws Exception {
        // Get SSA Payload by splitting JWT and grabbing payload part
        String[] splitSSA = ssa.split("\\.");

        if(splitSSA.length != 3) {
            System.out.println("SSA is not a valid JWT format (header.payload.signature), please check properties file.");
            throw new RuntimeException("Invalid SSA.");
        }

        byte[] payload = splitSSA[1].getBytes(StandardCharsets.UTF_8);

        // Convert payload from Base64 into JSON String
        String ssaPayload = new String(
                // Decode payload from Base64
                Base64.getDecoder().decode(payload)
        );

        String jwksEndpoint = getJwksEndpointFromSSAPayload(ssaPayload);
        String jwks = getJwksFromSoftwareJwksEndpoint(jwksEndpoint);
        
        return getSigningKeyIdFromJwks(jwks);
    }

    private static String getJwksEndpointFromSSAPayload(String ssaPayload) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode ssaPayloadNode = mapper.readValue(ssaPayload, JsonNode.class);
            return ssaPayloadNode.get("software_jwks_endpoint").asText("");
        } catch(IOException e) {
            System.out.println("Error mapping SSA as JSON, contact your SSA provider for more help.");
            throw e;
        } catch (Exception e) {
            System.out.println("Unknown exception, please check your SSA and verify its contents.");
            throw e;
        }
    }

    private static String getJwksFromSoftwareJwksEndpoint(String jwksEndpoint) throws Exception {
        MtlsHttpClient jksHttpClient = new MtlsHttpClient("", ENV.getApiRequireProxy());
        HttpResponse jwksResponse = jksHttpClient.get(jwksEndpoint, new HashMap<>());
        return EntityUtils.toString(
                jwksResponse.getEntity()
        );
    }

    private static String getSigningKeyIdFromJwks(String jwks) throws IOException {
        // Map JWKS as Jackson JsonNode Object to read values easier
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jwksJson = mapper.readValue(jwks, JsonNode.class);

        // Find index of sig key
        int keyIndex = jwksJson
                .get("keys")
                .findValues("use")
                .indexOf(
                        new TextNode("sig")
                );

        if (keyIndex < 0) {
            System.out.println("Signing key not found in JWKS, please contact SSA provider.");
            throw new RuntimeException("Invalid JWKS.");
        }

        // Return kid for sig key
        return jwksJson
                .get("keys")
                .get(keyIndex)
                .get("kid")
                .asText();
    }

    private static JwtBuilder generateJsonWebToken(String kid, Map<String, Object> claims, String alg) throws Exception {
        PrivateKey k = ENV.getPrivateKeyObj();
        String aud = BrandConstants.getConstantsForBrand(ENV.getBrand()).getFapiId();
        
        LocalDateTime iat = LocalDateTime.now();
        LocalDateTime exp = iat.plusMinutes(10);
        
        Date issuedAt =  Date.from(iat.atZone(ZoneId.systemDefault()).toInstant());
        Date expiration = Date.from(exp.atZone(ZoneId.systemDefault()).toInstant());
        
        UUID uuid = UUID.randomUUID();
        SignatureAlgorithm signWith = SignatureAlgorithm.forName(alg);

        return Jwts.builder()
                .setClaims(claims)
                .setAudience(aud)
                .setExpiration(expiration)
                .setIssuedAt(issuedAt)
                .setId(uuid.toString())
                .setHeaderParam("alg", alg)
                .setHeaderParam("typ", "JWS")
                .setHeaderParam("kid", kid)
                .signWith(k, signWith);
    }
}
