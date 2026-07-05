import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.api.BlobRecordResourceApi;
import su.svn.lib.api.auth.HttpBearerAuth;
import su.svn.lib.api.models.NewBlobRecord;
import su.svn.lib.api.models.ResourceBlobRecord;
import su.svn.lib.api.models.UpdateBlobRecord;

import java.time.OffsetDateTime;

@SuppressWarnings("ALL")
public class BlobRecordResourceApiExample {

    public static final String BASE_PATH = "http://localhost:8081";

    @SuppressWarnings("FieldCanBeLocal")
    private final ApiClient defaultClient;
    private final BlobRecordResourceApi apiInstance;

    BlobRecordResourceApiExample(String basePath, String token) {
        defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath(basePath);

        // Configure HTTP bearer authorization: SecurityScheme
        var SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
        SecurityScheme.setBearerToken(token);

        apiInstance = new BlobRecordResourceApi(defaultClient);
    }

    ResourceBlobRecord post(NewBlobRecord record) throws ApiException {
        return apiInstance.apiV2RecordBlobPost(record);
    }

    ResourceBlobRecord put(UpdateBlobRecord record) throws ApiException {
        return apiInstance.apiV2RecordBlobPut(record);
    }


    public static void main(String[] args) {
        var token = new AuthLogin(AuthLogin.AUTH_API, AuthLogin.USER_NAME, AuthLogin.PASSWORD).token();
        var resource = new BlobRecordResourceApiExample(BASE_PATH, token);
        try {
            var newRecordBadRequest1 = new NewBlobRecord();
            resource.post(newRecordBadRequest1);
        } catch (ApiException e) {
            System.err.println("Exception when calling BlobRecordResourceApi#apiV2RecordBlobPost");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
        try {
            var newRecordBadRequest2 = new NewBlobRecord();
            newRecordBadRequest2.postAt(OffsetDateTime.now());
            resource.post(newRecordBadRequest2);
        } catch (ApiException e) {
            System.err.println("Exception when calling BlobRecordResourceApi#apiV2RecordBlobPost");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
        var record = new NewBlobRecord();
        record.blob("");
        record.postAt(OffsetDateTime.now());
        ResourceBlobRecord resourceRecord;
        try {
            resourceRecord = resource.post(record);
            System.out.println("post resource record = " + resourceRecord);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        assert resourceRecord != null;
        assert resourceRecord.getId() != null;
        assert resourceRecord.getParentId() != null;
        UpdateBlobRecord updateRecord = new UpdateBlobRecord();
        updateRecord.id(resourceRecord.getId());
        updateRecord.parentId(resourceRecord.getParentId());
        updateRecord.title("updated");
        updateRecord.setaHref("<a href='/'>updated</a>");
        updateRecord.blob(resourceRecord.getBlob());
        updateRecord.refreshAt(OffsetDateTime.now());
        try {
            ResourceBlobRecord result = resource.put(updateRecord);
            System.out.println("put result = " + result);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        try {
            UpdateBlobRecord updateRecordBadRequest1 = new UpdateBlobRecord();
            updateRecordBadRequest1.id(resourceRecord.getId());
            updateRecordBadRequest1.parentId(resourceRecord.getParentId());
            updateRecordBadRequest1.title("updated");
            updateRecordBadRequest1.blob("updated");
            resource.put(updateRecord);
        } catch (ApiException e) {
            System.err.println("Exception when calling BlobRecordResourceApi#apiV2RecordBlobPut");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
