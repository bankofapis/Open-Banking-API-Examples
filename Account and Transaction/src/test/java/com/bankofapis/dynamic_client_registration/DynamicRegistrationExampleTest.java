package com.bankofapis.dynamic_client_registration;

import com.bankofapis.util.Env;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DynamicRegistrationExampleTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;


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
    public void testMainMethodSucceedsOnCorrectEndpointConfiguration() throws Exception {
        stubFor(post("/register/v1.0").willReturn(ok("test_register_success")));
        stubFor(post("/as/token.oauth2").willReturn(ok("test_bearer_token_success")));
        DynamicRegistrationExample.main(null);
        assertThat(outContent.toString()).contains("test_register_success");
        assertThat(outContent.toString()).contains("test_bearer_token_success");
    }

    @Test
    public void testMainMethodFailsOnDCRErrorReturnWithout400StatusCode() {
        stubFor(post("/register/v1.0").willReturn(ok("error_description: test_dcr_failure_without_400")));
        assertThrows(RuntimeException.class, (() -> DynamicRegistrationExample.main(null)));
        assertThat(outContent.toString()).contains("POST request to URL: " + Env.getInstance().getApiBaseUrl() + "/register/v1.0");
        assertThat(outContent.toString()).contains("error_description: test_dcr_failure_without_400");
    }

    @Test
    public void testMainMethodFailsOnDCR400() {
        stubFor(post("/register/v1.0").willReturn(aResponse().withStatus(400).withBody("error_description: test_400_error")));
        assertThrows(RuntimeException.class, (() -> DynamicRegistrationExample.main(null)));
        assertThat(outContent.toString()).contains("POST request to URL: " + Env.getInstance().getApiBaseUrl() + "/register/v1.0");
        assertThat(outContent.toString()).contains("Server Error: Bad Request");
        assertThat(outContent.toString()).doesNotContain("error_description: test_400_error");
    }

    @AfterEach
    public void tearDown() {
        // Reset original System.out and System.err and print all outContent and errContent to console
        System.setOut(originalOut);
        System.out.println(outContent);
        System.setErr(originalErr);
        System.err.println(errContent);
    }
}
