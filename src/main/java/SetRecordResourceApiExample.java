import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.api.SetRecordResourceApi;
import su.svn.lib.api.auth.HttpBearerAuth;
import su.svn.lib.api.models.NewSetRecord;
import su.svn.lib.api.models.ResourceSetRecord;
import su.svn.lib.api.models.UpdateSetRecord;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("ALL")
public class SetRecordResourceApiExample {

    public static final String BASE_PATH = "http://localhost:8081";

    @SuppressWarnings("FieldCanBeLocal")
    private final ApiClient defaultClient;
    private final SetRecordResourceApi apiInstance;

    SetRecordResourceApiExample(String basePath, String token) {
        defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath(basePath);

        // Configure HTTP bearer authorization: SecurityScheme
        var SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
        SecurityScheme.setBearerToken(token);

        apiInstance = new SetRecordResourceApi(defaultClient);
    }

    ResourceSetRecord post(NewSetRecord record) throws ApiException {
        return apiInstance.apiV2RecordSetPost(record);
    }

    ResourceSetRecord put(UpdateSetRecord record) throws ApiException {
        return apiInstance.apiV2RecordSetPut(record);
    }


    public static void main(String[] args) {
        var token = new AuthLogin(AuthLogin.AUTH_API, AuthLogin.USER_NAME, AuthLogin.PASSWORD).token();
        var resource = new SetRecordResourceApiExample(BASE_PATH, token);
        try {
            var newRecordBadRequest1 = new NewSetRecord();
            resource.post(newRecordBadRequest1);
        } catch (ApiException e) {
            System.err.println("Exception when calling SetRecordResourceApi#apiV2RecordBlobPost");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
        try {
            var newRecordBadRequest2 = new NewSetRecord();
            newRecordBadRequest2.postAt(OffsetDateTime.now());
            newRecordBadRequest2.texts(null);
            resource.post(newRecordBadRequest2);
        } catch (ApiException e) {
            System.err.println("Exception when calling SetRecordResourceApi#apiV2RecordBlobPost");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
        var record = new NewSetRecord();
        record.texts(Collections.emptySet());
        record.postAt(OffsetDateTime.now());
        ResourceSetRecord resourceRecord;
        try {
            resourceRecord = resource.post(record);
            System.out.println("post resource record = " + resourceRecord);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        assert resourceRecord != null;
        assert resourceRecord.getId() != null;
        assert resourceRecord.getParentId() != null;
        UpdateSetRecord updateRecord = new UpdateSetRecord();
        updateRecord.id(resourceRecord.getId());
        updateRecord.parentId(resourceRecord.getParentId());
        updateRecord.title("updated");
        updateRecord.setaHref("<a href='/'>updated</a>");
        updateRecord.texts(Set.of("set", "updated"));
        updateRecord.refreshAt(OffsetDateTime.now());
        try {
            ResourceSetRecord result = resource.put(updateRecord);
            System.out.println("put result = " + result);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        try {
            UpdateSetRecord updateRecordBadRequest1 = new UpdateSetRecord();
            updateRecordBadRequest1.id(resourceRecord.getId());
            updateRecordBadRequest1.parentId(resourceRecord.getParentId());
            updateRecordBadRequest1.title("updated");
            updateRecordBadRequest1.texts(null);
            resource.put(updateRecord);
        } catch (ApiException e) {
            System.err.println("Exception when calling SetRecordResourceApi#apiV2RecordBlobPut");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
