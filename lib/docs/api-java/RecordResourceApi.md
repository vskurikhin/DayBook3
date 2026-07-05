# RecordResourceApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**apiV2RecordsGet**](RecordResourceApi.md#apiV2RecordsGet) | **GET** /api/v2/records | Get page with list of records |
| [**apiV2RecordsIdGet**](RecordResourceApi.md#apiV2RecordsIdGet) | **GET** /api/v2/records/{id} | Retrieves a single record by its unique identifier |


<a id="apiV2RecordsGet"></a>
# **apiV2RecordsGet**
> RecordDataPage apiV2RecordsGet(page, size)

Get page with list of records

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.RecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");

    RecordResourceApi apiInstance = new RecordResourceApi(defaultClient);
    Integer page = 56; // Integer | 
    Integer size = 56; // Integer | 
    try {
      RecordDataPage result = apiInstance.apiV2RecordsGet(page, size);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RecordResourceApi#apiV2RecordsGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **page** | **Integer**|  | [optional] |
| **size** | **Integer**|  | [optional] |

### Return type

[**RecordDataPage**](RecordDataPage.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Created |  -  |
| **500** | Internal Server Error |  -  |

<a id="apiV2RecordsIdGet"></a>
# **apiV2RecordsIdGet**
> RecordData apiV2RecordsIdGet(id)

Retrieves a single record by its unique identifier

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.RecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");

    RecordResourceApi apiInstance = new RecordResourceApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      RecordData result = apiInstance.apiV2RecordsIdGet(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RecordResourceApi#apiV2RecordsIdGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **UUID**|  | |

### Return type

[**RecordData**](RecordData.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **500** | Internal Server Error |  -  |

