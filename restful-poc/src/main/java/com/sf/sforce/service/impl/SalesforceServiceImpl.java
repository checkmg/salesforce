package com.sf.sforce.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.sf.sforce.beans.Account;
import com.sf.sforce.beans.CreateAccountResponse;
import com.sf.sforce.integration.SalesforceRestIntegration;
import com.sf.sforce.integration.impl.SalesforceRestIntegrationImpl;
import com.sf.sforce.service.SalesforceService;

@Component
public class SalesforceServiceImpl implements SalesforceService {

	private Logger logger = LoggerFactory.getLogger(SalesforceServiceImpl.class);
	
	@Autowired
	private SalesforceRestIntegration salesforceRestIntegration;
	
	@Override
	public CreateAccountResponse createAccount(String name) {
		Account account = new Account();
		account.setName(name);

		Gson gson = new Gson();
		String sfAccount = gson.toJson(account);
        ResponseEntity<String> response = salesforceRestIntegration.createSObject(sfAccount, "Account");
        logger.info("Create Account Response: {}", response.getBody());
        CreateAccountResponse createAccountResponse = new CreateAccountResponse();
		if (response != null) {
			if (HttpStatus.CREATED == response.getStatusCode() && response.getBody() != null) {
				createAccountResponse = gson.fromJson(response.getBody(), CreateAccountResponse.class);
			}
			logger.debug("Success= Created Account  {}", response.getBody());
		} else {
			logger.error("Failure= Creating Account");
		}
		return createAccountResponse;
	}
	
	@Override	
	public CreateAccountResponse updateAccount(Account updateAccount, String Id) {
		Gson gson = new Gson();
		String sfAccount = gson.toJson(updateAccount);
		logger.info("Update Account request: {}", sfAccount);
        ResponseEntity<String> response = salesforceRestIntegration.updateSObject(sfAccount, "Account", Id);
        CreateAccountResponse createAccountResponse = new CreateAccountResponse();
		if (response != null) {
			if (HttpStatus.NO_CONTENT == response.getStatusCode() && response.getBody() != null) {
				createAccountResponse = gson.fromJson(response.getBody(), CreateAccountResponse.class);
			}
			logger.debug("Success= Update Account  Status:{} Body:{}", response.getStatusCode(), response.getBody());
		} else {
			logger.error("Failure= Update Account");
		}
		return createAccountResponse;
	}

	@Override
	public void deleteAccount(String Id) {
		ResponseEntity<String> response = salesforceRestIntegration.deleteSObject(Id, "Account");
		if (response != null) {
			logger.debug("Success= Delete Account  Status:{} Body:{}", response.getStatusCode(), response.getBody());
		} else {
			logger.error("Failure= Delete Account");
		}
		
	}
	
}

