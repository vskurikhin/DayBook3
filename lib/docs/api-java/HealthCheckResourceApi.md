# HealthCheckResourceApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**healthGet**](HealthCheckResourceApi.md#healthGet) | **GET** /health | Hello |


<a id="healthGet"></a>
# **healthGet**
> String healthGet()

Hello

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.HealthCheckResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");

    HealthCheckResourceApi apiInstance = new HealthCheckResourceApi(defaultClient);
    try {
      String result = apiInstance.healthGet();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling HealthCheckResourceApi#healthGet");
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

**String**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

