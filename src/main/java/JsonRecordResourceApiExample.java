import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.api.JsonRecordResourceApi;
import su.svn.lib.api.auth.HttpBearerAuth;
import su.svn.lib.api.models.NewJsonRecord;
import su.svn.lib.api.models.ResourceJsonRecord;
import su.svn.lib.api.models.UpdateJsonRecord;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;

@SuppressWarnings("ALL")
public class JsonRecordResourceApiExample {

    public static final String BASE_PATH = "http://localhost:8081";

    @SuppressWarnings("FieldCanBeLocal")
    private final ApiClient defaultClient;
    private final JsonRecordResourceApi apiInstance;

    JsonRecordResourceApiExample(String basePath, String token) {
        defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath(basePath);

        // Configure HTTP bearer authorization: SecurityScheme
        var SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
        SecurityScheme.setBearerToken(token);

        apiInstance = new JsonRecordResourceApi(defaultClient);
    }

    ResourceJsonRecord post(NewJsonRecord record) throws ApiException {
        return apiInstance.apiV2RecordJsonPost(record);
    }

    ResourceJsonRecord put(UpdateJsonRecord record) throws ApiException {
        return apiInstance.apiV2RecordJsonPut(record);
    }


    public static void main(String[] args) {
        var token = new AuthLogin(AuthLogin.AUTH_API, AuthLogin.USER_NAME, AuthLogin.PASSWORD).token();
        var resource = new JsonRecordResourceApiExample(BASE_PATH, token);
        try {
            var newRecordBadRequest1 = new NewJsonRecord();
            resource.post(newRecordBadRequest1);
        } catch (ApiException e) {
            System.err.println("Exception when calling JsonRecordResourceApi#apiV2RecordBlobPost");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
        try {
            var newRecordBadRequest2 = new NewJsonRecord();
            newRecordBadRequest2.postAt(OffsetDateTime.now());
            newRecordBadRequest2.json(null);
            resource.post(newRecordBadRequest2);
        } catch (ApiException e) {
            System.err.println("Exception when calling JsonRecordResourceApi#apiV2RecordBlobPost");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
        var record = new NewJsonRecord();
        record.json(Collections.emptyMap());
        record.postAt(OffsetDateTime.now());
        ResourceJsonRecord resourceRecord;
        try {
            resourceRecord = resource.post(record);
            System.out.println("post resource record = " + resourceRecord);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        assert resourceRecord != null;
        assert resourceRecord.getId() != null;
        assert resourceRecord.getParentId() != null;
        UpdateJsonRecord updateRecord = new UpdateJsonRecord();
        updateRecord.id(resourceRecord.getId());
        updateRecord.parentId(resourceRecord.getParentId());
        updateRecord.title("updated");
        updateRecord.setaHref("<a href='/'>updated</a>");
        updateRecord.json(Map.of("json", "updated"));
        updateRecord.refreshAt(OffsetDateTime.now());
        try {
            ResourceJsonRecord result = resource.put(updateRecord);
            System.out.println("put result = " + result);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        try {
            UpdateJsonRecord updateRecordBadRequest1 = new UpdateJsonRecord();
            updateRecordBadRequest1.id(resourceRecord.getId());
            updateRecordBadRequest1.parentId(resourceRecord.getParentId());
            updateRecordBadRequest1.title("updated");
            updateRecordBadRequest1.json(null);
            resource.put(updateRecord);
        } catch (ApiException e) {
            System.err.println("Exception when calling JsonRecordResourceApi#apiV2RecordBlobPut");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
