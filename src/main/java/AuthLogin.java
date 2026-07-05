import su.svn.lib.auth.ApiClient;
import su.svn.lib.auth.ApiException;
import su.svn.lib.auth.Configuration;
import su.svn.lib.auth.api.AuthApi;
import su.svn.lib.auth.models.Login;
import su.svn.lib.auth.models.V2AuthPost200Response;

public class AuthLogin {

    public static final String AUTH_API = "http://localhost:64148/auth/api";
    public static final String USER_NAME = "user";
    public static final String PASSWORD = "password";

    private final String token;

    AuthLogin(String url, String userName, String password) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath(url);

        AuthApi apiInstance = new AuthApi(defaultClient);
        Login request = new Login(); // Login | User credentials
        request.setUserName(userName);
        request.setPassword(password);
        try {
            V2AuthPost200Response result = apiInstance.v2AuthPost(request);
            System.out.printf("status: %b\n", result.getSuccess());
            if (Boolean.FALSE.equals(result.getSuccess())) {
                throw new RuntimeException("isn't Success");
            }
            if (result.getData() == null) {
                throw new RuntimeException("token is null");
            }
            token = result.getData().getToken();
        } catch (ApiException e) {
            System.err.println("Exception when calling AuthApi#v2AuthPost");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            throw new RuntimeException(e);
        }
    }

    public String token() {
        return this.token;
    }

    public static void main(String[] args) {
        try {
            var token = new AuthLogin(AUTH_API, USER_NAME, PASSWORD).token;
            System.out.println("token = " + token);
        } catch (RuntimeException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
