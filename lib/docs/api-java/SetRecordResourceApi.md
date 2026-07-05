# SetRecordResourceApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**apiV2RecordSetIdDelete**](SetRecordResourceApi.md#apiV2RecordSetIdDelete) | **DELETE** /api/v2/record/set/{id} | Delete BLOB record |
| [**apiV2RecordSetPost**](SetRecordResourceApi.md#apiV2RecordSetPost) | **POST** /api/v2/record/set | Create BLOB record |
| [**apiV2RecordSetPut**](SetRecordResourceApi.md#apiV2RecordSetPut) | **PUT** /api/v2/record/set | Update BLOB record |


<a id="apiV2RecordSetIdDelete"></a>
# **apiV2RecordSetIdDelete**
> apiV2RecordSetIdDelete(id)

Delete BLOB record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.SetRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    SetRecordResourceApi apiInstance = new SetRecordResourceApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      apiInstance.apiV2RecordSetIdDelete(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling SetRecordResourceApi#apiV2RecordSetIdDelete");
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

<a id="apiV2RecordSetPost"></a>
# **apiV2RecordSetPost**
> ResourceSetRecord apiV2RecordSetPost(newSetRecord)

Create BLOB record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.SetRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    SetRecordResourceApi apiInstance = new SetRecordResourceApi(defaultClient);
    NewSetRecord newSetRecord = new NewSetRecord(); // NewSetRecord | 
    try {
      ResourceSetRecord result = apiInstance.apiV2RecordSetPost(newSetRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SetRecordResourceApi#apiV2RecordSetPost");
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
| **newSetRecord** | [**NewSetRecord**](NewSetRecord.md)|  | |

### Return type

[**ResourceSetRecord**](ResourceSetRecord.md)

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

<a id="apiV2RecordSetPut"></a>
# **apiV2RecordSetPut**
> ResourceSetRecord apiV2RecordSetPut(updateSetRecord)

Update BLOB record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.SetRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    SetRecordResourceApi apiInstance = new SetRecordResourceApi(defaultClient);
    UpdateSetRecord updateSetRecord = new UpdateSetRecord(); // UpdateSetRecord | 
    try {
      ResourceSetRecord result = apiInstance.apiV2RecordSetPut(updateSetRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SetRecordResourceApi#apiV2RecordSetPut");
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
| **updateSetRecord** | [**UpdateSetRecord**](UpdateSetRecord.md)|  | |

### Return type

[**ResourceSetRecord**](ResourceSetRecord.md)

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

