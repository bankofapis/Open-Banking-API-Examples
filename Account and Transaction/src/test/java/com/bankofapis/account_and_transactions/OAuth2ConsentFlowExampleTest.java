package com.bankofapis.account_and_transactions;

import com.bankofapis.dto.accounts.ShellConsentResponseDTO;
import com.bankofapis.dto.oauth2.OAuthResponseDTO;
import com.bankofapis.testutil.TestUtil;
import com.bankofapis.util.Env;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OAuth2ConsentFlowExampleTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final InputStream originalIn = System.in;

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
    public static void setUp() {
        Env.setProperties("test.properties");
    }

    @BeforeEach
    public void initTests() {
        // Capture System.out and System.err to outContent and errContent respectively
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void testMainMethodSavesAccessTokenOnCorrectInput() throws Exception {
        String linkReturnValue = "<div class=\"ob-service-type-section\"> <a class=\"zb-button zb-button-primary\" href=\"http://localhost:8080/test\">";
        String codeInputValue = "code=testCode";
        System.setIn(new ByteArrayInputStream(codeInputValue.getBytes(StandardCharsets.UTF_8)));
        ShellConsentResponseDTO consentResponseDTO = new ShellConsentResponseDTO();
        consentResponseDTO.setConsentId("test");
        URL testAccessTokenStream = TestUtil.loadTestData("json/test_access_token.json");
        ObjectMapper mapper = new ObjectMapper();
        OAuthResponseDTO responseObject = mapper.readValue(testAccessTokenStream, OAuthResponseDTO.class);
        generateStubs(linkReturnValue,  mapper.writeValueAsString(consentResponseDTO), mapper.writeValueAsString(responseObject));
        OAuth2ConsentFlowExample.main(null);
        assertThat(outContent.toString()).contains("Saving access token to:");
    }

    @Test
    public void testMainMethodFailsOnBadAuthorizationResponse() throws Exception {
        String linkReturnValue = "Bad authorization response.";
        String codeInputValue = "code=testCode";
        System.setIn(new ByteArrayInputStream(codeInputValue.getBytes(StandardCharsets.UTF_8)));
        ShellConsentResponseDTO consentResponseDTO = new ShellConsentResponseDTO();
        consentResponseDTO.setConsentId("test");
        URL testAccessTokenStream = TestUtil.loadTestData("json/test_access_token.json");
        ObjectMapper mapper = new ObjectMapper();
        OAuthResponseDTO responseObject = mapper.readValue(testAccessTokenStream, OAuthResponseDTO.class);
        generateStubs(linkReturnValue,  mapper.writeValueAsString(consentResponseDTO), mapper.writeValueAsString(responseObject));
        assertThrows(Exception.class, () -> OAuth2ConsentFlowExample.main(null));
        assertThat(outContent.toString()).doesNotContain("Saving access token to:");
    }

    @Test
    public void testMainMethodFailsOnBadUserInput() throws Exception {
        String linkReturnValue = "<div class=\"ob-service-type-section\"> <a class=\"zb-button zb-button-primary\" href=\"http://localhost:8080/test\">";
        String codeInputValue = "Bad user input";
        System.setIn(new ByteArrayInputStream(codeInputValue.getBytes(StandardCharsets.UTF_8)));
        ShellConsentResponseDTO consentResponseDTO = new ShellConsentResponseDTO();
        consentResponseDTO.setConsentId("test");
        URL testAccessTokenStream = TestUtil.loadTestData("json/test_access_token.json");
        ObjectMapper mapper = new ObjectMapper();
        OAuthResponseDTO responseObject = mapper.readValue(testAccessTokenStream, OAuthResponseDTO.class);
        generateStubs(linkReturnValue, mapper.writeValueAsString(consentResponseDTO), mapper.writeValueAsString(responseObject));
        assertThrows(Exception.class, () -> OAuth2ConsentFlowExample.main(null));
        assertThat(outContent.toString()).doesNotContain("Saving access token to:");
    }

    @Test
    public void testMainMethodFailsOnBadOAuthResponse() throws Exception {
        String linkReturnValue = "<div class=\"ob-service-type-section\"> <a class=\"zb-button zb-button-primary\" href=\"http://localhost:8080/test\">";
        String codeInputValue = "code=testCode";
        System.setIn(new ByteArrayInputStream(codeInputValue.getBytes(StandardCharsets.UTF_8)));
        ShellConsentResponseDTO consentResponseDTO = new ShellConsentResponseDTO();
        consentResponseDTO.setConsentId("test");
        ObjectMapper mapper = new ObjectMapper();
        generateStubs(linkReturnValue, mapper.writeValueAsString(consentResponseDTO), "Bad OAuth Response");
        assertThrows(Exception.class, () -> OAuth2ConsentFlowExample.main(null));
        assertThat(outContent.toString()).doesNotContain("Saving access token to:");
    }

    @Test
    public void testMainMethodFailsOnBadConsentResponse() throws Exception {
        String linkReturnValue = "<div class=\"ob-service-type-section\"> <a class=\"zb-button zb-button-primary\" href=\"http://localhost:8080/test\">";
        String codeInputValue = "code=testCode";
        System.setIn(new ByteArrayInputStream(codeInputValue.getBytes(StandardCharsets.UTF_8)));
        URL testAccessTokenStream = TestUtil.loadTestData("json/test_access_token.json");
        ObjectMapper mapper = new ObjectMapper();
        OAuthResponseDTO responseObject = mapper.readValue(testAccessTokenStream, OAuthResponseDTO.class);
        generateStubs(linkReturnValue, "Bad consent response", mapper.writeValueAsString(responseObject));
        assertThrows(Exception.class, () -> OAuth2ConsentFlowExample.main(null));
        assertThat(outContent.toString()).doesNotContain("Saving access token to:");
    }

    @AfterEach
    public void tearDown() {
        // Reset original System.out and System.err and print all outContent and errContent to console
        System.setOut(originalOut);
        System.out.println(outContent);
        System.setErr(originalErr);
        System.err.println(errContent);
        System.setIn(originalIn);
    }

    private void generateStubs(String linkReturnValue, String consentResponse, String oAuthResponse) {
        stubFor(post("/as/token.oauth2").willReturn(ok("{\"access_token\": \"test_access_token\"}")));
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=authorization_code")).willReturn(ok(oAuthResponse)));
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(post("/open-banking/v3.1/aisp/account-access-consents").willReturn(ok(consentResponse)));
        stubFor(get(urlMatching("/as/authorization.oauth2?.*")).willReturn(ok(linkReturnValue)));
        stubFor(get("/open-banking/v3.1/aisp/accounts/").willReturn(ok("success")));
    }
}
