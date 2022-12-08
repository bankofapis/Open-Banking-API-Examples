package com.bankofapis.account_and_transactions;

import com.bankofapis.dto.accounts.GetAccountsDTO;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GetAccountsExampleTest {

    String mockJsonResponse = "{\n" +
            "  \"Data\": {\n" +
            "    \"Account\": [\n" +
            "      {\n" +
            "        \"AccountId\": \"12345\",\n" +
            "        \"Currency\": \"GBP\",\n" +
            "        \"AccountType\": \"Business\",\n" +
            "        \"AccountSubType\": \"ChargeCard\",\n" +
            "        \"Description\": \"string\",\n" +
            "        \"Nickname\": \"string\",\n" +
            "        \"Account\": [{\n" +
            "          \"SchemeName\": \"UK.OBIE.IBAN\",\n" +
            "          \"Identification\": \"string\",\n" +
            "          \"SecondaryIdentification\": \"string\",\n" +
            "          \"Name\": \"string\"\n" +
            "        }],\n" +
            "        \"Servicer\": {\n" +
            "          \"SchemeName\": \"UK.OBIE.BICFI\",\n" +
            "          \"Identification\": \"string\"\n" +
            "        },\n" +
            "        \"SwitchStatus\": \"UK.CASS.NotSwitched\"\n" +
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
        stubFor(get("/open-banking/v3.1/aisp/accounts/").willReturn(ok(mockJsonResponse)));
        GetAccountsExample.main(null);
        assertThat(outContent.toString()).contains("accountId='12345'");
    }

    @Test
    public void testMainMethodFailsWithNullResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/").willReturn(ok(null)));
        assertThrows(Exception.class, () -> GetAccountsExample.main(null));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testMainMethodFailsWithNonJsonResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/").willReturn(ok("This is not JSON")));
        assertThrows(Exception.class, () -> GetAccountsExample.main(null));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testMainMethodFailsWithInvalidJsonResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/").willReturn(ok("{\"Description\": \"Valid JSON but invalid input\"}")));
        assertThrows(Exception.class, () -> GetAccountsExample.main(null));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testExpectedInput() throws IOException {
        GetAccountsDTO getAccountsDTOExpected = getExpectedHappyPathDTO();

        GetAccountsDTO getAccountsDTOTest = GetAccountsExample.createResponseDTO(mockJsonResponse);
        assertThat(getAccountsDTOTest).usingRecursiveComparison().isEqualTo(getAccountsDTOExpected);
    }

    @Test
    public void testNullInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountsExample.createResponseDTO(null));
    }

    @Test
    public void testNonJsonInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountsExample.createResponseDTO("I am not a JSON string"));
    }

    @Test
    public void testInvalidJsonInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountsExample.createResponseDTO("{\"Description\": \"Valid JSON but invalid input\"}"));
    }

    @AfterEach
    public void tearDown() {
        // Reset original System.out and System.err and print all outContent and errContent to console
        System.setOut(originalOut);
        System.out.println(outContent);
        System.setErr(originalErr);
        System.err.println(errContent);
    }

    private GetAccountsDTO getExpectedHappyPathDTO() {
        GetAccountsDTO getAccountsDTOExpected = new GetAccountsDTO();
        getAccountsDTOExpected.setData(new GetAccountsDTO.Data());
        getAccountsDTOExpected.setLinks(new GetAccountsDTO.Links());
        getAccountsDTOExpected.setMeta(new GetAccountsDTO.Meta());
        GetAccountsDTO.Data_Account[] accounts = new GetAccountsDTO.Data_Account[1];
        GetAccountsDTO.Data_Account_Account[] accountAccounts = new GetAccountsDTO.Data_Account_Account[1];
        accounts[0] = new GetAccountsDTO.Data_Account();
        accountAccounts[0] = new GetAccountsDTO.Data_Account_Account();
        getAccountsDTOExpected.getData().setAccounts(accounts);
        getAccountsDTOExpected.getData().getAccounts()[0].setAccountId("12345");
        getAccountsDTOExpected.getData().getAccounts()[0].setCurrency("GBP");
        getAccountsDTOExpected.getData().getAccounts()[0].setAccountType(GetAccountsDTO.Data_Account.AccountTypes.Business);
        getAccountsDTOExpected.getData().getAccounts()[0].setAccountSubType(GetAccountsDTO.Data_Account.AccountSubTypes.ChargeCard);
        getAccountsDTOExpected.getData().getAccounts()[0].setDescription("string");
        getAccountsDTOExpected.getData().getAccounts()[0].setNickname("string");
        getAccountsDTOExpected.getData().getAccounts()[0].setAccount(accountAccounts);
        getAccountsDTOExpected.getData().getAccounts()[0].getAccount()[0].setSchemeName(GetAccountsDTO.Data_Account_Account.SchemeNames.IBAN);
        getAccountsDTOExpected.getData().getAccounts()[0].getAccount()[0].setIdentification("string");
        getAccountsDTOExpected.getData().getAccounts()[0].getAccount()[0].setSecondaryIdentification("string");
        getAccountsDTOExpected.getData().getAccounts()[0].getAccount()[0].setName("string");
        getAccountsDTOExpected.getData().getAccounts()[0].setServicer(new GetAccountsDTO.Servicer());
        getAccountsDTOExpected.getData().getAccounts()[0].getServicer().setSchemeName(GetAccountsDTO.Servicer.SchemeNames.BICFI);
        getAccountsDTOExpected.getData().getAccounts()[0].getServicer().setIdentification("string");
        getAccountsDTOExpected.getData().getAccounts()[0].setSwitchStatus(GetAccountsDTO.Data_Account.SwitchStatus.NotSwitched);
        getAccountsDTOExpected.getLinks().setSelf("string");
        getAccountsDTOExpected.getLinks().setPrev("string");
        getAccountsDTOExpected.getLinks().setNext("string");
        getAccountsDTOExpected.getMeta().setTotalPages(0);
        return getAccountsDTOExpected;
    }
}
