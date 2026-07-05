# VectorRecordResourceApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**apiV2RecordVectorIdDelete**](VectorRecordResourceApi.md#apiV2RecordVectorIdDelete) | **DELETE** /api/v2/record/vector/{id} | Delete BLOB record |
| [**apiV2RecordVectorPost**](VectorRecordResourceApi.md#apiV2RecordVectorPost) | **POST** /api/v2/record/vector | Create BLOB record |
| [**apiV2RecordVectorPut**](VectorRecordResourceApi.md#apiV2RecordVectorPut) | **PUT** /api/v2/record/vector | Update BLOB record |


<a id="apiV2RecordVectorIdDelete"></a>
# **apiV2RecordVectorIdDelete**
> apiV2RecordVectorIdDelete(id)

Delete BLOB record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.VectorRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    VectorRecordResourceApi apiInstance = new VectorRecordResourceApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      apiInstance.apiV2RecordVectorIdDelete(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling VectorRecordResourceApi#apiV2RecordVectorIdDelete");
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

<a id="apiV2RecordVectorPost"></a>
# **apiV2RecordVectorPost**
> ResourceVectorRecord apiV2RecordVectorPost(newVectorRecord)

Create BLOB record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.VectorRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    VectorRecordResourceApi apiInstance = new VectorRecordResourceApi(defaultClient);
    NewVectorRecord newVectorRecord = new NewVectorRecord(); // NewVectorRecord | 
    try {
      ResourceVectorRecord result = apiInstance.apiV2RecordVectorPost(newVectorRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling VectorRecordResourceApi#apiV2RecordVectorPost");
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
| **newVectorRecord** | [**NewVectorRecord**](NewVectorRecord.md)|  | |

### Return type

[**ResourceVectorRecord**](ResourceVectorRecord.md)

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

<a id="apiV2RecordVectorPut"></a>
# **apiV2RecordVectorPut**
> ResourceVectorRecord apiV2RecordVectorPut(updateVectorRecord)

Update BLOB record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.VectorRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    VectorRecordResourceApi apiInstance = new VectorRecordResourceApi(defaultClient);
    UpdateVectorRecord updateVectorRecord = new UpdateVectorRecord(); // UpdateVectorRecord | 
    try {
      ResourceVectorRecord result = apiInstance.apiV2RecordVectorPut(updateVectorRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling VectorRecordResourceApi#apiV2RecordVectorPut");
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
| **updateVectorRecord** | [**UpdateVectorRecord**](UpdateVectorRecord.md)|  | |

### Return type

[**ResourceVectorRecord**](ResourceVectorRecord.md)

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

