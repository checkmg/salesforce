package com.sf.sforce.auth.config;

import java.io.FileNotFoundException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.sforce.ws.ConnectorConfig;

@Configuration
public class SforceAuthConfig {

	private static final String TRACE_LOG_FILE = "traceLogs.txt";
	
	private Logger logger = LoggerFactory.getLogger(SforceAuthConfig.class);
	
	@Value("${SF_VERSION}")
	private String version;

	@Value("${HTTP_PROXY_HOST}")
	private String proxyHost;

	@Value("${HTTP_PROXY_PORT}")
	private String proxyPort;
	
	@Value("${SF_LIGHTNING_LOGIN_URL}")
	private String lightningLoginUrl;
	
	@Value("${SF_LIGHTNING_USERNAME}")
	private String lightningUsername;

	@Value("${SF_LIGHTNING_PASSWORD}")
	private String lightningPassword; 
	
	@Value("${SF_LIGHTNING_CLIENT_ID}")
	private String lightningClientId; 

	@Value("${SF_LIGHTNING_CLIENT_SECRET}")
	private String lightningClientSecret;

	@Value("${SF_LIGHTNING_AUTH_URI}")
	private String lightningLoginUri;
	
	@Value("${SF_LIGHTNING_SOBJECT_URL}")
	private String lightningSobjectUrl;

	@Value("${SF_LIGHTNING_SOBJECT_URI}")
	private String lightningSobjectUri;

	@Value("${SF_LIGHTNING_BATCH_AUTH_URL}")
	private String lightningBatchAuthtUrl;

	@Value("${SF_LIGHTNING_TOKEN}")
	private String lightningToken;

	@Value("${SF_LIGHTNING_BATCH_ASYNC_URL}")
	private String lightningBatchAsyncUrl;
	
	
	public static final String SF_KEY_AUTH_TYPE = "grant_type";
	public static final String SF_VALUE_AUTH_TYPE = "password";
	public static final String SF_KEY_CLIENT_ID = "client_id";
	public static final String SF_KEY_CLIENT_SECRET = "client_secret";
	public static final String SF_KEY_USERNAME = "username";
	public static final String SF_KEY_PASSWORD = "password";

	
	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public String getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getLightningBatchAsyncUrl() {
		return lightningBatchAsyncUrl;
	}

	public void setLightningBatchAsyncUrl(String lightningBatchAsyncUrl) {
		this.lightningBatchAsyncUrl = lightningBatchAsyncUrl;
	}

	public String getLightningToken() {
		return lightningToken;
	}

	public void setLightningToken(String lightningToken) {
		this.lightningToken = lightningToken;
	}

	public String getLightningBatchAuthtUrl() {
		return lightningBatchAuthtUrl;
	}

	public void setLightningBatchAuthtUrl(String lightningBatchAuthtUrl) {
		this.lightningBatchAuthtUrl = lightningBatchAuthtUrl;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getLightningLoginUrl() {
		return lightningLoginUrl;
	}

	public void setLightningLoginUrl(String lightningLoginUrl) {
		this.lightningLoginUrl = lightningLoginUrl;
	}

	public String getLightningUsername() {
		return lightningUsername;
	}

	public void setLightningUsername(String lightningUsername) {
		this.lightningUsername = lightningUsername;
	}

	public String getLightningPassword() {
		return lightningPassword;
	}

	public void setLightningPassword(String lightningPassword) {
		this.lightningPassword = lightningPassword;
	}

	public String getLightningClientId() {
		return lightningClientId;
	}

	public void setLightningClientId(String lightningClientId) {
		this.lightningClientId = lightningClientId;
	}

	public String getLightningClientSecret() {
		return lightningClientSecret;
	}

	public void setLightningClientSecret(String lightningClientSecret) {
		this.lightningClientSecret = lightningClientSecret;
	} 
	
	public String getLightningLoginUri() {
		return lightningLoginUri;
	}

	public void setLightningLoginUri(String lightningLoginUri) {
		this.lightningLoginUri = lightningLoginUri;
	}

	public String getLightningSobjectUri() {
		return lightningSobjectUri;
	}

	public void setLightningSobjectUri(String lightningSobjectUri) {
		this.lightningSobjectUri = lightningSobjectUri;
	}

	
	public String getLightningSobjectUrl() {
		return lightningSobjectUrl;
	}

	public void setLightningSobjectUrl(String lightningSobjectUrl) {
		this.lightningSobjectUrl = lightningSobjectUrl;
	}

	
	public HttpHeaders getSforceAuthHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
	}
	
	public MultiValueMap<String, String> getSforceAuthPayload() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add(SF_KEY_AUTH_TYPE, SF_VALUE_AUTH_TYPE);
        body.add(SF_KEY_CLIENT_ID, getLightningClientId());
        body.add(SF_KEY_CLIENT_SECRET, getLightningClientSecret());
        body.add(SF_KEY_USERNAME, getLightningUsername());
        body.add(SF_KEY_PASSWORD, getLightningPassword());
        return body;
	}
	
	public ConnectorConfig getBulkConnectionConfig() {
        ConnectorConfig bulkConnectionConfig = new ConnectorConfig();
        bulkConnectionConfig.setUsername(getLightningUsername());
        bulkConnectionConfig.setPassword(getLightningPassword().concat(getLightningToken()));
        bulkConnectionConfig.setAuthEndpoint(getLightningBatchAuthtUrl());
        try {
			bulkConnectionConfig.setTraceFile(TRACE_LOG_FILE);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
        bulkConnectionConfig.setTraceMessage(true); 
		return bulkConnectionConfig;
	}
}
