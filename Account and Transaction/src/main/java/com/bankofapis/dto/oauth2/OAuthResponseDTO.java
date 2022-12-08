package com.bankofapis.dto.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class OAuthResponseDTO {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("id_token")
    private String idToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class, as = LocalDateTime.class)
    private LocalDateTime expirationTime;

    public OAuthResponseDTO() {}

    public void updateExpirationTime(LocalDateTime requestTime) {
        expirationTime = requestTime.plus(expiresIn, ChronoUnit.SECONDS);
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void updateFromRefreshToken(RefreshTokenDTO refreshTokenDTO) {
        this.accessToken = refreshTokenDTO.getAccessToken();
        this.refreshToken = refreshTokenDTO.getRefreshToken();
        this.expirationTime = refreshTokenDTO.getExpirationTime();
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public String getScope(){
        return this.scope;
    }

    public void saveToFile() throws IOException {
        String tokenPath = "access_token.json";
        File tokenOutputFile = new File(tokenPath);
        System.out.println("Saving access token to: " + tokenOutputFile.getAbsolutePath());
        if(tokenOutputFile.createNewFile()) {
            System.out.println("Created new access token: " + tokenOutputFile.getAbsolutePath());
        }
        OutputStream outputStream = new FileOutputStream(tokenPath);
        ObjectMapper mapper = new ObjectMapper();
        outputStream.write(mapper.writeValueAsString(this).getBytes(StandardCharsets.US_ASCII));
        outputStream.flush();
        outputStream.close();
    }

    public static OAuthResponseDTO loadFromFile() throws IOException {
        String tokenPath = "access_token.json";
        File tokenInputFile = new File(tokenPath);
        System.out.println("Loading access token from " + tokenInputFile.getAbsolutePath());
        if (!tokenInputFile.exists()) {
            throw new RuntimeException("Access Token not found, please run AccountsAndTransactionsConsentFlowExample.java first.");
        }
        InputStream inputStream = new FileInputStream(tokenInputFile);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(inputStream, OAuthResponseDTO.class);

    }
}
