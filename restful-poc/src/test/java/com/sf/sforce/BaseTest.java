package com.sf.sforce;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sf.sforce.auth.config.SforceAuthConfig;
import com.sf.sforce.batch.impl.SalesforceBatch;
import com.sf.sforce.batch.impl.SalesforceBulkJobImpl;
import com.sf.sforce.beans.Account;
import com.sf.sforce.boot.config.TestPropertiesConfigurer;
import com.sf.sforce.integration.impl.SalesforceRestIntegrationImpl;
import com.sf.sforce.service.impl.SalesforceServiceImpl;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
		TestPropertiesConfigurer.class,
		SforceAuthConfig.class,
		SalesforceServiceImpl.class,
		SalesforceRestIntegrationImpl.class,
		SalesforceBulkJobImpl.class,
		SalesforceBatch.class
})
@Ignore
public class BaseTest {

	public static final String ACCOUNTS_FILE = "src/test/resources/testAccountsBulk.csv";
	public static final String ACCOUNTS_RESULTS = "src/test/resources/resultsAccount.csv";
	public static final String CONTACTS_FILE = "src/test/resources/testContactBulk.csv";
	public static final String CONTACTS_RESULTS = "src/test/resources/resultsContact.csv";
	
	public long recordsCount(String fileName) throws Exception {
		Reader reader = Files.newBufferedReader(Paths.get(fileName));
		CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim());
		return csvParser.getRecords().size();
	}
	
	public void cleanup(String filename) {
		FileUtils.deleteQuietly(new File(filename));
	}
	
	public List<Account> loadAccounts(String fileName) throws IOException {
		List<Account> accounts = new ArrayList<Account>();
		Reader reader = Files.newBufferedReader(Paths.get(fileName));
		CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim());
		for(CSVRecord csvrecord : csvParser.getRecords()) {
			Account tmp = new Account();
			tmp.setName(csvrecord.get("Name"));
			tmp.setDescription(csvrecord.get("Description"));
			accounts.add(tmp);
		}
		return accounts;
	}
}
