package com.bankofapis.account_and_transactions;

import com.bankofapis.dto.accounts.GetAccountScheduledPaymentsDTO;
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

public class GetAccountScheduledPaymentsExampleTest {

    String mockJsonResponse = "{\n" +
            "  \"Data\": {\n" +
            "    \"ScheduledPayment\": [\n" +
            "      {\n" +
            "        \"AccountId\": \"12345\",\n" +
            "        \"ScheduledPaymentDateTime\": \"2022-01-04T15:54:35.408Z\",\n" +
            "        \"ScheduledType\": \"Arrival\",\n" +
            "        \"Reference\": \"string\",\n" +
            "        \"InstructedAmount\": {\n" +
            "          \"Amount\": \"string\",\n" +
            "          \"Currency\": \"GBP\"\n" +
            "        },\n" +
            "        \"CreditorAgent\": {\n" +
            "          \"SchemeName\": \"UK.OBIE.BICFI\",\n" +
            "          \"Identification\": \"string\"\n" +
            "        },\n" +
            "        \"CreditorAccount\": {\n" +
            "          \"SchemeName\": \"UK.OBIE.IBAN\",\n" +
            "          \"Identification\": \"string\",\n" +
            "          \"Name\": \"string\"\n" +
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
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/scheduled-payments").willReturn(ok(mockJsonResponse)));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        GetAccountScheduledPaymentsExample.main(args);
        assertThat(outContent.toString()).contains("accountId='12345'");
    }

    @Test
    public void testMainMethodFailsWithNullResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/scheduled-payments").willReturn(ok(null)));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountScheduledPaymentsExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testMainMethodFailsWithNonJsonResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/scheduled-payments").willReturn(ok("This is not JSON")));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountScheduledPaymentsExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testMainMethodFailsWithInvalidJsonResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/scheduled-payments").willReturn(ok("{\"Description\": \"Valid JSON but invalid input\"}")));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountScheduledPaymentsExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testExpectedInput() throws IOException {
        GetAccountScheduledPaymentsDTO getAccountScheduledPaymentsDTOExpected = getExpectedAccountScheduledPaymentsDTO();
        GetAccountScheduledPaymentsDTO getAccountScheduledPaymentsDTOTest = GetAccountScheduledPaymentsExample.createResponseDTO(mockJsonResponse);
        assertThat(getAccountScheduledPaymentsDTOTest).usingRecursiveComparison().isEqualTo(getAccountScheduledPaymentsDTOExpected);
    }

    @Test
    public void testNullInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountScheduledPaymentsExample.createResponseDTO(null));
    }

    @Test
    public void testNonJsonInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountScheduledPaymentsExample.createResponseDTO("This is not a JSON string"));
    }

    @Test
    public void testInvalidJsonInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountScheduledPaymentsExample.createResponseDTO("{\"Description\": \"Valid JSON but invalid input\""));
    }

    @AfterEach
    public void tearDown() {
        // Reset original System.out and System.err and print all outContent and errContent to console
        System.setOut(originalOut);
        System.out.println(outContent);
        System.setErr(originalErr);
        System.err.println(errContent);
    }

    private GetAccountScheduledPaymentsDTO getExpectedAccountScheduledPaymentsDTO() {
        GetAccountScheduledPaymentsDTO getAccountScheduledPaymentsDTO = new GetAccountScheduledPaymentsDTO();
        getAccountScheduledPaymentsDTO.setData(new GetAccountScheduledPaymentsDTO.Data());
        getAccountScheduledPaymentsDTO.setLinks(new GetAccountScheduledPaymentsDTO.Links());
        getAccountScheduledPaymentsDTO.setMeta(new GetAccountScheduledPaymentsDTO.Meta());
        getAccountScheduledPaymentsDTO.getData().setScheduledPayment(new GetAccountScheduledPaymentsDTO.Data.ScheduledPayment[]{new GetAccountScheduledPaymentsDTO.Data.ScheduledPayment()});
        getAccountScheduledPaymentsDTO.getData().getScheduledPayment()[0].setAccountId("12345");
        getAccountScheduledPaymentsDTO.getData().getScheduledPayment()[0].setScheduledPaymentDateTime("2022-01-04T15:54:35.408Z");
        getAccountScheduledPaymentsDTO.getData().getScheduledPayment()[0].setScheduledType(GetAccountScheduledPaymentsDTO.Data.ScheduledPayment.ScheduledType.Arrival);
        getAccountScheduledPaymentsDTO.getData().getScheduledPayment()[0].setReference("string");
        getAccountScheduledPaymentsDTO.getData().getScheduledPayment()[0].setInstructedAmount(new GetAccountScheduledPaymentsDTO.Data.ScheduledPayment.InstructedAmount());
        getAccountScheduledPaymentsDTO.getData().getScheduledPayment()[0].getInstructedAmount().setAmount("string");
        getAccountScheduledPaymentsDTO.getData().getScheduledPayment()[0].getInstructedAmount().setCurrency("GBP");
        getAccountScheduledPaymentsDTO.getData().getScheduledPayment()[0].setCreditorAgent(new GetAccountScheduledPaymentsDTO.Data.ScheduledPayment.CreditorAgent());
        getAccountScheduledPaymentsDTO.getData().getScheduledPayment()[0].getCreditorAgent().setSchemeName(GetAccountScheduledPaymentsDTO.Data.ScheduledPayment.CreditorAgent.SchemeName.BICFI);
        getAccountScheduledPaymentsDTO.getData().getScheduledPayment()[0].getCreditorAgent().setIdentification("string");
        getAccountScheduledPaymentsDTO.getData().getScheduledPayment()[0].setCreditorAccount(new GetAccountScheduledPaymentsDTO.Data.ScheduledPayment.CreditorAccount());
        getAccountScheduledPaymentsDTO.getData().getScheduledPayment()[0].getCreditorAccount().setSchemeName(GetAccountScheduledPaymentsDTO.Data.ScheduledPayment.CreditorAccount.SchemeName.IBAN);
        getAccountScheduledPaymentsDTO.getData().getScheduledPayment()[0].getCreditorAccount().setIdentification("string");
        getAccountScheduledPaymentsDTO.getData().getScheduledPayment()[0].getCreditorAccount().setName("string");
        getAccountScheduledPaymentsDTO.getLinks().setSelf("string");
        getAccountScheduledPaymentsDTO.getLinks().setPrev("string");
        getAccountScheduledPaymentsDTO.getLinks().setNext("string");
        getAccountScheduledPaymentsDTO.getMeta().setTotalPages(0);

        return getAccountScheduledPaymentsDTO;
    }
}
