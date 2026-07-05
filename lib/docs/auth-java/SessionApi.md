# SessionApi

All URIs are relative to *http://localhost:64148/auth/api*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**v2SessionRolesGet**](SessionApi.md#v2SessionRolesGet) | **GET** /v2/session/roles | for user get roles in session |


<a id="v2SessionRolesGet"></a>
# **v2SessionRolesGet**
> V2SessionRolesGet200Response v2SessionRolesGet()

for user get roles in session

Invalidates user session. Requires JWT authentication.

### Example
```java
// Import classes:
import su.svn.lib.auth.ApiClient;
import su.svn.lib.auth.ApiException;
import su.svn.lib.auth.Configuration;
import su.svn.lib.auth.auth.*;
import su.svn.lib.auth.models.*;
import su.svn.lib.auth.api.SessionApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:64148/auth/api");
    
    // Configure API key authorization: BearerAuth
    ApiKeyAuth BearerAuth = (ApiKeyAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //BearerAuth.setApiKeyPrefix("Token");

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
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**V2SessionRolesGet200Response**](V2SessionRolesGet200Response.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | unauthorized |  -  |
| **500** | server error &#39;success&#39;: false |  -  |
| **503** | service unavailable |  -  |
| **504** | gateway timeout |  -  |

