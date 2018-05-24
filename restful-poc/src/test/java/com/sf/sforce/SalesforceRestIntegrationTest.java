package com.sf.sforce;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sf.sforce.auth.config.SforceAuthConfig;
import com.sf.sforce.beans.Account;
import com.sf.sforce.beans.CreateAccountResponse;
import com.sf.sforce.integration.SalesforceAuthStatusEnum;
import com.sf.sforce.integration.impl.SalesforceRestIntegrationImpl;
import com.sf.sforce.service.impl.SalesforceServiceImpl;

public class SalesforceRestIntegrationTest extends BaseTest{

	private Logger logger = LoggerFactory.getLogger(SalesforceRestIntegrationTest.class);
	
	@Autowired
	public SforceAuthConfig sforceAuthConfig;
	
	@Autowired
	public SalesforceRestIntegrationImpl salesforceRestIntegration;
	
	@Autowired
	private SalesforceServiceImpl salesforceService;
	
	@Test
	public void testAuthentication() {
		Assert.assertNotNull(sforceAuthConfig);
		try {
			String authToken = salesforceRestIntegration.authenticate(SalesforceAuthStatusEnum.AUTH);
			Assert.assertNotNull(authToken);
			Assert.assertNotSame(authToken, salesforceRestIntegration.authenticate(SalesforceAuthStatusEnum.AUTH_RETRY));
		} catch(Exception e) {
			Assert.fail();
		}
	}
	
	@Test
	public void testCreateAccount() {
		Assert.assertNotNull(sforceAuthConfig);
		String name = UUID.randomUUID().toString();
		CreateAccountResponse account = salesforceService.createAccount(name);
		Assert.assertNotNull(account);
		Assert.assertNotNull(account.getId());
		logger.info("Create Account Success. Id:{}", account.getId());
		Assert.assertTrue(account.isSuccess());
		Account updateAccount = new Account();
		String name1 = UUID.randomUUID().toString();
		updateAccount.setName(name1);
		try {
			account = salesforceService.updateAccount(updateAccount, null);	
			Assert.fail();
		} catch(Exception e) {
			logger.error(e.getMessage());
		}
		
		account = salesforceService.updateAccount(updateAccount, account.getId());
	}

	
}
