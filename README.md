# salesforce
Salesforce RESTful and BULK Job Sample

Pre Req:
---------
- Maven
- Java JDK
- Salesforce Org 
- Salesforce - Connected app Credentials

Configuration:
--------------
Set below properties pertaining to your SF Org. in test.properties
SF_LIGHTNING_USERNAME=<TODO: Your SF ID>
SF_LIGHTNING_PASSWORD=<TODO: Your SF PASSWORD>
SF_LIGHTNING_CLIENT_ID=<TODO: Your SF CLIENT ID>
SF_LIGHTNING_CLIENT_SECRET=<TODO: Your SF CLIENT SECRET>
SF_LIGHTNING_TOKEN=<TODO: Your SF TOKEN>
SF_LIGHTNING_BATCH_ASYNC_URL=<TODO: Your SF Orgs Async URL>
SF_LIGHTNING_SOBJECT_URL=<TODO: Your SF Orgs Sobjects URL>

RESTful Integrations Tests: com.sf.sforce.SalesforceRestIntegrationTest

Bulk Operation Tests: com.sf.sforce.batch.SalesforceBulkBatchTest

Command:
-------
1> To build and test
mvn clean install

2> To test
mvn test

3> To Build only
mvn clean install -DskipTests
