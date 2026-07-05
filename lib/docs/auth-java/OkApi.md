# OkApi

All URIs are relative to *http://localhost:64148/auth/api*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**resourceV1Ok**](OkApi.md#resourceV1Ok) | **GET** /v1/ok | Ok Краткое содержание |


<a id="resourceV1Ok"></a>
# **resourceV1Ok**
> String resourceV1Ok()

Ok Краткое содержание

Ok - Описание (v1)

### Example
```java
// Import classes:
import su.svn.lib.auth.ApiClient;
import su.svn.lib.auth.ApiException;
import su.svn.lib.auth.Configuration;
import su.svn.lib.auth.models.*;
import su.svn.lib.auth.api.OkApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:64148/auth/api");

    OkApi apiInstance = new OkApi(defaultClient);
    try {
      String result = apiInstance.resourceV1Ok();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OkApi#resourceV1Ok");
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
| **200** | успешно! |  -  |

