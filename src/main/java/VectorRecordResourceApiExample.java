import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.api.VectorRecordResourceApi;
import su.svn.lib.api.auth.HttpBearerAuth;
import su.svn.lib.api.models.NewVectorRecord;
import su.svn.lib.api.models.ResourceVectorRecord;
import su.svn.lib.api.models.UpdateVectorRecord;

import java.time.OffsetDateTime;
import java.util.stream.IntStream;

@SuppressWarnings("ALL")
public class VectorRecordResourceApiExample {

    public static final String BASE_PATH = "http://localhost:8081";

    @SuppressWarnings("FieldCanBeLocal")
    private final ApiClient defaultClient;
    private final VectorRecordResourceApi apiInstance;

    VectorRecordResourceApiExample(String basePath, String token) {
        defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath(basePath);

        // Configure HTTP bearer authorization: SecurityScheme
        var SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
        SecurityScheme.setBearerToken(token);

        apiInstance = new VectorRecordResourceApi(defaultClient);
    }

    ResourceVectorRecord post(NewVectorRecord record) throws ApiException {
        return apiInstance.apiV2RecordVectorPost(record);
    }

    ResourceVectorRecord put(UpdateVectorRecord record) throws ApiException {
        return apiInstance.apiV2RecordVectorPut(record);
    }


    public static void main(String[] args) {
        var token = new AuthLogin(AuthLogin.AUTH_API, AuthLogin.USER_NAME, AuthLogin.PASSWORD).token();
        var resource = new VectorRecordResourceApiExample(BASE_PATH, token);
        try {
            var newRecordBadRequest1 = new NewVectorRecord();
            resource.post(newRecordBadRequest1);
        } catch (ApiException e) {
            System.err.println("Exception when calling VectorRecordResourceApi#apiV2RecordBlobPost");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
        try {
            var newRecordBadRequest2 = new NewVectorRecord();
            newRecordBadRequest2.postAt(OffsetDateTime.now());
            resource.post(newRecordBadRequest2);
        } catch (ApiException e) {
            System.err.println("Exception when calling VectorRecordResourceApi#apiV2RecordBlobPost");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
        var record = new NewVectorRecord();
        var listVector = IntStream.rangeClosed(1, 1024)
                .mapToObj(Float::valueOf)
                .toList();
        record.vector(listVector);
        record.postAt(OffsetDateTime.now());
        ResourceVectorRecord resourceRecord;
        try {
            resourceRecord = resource.post(record);
            System.out.println("post resource record = " + resourceRecord);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        assert resourceRecord != null;
        assert resourceRecord.getId() != null;
        assert resourceRecord.getParentId() != null;
        UpdateVectorRecord updateRecord = new UpdateVectorRecord();
        updateRecord.id(resourceRecord.getId());
        updateRecord.parentId(resourceRecord.getParentId());
        updateRecord.title("updated");
        updateRecord.setaHref("<a href='/'>updated</a>");
        updateRecord.vector(resourceRecord.getVector());
        updateRecord.refreshAt(OffsetDateTime.now());
        try {
            ResourceVectorRecord result = resource.put(updateRecord);
            System.out.println("put result = " + result);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        try {
            UpdateVectorRecord updateRecordBadRequest1 = new UpdateVectorRecord();
            updateRecordBadRequest1.id(resourceRecord.getId());
            updateRecordBadRequest1.parentId(resourceRecord.getParentId());
            updateRecordBadRequest1.title("updated");
            updateRecordBadRequest1.vector(null);
            resource.put(updateRecord);
        } catch (ApiException e) {
            System.err.println("Exception when calling VectorRecordResourceApi#apiV2RecordBlobPut");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
