package com.bankofapis.account_and_transactions;

import com.bankofapis.dto.accounts.GetAccountBalancesDTO;
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
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetAccountBalancesExampleTest {

    String mockJsonResponse = "{\n" +
            "  \"Data\": {\n" +
            "    \"Balance\": [\n" +
            "      {\n" +
            "        \"AccountId\": \"12345\",\n" +
            "        \"Amount\": {\n" +
            "          \"Amount\": \"string\",\n" +
            "          \"Currency\": \"string\"\n" +
            "        },\n" +
            "        \"CreditDebitIndicator\": \"Credit\",\n" +
            "        \"Type\": \"ClosingAvailable\",\n" +
            "        \"DateTime\": \"2021-12-13T16:08:08.583Z\",\n" +
            "        \"CreditLine\": [\n" +
            "          {\n" +
            "            \"Included\": true,\n" +
            "            \"Amount\": {\n" +
            "              \"Amount\": \"string\",\n" +
            "              \"Currency\": \"string\"\n" +
            "            },\n" +
            "            \"Type\": \"Pre-Agreed\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"Links\": {\n" +
            "    \"Self\": \"string\",\n" +
            "    \"Prev\": \"string\",\n" +
            "    \"Next\": \"string\"\n" +
            "  },\n" +
            "  \"Meta\": {\n" +
            "    \"TotalPages\": 0\n" +
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
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/balances").willReturn(ok(mockJsonResponse)));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        GetAccountBalancesExample.main(args);
        assertThat(outContent.toString()).contains("accountId='12345'");
    }

    @Test
    public void testMainMethodFailsWithNullResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/balances").willReturn(ok(null)));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountBalancesExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testMainMethodFailsWithNonJsonResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/balances").willReturn(ok("This is not JSON")));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountBalancesExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testMainMethodFailsWithInvalidJsonResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/balances").willReturn(ok("{\"Description\": \"Valid JSON but invalid input\"}")));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountBalancesExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testExpectedInput() throws IOException {
        GetAccountBalancesDTO getAccountBalancesDTOExpected = getExpectedAccountBalancesDTO();
        GetAccountBalancesDTO getAccountBalancesDTOTest = GetAccountBalancesExample.createResponseDTO(mockJsonResponse);
        assertThat(getAccountBalancesDTOTest).usingRecursiveComparison().isEqualTo(getAccountBalancesDTOExpected);
    }

    @Test
    public void testNullInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountBalancesExample.createResponseDTO(null));
    }

    @Test
    public void testNonJsonInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountBalancesExample.createResponseDTO("This is not a JSON string"));
    }

    @Test
    public void testInvalidJsonInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountBalancesExample.createResponseDTO("{\"Description\": \"Valid JSON but invalid input\""));
    }

    @AfterEach
    public void tearDown() {
        // Reset original System.out and System.err and print all outContent and errContent to console
        System.setOut(originalOut);
        System.out.println(outContent);
        System.setErr(originalErr);
        System.err.println(errContent);
    }

    private GetAccountBalancesDTO getExpectedAccountBalancesDTO() {
        GetAccountBalancesDTO getAccountBalancesDTOExpected = new GetAccountBalancesDTO();
        getAccountBalancesDTOExpected.setData(new GetAccountBalancesDTO.Data());
        getAccountBalancesDTOExpected.setLinks(new GetAccountBalancesDTO.Links());
        getAccountBalancesDTOExpected.setMeta(new GetAccountBalancesDTO.Meta());
        getAccountBalancesDTOExpected.getData().setBalance(new GetAccountBalancesDTO.Data.Balance[]{new GetAccountBalancesDTO.Data.Balance()});
        getAccountBalancesDTOExpected.getData().getBalance()[0].setAccountId("12345");
        getAccountBalancesDTOExpected.getData().getBalance()[0].setBalanceAmount(new GetAccountBalancesDTO.Data.Balance.Balance_Amount());
        getAccountBalancesDTOExpected.getData().getBalance()[0].getBalanceAmount().setAmount("string");
        getAccountBalancesDTOExpected.getData().getBalance()[0].getBalanceAmount().setCurrency("string");
        getAccountBalancesDTOExpected.getData().getBalance()[0].setCreditDebitIndicator(GetAccountBalancesDTO.Data.Balance.CreditDebitIndicator.Credit);
        getAccountBalancesDTOExpected.getData().getBalance()[0].setType(GetAccountBalancesDTO.Data.Balance.Type.ClosingAvailable);
        getAccountBalancesDTOExpected.getData().getBalance()[0].setDateTime("2021-12-13T16:08:08.583Z");
        getAccountBalancesDTOExpected.getData().getBalance()[0].setCreditLine(new GetAccountBalancesDTO.Data.Balance.CreditLine[]{new GetAccountBalancesDTO.Data.Balance.CreditLine()});
        getAccountBalancesDTOExpected.getData().getBalance()[0].getCreditLine()[0].setIncluded(true);
        getAccountBalancesDTOExpected.getData().getBalance()[0].getCreditLine()[0].setBalanceCreditLineAmount(new GetAccountBalancesDTO.Data.Balance.CreditLine.Balance_CreditLine_Amount());
        getAccountBalancesDTOExpected.getData().getBalance()[0].getCreditLine()[0].getBalanceCreditLineAmount().setAmount("string");
        getAccountBalancesDTOExpected.getData().getBalance()[0].getCreditLine()[0].getBalanceCreditLineAmount().setCurrency("string");
        getAccountBalancesDTOExpected.getData().getBalance()[0].getCreditLine()[0].setType(GetAccountBalancesDTO.Data.Balance.CreditLine.Type.PreAgreed);
        getAccountBalancesDTOExpected.getLinks().setSelf("string");
        getAccountBalancesDTOExpected.getLinks().setPrev("string");
        getAccountBalancesDTOExpected.getLinks().setNext("string");
        getAccountBalancesDTOExpected.getMeta().setTotalPages(0);

        return getAccountBalancesDTOExpected;
    }
}