package com.sf.sforce.service;

import com.sf.sforce.beans.Account;
import com.sf.sforce.beans.CreateAccountResponse;

public interface SalesforceService {

	public CreateAccountResponse createAccount(String name);
	
	public CreateAccountResponse updateAccount(Account updateAccount, String Id);
	
	public void deleteAccount(String Id);
}
