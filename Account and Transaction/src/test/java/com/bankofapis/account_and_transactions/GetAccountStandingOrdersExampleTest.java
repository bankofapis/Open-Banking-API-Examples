package com.bankofapis.account_and_transactions;

import com.bankofapis.dto.accounts.GetAccountStandingOrdersDTO;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GetAccountStandingOrdersExampleTest {
    String mockJsonResponse = "{\n" +
            "  \"Data\": {\n" +
            "    \"StandingOrder\": [\n" +
            "      {\n" +
            "        \"AccountId\": \"12345\",\n" +
            "        \"Frequency\": \"string\",\n" +
            "        \"Reference\": \"string\",\n" +
            "        \"FirstPaymentDateTime\": \"2021-12-14T16:55:22.489Z\",\n" +
            "        \"NextPaymentDateTime\": \"2021-12-14T16:55:22.489Z\",\n" +
            "        \"FinalPaymentDateTime\": \"2021-12-14T16:55:22.489Z\",\n" +
            "        \"FirstPaymentAmount\": {\n" +
            "          \"Amount\": \"string\",\n" +
            "          \"Currency\": \"string\"\n" +
            "        },\n" +
            "        \"NextPaymentAmount\": {\n" +
            "          \"Amount\": \"string\",\n" +
            "          \"Currency\": \"string\"\n" +
            "        },\n" +
            "        \"FinalPaymentAmount\": {\n" +
            "          \"Amount\": \"string\",\n" +
            "          \"Currency\": \"string\"\n" +
            "        },\n" +
            "        \"StandingOrderStatusCode\": \"Active\",\n" +
            "        \"CreditorAgent\": {\n" +
            "        \"SchemeName\": \"UK.OBIE.BICFI\",\n" +
            "        \"Identification\": \"string\"\n" +
            "        },\n" +
            "        \"CreditorAccount\": {\n" +
            "        \"SchemeName\": \"UK.OBIE.IBAN\",\n" +
            "        \"Identification\": \"string\",\n" +
            "        \"Name\": \"string\"\n" +
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
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/standing-orders").willReturn(ok(mockJsonResponse)));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        GetAccountStandingOrdersExample.main(args);
        assertThat(outContent.toString()).contains("accountId='12345'");
    }

    @Test
    public void testMainMethodFailsWithNullResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/standing-orders").willReturn(ok(null)));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountStandingOrdersExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testMainMethodFailsWithNonJsonResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/standing-orders").willReturn(ok("This is not JSON")));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountStandingOrdersExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testMainMethodFailsWithInvalidJsonResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/standing-orders").willReturn(ok("{\"Description\": \"Valid JSON but invalid input\"}")));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountStandingOrdersExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testExpectedInput() throws IOException {
        GetAccountStandingOrdersDTO getAccountStandingOrdersDTOExpected = getExpectedAccountStandingOrdersDTO();
        GetAccountStandingOrdersDTO getAccountStandingOrdersDTOTest = GetAccountStandingOrdersExample.generateAccountStandingOrdersDTOFromResponse(mockJsonResponse);
        assertThat(getAccountStandingOrdersDTOTest).usingRecursiveComparison().isEqualTo(getAccountStandingOrdersDTOExpected);
    }

    @Test
    public void testNullInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountStandingOrdersExample.generateAccountStandingOrdersDTOFromResponse(null));
    }

    @Test
    public void testNonJsonInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountStandingOrdersExample.generateAccountStandingOrdersDTOFromResponse("This is not a JSON string"));
    }

    @Test
    public void testInvalidJsonInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountStandingOrdersExample.generateAccountStandingOrdersDTOFromResponse("{\"Description\": \"Valid JSON but invalid input\""));
    }

    @AfterEach
    public void tearDown() {
        // Reset original System.out and System.err and print all outContent and errContent to console
        System.setOut(originalOut);
        System.out.println(outContent);
        System.setErr(originalErr);
        System.err.println(errContent);
    }

    private GetAccountStandingOrdersDTO getExpectedAccountStandingOrdersDTO() {
        GetAccountStandingOrdersDTO getAccountStandingOrdersDTO = new GetAccountStandingOrdersDTO();
        getAccountStandingOrdersDTO.setData(new GetAccountStandingOrdersDTO.Data());
        getAccountStandingOrdersDTO.setLinks(new GetAccountStandingOrdersDTO.Links());
        getAccountStandingOrdersDTO.setMeta(new GetAccountStandingOrdersDTO.Meta());
        getAccountStandingOrdersDTO.getData().setStandingOrders(new GetAccountStandingOrdersDTO.StandingOrder[]{new GetAccountStandingOrdersDTO.StandingOrder()});
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].setAccountId("12345");
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].setFrequency("string");
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].setReference("string");
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].setFirstPaymentDateTime("2021-12-14T16:55:22.489Z");
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].setNextPaymentDateTime("2021-12-14T16:55:22.489Z");
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].setFinalPaymentDateTime("2021-12-14T16:55:22.489Z");
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].setFirstPaymentAmount(new GetAccountStandingOrdersDTO.FirstPaymentAmount());
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].getFirstPaymentAmount().setAmount("string");
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].getFirstPaymentAmount().setCurrency("string");
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].setNextPaymentAmount(new GetAccountStandingOrdersDTO.NextPaymentAmount());
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].getNextPaymentAmount().setAmount("string");
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].getNextPaymentAmount().setCurrency("string");
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].setFinalPaymentAmount(new GetAccountStandingOrdersDTO.FinalPaymentAmount());
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].getFinalPaymentAmount().setAmount("string");
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].getFinalPaymentAmount().setCurrency("string");
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].setStandingOrderStatusCode(GetAccountStandingOrdersDTO.StandingOrder.StandingOrderStatusCodes.Active);
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].setCreditorAgent(new GetAccountStandingOrdersDTO.CreditorAgent());
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].getCreditorAgent().setSchemeName(GetAccountStandingOrdersDTO.CreditorAgent.SchemeNames.BICFI);
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].getCreditorAgent().setIdentification("string");
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].setCreditorAccount(new GetAccountStandingOrdersDTO.CreditorAccount());
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].getCreditorAccount().setSchemeName(GetAccountStandingOrdersDTO.CreditorAccount.SchemeNames.IBAN);
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].getCreditorAccount().setIdentification("string");
        getAccountStandingOrdersDTO.getData().getStandingOrders()[0].getCreditorAccount().setName("string");
        getAccountStandingOrdersDTO.getLinks().setSelf("string");
        getAccountStandingOrdersDTO.getLinks().setPrev("string");
        getAccountStandingOrdersDTO.getLinks().setNext("string");
        getAccountStandingOrdersDTO.getMeta().setTotalPages(0);

        return getAccountStandingOrdersDTO;
    }
}
