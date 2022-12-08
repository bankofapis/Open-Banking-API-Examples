package com.bankofapis.account_and_transactions;

import com.bankofapis.constant.BrandConstants;
import com.bankofapis.dto.accounts.GetAccountStandingOrdersDTO;
import com.bankofapis.dto.oauth2.OAuthResponseDTO;
import com.bankofapis.util.AuthFlowUtil;
import com.bankofapis.util.Env;
import com.bankofapis.util.MtlsHttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.bankofapis.util.GeneralUtil.GetAccountIdFromFlags;

public class GetAccountStandingOrdersExample {
    private static final Env ENV = Env.getInstance();

    public static void main(String[] args) throws Exception {
        String accountId = GetAccountIdFromFlags(args);
        OAuthResponseDTO authToken = AuthFlowUtil.loadAndRefreshAccessToken();
        String accountStandingOrders = getAccountStandingOrdersUsingToken(authToken, accountId);
        GetAccountStandingOrdersDTO getAccountStandingOrdersDTO = generateAccountStandingOrdersDTOFromResponse(accountStandingOrders);
        System.out.println(getAccountStandingOrdersDTO);
    }

    public static GetAccountStandingOrdersDTO generateAccountStandingOrdersDTOFromResponse(String accountStandingOrders) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(accountStandingOrders, GetAccountStandingOrdersDTO.class);
    }

    private static String getAccountStandingOrdersUsingToken(OAuthResponseDTO accountAccessToken, String accountId) throws Exception {

        String fapiId = BrandConstants.getConstantsForBrand(ENV.getBrand()).getFapiId();
        MtlsHttpClient client = new MtlsHttpClient(ENV.getApiBaseUrl(), ENV.getApiRequireProxy());
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + accountAccessToken.getAccessToken());
        headers.put("x-fapi-financial-id", fapiId);
        HttpResponse response = client.get("/open-banking/v3.1/aisp/accounts/" + accountId + "/standing-orders", headers);
        String responseString = EntityUtils.toString(response.getEntity());

        if (response.getStatusLine().getStatusCode() != 200){
            System.out.println("Bad server response." + response.getStatusLine().getStatusCode());
            System.out.println(responseString);
            throw new RuntimeException("Invalid server response.");
        }

        return responseString;
    }
}
