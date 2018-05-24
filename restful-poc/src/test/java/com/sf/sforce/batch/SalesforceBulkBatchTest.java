package com.sf.sforce.batch;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sf.sforce.BaseTest;
import com.sf.sforce.auth.config.SforceAuthConfig;
import com.sf.sforce.batch.impl.SalesforceBatch;
import com.sf.sforce.beans.Account;
import com.sf.sforce.beans.CreateAccountResponse;
import com.sf.sforce.integration.impl.SalesforceRestIntegrationImpl;
import com.sf.sforce.service.impl.SalesforceServiceImpl;
import com.sforce.async.OperationEnum;

public class SalesforceBulkBatchTest extends BaseTest {

	private Logger logger = LoggerFactory.getLogger(SalesforceBulkBatchTest.class);
	
	@Autowired
	public SforceAuthConfig sforceAuthConfig;
	
	@Autowired
	public SalesforceBatch salesforceBulkBatchImpl;
	
	@Autowired
	public SalesforceRestIntegrationImpl salesforceRestIntegration;

	@Autowired
	private SalesforceServiceImpl salesforceService;
	
	
	@Test
	@Ignore
	public void testBulkInsertUpsertDelete() throws Exception {		
		Assert.assertNotNull(sforceAuthConfig);
		Assert.assertNotNull(salesforceBulkBatchImpl);
		long start = System.currentTimeMillis();
		salesforceBulkBatchImpl.runBatch(ACCOUNTS_FILE, "Account", OperationEnum.insert);
		salesforceBulkBatchImpl.runBatch(ACCOUNTS_RESULTS, "Account", OperationEnum.delete);
		logger.info("Batch Performance: {}", (System.currentTimeMillis()-start));
		Assert.assertTrue(recordsCount(ACCOUNTS_FILE) == recordsCount(ACCOUNTS_RESULTS));
		
		salesforceBulkBatchImpl.runBatch(ACCOUNTS_FILE, "Account", OperationEnum.insert);
		salesforceBulkBatchImpl.runBatch(ACCOUNTS_RESULTS, "Account", OperationEnum.upsert);
		salesforceBulkBatchImpl.runBatch(ACCOUNTS_RESULTS, "Account", OperationEnum.delete);
		Assert.assertTrue(recordsCount(ACCOUNTS_FILE) == recordsCount(ACCOUNTS_RESULTS));
		cleanup(ACCOUNTS_RESULTS);
	}
	
	@Test
	public void testComparePerformanceInsert() throws Exception {
		List<Account> accounts = loadAccounts(ACCOUNTS_FILE);
				
		long start = System.currentTimeMillis();
		for(Account account : accounts) {
			CreateAccountResponse response = salesforceService.createAccount(account.getName());
			logger.debug("Created Account : {}", response.getId());
			salesforceService.deleteAccount(response.getId());
			logger.debug("Deleted Account : {}", response.getId());
		}
		logger.info("Sequential Performance: {}", (System.currentTimeMillis() - start));		
	}	
	
	
}
