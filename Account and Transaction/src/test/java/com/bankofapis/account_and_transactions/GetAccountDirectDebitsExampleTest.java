package com.bankofapis.account_and_transactions;

import com.bankofapis.dto.accounts.GetAccountDirectDebitsDTO;
import com.bankofapis.util.Env;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetAccountDirectDebitsExampleTest {

    String mockJsonResponse = "{\n" +
            "  \"Data\": {\n" +
            "    \"DirectDebit\": [\n" +
            "      {\n" +
            "        \"AccountId\": \"12345\",\n" +
            "        \"MandateIdentification\": \"string\",\n" +
            "        \"DirectDebitStatusCode\": \"Active\",\n" +
            "        \"Name\": \"UNKNOWN\",\n" +
            "        \"PreviousPaymentDateTime\": \"2021-12-14T17:06:15.258Z\",\n" +
            "        \"PreviousPaymentAmount\": {\n" +
            "          \"Amount\": \"string\",\n" +
            "          \"Currency\": \"GBP\"\n" +
            "        }\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"Links\": {\n" +
            "    \"Self\": \"string\",\n" +
            "    \"Prev\": \"string\",\n" +
            "    \"Next\": \"string\"\n" +
            "  },\n" +
            "  \"Meta\": {\n" +
            "    \"TotalPages\": 0,\n" +
            "    \"FirstAvailableDateTime\": \"2021-12-14T17:06:15.258Z\",\n" +
            "    \"LastAvailableDateTime\": \"2021-12-14T17:06:15.258Z\"\n" +
            "  }\n" +
            "}";

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
    public void testMainMethodSucceedsWithValidResponse() throws Exception {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/direct-debits").willReturn(ok(mockJsonResponse)));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        GetAccountDirectDebitsExample.main(args);
        assertThat(outContent.toString()).contains("accountId='12345'");
    }

    @Test
    public void testMainMethodFailsWithNullResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/direct-debits").willReturn(ok(null)));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountDirectDebitsExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testMainMethodFailsWithNonJsonResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/direct-debits").willReturn(ok("This is not JSON")));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountDirectDebitsExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testMainMethodFailsWithInvalidJsonResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/direct-debits").willReturn(ok("{\"Description\": \"Valid JSON but invalid input\"}")));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountDirectDebitsExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testExpectedInput() throws IOException {
        GetAccountDirectDebitsDTO getAccountDirectDebitsDTOExpected = getExpectedHappyPathDTO();

        GetAccountDirectDebitsDTO getAccountDirectDebitsDTOTest = GetAccountDirectDebitsExample.createResponseDTO(mockJsonResponse);
        assertThat(getAccountDirectDebitsDTOTest).usingRecursiveComparison().isEqualTo(getAccountDirectDebitsDTOExpected);
    }

    @Test
    public void testNullInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountDirectDebitsExample.createResponseDTO(null));
    }

    @Test
    public void testNonJsonInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountDirectDebitsExample.createResponseDTO("I am not a JSON string"));
    }

    @Test
    public void testInvalidJsonInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountDirectDebitsExample.createResponseDTO("{\"Description\": \"Valid JSON but invalid input\"}"));
    }

    @AfterEach
    public void tearDown() {
        // Reset original System.out and System.err and print all outContent and errContent to console
        System.setOut(originalOut);
        System.out.println(outContent);
        System.setErr(originalErr);
        System.err.println(errContent);
    }

    private GetAccountDirectDebitsDTO getExpectedHappyPathDTO() {
        GetAccountDirectDebitsDTO getAccountDirectDebitsDTOExpected = new GetAccountDirectDebitsDTO();
        getAccountDirectDebitsDTOExpected.setData(new GetAccountDirectDebitsDTO.Data());
        getAccountDirectDebitsDTOExpected.setLinks(new GetAccountDirectDebitsDTO.Links());
        getAccountDirectDebitsDTOExpected.setMeta(new GetAccountDirectDebitsDTO.Meta());
        getAccountDirectDebitsDTOExpected.getData().setDirectDebit(new GetAccountDirectDebitsDTO.Data.DirectDebit[]{new GetAccountDirectDebitsDTO.Data.DirectDebit()});
        getAccountDirectDebitsDTOExpected.getData().getDirectDebit()[0].setAccountId("12345");
        getAccountDirectDebitsDTOExpected.getData().getDirectDebit()[0].setMandateIdentification("string");
        getAccountDirectDebitsDTOExpected.getData().getDirectDebit()[0].setDirectDebitStatusCode(GetAccountDirectDebitsDTO.Data.DirectDebit.DirectDebitStatusCode.Active);
        getAccountDirectDebitsDTOExpected.getData().getDirectDebit()[0].setName("UNKNOWN");
        getAccountDirectDebitsDTOExpected.getData().getDirectDebit()[0].setPreviousPaymentDateTime("2021-12-14T17:06:15.258Z");
        getAccountDirectDebitsDTOExpected.getData().getDirectDebit()[0].setPreviousPaymentAmount(new GetAccountDirectDebitsDTO.Data.DirectDebit.PreviousPaymentAmount());
        getAccountDirectDebitsDTOExpected.getData().getDirectDebit()[0].getPreviousPaymentAmount().setAmount("string");
        getAccountDirectDebitsDTOExpected.getData().getDirectDebit()[0].getPreviousPaymentAmount().setCurrency("GBP");
        getAccountDirectDebitsDTOExpected.getLinks().setSelf("string");
        getAccountDirectDebitsDTOExpected.getLinks().setPrev("string");
        getAccountDirectDebitsDTOExpected.getLinks().setNext("string");
        getAccountDirectDebitsDTOExpected.getMeta().setTotalPages(0);
        getAccountDirectDebitsDTOExpected.getMeta().setFirstAvailableDateTime("2021-12-14T17:06:15.258Z");
        getAccountDirectDebitsDTOExpected.getMeta().setLastAvailableDateTime("2021-12-14T17:06:15.258Z");

        return getAccountDirectDebitsDTOExpected;
    }
}