package com.bankofapis.account_and_transactions;

import com.bankofapis.dto.accounts.GetAccountProductDTO;
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
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GetAccountProductExampleTest {

    String mockJsonResponse = "{\n" +
            "  \"Data\": {\n" +
            "    \"Product\": [\n" +
            "      {\n" +
            "        \"AccountId\": \"12345\",\n" +
            "        \"ProductId\": \"string\",\n" +
            "        \"ProductType\": \"PersonalCurrentAccount\",\n" +
            "        \"MarketingStateId\": \"string\",\n" +
            "        \"SecondaryProductId\": \"string\",\n" +
            "        \"OtherProductType\": {\n" +
            "          \"Name\": \"string\",\n" +
            "          \"Description\": \"string\"\n" +
            "        },\n" +
            "        \"ProductName\": \"string\"\n" +
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
            "    \"FirstAvailableDateTime\": \"2021-12-16T14:16:38.914Z\",\n" +
            "    \"LastAvailableDateTime\": \"2021-12-16T14:16:38.914Z\"\n" +
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
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/product").willReturn(ok(mockJsonResponse)));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        GetAccountProductExample.main(args);
        assertThat(outContent.toString()).contains("accountId='12345'");
    }

    @Test
    public void testMainMethodFailsWithNullResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/product").willReturn(ok(null)));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountProductExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testMainMethodFailsWithNonJsonResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/product").willReturn(ok("This is not JSON")));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountProductExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testMainMethodFailsWithInvalidJsonResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/product").willReturn(ok("{\"Description\": \"Valid JSON but invalid input\"}")));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountProductExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testExpectedInput() throws IOException {
        GetAccountProductDTO getAccountProductDTOExpected = getExpectedAccountProductDTO();
        GetAccountProductDTO getAccountProductDTOTest = GetAccountProductExample.generateAccountProductDTOFromResponse(mockJsonResponse);
        assertThat(getAccountProductDTOTest).usingRecursiveComparison().isEqualTo(getAccountProductDTOExpected);
    }

    @Test
    public void testNullInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountProductExample.generateAccountProductDTOFromResponse(null));
    }

    @Test
    public void testNonJsonInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountProductExample.generateAccountProductDTOFromResponse("This is not a JSON string"));
    }

    @Test
    public void testInvalidJsonInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountProductExample.generateAccountProductDTOFromResponse("{\"Description\": \"Valid JSON but invalid input\""));
    }

    @AfterEach
    public void tearDown() {
        // Reset original System.out and System.err and print all outContent and errContent to console
        System.setOut(originalOut);
        System.out.println(outContent);
        System.setErr(originalErr);
        System.err.println(errContent);
    }

    private GetAccountProductDTO getExpectedAccountProductDTO() {
        GetAccountProductDTO getAccountProductDTO = new GetAccountProductDTO();
        getAccountProductDTO.setData(new GetAccountProductDTO.Data());
        getAccountProductDTO.setLinks(new GetAccountProductDTO.Links());
        getAccountProductDTO.setMeta(new GetAccountProductDTO.Meta());
        getAccountProductDTO.getData().setProduct(new GetAccountProductDTO.Product[]{new GetAccountProductDTO.Product()});
        getAccountProductDTO.getData().getProduct()[0].setAccountId("12345");
        getAccountProductDTO.getData().getProduct()[0].setProductId("string");
        getAccountProductDTO.getData().getProduct()[0].setProductType(GetAccountProductDTO.Product.ProductType.PersonalCurrentAccount);
        getAccountProductDTO.getData().getProduct()[0].setMarketingStateId("string");
        getAccountProductDTO.getData().getProduct()[0].setSecondaryProductId("string");
        getAccountProductDTO.getData().getProduct()[0].setOtherProductType(new GetAccountProductDTO.OtherProductType());
        getAccountProductDTO.getData().getProduct()[0].getOtherProductType().setName("string");
        getAccountProductDTO.getData().getProduct()[0].getOtherProductType().setDescription("string");
        getAccountProductDTO.getData().getProduct()[0].setProductName("string");
        getAccountProductDTO.getLinks().setSelf("string");
        getAccountProductDTO.getLinks().setPrev("string");
        getAccountProductDTO.getLinks().setNext("string");
        getAccountProductDTO.getMeta().setTotalPages(0);
        getAccountProductDTO.getMeta().setFirstAvailableDateTime("2021-12-16T14:16:38.914Z");
        getAccountProductDTO.getMeta().setLastAvailableDateTime("2021-12-16T14:16:38.914Z");

        return getAccountProductDTO;
    }
}
