# HealthApi

All URIs are relative to *http://localhost:64148/auth/api*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**v2OkGet**](HealthApi.md#v2OkGet) | **GET** /v2/ok | Health check |


<a id="v2OkGet"></a>
# **v2OkGet**
> V2OkGet200Response v2OkGet()

Health check

Returns service status.

### Example
```java
// Import classes:
import su.svn.lib.auth.ApiClient;
import su.svn.lib.auth.ApiException;
import su.svn.lib.auth.Configuration;
import su.svn.lib.auth.models.*;
import su.svn.lib.auth.api.HealthApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:64148/auth/api");

    HealthApi apiInstance = new HealthApi(defaultClient);
    try {
      V2OkGet200Response result = apiInstance.v2OkGet();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling HealthApi#v2OkGet");
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

[**V2OkGet200Response**](V2OkGet200Response.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

