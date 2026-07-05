# JsonRecordResourceApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**apiV2RecordJsonIdDelete**](JsonRecordResourceApi.md#apiV2RecordJsonIdDelete) | **DELETE** /api/v2/record/json/{id} | Delete JSON record |
| [**apiV2RecordJsonPost**](JsonRecordResourceApi.md#apiV2RecordJsonPost) | **POST** /api/v2/record/json | Create JSON record |
| [**apiV2RecordJsonPut**](JsonRecordResourceApi.md#apiV2RecordJsonPut) | **PUT** /api/v2/record/json | Update JSON record |


<a id="apiV2RecordJsonIdDelete"></a>
# **apiV2RecordJsonIdDelete**
> apiV2RecordJsonIdDelete(id)

Delete JSON record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.JsonRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    JsonRecordResourceApi apiInstance = new JsonRecordResourceApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      apiInstance.apiV2RecordJsonIdDelete(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling JsonRecordResourceApi#apiV2RecordJsonIdDelete");
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

<a id="apiV2RecordJsonPost"></a>
# **apiV2RecordJsonPost**
> ResourceJsonRecord apiV2RecordJsonPost(newJsonRecord)

Create JSON record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.JsonRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    JsonRecordResourceApi apiInstance = new JsonRecordResourceApi(defaultClient);
    NewJsonRecord newJsonRecord = new NewJsonRecord(); // NewJsonRecord | 
    try {
      ResourceJsonRecord result = apiInstance.apiV2RecordJsonPost(newJsonRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling JsonRecordResourceApi#apiV2RecordJsonPost");
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
| **newJsonRecord** | [**NewJsonRecord**](NewJsonRecord.md)|  | |

### Return type

[**ResourceJsonRecord**](ResourceJsonRecord.md)

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

<a id="apiV2RecordJsonPut"></a>
# **apiV2RecordJsonPut**
> ResourceJsonRecord apiV2RecordJsonPut(updateJsonRecord)

Update JSON record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.JsonRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    JsonRecordResourceApi apiInstance = new JsonRecordResourceApi(defaultClient);
    UpdateJsonRecord updateJsonRecord = new UpdateJsonRecord(); // UpdateJsonRecord | 
    try {
      ResourceJsonRecord result = apiInstance.apiV2RecordJsonPut(updateJsonRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling JsonRecordResourceApi#apiV2RecordJsonPut");
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
| **updateJsonRecord** | [**UpdateJsonRecord**](UpdateJsonRecord.md)|  | |

### Return type

[**ResourceJsonRecord**](ResourceJsonRecord.md)

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

