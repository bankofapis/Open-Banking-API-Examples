package com.bankofapis.account_and_transactions;

import com.bankofapis.constant.BrandConstants;
import com.bankofapis.dto.accounts.GetAccountProductDTO;
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

public class GetAccountProductExample {
    private static final Env ENV = Env.getInstance();

    public static void main(String[] args) throws Exception {
        String accountId = GetAccountIdFromFlags(args);
        OAuthResponseDTO authToken = AuthFlowUtil.loadAndRefreshAccessToken();
        String accountProduct = getAccountProductUsingToken(authToken, accountId);
        GetAccountProductDTO getAccountProductDTO = generateAccountProductDTOFromResponse(accountProduct);
        System.out.println(getAccountProductDTO);
    }

    public static GetAccountProductDTO generateAccountProductDTOFromResponse(String accountProduct) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(accountProduct, GetAccountProductDTO.class);
    }

    private static String getAccountProductUsingToken(OAuthResponseDTO accountAccessToken, String accountId) throws Exception {

        String fapiId = BrandConstants.getConstantsForBrand(ENV.getBrand()).getFapiId();
        MtlsHttpClient client = new MtlsHttpClient(ENV.getApiBaseUrl(), ENV.getApiRequireProxy());
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + accountAccessToken.getAccessToken());
        headers.put("x-fapi-financial-id", fapiId);
        HttpResponse response = client.get("/open-banking/v3.1/aisp/accounts/" + accountId + "/product", headers);
        String responseString = EntityUtils.toString(response.getEntity());

        if (response.getStatusLine().getStatusCode() != 200){
            System.out.println("Bad server response." + response.getStatusLine().getStatusCode());
            System.out.println(responseString);
            throw new RuntimeException("Invalid server response.");
        }

        return responseString;
    }
}
