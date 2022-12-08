package com.bankofapis.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Env {
    private static final Properties env = new Properties();

    private static final Env INSTANCE = new Env();

    public static final String KEYSTORE_PATH = "keystore_path";
    public static final String API_BASE_URL = "api_base_url";
    public static final String AUTH_TOKEN_BASE_URL = "auth_token_base_url";
    public static final String AUTH_CONSENT_BASE_URL = "auth_consent_base_url";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String SSA = "ssa";
    public static final String PRIVATE_KEY = "private_key";
    public static final String PROXY_HOST = "proxy_host";
    public static final String PROXY_PORT = "proxy_port";
    public static final String PROXY_SCHEME = "proxy_scheme";
    public static final String CLIENT_ID = "client_id";
    public static final String SCOPE = "scope";
    public static final String SIGNING_KID = "signing_kid";
    public static final String BRAND = "brand";
    public static final String KEYSTORE_PASSWORD = "keystore_password";
    public static final String CLIENT_SECRET = "client_secret";
    private static final String API_REQUIRE_PROXY = "api_require_proxy";
    private static final String AUTH_TOKEN_REQUIRE_PROXY = "auth_token_require_proxy";
    private static final String AUTH_CONSENT_REQUIRE_PROXY = "auth_consent_require_proxy";

    private Env() {
        setProperties("dev.properties");
        verifyProperties();
    }

    public static void setProperties(String propertiesFile) {
        InputStream props = Env.class.getClassLoader().getResourceAsStream(propertiesFile);
        try {
            env.load(props);
            if (INSTANCE != null){
                INSTANCE.verifyProperties();
            }
        } catch (IOException e) {
            System.out.println("Could not initialise properties, " +
                    "ensure your properties file is valid and located in the resources directory.");
            e.printStackTrace();
        }
    }

    private void verifyProperties() {
        Map<String, String> errors = getErrors();
        Map<String, String> warnings = getWarnings();
        printIssuesMap(warnings, false);
        printIssuesMap(errors, true);
    }

    private Map<String, String> getErrors() {
        Map<String, String> errors = new HashMap<>();
        checkMissingOrInvalid(SSA, errors, null);
        checkMissingOrInvalid(API_BASE_URL, errors, null);
        checkMissingOrInvalid(AUTH_CONSENT_BASE_URL, errors, null);
        checkMissingOrInvalid(AUTH_TOKEN_BASE_URL, errors, null);
        checkMissingOrInvalid(KEYSTORE_PATH, errors, null);
        checkMissingOrInvalid(REDIRECT_URI, errors, null);
        checkMissingOrInvalid(PRIVATE_KEY, errors, null);
        checkMissingOrInvalid(PROXY_HOST, errors, null);
        checkMissingOrInvalid(CLIENT_ID, errors, null);
        checkMissingOrInvalid(SCOPE, errors, null);
        checkMissingOrInvalid(BRAND, errors, null);
        checkMissingOrInvalid(KEYSTORE_PASSWORD, errors, null);
        return errors;
    }

    private Map<String, String> getWarnings() {
        Map<String, String> warnings = new HashMap<>();
        checkMissingOrInvalid(PROXY_PORT, warnings, "8080");
        checkMissingOrInvalid(PROXY_SCHEME, warnings, "http");
        checkMissingOrInvalid(CLIENT_SECRET, warnings, "empty string");
        checkMissingOrInvalid(SIGNING_KID, warnings, "Gathered from software_jwks_endpoint (if reachable) in SSA claims.");
        checkMissingOrInvalid(API_REQUIRE_PROXY, warnings, "false");
        checkMissingOrInvalid(AUTH_CONSENT_REQUIRE_PROXY, warnings, "false");
        checkMissingOrInvalid(AUTH_TOKEN_REQUIRE_PROXY, warnings, "false");
        return warnings;
    }

    private void printIssuesMap(Map<String, String> issuesMap, boolean required) {
        StringBuilder outputString = new StringBuilder();
        for(Map.Entry<String, String> entry : issuesMap.entrySet()) {
            String val = entry.getValue();
            String key = entry.getKey();
            outputString.append(key).append(": ").append(val).append("\n");
        }
        if (!GeneralUtil.isNullOrEmpty(outputString.toString())) {
            if (required) {
                System.out.println("ERROR, please add these values to properties file:");
                System.out.println(outputString);
                throw new RuntimeException("Missing or invalid properties.");
            }
            System.out.println("WARNING, these properties use default values," +
                    " add to properties file if specific values are required:");
            System.out.println(outputString);
        }
    }

    private void checkMissingOrInvalid(String propertyName, Map<String, String> issuesMap, String defaultValue) {
        String propertyValue = env.getProperty(propertyName);
        if (GeneralUtil.isNullOrEmpty(propertyValue)) {
            if(!GeneralUtil.isNullOrEmpty(defaultValue)){
                issuesMap.put(propertyName, "Default value: " + defaultValue);
            } else {
                issuesMap.put(propertyName, "No default value");
            }
        }
    }

    public static Env getInstance() {
        return INSTANCE;
    }

    public SSLContext getSslContext() throws Exception {
        KeyStore keyStore = getKeyStore();

        try {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, this.getKeystorePassword());
            KeyManager[] keyManager = kmf.getKeyManagers();
            TrustManager[] trustManager = getTrustManagers();
            SSLContext sslContext = SSLContext.getInstance("TLS");

            sslContext.init(keyManager, trustManager, null);
            return sslContext;
        } catch(Exception e) {
            System.out.println("Could not initialise SSL context, verify your keystore_password.");
            System.out.println("If error persists, contact your keystore provider.");
            throw e;
        }
    }

    private KeyStore getKeyStore() throws Exception {
        KeyStore keyStore;
        String keyStorePath = env.getProperty(KEYSTORE_PATH);
        String keyStoreInstanceType = "JKS";

        if (keyStorePath.endsWith(".p12")) {
            keyStoreInstanceType = "PCKS12";
        }

        try(InputStream keyStoreStream = getClass().getResourceAsStream("/" + keyStorePath)) {
            keyStore = KeyStore.getInstance(keyStoreInstanceType);
            keyStore.load(keyStoreStream, this.getKeystorePassword());
            return keyStore;
        } catch(Exception e) {
            System.out.println("Error getting keystore instance, verify your keystore_path and keystore_password.");
            System.out.println("If error persists, contact your keystore provider.");
            throw e;
        }
    }

    public PrivateKey getPrivateKeyObj() throws Exception {
        // RegEx pattern to find valid private key contents
        Pattern keyPattern = Pattern.compile(
                "-+BEGIN[\\sRSA]+PRIVATE\\s+KEY-+(?:\\s|\\r|\\n)+" +
                        "([a-z0-9+/=\r\\n]+)" +
                        "-+END[\\sRSA]+PRIVATE\\s+KEY-+(?:\\s|\\r|\\n)*",
                Pattern.CASE_INSENSITIVE
        );

        String privateKeyContent = this.getPrivateKey();
        Matcher matcher = keyPattern.matcher(privateKeyContent);

        if (!matcher.find()) {
            System.out.println("Private key not found, check your properties file.");
            System.out.println("If error persists, contact your auth provider.");
            throw new KeyStoreException("Private key not found, check your properties file.");
        }

        // If private key begins with "-----BEGIN RSA..." then it's PKCS1 format,
        // otherwise it's PKCS8
        boolean isPkcs1KeyType = matcher.group(0).contains("RSA");
        if (isPkcs1KeyType) {
            return parsePkcs1PrivateKey(privateKeyContent);
        } else {
            return parsePkcs8PrivateKey(matcher);
        }
    }

    public String getApiBaseUrl() {
        return env.getProperty(API_BASE_URL);
    }

    public boolean getApiRequireProxy() {
        return env.getProperty(API_REQUIRE_PROXY, "").equals("true");
    }

    public String getAuthTokenBaseUrl() {
        return env.getProperty(AUTH_TOKEN_BASE_URL);
    }

    public boolean getAuthTokenRequireProxy() {
        return env.getProperty(AUTH_TOKEN_REQUIRE_PROXY, "").equals("true");
    }

    public String getAuthConsentBaseUrl() {
        return env.getProperty(AUTH_CONSENT_BASE_URL);
    }

    public boolean getAuthConsentRequireProxy() {
        return env.getProperty(AUTH_CONSENT_REQUIRE_PROXY, "").equals("true");
    }

    public String getRedirectUri() {
        return env.getProperty(REDIRECT_URI);
    }

    public String getSSA() {
        return env.getProperty(SSA);
    }

    public String getPrivateKey() {
        return env.getProperty(PRIVATE_KEY);
    }

    public String getProxyHost() {
        return env.getProperty(PROXY_HOST);
    }

    public int getProxyPort() {
        return Integer.parseInt(env.getProperty(PROXY_PORT, "8080"));
    }

    public String getProxyScheme() {
        return env.getProperty(PROXY_SCHEME, "http");
    }

    public String getClientId() {
        return env.getProperty(CLIENT_ID);
    }

    public String getClientSecret() {
        return env.getProperty(CLIENT_SECRET, "");
    }

    public String getScope() {
        return env.getProperty(SCOPE);
    }

    public String getSigningKid() {
        return env.getProperty(SIGNING_KID, null);
    }

    public String getBrand() {
        return env.getProperty(BRAND);
    }

    private char[] getKeystorePassword() {
        return env.getProperty(KEYSTORE_PASSWORD).toCharArray();
    }
    private byte[] base64Decode(String group) {
        return Base64.getMimeDecoder().decode(group.getBytes(StandardCharsets.US_ASCII));
    }

    private PrivateKey parsePkcs1PrivateKey(String privateKeyContent) throws IOException {
        PEMParser parser = new PEMParser(new StringReader(privateKeyContent));
        JcaPEMKeyConverter keyConverter = new JcaPEMKeyConverter()
                .setProvider(new BouncyCastleProvider());
        Object pemObject = parser.readObject();
        KeyPair keyPair = keyConverter.getKeyPair((PEMKeyPair) pemObject);
        return keyPair.getPrivate();
    }

    private PrivateKey parsePkcs8PrivateKey(Matcher matcher) throws Exception {
        byte[] encodedKey = base64Decode(matcher.group(1));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encodedKey);
        return kf.generatePrivate(spec);
    }

    private TrustManager[] getTrustManagers() {
        return new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
    }
}
