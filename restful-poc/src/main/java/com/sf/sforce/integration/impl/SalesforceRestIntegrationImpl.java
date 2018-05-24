package com.sf.sforce.integration.impl;

import java.util.Arrays;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sf.sforce.auth.config.SforceAuthConfig;
import com.sf.sforce.integration.SalesforceAuthStatusEnum;
import com.sf.sforce.integration.SalesforceRestIntegration;

@Component
public class SalesforceRestIntegrationImpl implements SalesforceRestIntegration {

	private Logger logger = LoggerFactory.getLogger(SalesforceRestIntegrationImpl.class);
			
	@Autowired
	private SforceAuthConfig sforceAuthConfig;
	
	private String authToken;
	
	private RestTemplate restTemplate;
	
	@Override
	public String authenticate(SalesforceAuthStatusEnum salesforceAuthStatus) throws Exception {
	     	
		if(authToken != null && !salesforceAuthStatus.equals(SalesforceAuthStatusEnum.AUTH_RETRY)) {
			logger.debug("Using Existing Auth Token");
			return authToken;			
		}
		else {
		    restTemplate = getRestTemplate();
		    HttpEntity<MultiValueMap<String,String>> httpEntity = new HttpEntity<>(sforceAuthConfig.getSforceAuthPayload(), sforceAuthConfig.getSforceAuthHeaders());

			ResponseEntity<String> response = restTemplate.postForEntity(sforceAuthConfig.getLightningLoginUrl().concat(sforceAuthConfig.getLightningLoginUri()), httpEntity, String.class);
			
			if (response != null) {
				if (HttpStatus.OK == response.getStatusCode() && response.getBody() != null) {					
					JsonElement jsonElement = new JsonParser().parse(response.getBody());
					authToken = jsonElement.getAsJsonObject().get("access_token").getAsString();
				} else {
					throw new Exception("No access token found");
				}
				logger.debug("Success= Created Auth Token");
			} else {
				logger.error("Failure= Auth Token Creation");
				throw new Exception("Error while Authenticating with salesforce");
			}
			return authToken;
		}
	}	
	
	@Override
	public ResponseEntity<String> createSObject(String sObject, String type) {
        String createURL = sforceAuthConfig.getLightningSobjectUrl().concat(type);
        ResponseEntity<String> response = callSalesforce(createURL, HttpMethod.POST, sObject, type, SalesforceAuthStatusEnum.AUTH);
        logger.debug("Created {}", type);
		return response;
	}
	
	private HttpHeaders getHeaders(String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer ".concat(authToken));		
        return headers;
	}
	
	public SforceAuthConfig getSforceAuthConfig() {
		return sforceAuthConfig;
	}

	public void setSforceAuthConfig(SforceAuthConfig sforceAuthConfig) {
		this.sforceAuthConfig = sforceAuthConfig;
	}

	@Override
	public ResponseEntity<String> updateSObject(String sObject, String type, String Id) {
        String updateURL = sforceAuthConfig.getLightningSobjectUrl().concat(type).concat("/").concat(Id).concat("?_HttpMethod=PATCH");  
        ResponseEntity<String> response = callSalesforce(updateURL, HttpMethod.POST, sObject, type, SalesforceAuthStatusEnum.AUTH);
        logger.debug("Updated {}", type);
		return response;	
	}

	@Override
	public ResponseEntity<String> upsertSObject(String sObject, String type, String Id) {
		ResponseEntity<String> response = this.updateSObject(sObject, type, Id);
		if(HttpStatus.NOT_FOUND == response.getStatusCode()) {
			response = this.createSObject(sObject, type);
		}
		return response;
	}

	@Override
	public ResponseEntity<String> deleteSObject(String id, String type) {
        String deleteURL = sforceAuthConfig.getLightningSobjectUrl().concat(type).concat("/").concat(id);
        	ResponseEntity<String> response = callSalesforce(deleteURL, HttpMethod.DELETE, null, type, SalesforceAuthStatusEnum.AUTH);
        logger.debug("Deleted {}", type);
        return response;
	}
	
	private ResponseEntity<String> callSalesforce(String url, HttpMethod method, String sobject, String sobjectType, SalesforceAuthStatusEnum authStatus) {
        logger.debug("Calling URL:{} for Method:{}", url, method);
        RestTemplate restTemplate = getRestTemplate();
        HttpEntity<String> httpEntity = null;
        ResponseEntity<String> response = null;
        try {
	        if(HttpMethod.DELETE.equals(method)) {
	        		httpEntity = new HttpEntity<>(getHeaders(authenticate(SalesforceAuthStatusEnum.AUTH)));
	        		response = restTemplate.exchange(url, method, httpEntity, String.class);
	        } else if(HttpMethod.POST.equals(method)){
	        		httpEntity = new HttpEntity<>(sobject, getHeaders(authenticate(SalesforceAuthStatusEnum.AUTH)));
	        		response = restTemplate.postForEntity(url, httpEntity, String.class);
	        }
	        	
	        if(response.getStatusCode() != null 
	        		&& HttpStatus.UNAUTHORIZED.equals(response.getStatusCode())) {
	        		callSalesforce(url, method, sobject, sobjectType, SalesforceAuthStatusEnum.AUTH_RETRY);
	        }        	
        } catch(Exception e) {
        		logger.error(e.getMessage(), e);
        }

        return response;
	}
	
	private RestTemplate getRestTemplate() {
		logger.info("Host:{},  Port:{}", sforceAuthConfig.getProxyHost(), Integer.parseInt(sforceAuthConfig.getProxyPort()));
		HttpComponentsClientHttpRequestFactory requestFactory =
				new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create()
						.setProxy(new HttpHost(sforceAuthConfig.getProxyHost(), Integer.parseInt(sforceAuthConfig.getProxyPort())))
						.build());
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}
	
}
