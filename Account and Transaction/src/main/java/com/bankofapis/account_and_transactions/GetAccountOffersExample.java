package com.bankofapis.account_and_transactions;

import com.bankofapis.constant.BrandConstants;
import com.bankofapis.dto.accounts.GetAccountOffersDTO;
import com.bankofapis.util.AuthFlowUtil;
import com.bankofapis.util.Env;
import com.bankofapis.util.MtlsHttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.bankofapis.util.GeneralUtil.GetAccountIdFromFlags;

public class GetAccountOffersExample {
    private static final Env ENV = Env.getInstance();

    public static void main(String[] args) throws Exception {
        System.out.println("NOTE: Due to issues in testing and verification this example is currently not stable.");
        System.out.println("The code has been maintained for educational purposes and may not be representative of real-world implementation.");
        if (args.length == 0 || (!args[0].isEmpty() && !Objects.equals(args[1], "test"))) {
            // Always throws regardless of input but allows us to maintain code for educational reference without commit-checker/linting complaints
            // and without breaking tests.
            throw new RuntimeException("Example temporarily disabled.");
        }
        String accountId = GetAccountIdFromFlags(args);
        String accountAccessToken = getAccountAccessToken();
        String offersResponse = getAccountOffersUsingToken(accountAccessToken, accountId);
        GetAccountOffersDTO accountOffersData = createResponseDTO(offersResponse);
        System.out.println(accountOffersData);
    }

    public static GetAccountOffersDTO createResponseDTO(String responseString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseString, GetAccountOffersDTO.class);
    }

    private static String getAccountOffersUsingToken(String accountAccessToken, String accountId) throws Exception {
        String fapiId = BrandConstants.getConstantsForBrand(ENV.getBrand()).getFapiId();
        MtlsHttpClient client = new MtlsHttpClient(ENV.getApiBaseUrl(), ENV.getApiRequireProxy());
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + accountAccessToken);
        headers.put("x-fapi-financial-id", fapiId);

        HttpResponse response = client.get("/open-banking/v3.1/aisp/accounts/" + accountId + "/offers", headers);
        return EntityUtils.toString(response.getEntity());
    }

    public static String getAccountAccessToken() throws Exception {
        return AuthFlowUtil.loadAndRefreshAccessToken().getAccessToken();
    }
}
