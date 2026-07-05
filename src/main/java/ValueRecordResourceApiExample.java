import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.api.ValueRecordResourceApi;
import su.svn.lib.api.auth.HttpBearerAuth;
import su.svn.lib.api.models.NewValueRecord;
import su.svn.lib.api.models.ResourceValueRecord;
import su.svn.lib.api.models.UpdateValueRecord;

import java.time.OffsetDateTime;

@SuppressWarnings("ALL")
public class ValueRecordResourceApiExample {

    public static final String BASE_PATH = "http://localhost:8081";

    @SuppressWarnings("FieldCanBeLocal")
    private final ApiClient defaultClient;
    private final ValueRecordResourceApi apiInstance;

    ValueRecordResourceApiExample(String basePath, String token) {
        defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath(basePath);

        // Configure HTTP bearer authorization: SecurityScheme
        var SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
        SecurityScheme.setBearerToken(token);

        apiInstance = new ValueRecordResourceApi(defaultClient);
    }

    ResourceValueRecord post(NewValueRecord record) throws ApiException {
        return apiInstance.apiV2RecordValuePost(record);
    }

    ResourceValueRecord put(UpdateValueRecord record) throws ApiException {
        return apiInstance.apiV2RecordValuePut(record);
    }


    public static void main(String[] args) {
        var token = new AuthLogin(AuthLogin.AUTH_API, AuthLogin.USER_NAME, AuthLogin.PASSWORD).token();
        var resource = new ValueRecordResourceApiExample(BASE_PATH, token);
        try {
            var newRecordBadRequest1 = new NewValueRecord();
            resource.post(newRecordBadRequest1);
        } catch (ApiException e) {
            System.err.println("Exception when calling ValueRecordResourceApi#apiV2RecordBlobPost");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
        try {
            var newRecordBadRequest2 = new NewValueRecord();
            newRecordBadRequest2.postAt(OffsetDateTime.now());
            resource.post(newRecordBadRequest2);
        } catch (ApiException e) {
            System.err.println("Exception when calling ValueRecordResourceApi#apiV2RecordBlobPost");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
        var record = new NewValueRecord();
        record.value("");
        record.postAt(OffsetDateTime.now());
        ResourceValueRecord resourceRecord;
        try {
            resourceRecord = resource.post(record);
            System.out.println("post resource record = " + resourceRecord);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        assert resourceRecord != null;
        assert resourceRecord.getId() != null;
        assert resourceRecord.getParentId() != null;
        UpdateValueRecord updateRecord = new UpdateValueRecord();
        updateRecord.id(resourceRecord.getId());
        updateRecord.parentId(resourceRecord.getParentId());
        updateRecord.title("updated");
        updateRecord.setaHref("<a href='/'>updated</a>");
        updateRecord.value("value updated");
        updateRecord.refreshAt(OffsetDateTime.now());
        try {
            ResourceValueRecord result = resource.put(updateRecord);
            System.out.println("put result = " + result);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        try {
            UpdateValueRecord updateRecordBadRequest1 = new UpdateValueRecord();
            updateRecordBadRequest1.id(resourceRecord.getId());
            updateRecordBadRequest1.parentId(resourceRecord.getParentId());
            updateRecordBadRequest1.title("updated");
            updateRecordBadRequest1.value(resourceRecord.getValue());
            resource.put(updateRecord);
        } catch (ApiException e) {
            System.err.println("Exception when calling ValueRecordResourceApi#apiV2RecordBlobPut");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
