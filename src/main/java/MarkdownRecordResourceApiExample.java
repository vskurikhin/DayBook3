import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.api.MarkdownRecordResourceApi;
import su.svn.lib.api.auth.HttpBearerAuth;
import su.svn.lib.api.models.NewMarkdownRecord;
import su.svn.lib.api.models.ResourceMarkdownRecord;
import su.svn.lib.api.models.UpdateMarkdownRecord;

import java.time.OffsetDateTime;

@SuppressWarnings("ALL")
public class MarkdownRecordResourceApiExample {

    public static final String BASE_PATH = "http://localhost:8081";

    @SuppressWarnings("FieldCanBeLocal")
    private final ApiClient defaultClient;
    private final MarkdownRecordResourceApi apiInstance;

    MarkdownRecordResourceApiExample(String basePath, String token) {
        defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath(basePath);

        // Configure HTTP bearer authorization: SecurityScheme
        var SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
        SecurityScheme.setBearerToken(token);

        apiInstance = new MarkdownRecordResourceApi(defaultClient);
    }

    ResourceMarkdownRecord post(NewMarkdownRecord record) throws ApiException {
        return apiInstance.apiV2RecordMarkdownPost(record);
    }

    ResourceMarkdownRecord put(UpdateMarkdownRecord record) throws ApiException {
        return apiInstance.apiV2RecordMarkdownPut(record);
    }


    public static void main(String[] args) {
        var token = new AuthLogin(AuthLogin.AUTH_API, AuthLogin.USER_NAME, AuthLogin.PASSWORD).token();
        var resource = new MarkdownRecordResourceApiExample(BASE_PATH, token);
        try {
            var newRecordBadRequest1 = new NewMarkdownRecord();
            resource.post(newRecordBadRequest1);
        } catch (ApiException e) {
            System.err.println("Exception when calling MarkdownRecordResourceApi#apiV2RecordBlobPost");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
        try {
            var newRecordBadRequest2 = new NewMarkdownRecord();
            newRecordBadRequest2.postAt(OffsetDateTime.now());
            resource.post(newRecordBadRequest2);
        } catch (ApiException e) {
            System.err.println("Exception when calling MarkdownRecordResourceApi#apiV2RecordBlobPost");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
        var record = new NewMarkdownRecord();
        record.markdown("markdown");
        record.postAt(OffsetDateTime.now());
        record.title(null);
        record.aHref(null);
        record.parentId(null);
        record.setTags(null);
        ResourceMarkdownRecord resourceRecord;
        try {
            resourceRecord = resource.post(record);
            System.out.println("post resource record = " + resourceRecord);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        assert resourceRecord != null;
        assert resourceRecord.getId() != null;
        assert resourceRecord.getParentId() != null;
        UpdateMarkdownRecord updateRecord = new UpdateMarkdownRecord();
        updateRecord.id(resourceRecord.getId());
        updateRecord.parentId(resourceRecord.getParentId());
        updateRecord.markdown(resourceRecord.getMarkdown() + " updated");
        updateRecord.refreshAt(OffsetDateTime.now());
        updateRecord.title(null);
        updateRecord.aHref(null);
        updateRecord.setTags(null);
        try {
            ResourceMarkdownRecord result = resource.put(updateRecord);
            System.out.println("put result = " + result);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        try {
            UpdateMarkdownRecord updateRecordBadRequest1 = new UpdateMarkdownRecord();
            updateRecordBadRequest1.id(resourceRecord.getId());
            updateRecordBadRequest1.parentId(resourceRecord.getParentId());
            updateRecordBadRequest1.title("updated");
            updateRecordBadRequest1.markdown("updated");
            resource.put(updateRecord);
        } catch (ApiException e) {
            System.err.println("Exception when calling MarkdownRecordResourceApi#apiV2RecordBlobPut");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
