package com.bankofapis.dto.oauth2;

import com.bankofapis.util.Env;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class AuthorizationRequestDTO {
    private static final Env ENV = Env.getInstance();

    @JsonProperty("consentRefId")
    private String consentRefId;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("acr_values")
    private String acrValues;

    @JsonProperty("iss")
    private String iss;

    @JsonProperty("claims")
    private ClaimsDTO claims;

    @JsonProperty("response_type")
    private String responseType;

    @JsonProperty("redirect_uri")
    private String redirectUri;

    @JsonProperty("nonce")
    private String nonce;

    @JsonProperty("client_id")
    private String clientId;

    public AuthorizationRequestDTO(String consentId, String nonce) {
        this.consentRefId = consentId;
        this.scope = "openid accounts";
        this.responseType = "code id_token";
        this.acrValues = "urn:openbanking:psd2:ca";
        this.iss = ENV.getClientId();
        this.claims = new ClaimsDTO(consentId);
        this.responseType = "code id_token";
        this.redirectUri = ENV.getRedirectUri();
        this.nonce = nonce;
        this.clientId = ENV.getClientId();
    }

    private static class ClaimsDTO{
        @JsonProperty("id_token")
        private ClaimsInfoDTO idToken;

        @JsonProperty("userinfo")
        private ClaimsInfoDTO userInfo;

        public ClaimsDTO(String consentId) {
            this.idToken = new ClaimsInfoDTO(consentId);
            this.userInfo = new ClaimsInfoDTO(consentId);
        }

        private static class ClaimsInfoDTO {
            @JsonProperty("acr")
            private Map<String, Object> acr;

            @JsonProperty("openbanking_intent_id")
            private Map<String, Object> openBankingIntentId;

            public ClaimsInfoDTO(String consentId) {
                this.acr = new HashMap<>();
                this.openBankingIntentId = new HashMap<>();
                this.acr.put("essential", true);
                this.openBankingIntentId.put("value", consentId);
                this.openBankingIntentId.put("essential", true);
            }
        }
    }
}
