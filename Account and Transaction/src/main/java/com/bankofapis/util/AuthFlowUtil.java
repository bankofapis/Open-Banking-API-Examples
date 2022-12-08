package com.bankofapis.util;

import com.bankofapis.dto.accounts.ConsentRequestPostDTO;
import com.bankofapis.dto.accounts.ShellConsentResponseDTO;
import com.bankofapis.dto.oauth2.AuthorizationRequestDTO;
import com.bankofapis.dto.oauth2.OAuthResponseDTO;
import com.bankofapis.dto.oauth2.RefreshTokenDTO;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AuthFlowUtil {
    private static final Env ENV = Env.getInstance();

    public static OAuthResponseDTO loadAndRefreshAccessToken() throws Exception {
        try {
            OAuthResponseDTO oauthAccessToken = OAuthResponseDTO.loadFromFile();
            LocalDateTime currentTime = LocalDateTime.now();
            if (oauthAccessToken.getExpirationTime().compareTo(currentTime) <= 0) {
                RefreshTokenDTO refreshTokenDTO = consumeRefreshToken(oauthAccessToken);
                refreshTokenDTO.updateExpirationTime(currentTime);
                oauthAccessToken.updateFromRefreshToken(refreshTokenDTO);
                oauthAccessToken.saveToFile();
            }
            return oauthAccessToken;
        } catch (Exception e) {
            System.out.println("Failed to load Access Token, please run AccountsAndTransactionsConsentFlowExample first.");
            throw e;
        }
    }

    public static RefreshTokenDTO consumeRefreshToken(OAuthResponseDTO accountAccessDTO) throws Exception {
        String refreshToken = accountAccessDTO.getRefreshToken();
        MtlsHttpClient client = new MtlsHttpClient(ENV.getAuthTokenBaseUrl(), ENV.getAuthTokenRequireProxy());
        String jwt = JSONWebTokenUtils.generateSignedJWTWithAdditionalScopes(Collections.singletonList("payments"))
                .setAudience(ENV.getAuthTokenBaseUrl() + "/as/token.oauth2").compact();
        List<NameValuePair> postBody = new ArrayList<>();
        postBody.add(GeneralUtil.getNameValuePair("grant_type", "refresh_token"));
        postBody.add(GeneralUtil.getNameValuePair("refresh_token", refreshToken));
        postBody.add(GeneralUtil.getNameValuePair("redirect_uri", ENV.getRedirectUri()));
        postBody.add(GeneralUtil.getNameValuePair("client_id", ENV.getClientId()));
        postBody.add(GeneralUtil.getNameValuePair("client_secret", ENV.getClientSecret()));
        postBody.add(GeneralUtil.getNameValuePair("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer"));
        postBody.add(GeneralUtil.getNameValuePair("client_assertion", jwt));
        HttpEntity data = new UrlEncodedFormEntity(postBody);
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application.json");
        HttpResponse response = client.post("/as/token.oauth2", data, headers);
        String responseString = EntityUtils.toString(response.getEntity());
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(responseString, RefreshTokenDTO.class);
        } catch (UnrecognizedPropertyException e) {
            System.out.println("Received error from server: " + responseString);
            throw e;
        }
    }

    public static OAuthResponseDTO getAccountAccessTokenUsingAuthorisationCode(String code, String pkceCodeVerifier, String jwt) throws Exception {
        List<NameValuePair> urlEncodedPostContent = generateOauthTokenPostBody(jwt, "authorization_code", code, pkceCodeVerifier);
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        MtlsHttpClient client = new MtlsHttpClient(ENV.getAuthTokenBaseUrl(), ENV.getAuthTokenRequireProxy());
        LocalDateTime tokenRequestTime = LocalDateTime.now();
        HttpResponse response = client.post("/as/token.oauth2", new UrlEncodedFormEntity(urlEncodedPostContent), headers);
        String responseString = EntityUtils.toString(response.getEntity());
        ObjectMapper mapper = new ObjectMapper();
        OAuthResponseDTO oAuthResponseDTO = mapper.readValue(responseString, OAuthResponseDTO.class);
        oAuthResponseDTO.updateExpirationTime(tokenRequestTime);
        return oAuthResponseDTO;
    }

    public static String getConsentAuthorisationCode(String consentId, String pkceCodeChallenge) throws Exception {
        String nonce = CryptoUtil.generateNonce();
        String linkResponse = getLinkToOAuthFlow(consentId, nonce, pkceCodeChallenge);
        return getUserInputForAuthCode(linkResponse);
    }

    private static String getUserInputForAuthCode(String linkResponse) {
        Pattern linkPattern = Pattern.compile("<div[\\s]+class=\"ob-service-type-section\">[\\s]+<a[\\s]+class=\"zb-button[\\s]+zb-button-primary\"([^>]+)>");
        Matcher matcher = linkPattern.matcher(linkResponse);
        if (!matcher.find()) {
            System.out.println("Error getting consent URL.  Please check response.");
            System.out.println(linkResponse);
            throw new RuntimeException("Bad server response.");
        }
        String linkDetails = matcher.group(1);
        Pattern hrefPattern = Pattern.compile("href=\"([^\"]+)\"");
        Matcher hrefMatcher = hrefPattern.matcher(linkDetails);
        if (!hrefMatcher.find()) {
            System.out.println("Error getting consent URL from response.  Please contact an administrator.");
            throw new RuntimeException("Bad server response, link not found: " + linkDetails);
        }
        System.out.println("To perform consent, go to this link and log in: " + hrefMatcher.group(1));
        Scanner sc = new Scanner(System.in);
        System.out.println("After login and consent, paste full URL in here: ");
        String redirectUrl = sc.nextLine();
        int startIndex = redirectUrl.indexOf("code=");
        if (startIndex == -1) {
            throw new RuntimeException("Code not found in pasted URL.  Please log in and select an account before copying the URL.");
        }
        int endIndex = redirectUrl
                .substring(startIndex)
                .indexOf("&") + startIndex;
        if (endIndex < startIndex) {
            endIndex = redirectUrl.length();
        }
        return redirectUrl.substring(startIndex, endIndex).replace("code=", "");
    }

    private static String getLinkToOAuthFlow(String consentId, String nonce, String pkceCodeChallenge) throws Exception {
        MtlsHttpClient client = new MtlsHttpClient(ENV.getAuthConsentBaseUrl(), ENV.getAuthConsentRequireProxy());
        Map<String, Object> claims = generateClaimsMap(consentId, nonce);
        String jwt = JSONWebTokenUtils.generateJWTForClaims(claims, "PS256")
                .setAudience(ENV.getAuthConsentBaseUrl())
                .compact();
        System.out.println(jwt);
        Map<String, String> queryParams = generateOAuthAuthorizationQueryParams(nonce, pkceCodeChallenge, jwt);

        String requestUrl = "/as/authorization.oauth2?";
        List<String> urlCodedQueryParams = queryParams.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.toList());
        requestUrl += String.join("&", urlCodedQueryParams);
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "text/html");
        return EntityUtils.toString(client.get(requestUrl, headers).getEntity());
    }

    public static String getUserConsentId(String shellConsentResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(shellConsentResponse);
        ShellConsentResponseDTO responseDTO = mapper.readValue(shellConsentResponse, ShellConsentResponseDTO.class);
        return responseDTO.getConsentId();

    }

    public static String generateShellConsent(String accessToken) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);

        ConsentRequestPostDTO requestPostDTO = new ConsentRequestPostDTO();
        requestPostDTO.addPermission("ReadAccountsBasic");
        requestPostDTO.addPermission("ReadAccountsDetail");
        requestPostDTO.addPermission("ReadBalances");
        requestPostDTO.addPermission("ReadOffers");
        requestPostDTO.addPermission("ReadBeneficiariesDetail");
        requestPostDTO.addPermission("ReadDirectDebits");
        requestPostDTO.addPermission("ReadProducts");
        requestPostDTO.addPermission("ReadStandingOrdersDetail");
        requestPostDTO.addPermission("ReadTransactionsCredits");
        requestPostDTO.addPermission("ReadTransactionsDebits");
        requestPostDTO.addPermission("ReadTransactionsDetail");
        requestPostDTO.addPermission("ReadScheduledPaymentsDetail");
        ObjectMapper mapper = new ObjectMapper();
        String data = mapper.writeValueAsString(requestPostDTO);

        MtlsHttpClient httpClient = new MtlsHttpClient(ENV.getApiBaseUrl(), ENV.getApiRequireProxy());
        HttpResponse response = httpClient.post(
                "/open-banking/v3.1/aisp/account-access-consents",
                new StringEntity(data, ContentType.APPLICATION_JSON),
                headers
        );
        return EntityUtils.toString(response.getEntity());
    }

    public static String getAccessTokenUsingJWT(String sJWT) throws Exception {
        List<NameValuePair> postBody = generateOauthTokenPostBody(sJWT, "client_credentials", null, null);

        MtlsHttpClient httpClient = new MtlsHttpClient(ENV.getAuthTokenBaseUrl(), ENV.getAuthTokenRequireProxy());
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        String response = EntityUtils.toString(
                httpClient.post(
                        "/as/token.oauth2", new UrlEncodedFormEntity(postBody), headers
                ).getEntity()
        );

        verifyResponseResultOrThrow(response.contains("500 Internal Server Error"), "Invalid response from authentication endpoint.  Please contact an administrator.", "Bad server response.");
        ObjectMapper mapper = new ObjectMapper();
        try{
            JsonNode tokenResponse = mapper.readValue(response, JsonNode.class);
            verifyResponseResultOrThrow(tokenResponse.get("error") != null, "Bad server response from authentication endpoint: " + tokenResponse, "Bad server response");
            return tokenResponse.get("access_token").asText("");
        } catch(JsonParseException e) {
            System.out.println("Failed to parse token response.  Please contact an administrator.");
            throw e;
        }
    }

    private static Map<String, String> generateOAuthAuthorizationQueryParams(String nonce, String pkceCodeChallenge, String jwt) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("client_id", ENV.getClientId());
        queryParams.put("response_type", "code%20id_token");
        queryParams.put("code_challenge_method", "S256");
        queryParams.put("code_challenge", pkceCodeChallenge);
        queryParams.put("request", jwt);
        queryParams.put("nonce", nonce);
        queryParams.put("authnMethod", "R4P");
        return queryParams;
    }

    private static List<NameValuePair> generateOauthTokenPostBody(String jwt, String grantType, String code, String pkceCodeVerifier) {
        List<NameValuePair> urlEncodedPostContent = new ArrayList<>();
        if (code != null) {
            urlEncodedPostContent.add(GeneralUtil.getNameValuePair("code", code));
        } else {
            urlEncodedPostContent.add(GeneralUtil.getNameValuePair("scope", ENV.getScope()));
        }
        if (pkceCodeVerifier != null) {
            urlEncodedPostContent.add(GeneralUtil.getNameValuePair("code_verifier", pkceCodeVerifier));
            urlEncodedPostContent.add(GeneralUtil.getNameValuePair("code_challenge_method", "S256"));
        }
        urlEncodedPostContent.add(GeneralUtil.getNameValuePair("grant_type", grantType));
        urlEncodedPostContent.add(GeneralUtil.getNameValuePair("redirect_uri", ENV.getRedirectUri()));
        urlEncodedPostContent.add(GeneralUtil.getNameValuePair("client_id", ENV.getClientId()));
        urlEncodedPostContent.add(GeneralUtil.getNameValuePair("client_secret", ENV.getClientSecret()));
        urlEncodedPostContent.add(GeneralUtil.getNameValuePair("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer"));
        urlEncodedPostContent.add(GeneralUtil.getNameValuePair("client_assertion", jwt));
        return urlEncodedPostContent;
    }

    private static void verifyResponseResultOrThrow(boolean verificationTest, String logMessage, String exceptionMessage) {
        if (verificationTest) {
            System.out.println(logMessage);
            throw new RuntimeException(exceptionMessage);
        }
    }

    private static Map<String, Object> generateClaimsMap(String consentId, String nonce) {
        ObjectMapper mapper = new ObjectMapper();
        AuthorizationRequestDTO consentRequestPostDTO = new AuthorizationRequestDTO(consentId, nonce);
        return mapper.convertValue(consentRequestPostDTO, new TypeReference<Map<String, Object>>(){});
    }
}
