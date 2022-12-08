package com.bankofapis.dynamic_client_registration;

import com.bankofapis.util.Env;
import com.bankofapis.util.JSONWebTokenUtils;
import com.bankofapis.util.MtlsHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicRegistrationExample {
    private static final Env ENV = Env.getInstance();

    public static void main(String[] args) throws Exception {
        String sJWT = JSONWebTokenUtils.generateSignedJWTForDynamicRegistrationPostCall().compact();
        System.out.println(sJWT);

        // Perform Dynamic Client Registration call
        HttpEntity dynamicClientRegistrationResponse = performDynamicClientRegistration(sJWT);
        String dcrResponseString = EntityUtils.toString(dynamicClientRegistrationResponse);
        System.out.println(dcrResponseString);

        if (dcrResponseString.contains("error_description")) {
            throw new RuntimeException("Bad server response from Dynamic Client Registration endpoint");
        }

        // Get bearer token to consume API
        HttpEntity bearerTokenResponse = getBearerToken();
        System.out.println(EntityUtils.toString(bearerTokenResponse));
    }


    private static HttpEntity performDynamicClientRegistration(String sJWT) throws Exception {
        String apiUrl = ENV.getApiBaseUrl();
        MtlsHttpClient apiClient = new MtlsHttpClient(apiUrl, ENV.getApiRequireProxy());

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        
        // Setting content type manually because application/jwt isn't a standard MIME type
        HttpEntity postEntity = new StringEntity(sJWT, ContentType.create("application/jwt"));
        
        HttpResponse response = apiClient.post(
                "/register/v1.0",
                postEntity,
                headers
        );

        return response.getEntity();
    }

    private static HttpEntity getBearerToken() throws Exception {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", ENV.getClientId());
        claims.put("iss", ENV.getClientId());
        claims.put("scope", ENV.getScope());
        String jwt = JSONWebTokenUtils.generateJWTForClaims(claims, "PS256")
                .setAudience(ENV.getAuthTokenBaseUrl() + "/as/token.oauth2")
                .compact();
        MtlsHttpClient apiClient = new MtlsHttpClient(ENV.getAuthTokenBaseUrl(), ENV.getAuthTokenRequireProxy());

        List<NameValuePair> postBody = wrapJwtAsPostRequestBody(jwt);

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        
        HttpEntity postEntity = new UrlEncodedFormEntity(postBody);

        HttpResponse response = apiClient.post(
                "/as/token.oauth2",
                postEntity,
                headers
        );
        
        return response.getEntity();
    }

    private static List<NameValuePair> wrapJwtAsPostRequestBody(String sJWT) {
        List<NameValuePair> postBody = new ArrayList<>();
        
        postBody.add(new BasicNameValuePair("client_id", ENV.getClientId()));
        postBody.add(new BasicNameValuePair("client_secret", ENV.getClientSecret()));
        postBody.add(new BasicNameValuePair("scope", ENV.getScope()));
        postBody.add(new BasicNameValuePair("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer"));
        postBody.add(new BasicNameValuePair("client_assertion", sJWT));
        postBody.add(new BasicNameValuePair("grant_type", "client_credentials"));
        
        return postBody;
    }
}
