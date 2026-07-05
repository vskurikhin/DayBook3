# ValueRecordResourceApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**apiV2RecordValueIdDelete**](ValueRecordResourceApi.md#apiV2RecordValueIdDelete) | **DELETE** /api/v2/record/value/{id} | Delete BLOB record |
| [**apiV2RecordValuePost**](ValueRecordResourceApi.md#apiV2RecordValuePost) | **POST** /api/v2/record/value | Create BLOB record |
| [**apiV2RecordValuePut**](ValueRecordResourceApi.md#apiV2RecordValuePut) | **PUT** /api/v2/record/value | Update BLOB record |


<a id="apiV2RecordValueIdDelete"></a>
# **apiV2RecordValueIdDelete**
> apiV2RecordValueIdDelete(id)

Delete BLOB record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.ValueRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    ValueRecordResourceApi apiInstance = new ValueRecordResourceApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      apiInstance.apiV2RecordValueIdDelete(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling ValueRecordResourceApi#apiV2RecordValueIdDelete");
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

null (empty response body)

### Authorization

[SecurityScheme](../README.md#SecurityScheme)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | No Content |  -  |
| **500** | Internal Server Error |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

<a id="apiV2RecordValuePost"></a>
# **apiV2RecordValuePost**
> ResourceValueRecord apiV2RecordValuePost(newValueRecord)

Create BLOB record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.ValueRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    ValueRecordResourceApi apiInstance = new ValueRecordResourceApi(defaultClient);
    NewValueRecord newValueRecord = new NewValueRecord(); // NewValueRecord | 
    try {
      ResourceValueRecord result = apiInstance.apiV2RecordValuePost(newValueRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ValueRecordResourceApi#apiV2RecordValuePost");
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
| **newValueRecord** | [**NewValueRecord**](NewValueRecord.md)|  | |

### Return type

[**ResourceValueRecord**](ResourceValueRecord.md)

### Authorization

[SecurityScheme](../README.md#SecurityScheme)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Created |  -  |
| **500** | Internal Server Error |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |
| **400** | Bad Request |  -  |

<a id="apiV2RecordValuePut"></a>
# **apiV2RecordValuePut**
> ResourceValueRecord apiV2RecordValuePut(updateValueRecord)

Update BLOB record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.ValueRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    ValueRecordResourceApi apiInstance = new ValueRecordResourceApi(defaultClient);
    UpdateValueRecord updateValueRecord = new UpdateValueRecord(); // UpdateValueRecord | 
    try {
      ResourceValueRecord result = apiInstance.apiV2RecordValuePut(updateValueRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ValueRecordResourceApi#apiV2RecordValuePut");
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
| **updateValueRecord** | [**UpdateValueRecord**](UpdateValueRecord.md)|  | |

### Return type

[**ResourceValueRecord**](ResourceValueRecord.md)

### Authorization

[SecurityScheme](../README.md#SecurityScheme)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **500** | Internal Server Error |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |
| **400** | Bad Request |  -  |

