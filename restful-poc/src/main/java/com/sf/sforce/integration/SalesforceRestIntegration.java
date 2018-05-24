package com.sf.sforce.integration;

import org.springframework.http.ResponseEntity;

public interface SalesforceRestIntegration {
	
	public String authenticate(SalesforceAuthStatusEnum salesforceAuthStatus) throws Exception;
	
	public ResponseEntity<String> updateSObject(String sObject, String type, String Id);
	
	public ResponseEntity<String> createSObject(String sObject, String type);	

	public ResponseEntity<String> deleteSObject(String id, String type);	
	
	public ResponseEntity<String> upsertSObject(String sObject, String type, String Id);
}
