package com.bankofapis.account_and_transactions;


import com.bankofapis.constant.BrandConstants;
import com.bankofapis.dto.accounts.GetAccountScheduledPaymentsDTO;
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

public class GetAccountScheduledPaymentsExample {
    private static final Env ENV = Env.getInstance();

    public static void main(String[] args) throws Exception {
        String accountId = GetAccountIdFromFlags(args);
        String accountAccessToken = getAccountAccessToken();
        String scheduledPaymentsResponse = getAccountScheduledPaymentsUsingToken(accountAccessToken, accountId);
        GetAccountScheduledPaymentsDTO accountScheduledPaymentsData = createResponseDTO(scheduledPaymentsResponse);
        System.out.println(accountScheduledPaymentsData);
    }

    public static GetAccountScheduledPaymentsDTO createResponseDTO(String responseString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseString, GetAccountScheduledPaymentsDTO.class);
    }

    private static String getAccountScheduledPaymentsUsingToken(String accountAccessToken, String accountId) throws Exception {
        String fapiId = BrandConstants.getConstantsForBrand(ENV.getBrand()).getFapiId();
        MtlsHttpClient client = new MtlsHttpClient(ENV.getApiBaseUrl(), ENV.getApiRequireProxy());
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + accountAccessToken);
        headers.put("x-fapi-financial-id", fapiId);

        HttpResponse response = client.get("/open-banking/v3.1/aisp/accounts/" + accountId + "/scheduled-payments", headers);
        return EntityUtils.toString(response.getEntity());
    }

    public static String getAccountAccessToken() throws Exception {
        return AuthFlowUtil.loadAndRefreshAccessToken().getAccessToken();
    }
}
