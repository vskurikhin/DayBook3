import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.api.RecordResourceApi;
import su.svn.lib.api.auth.HttpBearerAuth;
import su.svn.lib.api.models.NewBlobRecord;
import su.svn.lib.api.models.RecordDataPage;

@SuppressWarnings("ALL")
public class RecordResourceApiExample {

    public static final String BASE_PATH = "http://localhost:8081";

    @SuppressWarnings("FieldCanBeLocal")
    private final ApiClient defaultClient;
    private final RecordResourceApi apiInstance;

    RecordResourceApiExample(String basePath, String token) {
        defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath(basePath);

        // Configure HTTP bearer authorization: SecurityScheme
        var SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
        SecurityScheme.setBearerToken(token);

        apiInstance = new RecordResourceApi(defaultClient);
    }

    RecordDataPage get(int page, int size) throws ApiException {
        return apiInstance.apiV2RecordsGet(page, size);
    }


    public static void main(String[] args) {
        var token = new AuthLogin(AuthLogin.AUTH_API, AuthLogin.USER_NAME, AuthLogin.PASSWORD).token();
        var resource = new RecordResourceApiExample(BASE_PATH, token);
        try {
            var newRecordBadRequest1 = new NewBlobRecord();
            var page = resource.get(0, 127);
            System.out.println("page = " + page);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }
}
