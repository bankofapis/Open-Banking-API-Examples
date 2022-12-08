package com.bankofapis.util;

import com.bankofapis.dto.oauth2.OAuthResponseDTO;
import com.bankofapis.testutil.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.URL;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthFlowUtilTest {
    @RegisterExtension
    static WireMockExtension wireMockExtension = WireMockExtension.extensionOptions()
            .options(
                    wireMockConfig()
                            .httpsPort(8090)
                            .needClientAuth(true)
                            .keystorePath("src/test/resources/test_data/jks/ca-cert.jks")
                            .keystorePassword("password")
                            .trustStorePath("src/test/resources/test_data/jks/ca-cert.jks")
                            .trustStorePassword("password")
            ).configureStaticDsl(true)
            .build();

    @BeforeAll
    public static void setUp(){
        Env.setProperties("test.properties");
    }

    @Test
    public void testGetAccessTokenUsingJWTReturnsAccessTokenFromOauthTokenEndpoint() throws Exception {
        stubFor(
                post("/as/token.oauth2")
                        .withRequestBody(containing("grant_type=client_credentials"))
                        .willReturn(ok("{ \"access_token\": \"test_access_token\"}"))
        );
        assertEquals(
                "test_access_token",
                AuthFlowUtil.getAccessTokenUsingJWT("test"),
                "not equal"
        );
    }

    @Test
    public void testGenerateShellConsentReturnsConsentFromAPIEndpoint() throws Exception {
        stubFor(
                post("/open-banking/v3.1/aisp/account-access-consents")
                        .withRequestBody(containing("ReadAccountsBasic"))
                        .willReturn(ok("test_shell_consent"))
        );
        assertEquals(
                "test_shell_consent",
                AuthFlowUtil.generateShellConsent("test"),
                "not equal"
        );
    }

    @Test
    public void testGetAccountAccessTokenUsingAuthorisationCodeReturnsAuthorizationCodeObjectFromAuthEndpoint() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        URL testAccessTokenStream = TestUtil.loadTestData("json/test_access_token.json");
        OAuthResponseDTO responseObject = mapper.readValue(testAccessTokenStream, OAuthResponseDTO.class);
        stubFor(
                post("/as/token.oauth2")
                        .withRequestBody(containing("grant_type=authorization_code"))
                        .willReturn(ok(mapper.writeValueAsString(responseObject)))
        );
        assertEquals(
                responseObject.getAccessToken(),
                AuthFlowUtil.getAccountAccessTokenUsingAuthorisationCode("test", "test", "test").getAccessToken(),
                "not equal");
    }
}
