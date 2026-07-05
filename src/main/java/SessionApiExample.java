import su.svn.lib.auth.ApiClient;
import su.svn.lib.auth.ApiException;
import su.svn.lib.auth.Configuration;
import su.svn.lib.auth.auth.*;
import su.svn.lib.auth.models.*;
import su.svn.lib.auth.api.SessionApi;

public class SessionApiExample {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:64148/auth/api");

        var token = new AuthLogin(AuthLogin.AUTH_API, AuthLogin.USER_NAME, AuthLogin.PASSWORD).token();

        // Configure API key authorization: BearerAuth
        ApiKeyAuth BearerAuth = (ApiKeyAuth) defaultClient.getAuthentication("BearerAuth");
        BearerAuth.setApiKey(token);
        // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
        BearerAuth.setApiKeyPrefix("Bearer");

        SessionApi apiInstance = new SessionApi(defaultClient);
        try {
            V2SessionRolesGet200Response result = apiInstance.v2SessionRolesGet();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling SessionApi#v2SessionRolesGet");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
