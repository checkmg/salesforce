package com.sf.sforce.batch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.sforce.async.AsyncApiException;
import com.sforce.async.BatchInfo;
import com.sforce.async.BulkConnection;
import com.sforce.async.JobInfo;
import com.sforce.async.OperationEnum;
import com.sforce.ws.ConnectionException;

public interface SalesforceBulkJob {

	public BulkConnection getBulkConnection() throws ConnectionException, AsyncApiException;
	
	public JobInfo scheduleJob(String sobjectType, OperationEnum operation, BulkConnection connection) throws AsyncApiException;

	public void createBatchForJob(ByteArrayOutputStream tmpOut,
	  	      List<BatchInfo> batchInfos, BulkConnection connection, JobInfo jobInfo) throws AsyncApiException, IOException;
		
	public boolean closeJob(BulkConnection connection, String jobId);
	
}
