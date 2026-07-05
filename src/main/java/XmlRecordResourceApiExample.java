import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.api.XmlRecordResourceApi;
import su.svn.lib.api.auth.HttpBearerAuth;
import su.svn.lib.api.models.NewXmlRecord;
import su.svn.lib.api.models.ResourceXmlRecord;
import su.svn.lib.api.models.UpdateXmlRecord;

import java.time.OffsetDateTime;

@SuppressWarnings("ALL")
public class XmlRecordResourceApiExample {

    public static final String BASE_PATH = "http://localhost:8081";

    @SuppressWarnings("FieldCanBeLocal")
    private final ApiClient defaultClient;
    private final XmlRecordResourceApi apiInstance;

    XmlRecordResourceApiExample(String basePath, String token) {
        defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath(basePath);

        // Configure HTTP bearer authorization: SecurityScheme
        var SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
        SecurityScheme.setBearerToken(token);

        apiInstance = new XmlRecordResourceApi(defaultClient);
    }

    ResourceXmlRecord post(NewXmlRecord record) throws ApiException {
        return apiInstance.apiV2RecordXmlPost(record);
    }

    ResourceXmlRecord put(UpdateXmlRecord record) throws ApiException {
        return apiInstance.apiV2RecordXmlPut(record);
    }


    public static void main(String[] args) {
        var token = new AuthLogin(AuthLogin.AUTH_API, AuthLogin.USER_NAME, AuthLogin.PASSWORD).token();
        var resource = new XmlRecordResourceApiExample(BASE_PATH, token);
        try {
            var newRecordBadRequest1 = new NewXmlRecord();
            resource.post(newRecordBadRequest1);
        } catch (ApiException e) {
            System.err.println("Exception when calling XmlRecordResourceApi#apiV2RecordBlobPost");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
        try {
            var newRecordBadRequest2 = new NewXmlRecord();
            newRecordBadRequest2.postAt(OffsetDateTime.now());
            resource.post(newRecordBadRequest2);
        } catch (ApiException e) {
            System.err.println("Exception when calling XmlRecordResourceApi#apiV2RecordBlobPost");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
        var record = new NewXmlRecord();
        record.xml("<root/>");
        record.postAt(OffsetDateTime.now());
        ResourceXmlRecord resourceRecord;
        try {
            resourceRecord = resource.post(record);
            System.out.println("post resource record = " + resourceRecord);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        assert resourceRecord != null;
        assert resourceRecord.getId() != null;
        assert resourceRecord.getParentId() != null;
        UpdateXmlRecord updateRecord = new UpdateXmlRecord();
        updateRecord.id(resourceRecord.getId());
        updateRecord.parentId(resourceRecord.getParentId());
        updateRecord.title("updated");
        updateRecord.setaHref("<a href='/'>updated</a>");
        updateRecord.xml("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><updated/></root>");
        updateRecord.refreshAt(OffsetDateTime.now());
        try {
            ResourceXmlRecord result = resource.put(updateRecord);
            System.out.println("put result = " + result);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        try {
            UpdateXmlRecord updateRecordBadRequest1 = new UpdateXmlRecord();
            updateRecordBadRequest1.id(resourceRecord.getId());
            updateRecordBadRequest1.parentId(resourceRecord.getParentId());
            updateRecordBadRequest1.title("updated");
            updateRecordBadRequest1.xml(resourceRecord.getXml());
            resource.put(updateRecord);
        } catch (ApiException e) {
            System.err.println("Exception when calling XmlRecordResourceApi#apiV2RecordBlobPut");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
