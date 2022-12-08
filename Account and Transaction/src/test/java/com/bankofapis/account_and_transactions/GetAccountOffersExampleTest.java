package com.bankofapis.account_and_transactions;

import com.bankofapis.dto.accounts.GetAccountOffersDTO;
import com.bankofapis.util.Env;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.assertj.core.api.AssertionsForClassTypes;
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

class GetAccountOffersExampleTest {

    String mockJsonResponse = "{\n" +
            "  \"Data\": {\n" +
            "    \"Offer\": [\n" +
            "      {\n" +
            "        \"AccountId\": \"12345\",\n" +
            "        \"OfferId\": \"string\",\n" +
            "        \"Description\": \"string\",\n" +
            "        \"StartDateTime\": \"2022-01-06T10:05:15.947Z\",\n" +
            "        \"EndDateTime\": \"2022-01-06T10:05:15.947Z\",\n" +
            "        \"Rate\": \"string\",\n" +
            "        \"Term\": \"string\",\n" +
            "        \"TransferFee\": \"string\"\n" +
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
            "    \"FirstAvailableDateTime\": \"2022-01-06T10:05:15.947Z\",\n" +
            "    \"LastAvailableDateTime\": \"2022-01-06T10:05:15.947Z\"\n" +
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
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/offers").willReturn(ok(mockJsonResponse)));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        GetAccountOffersExample.main(args);
        AssertionsForClassTypes.assertThat(outContent.toString()).contains("accountId='12345'");
    }

    @Test
    public void testMainMethodFailsWithNullResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/offers").willReturn(ok(null)));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountOffersExample.main(args));
        AssertionsForClassTypes.assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        AssertionsForClassTypes.assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testMainMethodFailsWithNonJsonResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/offers").willReturn(ok("This is not JSON")));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountOffersExample.main(args));
        AssertionsForClassTypes.assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        AssertionsForClassTypes.assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testMainMethodFailsWithInvalidJsonResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/offers").willReturn(ok("{\"Description\": \"Valid JSON but invalid input\"}")));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountOffersExample.main(args));
        AssertionsForClassTypes.assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        AssertionsForClassTypes.assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testExpectedInput() throws IOException {
        GetAccountOffersDTO getAccountOffersDTOExpected = getExpectedAccountOffersDTO();
        GetAccountOffersDTO getAccountOffersDTOTest = GetAccountOffersExample.createResponseDTO(mockJsonResponse);
        assertThat(getAccountOffersDTOTest).usingRecursiveComparison().isEqualTo(getAccountOffersDTOExpected);
    }

    @Test
    public void testNullInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountOffersExample.createResponseDTO(null));
    }

    @Test
    public void testNonJsonInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountOffersExample.createResponseDTO("This is not a JSON string"));
    }

    @Test
    public void testInvalidJsonInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountOffersExample.createResponseDTO("{\"Description\": \"Valid JSON but invalid input\""));
    }

    @AfterEach
    public void tearDown() {
        // Reset original System.out and System.err and print all outContent and errContent to console
        System.setOut(originalOut);
        System.out.println(outContent);
        System.setErr(originalErr);
        System.err.println(errContent);
    }

    private GetAccountOffersDTO getExpectedAccountOffersDTO() {
        GetAccountOffersDTO getAccountOffersDTO = new GetAccountOffersDTO();
        getAccountOffersDTO.setData(new GetAccountOffersDTO.Data());
        getAccountOffersDTO.setLinks(new GetAccountOffersDTO.Links());
        getAccountOffersDTO.setMeta(new GetAccountOffersDTO.Meta());
        getAccountOffersDTO.getData().setOffer(new GetAccountOffersDTO.Data.Offer[]{new GetAccountOffersDTO.Data.Offer()});
        getAccountOffersDTO.getData().getOffer()[0].setAccountId("12345");
        getAccountOffersDTO.getData().getOffer()[0].setOfferId("string");
        getAccountOffersDTO.getData().getOffer()[0].setDescription("string");
        getAccountOffersDTO.getData().getOffer()[0].setStartDateTime("2022-01-06T10:05:15.947Z");
        getAccountOffersDTO.getData().getOffer()[0].setEndDateTime("2022-01-06T10:05:15.947Z");
        getAccountOffersDTO.getData().getOffer()[0].setRate("string");
        getAccountOffersDTO.getData().getOffer()[0].setTerm("string");
        getAccountOffersDTO.getData().getOffer()[0].setTransferFee("string");
        getAccountOffersDTO.getLinks().setSelf("string");
        getAccountOffersDTO.getLinks().setPrev("string");
        getAccountOffersDTO.getLinks().setNext("string");
        getAccountOffersDTO.getMeta().setTotalPages(0);
        getAccountOffersDTO.getMeta().setFirstAvailableDateTime("2022-01-06T10:05:15.947Z");
        getAccountOffersDTO.getMeta().setLastAvailableDateTime("2022-01-06T10:05:15.947Z");

        return getAccountOffersDTO;
    }
}