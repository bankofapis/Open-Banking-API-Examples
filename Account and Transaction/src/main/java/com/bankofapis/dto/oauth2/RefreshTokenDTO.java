package com.bankofapis.dto.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class RefreshTokenDTO {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Long expiresIn;

    private LocalDateTime expirationTime;

    public RefreshTokenDTO() {}

    public void updateExpirationTime(LocalDateTime requestTime) {
        expirationTime = requestTime.plus(expiresIn, ChronoUnit.SECONDS);
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }
}
