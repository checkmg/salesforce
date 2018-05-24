package com.sf.sforce.batch.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sf.sforce.auth.config.SforceAuthConfig;
import com.sf.sforce.batch.SalesforceBulkJob;
import com.sforce.async.AsyncApiException;
import com.sforce.async.BatchInfo;
import com.sforce.async.BulkConnection;
import com.sforce.async.ContentType;
import com.sforce.async.JobInfo;
import com.sforce.async.JobStateEnum;
import com.sforce.async.OperationEnum;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

@Component
public class SalesforceBulkJobImpl implements SalesforceBulkJob {

	private Logger logger = LoggerFactory.getLogger(SalesforceBulkJobImpl.class);
	
	@Autowired
	private SforceAuthConfig sforceAuthConfig;
		
	@Override
    public BulkConnection getBulkConnection() throws ConnectionException, AsyncApiException {
          ConnectorConfig bulkConfig = sforceAuthConfig.getBulkConnectionConfig();
          	new PartnerConnection(bulkConfig);
          	
          ConnectorConfig config = new ConnectorConfig();
          config.setSessionId(bulkConfig.getSessionId()); 
          config.setRestEndpoint(sforceAuthConfig.getLightningBatchAsyncUrl());
          config.setCompression(true);
          config.setTraceMessage(false);
          BulkConnection connection = new BulkConnection(config);
          return connection;
     }	
	
	@Override
	public JobInfo scheduleJob(String sobjectType, OperationEnum operation, BulkConnection connection) throws AsyncApiException {
        JobInfo job = new JobInfo();
        job.setObject(sobjectType);
        job.setOperation(operation);
        job.setContentType(ContentType.CSV);
        job.setExternalIdFieldName("Id");
        	job = connection.createJob(job);
        return job;
	}
	
	@Override
    public boolean closeJob(BulkConnection connection, String jobId) {
		  boolean isClosed = true;
          JobInfo job = new JobInfo(new JobInfo.Builder().id(jobId).state(JobStateEnum.Closed));
          try {
			connection.updateJob(job);
          } catch (AsyncApiException e) {
        	  	logger.error("Error closing Job = {}", jobId, e);
        	  	isClosed = false;
          }
          
          return isClosed;
      }

	@Override
	public void createBatchForJob(ByteArrayOutputStream tmpOut, List<BatchInfo> batchInfos,
			BulkConnection connection, JobInfo jobInfo) throws AsyncApiException, IOException {
	        tmpOut.flush();
	        InputStream tmpInputStream = new ByteArrayInputStream(tmpOut.toByteArray());
	        tmpOut.close();
	        tmpOut.reset();

	        try {
	            BatchInfo batchInfo =
	              connection.createBatchFromStream(jobInfo, tmpInputStream);
	            logger.debug(batchInfo.toString());
	            batchInfos.add(batchInfo);

	        } finally {
	            tmpInputStream.close();
	        }	
	}
	
}
