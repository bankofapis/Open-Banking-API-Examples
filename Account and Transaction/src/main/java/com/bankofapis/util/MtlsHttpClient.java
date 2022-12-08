package com.bankofapis.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MtlsHttpClient {
    private static final Env ENV = Env.getInstance();
    private final String baseUrl;
    private HttpClient httpClient;
    private RequestConfig requestConfig;

    public MtlsHttpClient(String baseUrl, boolean useProxy) throws Exception {
        if (useProxy) {
            setupClientWithProxy();
        } else {
            this.httpClient = HttpClients.custom()
                    .useSystemProperties()
                    .setSSLContext(ENV.getSslContext())
                    .build();
            this.requestConfig = RequestConfig.DEFAULT;
        }

        this.baseUrl = baseUrl;
    }

    public HttpResponse get(String url, Map<String, String> headers) throws IOException {
        HttpGet request = new HttpGet(this.baseUrl + url);
        setRequestHeaders(request, headers);
        request.setConfig(this.requestConfig);
        System.out.println("GET request to URL: " + request.getURI());
        System.out.println("Request Headers: " + headersToLogString(request));
        try {
            HttpResponse response = httpClient.execute(request);
            System.out.println("Server Response Code: " + response.getStatusLine().getStatusCode());
            handleErrorResponse(response);
            return response;
        } catch (Exception e) {
            System.out.println("GET Request to " + this.baseUrl + url + " failed.");
            throw e;
        }
    }

    public HttpResponse put(String url, HttpEntity data, Map<String, String> headers) throws Exception {
        HttpPut request = new HttpPut(this.baseUrl + url);
        request.setEntity(data);
        setRequestHeaders(request, headers);
        request.setConfig(this.requestConfig);
        System.out.println("PUT request to URL: " + request.getURI());
        System.out.println("Request Headers: " + headersToLogString(request));
        System.out.println("Request Data: " + EntityUtils.toString(data));
        try {
            HttpResponse response = httpClient.execute(request);
            System.out.println("Server Response Code: " + response.getStatusLine().getStatusCode());
            handleErrorResponse(response);
            return response;
        } catch (Exception e) {
            System.out.println("PUT Request to " + this.baseUrl + url + " failed.");
            System.out.println("Data supplied:");
            System.out.println(EntityUtils.toString(data));
            throw e;
        }
    }

    public HttpResponse post(String url, HttpEntity data, Map<String, String> headers) throws Exception {
        HttpPost request = new HttpPost(this.baseUrl + url);
        request.setEntity(data);
        request.setConfig(this.requestConfig);
        setRequestHeaders(request, headers);
        System.out.println("POST request to URL: " + request.getURI());
        System.out.println("Request Headers: \n" + headersToLogString(request));
        System.out.println("Request Data: " + EntityUtils.toString(data));
        try {
            HttpResponse response = httpClient.execute(request);
            System.out.println("Server Response Code: " + response.getStatusLine().getStatusCode());
            handleErrorResponse(response);
            return response;
        } catch (Exception e) {
            System.out.println("POST Request to " + this.baseUrl + url + " failed.");
            System.out.println("Data supplied:");
            System.out.println(EntityUtils.toString(data));
            throw e;
        }
    }

    public HttpResponse delete(String url, Map<String, String> headers) throws IOException {
        HttpDelete request = new HttpDelete(this.baseUrl + url);
        setRequestHeaders(request, headers);
        request.setConfig(this.requestConfig);
        System.out.println("DELETE request to URL: " + request.getURI());
        System.out.println("Request Headers: " + headersToLogString(request));
        try {
            HttpResponse response = httpClient.execute(request);
            System.out.println("Server Response Code: " + response.getStatusLine().getStatusCode());
            handleErrorResponse(response);
            return response;
        } catch (Exception e) {
            System.out.println("DELETE Request to " + this.baseUrl + url + " failed.");
            throw e;
        }
    }

    private void setupClientWithProxy() throws Exception {
        HttpHost proxy = new HttpHost(ENV.getProxyHost(), ENV.getProxyPort());
        AuthenticationStrategy authenticationStrategy = new ProxyAuthenticationStrategy();
        CredentialsProvider credentialsProvider = getCredentialsProvider();
        this.httpClient = HttpClients.custom()
                .useSystemProperties()
                .setProxyAuthenticationStrategy(authenticationStrategy)
                .setProxy(proxy)
                .setDefaultCredentialsProvider(credentialsProvider)
                .setSSLContext(ENV.getSslContext())
                .build();
        this.requestConfig = RequestConfig.custom()
                .setProxy(proxy)
                .setAuthenticationEnabled(true)
                .build();
    }

    private CredentialsProvider getCredentialsProvider() {
        Credentials credentials = new NTCredentials(
                System.getProperty("http.proxyUser"),
                System.getProperty("http.proxyPassword"),
                System.getProperty("nt.workstation"),
                System.getProperty("nt.domain")
        );

        checkSystemProperties();

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        AuthScope auth = new AuthScope(ENV.getProxyHost(), ENV.getProxyPort(), ENV.getProxyScheme());
        credentialsProvider.setCredentials(auth, credentials);
        return credentialsProvider;
    }

    private void setRequestHeaders(HttpUriRequest request, Map<String, String> headers) {
        if (headers == null) {
            return;
        }
        for (Map.Entry<String, String> header : headers.entrySet()) {
            request.setHeader(header.getKey(), header.getValue());
        }
    }

    private void checkSystemProperties() {
        if (Objects.equals(System.getProperty("http.proxyUser"), "")) {
            System.out.println("WARNING: No Proxy User provided, please ensure you run using -Dhttp.proxyUser=XXXX");
        }

        if (Objects.equals(System.getProperty("http.proxyUser"), "")) {
            System.out.println("WARNING: No Proxy Password provided, please ensure you run using -Dhttp.proxyPassword=XXXX");
        }

        if (Objects.equals(System.getProperty("http.domain"), "")) {
            System.out.println("WARNING: No NT Domain provided, please ensure you run using -Dnt.domain=XXXX");
        }

        if (Objects.equals(System.getProperty("http.domain"), "")) {
            System.out.println("WARNING: No NT Workstation provided, please ensure you run using -Dnt.domain=XXXX");
        }
    }

    private void handleErrorResponse(HttpResponse response) {
        if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 400) {
            System.out.println("Response Headers: " + headersToLogString(response));
            System.out.println("Server Error: " + response.getStatusLine().getReasonPhrase());
            throw new RuntimeException("API Response Error");
        }
    }

    private String headersToLogString(HttpMessage message) {
        return Arrays.stream(message.getAllHeaders())
                .map(h -> "    " + h.getName() + "=" + h.getValue())
                .collect(Collectors.joining("\n"));
    }
}
