package com.bankofapis.account_and_transactions;

import com.bankofapis.dto.accounts.GetAccountDTO;
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

public class GetAccountExampleTest {

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
        stubFor(get("/open-banking/v3.1/aisp/accounts/test").willReturn(ok(mockJsonResponse)));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        GetAccountExample.main(args);
        assertThat(outContent.toString()).contains("accountId='12345'");
    }

    @Test
    public void testMainMethodFailsWithNullResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test").willReturn(ok(null)));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testMainMethodFailsWithNonJsonResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test").willReturn(ok("This is not JSON")));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testMainMethodFailsWithInvalidJsonResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test").willReturn(ok("{\"Description\": \"Valid JSON but invalid input\"}")));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }
    
    @Test
    public void testExpectedInput() throws IOException {
        GetAccountDTO getAccountDTOExpected = getExpectedHappyPathDTO();

        GetAccountDTO getAccountDTOTest = GetAccountExample.createResponseDTO(mockJsonResponse);
        assertThat(getAccountDTOTest).usingRecursiveComparison().isEqualTo(getAccountDTOExpected);
    }

    @Test
    public void testNullInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountExample.createResponseDTO(null));
    }

    @Test
    public void testNonJsonInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountExample.createResponseDTO("I am not a JSON string"));
    }

    @Test
    public void testInvalidJsonInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountExample.createResponseDTO("{\"Description\": \"Valid JSON but invalid input\"}"));
    }

    @AfterEach
    public void tearDown() {
        // Reset original System.out and System.err and print all outContent and errContent to console
        System.setOut(originalOut);
        System.out.println(outContent);
        System.setErr(originalErr);
        System.err.println(errContent);
    }

    private GetAccountDTO getExpectedHappyPathDTO() {
        GetAccountDTO getAccountDTOExpected = new GetAccountDTO();
        getAccountDTOExpected.setData(new GetAccountDTO.Data());
        getAccountDTOExpected.setLinks(new GetAccountDTO.Links());
        getAccountDTOExpected.setMeta(new GetAccountDTO.Meta());
        GetAccountDTO.Data_Account[] accounts = new GetAccountDTO.Data_Account[1];
        GetAccountDTO.Data_Account_Account[] accountAccounts = new GetAccountDTO.Data_Account_Account[1];
        accounts[0] = new GetAccountDTO.Data_Account();
        accountAccounts[0] = new GetAccountDTO.Data_Account_Account();
        getAccountDTOExpected.getData().setAccounts(accounts);
        getAccountDTOExpected.getData().getAccounts()[0].setAccountId("12345");
        getAccountDTOExpected.getData().getAccounts()[0].setCurrency("GBP");
        getAccountDTOExpected.getData().getAccounts()[0].setAccountType(GetAccountDTO.Data_Account.AccountTypes.Business);
        getAccountDTOExpected.getData().getAccounts()[0].setAccountSubType(GetAccountDTO.Data_Account.AccountSubTypes.ChargeCard);
        getAccountDTOExpected.getData().getAccounts()[0].setDescription("string");
        getAccountDTOExpected.getData().getAccounts()[0].setNickname("string");
        getAccountDTOExpected.getData().getAccounts()[0].setAccount(accountAccounts);
        getAccountDTOExpected.getData().getAccounts()[0].getAccount()[0].setSchemeName(GetAccountDTO.Data_Account_Account.SchemeNames.IBAN);
        getAccountDTOExpected.getData().getAccounts()[0].getAccount()[0].setIdentification("string");
        getAccountDTOExpected.getData().getAccounts()[0].getAccount()[0].setSecondaryIdentification("string");
        getAccountDTOExpected.getData().getAccounts()[0].getAccount()[0].setName("string");
        getAccountDTOExpected.getData().getAccounts()[0].setServicer(new GetAccountDTO.Servicer());
        getAccountDTOExpected.getData().getAccounts()[0].getServicer().setSchemeName(GetAccountDTO.Servicer.SchemeNames.BICFI);
        getAccountDTOExpected.getData().getAccounts()[0].getServicer().setIdentification("string");
        getAccountDTOExpected.getData().getAccounts()[0].setSwitchStatus(GetAccountDTO.Data_Account.SwitchStatus.NotSwitched);
        getAccountDTOExpected.getLinks().setSelf("string");
        getAccountDTOExpected.getLinks().setPrev("string");
        getAccountDTOExpected.getLinks().setNext("string");
        getAccountDTOExpected.getMeta().setTotalPages(0);
        return getAccountDTOExpected;
    }
}
