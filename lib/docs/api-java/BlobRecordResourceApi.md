# BlobRecordResourceApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**apiV2RecordBlobIdDelete**](BlobRecordResourceApi.md#apiV2RecordBlobIdDelete) | **DELETE** /api/v2/record/blob/{id} | Delete BLOB record |
| [**apiV2RecordBlobPost**](BlobRecordResourceApi.md#apiV2RecordBlobPost) | **POST** /api/v2/record/blob | Create BLOB record |
| [**apiV2RecordBlobPut**](BlobRecordResourceApi.md#apiV2RecordBlobPut) | **PUT** /api/v2/record/blob | Update BLOB record |


<a id="apiV2RecordBlobIdDelete"></a>
# **apiV2RecordBlobIdDelete**
> apiV2RecordBlobIdDelete(id)

Delete BLOB record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.BlobRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    BlobRecordResourceApi apiInstance = new BlobRecordResourceApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      apiInstance.apiV2RecordBlobIdDelete(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling BlobRecordResourceApi#apiV2RecordBlobIdDelete");
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

<a id="apiV2RecordBlobPost"></a>
# **apiV2RecordBlobPost**
> ResourceBlobRecord apiV2RecordBlobPost(newBlobRecord)

Create BLOB record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.BlobRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    BlobRecordResourceApi apiInstance = new BlobRecordResourceApi(defaultClient);
    NewBlobRecord newBlobRecord = new NewBlobRecord(); // NewBlobRecord | 
    try {
      ResourceBlobRecord result = apiInstance.apiV2RecordBlobPost(newBlobRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling BlobRecordResourceApi#apiV2RecordBlobPost");
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
| **newBlobRecord** | [**NewBlobRecord**](NewBlobRecord.md)|  | |

### Return type

[**ResourceBlobRecord**](ResourceBlobRecord.md)

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

<a id="apiV2RecordBlobPut"></a>
# **apiV2RecordBlobPut**
> ResourceBlobRecord apiV2RecordBlobPut(updateBlobRecord)

Update BLOB record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.BlobRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    BlobRecordResourceApi apiInstance = new BlobRecordResourceApi(defaultClient);
    UpdateBlobRecord updateBlobRecord = new UpdateBlobRecord(); // UpdateBlobRecord | 
    try {
      ResourceBlobRecord result = apiInstance.apiV2RecordBlobPut(updateBlobRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling BlobRecordResourceApi#apiV2RecordBlobPut");
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
| **updateBlobRecord** | [**UpdateBlobRecord**](UpdateBlobRecord.md)|  | |

### Return type

[**ResourceBlobRecord**](ResourceBlobRecord.md)

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

