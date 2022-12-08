package com.bankofapis.account_and_transactions;

import com.bankofapis.dto.accounts.GetAccountTransactionsDTO;
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

public class GetAccountTransactionsExamplesTest {

    String mockJsonResponse = "{\n" +
            "  \"Data\": {\n" +
            "    \"Transaction\": [\n" +
            "      {\n" +
            "        \"AccountId\": \"12345\",\n" +
            "        \"TransactionId\": \"string\",\n" +
            "        \"Amount\": {\n" +
            "          \"Amount\": \"string\",\n" +
            "          \"Currency\": \"string\"\n" +
            "        },\n" +
            "        \"CreditDebitIndicator\": \"Credit\",\n" +
            "        \"Status\": \"Booked\",\n" +
            "        \"BookingDateTime\": \"2021-12-15T12:21:56.679Z\",\n" +
            "        \"ValueDateTime\": \"2021-12-15T12:21:56.679Z\",\n" +
            "        \"TransactionInformation\": \"string\",\n" +
            "        \"AddressLine\": \"string\",\n" +
            "        \"BankTransactionCode\": {\n" +
            "          \"Code\": \"string\",\n" +
            "          \"SubCode\": \"string\"\n" +
            "        },\n" +
            "        \"ProprietaryBankTransactionCode\": {\n" +
            "          \"Code\": \"string\",\n" +
            "          \"Issuer\": \"string\"\n" +
            "        },\n" +
            "        \"Balance\": {\n" +
            "          \"Amount\": {\n" +
            "            \"Amount\": \"string\",\n" +
            "            \"Currency\": \"string\"\n" +
            "          },\n" +
            "          \"CreditDebitIndicator\": \"Credit\",\n" +
            "          \"Type\": \"ClosingAvailable\"\n" +
            "        },\n" +
            "        \"MerchantDetails\": {\n" +
            "          \"MerchantName\": \"string\",\n" +
            "          \"MerchantCategoryCode\": \"string\"\n" +
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
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/transactions").willReturn(ok(mockJsonResponse)));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        GetAccountTransactionsExample.main(args);
        assertThat(outContent.toString()).contains("accountId='12345'");
    }

    @Test
    public void testMainMethodFailsWithNullResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/transactions").willReturn(ok(null)));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountTransactionsExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testMainMethodFailsWithNonJsonResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/transactions").willReturn(ok("This is not JSON")));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountTransactionsExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }

    @Test
    public void testMainMethodFailsWithInvalidJsonResponse() {
        stubFor(post("/as/token.oauth2").withRequestBody(containing("grant_type=refresh_token")).willReturn(ok("{\"access_token\": \"test_access_token\", \"expires_in\": \"599\"}")));
        stubFor(get("/open-banking/v3.1/aisp/accounts/test/transactions").willReturn(ok("{\"Description\": \"Valid JSON but invalid input\"}")));
        String[] args = new String[2];
        args[0] = "-accountId";
        args[1] = "test";
        assertThrows(Exception.class, () -> GetAccountTransactionsExample.main(args));
        assertThat(outContent.toString()).doesNotContain("accountId='12345'");
        assertThat(outContent.toString()).contains("Server Response Code: 200");
    }
    
    @Test
    public void testExpectedInput() throws IOException {
        GetAccountTransactionsDTO getAccountTransactionsDTOExpected = getExpectedAccountTransactionsDTO();
        GetAccountTransactionsDTO getAccountTransactionsDTOTest = GetAccountTransactionsExample.generateAccountTransactionsDTOFromResponse(mockJsonResponse);
        assertThat(getAccountTransactionsDTOTest).usingRecursiveComparison().isEqualTo(getAccountTransactionsDTOExpected);
    }

    @Test
    public void testNullInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountTransactionsExample.generateAccountTransactionsDTOFromResponse(null));
    }

    @Test
    public void testNonJsonInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountTransactionsExample.generateAccountTransactionsDTOFromResponse("This is not a JSON string"));
    }

    @Test
    public void testInvalidJsonInputThrowsError() {
        assertThrows(Exception.class, () -> GetAccountTransactionsExample.generateAccountTransactionsDTOFromResponse("{\"Description\": \"Valid JSON but invalid input\""));
    }

    @AfterEach
    public void tearDown() {
        // Reset original System.out and System.err and print all outContent and errContent to console
        System.setOut(originalOut);
        System.out.println(outContent);
        System.setErr(originalErr);
        System.err.println(errContent);
    }

    private GetAccountTransactionsDTO getExpectedAccountTransactionsDTO() {
        GetAccountTransactionsDTO getAccountTransactionsDTO = new GetAccountTransactionsDTO();
        getAccountTransactionsDTO.setData(new GetAccountTransactionsDTO.Data());
        getAccountTransactionsDTO.setLinks(new GetAccountTransactionsDTO.Links());
        getAccountTransactionsDTO.setMeta(new GetAccountTransactionsDTO.Meta());
        getAccountTransactionsDTO.getData().setTransactions(new GetAccountTransactionsDTO.Transaction[]{new GetAccountTransactionsDTO.Transaction()});
        getAccountTransactionsDTO.getData().getTransactions()[0].setAccountId("12345");
        getAccountTransactionsDTO.getData().getTransactions()[0].setTransactionId("string");
        getAccountTransactionsDTO.getData().getTransactions()[0].setAmount(new GetAccountTransactionsDTO.Amount());
        getAccountTransactionsDTO.getData().getTransactions()[0].getAmount().setAmount("string");
        getAccountTransactionsDTO.getData().getTransactions()[0].getAmount().setCurrency("string");
        getAccountTransactionsDTO.getData().getTransactions()[0].setCreditDebitIndicator(GetAccountTransactionsDTO.Transaction.CreditDebitIndicators.Credit);
        getAccountTransactionsDTO.getData().getTransactions()[0].setStatus(GetAccountTransactionsDTO.Transaction.Statuses.Booked);
        getAccountTransactionsDTO.getData().getTransactions()[0].setBookingDateTime("2021-12-15T12:21:56.679Z");
        getAccountTransactionsDTO.getData().getTransactions()[0].setValueDateTime("2021-12-15T12:21:56.679Z");
        getAccountTransactionsDTO.getData().getTransactions()[0].setTransactionInformation("string");
        getAccountTransactionsDTO.getData().getTransactions()[0].setAddressLine("string");
        getAccountTransactionsDTO.getData().getTransactions()[0].setBankTransactionCode(new GetAccountTransactionsDTO.BankTransactionCode());
        getAccountTransactionsDTO.getData().getTransactions()[0].getBankTransactionCode().setCode("string");
        getAccountTransactionsDTO.getData().getTransactions()[0].getBankTransactionCode().setSubCode("string");
        getAccountTransactionsDTO.getData().getTransactions()[0].setProprietaryBankTransactionCode(new GetAccountTransactionsDTO.ProprietaryBankTransactionCode());
        getAccountTransactionsDTO.getData().getTransactions()[0].getProprietaryBankTransactionCode().setCode("string");
        getAccountTransactionsDTO.getData().getTransactions()[0].getProprietaryBankTransactionCode().setIssuer("string");
        getAccountTransactionsDTO.getData().getTransactions()[0].setBalance(new GetAccountTransactionsDTO.Balance());
        getAccountTransactionsDTO.getData().getTransactions()[0].getBalance().setAmount(new GetAccountTransactionsDTO.Amount());
        getAccountTransactionsDTO.getData().getTransactions()[0].getBalance().getAmount().setAmount("string");
        getAccountTransactionsDTO.getData().getTransactions()[0].getBalance().getAmount().setCurrency("string");
        getAccountTransactionsDTO.getData().getTransactions()[0].getBalance().setCreditDebitIndicator(GetAccountTransactionsDTO.Balance.CreditDebitIndicators.Credit);
        getAccountTransactionsDTO.getData().getTransactions()[0].getBalance().setType(GetAccountTransactionsDTO.Balance.Types.ClosingAvailable);
        getAccountTransactionsDTO.getData().getTransactions()[0].setMerchantDetails(new GetAccountTransactionsDTO.MerchantDetails());
        getAccountTransactionsDTO.getData().getTransactions()[0].getMerchantDetails().setMerchantName("string");
        getAccountTransactionsDTO.getData().getTransactions()[0].getMerchantDetails().setMerchantCategoryCode("string");
        getAccountTransactionsDTO.getLinks().setSelf("string");
        getAccountTransactionsDTO.getLinks().setPrev("string");
        getAccountTransactionsDTO.getLinks().setNext("string");
        getAccountTransactionsDTO.getMeta().setTotalPages(0);

        return getAccountTransactionsDTO;
    }
}
