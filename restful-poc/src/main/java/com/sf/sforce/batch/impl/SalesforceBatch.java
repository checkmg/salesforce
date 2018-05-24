package com.sf.sforce.batch.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sforce.async.AsyncApiException;
import com.sforce.async.BatchInfo;
import com.sforce.async.BatchStateEnum;
import com.sforce.async.BulkConnection;
import com.sforce.async.CSVReader;
import com.sforce.async.JobInfo;
import com.sforce.async.OperationEnum;
import com.sforce.ws.ConnectionException;

@Component    
public class SalesforceBatch {

	private Logger logger = LoggerFactory.getLogger(SalesforceBatch.class);
	
	private final long SLEEP_TIME = 10000;
	private final long MAX_RECORDS = 2;
	
	
	@Autowired
	private SalesforceBulkJobImpl salesforceBulkJobImpl;
	
    public void runBatch(String sampleFileName, String sobjectType, OperationEnum operation)
            throws AsyncApiException, ConnectionException, IOException {
        BulkConnection connection = salesforceBulkJobImpl.getBulkConnection();
        JobInfo job = salesforceBulkJobImpl.scheduleJob(sobjectType, operation, connection);
        List<BatchInfo> batchInfoList = null;
        	batchInfoList = loadBatches(connection, job, sampleFileName);
        salesforceBulkJobImpl.closeJob(connection, job.getId());
        checkStatus(sobjectType, connection, job, batchInfoList);
    }



    private void checkStatus(String sobjectType, BulkConnection connection, JobInfo job,
              List<BatchInfo> batchInfoList)
            throws AsyncApiException, IOException {
    	
    		if(waitForJobs(connection, job, batchInfoList)) {
            File results = new File("src/test/resources/results".concat(sobjectType).concat(".csv"));
            FileOutputStream fos = new FileOutputStream(results);
            fos.write(("Id").getBytes("UTF-8"));
    	        for (BatchInfo b : batchInfoList) {
    	            CSVReader rdr =
    	              new CSVReader(connection.getBatchResultStream(job.getId(), b.getId()));
    	            List<String> resultHeader = rdr.nextRecord();
    	            int resultCols = resultHeader.size();
                
    	            List<String> row;
    	            while ((row = rdr.nextRecord()) != null) {
    	                Map<String, String> resultInfo = new HashMap<String, String>();
    	                for (int i = 0; i < resultCols; i++) {
    	                    resultInfo.put(resultHeader.get(i), row.get(i));
    	                }
    	                boolean success = Boolean.valueOf(resultInfo.get("Success"));
    	                boolean created = Boolean.valueOf(resultInfo.get("Created"));
    	                String id = resultInfo.get("Id");
    	                String error = resultInfo.get("Error");
    	                if(success && created) {
    	                    logger.debug("Created row with id {}", id);
    	                    fos.write(("\n".concat(id)).getBytes("UTF-8"));    	                	
    	                } else if (success) {
    	                    logger.debug("Deleted row with id {}", id);
    	                    fos.write(("\n".concat(id)).getBytes("UTF-8"));
    	                } else if (!success) {
    	                		logger.debug("Failed with error: {}", error);
    	                }
    	            }
    	        }
    	        fos.flush();
    	        fos.close();
    		}          		
    }

    private boolean waitForJobs(BulkConnection connection, JobInfo job, List<BatchInfo> batchInfoList) throws AsyncApiException {
    		boolean isProcessed = false;
		long batchCount = 0;

		while (batchCount < batchInfoList.size()) {
	        try {
	            Thread.sleep(SLEEP_TIME);
	        } catch (InterruptedException e) {}
	        
	        BatchInfo[] statusList =
	          connection.getBatchInfoList(job.getId()).getBatchInfo();
	        for (BatchInfo b : statusList) {
	            if (b.getState() == BatchStateEnum.Completed
	              || b.getState() == BatchStateEnum.Failed) {
	            		logger.debug("Processed Batch Id:{} Status:{}", b.getId(), b.getState());
	            		batchCount++;
	            }
	        }
		}      
		if(batchCount == batchInfoList.size()) {
			isProcessed = true;
		}
		return isProcessed;
    }
    
    private List<BatchInfo> loadBatches(BulkConnection connection,
          JobInfo jobInfo, String csvFileName)
            throws IOException, AsyncApiException {
        List<BatchInfo> listBatchInfo = new ArrayList<BatchInfo>();
        FileInputStream fis = new FileInputStream(csvFileName);        
        BufferedReader rdr = new BufferedReader(
            new InputStreamReader(fis)
        );
        byte[] headerBytes = (rdr.readLine() + "\n").getBytes("UTF-8");

        try {
        		ByteArrayOutputStream tmpOut = new ByteArrayOutputStream(fis.available());
        		tmpOut.write(headerBytes);
            int currentLines = 0;
            String nextLine;
            while ((nextLine = rdr.readLine()) != null) {
                byte[] bytes = (nextLine + "\n").getBytes("UTF-8");
                if (currentLines >= MAX_RECORDS) {
                	salesforceBulkJobImpl.createBatchForJob(tmpOut, listBatchInfo, connection, jobInfo);
                    currentLines = 0;
                    tmpOut.write(headerBytes);
                }
                tmpOut.write(bytes);
                currentLines++;
            }

            if (currentLines >= 1) {
            		salesforceBulkJobImpl.createBatchForJob(tmpOut, listBatchInfo, connection, jobInfo);
            }
        } finally {
        		fis.close();
        }
        return listBatchInfo;
    }

}