package com.bankofapis.account_and_transactions;

import com.bankofapis.dto.oauth2.OAuthResponseDTO;
import com.bankofapis.dto.oauth2.RefreshTokenDTO;
import com.bankofapis.util.*;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OAuth2ConsentFlowExample {
    private static final Env ENV = Env.getInstance();

    public static void main(String[] args) throws Exception {
        OAuthResponseDTO accountAccessDTO = getAccountAccessToken();
        accountAccessDTO.saveToFile();

        String accountsResponse = getAccountsUsingToken(accountAccessDTO.getAccessToken());
        System.out.println(accountsResponse);

        RefreshTokenDTO refreshTokenDTO = AuthFlowUtil.consumeRefreshToken(accountAccessDTO);
        refreshTokenDTO.updateExpirationTime(LocalDateTime.now());
        accountAccessDTO.updateFromRefreshToken(refreshTokenDTO);
        accountAccessDTO.saveToFile();
        System.out.println(refreshTokenDTO);
    }

    private static OAuthResponseDTO getAccountAccessToken() throws Exception {
        List<String> scope = new ArrayList<>();
        scope.add("payments");
        scope.add("accounts");
        String sJWT = JSONWebTokenUtils.generateSignedJWTWithAdditionalScopes(scope)
                .setAudience(ENV.getAuthTokenBaseUrl() + "/as/token.oauth2")
                .compact();
        String accessToken = AuthFlowUtil.getAccessTokenUsingJWT(sJWT);
        String shellConsentResponse = AuthFlowUtil.generateShellConsent(accessToken);
        String consentId = AuthFlowUtil.getUserConsentId(shellConsentResponse);

        String pkceCodeVerifier = CryptoUtil.generateCodeVerifier();
        String pkceCodeChallenge = CryptoUtil.generateCodeChallenge(pkceCodeVerifier);
        String code = AuthFlowUtil.getConsentAuthorisationCode(consentId, pkceCodeChallenge);
        return AuthFlowUtil.getAccountAccessTokenUsingAuthorisationCode(code, pkceCodeVerifier, sJWT);
    }

    private static String getAccountsUsingToken(String accountAccessToken) throws Exception {
        MtlsHttpClient client = new MtlsHttpClient(ENV.getApiBaseUrl(), ENV.getApiRequireProxy());
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + accountAccessToken);
        HttpResponse response = client.get("/open-banking/v3.1/aisp/accounts/", headers);
        return EntityUtils.toString(response.getEntity());
    }


}
