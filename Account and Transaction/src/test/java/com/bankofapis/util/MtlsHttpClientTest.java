package com.bankofapis.util;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MtlsHttpClientTest {

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

    @Test
    public void testGetSucceedsOn200() throws Exception {
        stubFor(get("/test").willReturn(ok("test")));
        MtlsHttpClient client = new MtlsHttpClient("https://localhost:8090", false);
        HttpEntity response = client.get("/test", Collections.emptyMap()).getEntity();
        assertEquals("test", EntityUtils.toString(response), "Bad server response");
    }

    @Test
    public void testGetThrowsOn400() throws Exception {
        stubFor(get("/test").willReturn(forbidden()));
        MtlsHttpClient client = new MtlsHttpClient("https://localhost:8090", false);
        assertThrows(RuntimeException.class,  (() -> client.get("/test", Collections.emptyMap()).getEntity()));
    }

    @Test
    public void testPutSucceedsOn200() throws Exception {
        stubFor(put("/test").willReturn(ok("test")));
        MtlsHttpClient client = new MtlsHttpClient("https://localhost:8090", false);
        HttpEntity response = client.put("/test", new StringEntity("test"), Collections.emptyMap()).getEntity();
        assertEquals("test", EntityUtils.toString(response), "Bad server response");
    }

    @Test
    public void testPutThrowsOn400() throws Exception {
        stubFor(put("/test").willReturn(forbidden()));
        MtlsHttpClient client = new MtlsHttpClient("https://localhost:8090", false);
        assertThrows(RuntimeException.class,  (() -> client.put("/test", new StringEntity("test"), Collections.emptyMap()).getEntity()));
    }

    @Test
    public void testPostSucceedsOn200() throws Exception {
        stubFor(post("/test").willReturn(ok("test")));
        MtlsHttpClient client = new MtlsHttpClient("https://localhost:8090", false);
        HttpEntity response = client.post("/test", new StringEntity("test"), Collections.emptyMap()).getEntity();
        assertEquals("test", EntityUtils.toString(response), "Bad server response");
    }

    @Test
    public void testPostThrowsOn400() throws Exception {
        stubFor(post("/test").willReturn(forbidden()));
        MtlsHttpClient client = new MtlsHttpClient("https://localhost:8090", false);
        assertThrows(RuntimeException.class,  (() -> client.post("/test", new StringEntity("test"), Collections.emptyMap()).getEntity()));
    }

    @Test
    public void testDeleteSucceedsOn200() throws Exception {
        stubFor(delete("/test").willReturn(ok("test")));
        MtlsHttpClient client = new MtlsHttpClient("https://localhost:8090", false);
        HttpEntity response = client.delete("/test", Collections.emptyMap()).getEntity();
        assertEquals("test", EntityUtils.toString(response), "Bad server response");
    }

    @Test
    public void testDeleteThrowsOn400() throws Exception {
        stubFor(delete("/test").willReturn(forbidden()));
        MtlsHttpClient client = new MtlsHttpClient("https://localhost:8090", false);
        assertThrows(RuntimeException.class,  (() -> client.delete("/test", Collections.emptyMap()).getEntity()));
    }

}
