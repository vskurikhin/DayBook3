# UsersApi

All URIs are relative to *http://localhost:64148/auth/api*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**v2UserListGet**](UsersApi.md#v2UserListGet) | **GET** /v2/user/list | Get users list |


<a id="v2UserListGet"></a>
# **v2UserListGet**
> V2UserListGet200Response v2UserListGet()

Get users list

Returns list of users. Requires JWT authentication.

### Example
```java
// Import classes:
import su.svn.lib.auth.ApiClient;
import su.svn.lib.auth.ApiException;
import su.svn.lib.auth.Configuration;
import su.svn.lib.auth.auth.*;
import su.svn.lib.auth.models.*;
import su.svn.lib.auth.api.UsersApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:64148/auth/api");
    
    // Configure API key authorization: BearerAuth
    ApiKeyAuth BearerAuth = (ApiKeyAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //BearerAuth.setApiKeyPrefix("Token");

    UsersApi apiInstance = new UsersApi(defaultClient);
    try {
      V2UserListGet200Response result = apiInstance.v2UserListGet();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling UsersApi#v2UserListGet");
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

[**V2UserListGet200Response**](V2UserListGet200Response.md)

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

