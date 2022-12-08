package com.bankofapis.account_and_transactions;

import com.bankofapis.dto.accounts.GetAccountBeneficiariesDTO;
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

class GetAccountBeneficiariesExampleTest {

    String mockJsonResponse = "{\n" +
            "  \"Data\": {\n" +
            "    \"Beneficiary\": [\n" +
            "      {\n" +
            "        \"AccountId\": \"12345\",\n" +
            "        \"BeneficiaryId\": \"string\",\n" +
            "        \"Reference\": \"string\",\n" +
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
            "    \"TotalPages\": 0,\n" +
            "    \"FirstAvailableDateTime\": \"2021-12-14T10:05:34.005Z\",\n" +
            "    \"LastAvailableDateTime\": \"2021-12-14T10:05:34.006Z\"\n" +
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
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/beneficiaries").willReturn(ok(mockJsonResponse)));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        GetAccountBeneficiariesExample.main(args);
        assertThat(outContent.toString()).contains("accountId='12345'");
    }

    @Test
    public void testMainMethodFailsWithNullResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/beneficiaries").willReturn(ok(null)));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountBeneficiariesExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testMainMethodFailsWithNonJsonResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/beneficiaries").willReturn(ok("This is not JSON")));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountBeneficiariesExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testMainMethodFailsWithInvalidJsonResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/beneficiaries").willReturn(ok("{\"Description\": \"Valid JSON but invalid input\"}")));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountBeneficiariesExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testExpectedInput() throws IOException {
        GetAccountBeneficiariesDTO getAccountBeneficiariesDTOExpected = getExpectedAccountBeneficiariesDTO();
        GetAccountBeneficiariesDTO getAccountBeneficiariesDTO = GetAccountBeneficiariesExample.createResponseDTO(mockJsonResponse);
        assertThat(getAccountBeneficiariesDTO).usingRecursiveComparison().isEqualTo(getAccountBeneficiariesDTOExpected);
    }

    @Test
    public void testNullInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountBeneficiariesExample.createResponseDTO(null));
    }

    @Test
    public void testNonJsonInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountBeneficiariesExample.createResponseDTO("This is not a JSON string"));
    }

    @Test
    public void testInvalidJsonInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountBeneficiariesExample.createResponseDTO("{\"Description\": \"Valid JSON but invalid input\""));
    }

    @AfterEach
    public void tearDown() {
        // Reset original System.out and System.err and print all outContent and errContent to console
        System.setOut(originalOut);
        System.out.println(outContent);
        System.setErr(originalErr);
        System.err.println(errContent);
    }

    private GetAccountBeneficiariesDTO getExpectedAccountBeneficiariesDTO() {
        GetAccountBeneficiariesDTO getAccountBeneficiariesDTOExpected = new GetAccountBeneficiariesDTO();
        getAccountBeneficiariesDTOExpected.setData(new GetAccountBeneficiariesDTO.Data());
        getAccountBeneficiariesDTOExpected.setLinks(new GetAccountBeneficiariesDTO.Links());
        getAccountBeneficiariesDTOExpected.setMeta(new GetAccountBeneficiariesDTO.Meta());
        getAccountBeneficiariesDTOExpected.getData().setBeneficiary(new GetAccountBeneficiariesDTO.Data.Beneficiary[]{new GetAccountBeneficiariesDTO.Data.Beneficiary()});
        getAccountBeneficiariesDTOExpected.getData().getBeneficiary()[0].setAccountId("12345");
        getAccountBeneficiariesDTOExpected.getData().getBeneficiary()[0].setBeneficiaryId("string");
        getAccountBeneficiariesDTOExpected.getData().getBeneficiary()[0].setReference("string");
        getAccountBeneficiariesDTOExpected.getData().getBeneficiary()[0].setCreditorAgent(new GetAccountBeneficiariesDTO.Data.Beneficiary.CreditorAgent());
        getAccountBeneficiariesDTOExpected.getData().getBeneficiary()[0].getCreditorAgent().setSchemeName(GetAccountBeneficiariesDTO.Data.Beneficiary.CreditorAgent.SchemeNames.BICFI);
        getAccountBeneficiariesDTOExpected.getData().getBeneficiary()[0].getCreditorAgent().setIdentification("string");
        getAccountBeneficiariesDTOExpected.getData().getBeneficiary()[0].setCreditorAccount(new GetAccountBeneficiariesDTO.Data.Beneficiary.CreditorAccount());
        getAccountBeneficiariesDTOExpected.getData().getBeneficiary()[0].getCreditorAccount().setSchemeName(GetAccountBeneficiariesDTO.Data.Beneficiary.CreditorAccount.SchemeNames.IBAN);
        getAccountBeneficiariesDTOExpected.getData().getBeneficiary()[0].getCreditorAccount().setIdentification("string");
        getAccountBeneficiariesDTOExpected.getData().getBeneficiary()[0].getCreditorAccount().setName("string");
        getAccountBeneficiariesDTOExpected.getLinks().setSelf("string");
        getAccountBeneficiariesDTOExpected.getLinks().setPrev("string");
        getAccountBeneficiariesDTOExpected.getLinks().setNext("string");
        getAccountBeneficiariesDTOExpected.getMeta().setTotalPages(0);
        getAccountBeneficiariesDTOExpected.getMeta().setFirstAvailableDateTime("2021-12-14T10:05:34.005Z");
        getAccountBeneficiariesDTOExpected.getMeta().setLastAvailableDateTime("2021-12-14T10:05:34.006Z");

        return getAccountBeneficiariesDTOExpected;
    }

}