# MarkdownRecordResourceApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**apiV2RecordMarkdownIdDelete**](MarkdownRecordResourceApi.md#apiV2RecordMarkdownIdDelete) | **DELETE** /api/v2/record/markdown/{id} | Delete markdown record |
| [**apiV2RecordMarkdownPost**](MarkdownRecordResourceApi.md#apiV2RecordMarkdownPost) | **POST** /api/v2/record/markdown | Create markdown record |
| [**apiV2RecordMarkdownPut**](MarkdownRecordResourceApi.md#apiV2RecordMarkdownPut) | **PUT** /api/v2/record/markdown | Update markdown record |


<a id="apiV2RecordMarkdownIdDelete"></a>
# **apiV2RecordMarkdownIdDelete**
> apiV2RecordMarkdownIdDelete(id)

Delete markdown record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.MarkdownRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    MarkdownRecordResourceApi apiInstance = new MarkdownRecordResourceApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      apiInstance.apiV2RecordMarkdownIdDelete(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling MarkdownRecordResourceApi#apiV2RecordMarkdownIdDelete");
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

<a id="apiV2RecordMarkdownPost"></a>
# **apiV2RecordMarkdownPost**
> ResourceMarkdownRecord apiV2RecordMarkdownPost(newMarkdownRecord)

Create markdown record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.MarkdownRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    MarkdownRecordResourceApi apiInstance = new MarkdownRecordResourceApi(defaultClient);
    NewMarkdownRecord newMarkdownRecord = new NewMarkdownRecord(); // NewMarkdownRecord | 
    try {
      ResourceMarkdownRecord result = apiInstance.apiV2RecordMarkdownPost(newMarkdownRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling MarkdownRecordResourceApi#apiV2RecordMarkdownPost");
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
| **newMarkdownRecord** | [**NewMarkdownRecord**](NewMarkdownRecord.md)|  | |

### Return type

[**ResourceMarkdownRecord**](ResourceMarkdownRecord.md)

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

<a id="apiV2RecordMarkdownPut"></a>
# **apiV2RecordMarkdownPut**
> ResourceMarkdownRecord apiV2RecordMarkdownPut(updateMarkdownRecord)

Update markdown record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.MarkdownRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    MarkdownRecordResourceApi apiInstance = new MarkdownRecordResourceApi(defaultClient);
    UpdateMarkdownRecord updateMarkdownRecord = new UpdateMarkdownRecord(); // UpdateMarkdownRecord | 
    try {
      ResourceMarkdownRecord result = apiInstance.apiV2RecordMarkdownPut(updateMarkdownRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling MarkdownRecordResourceApi#apiV2RecordMarkdownPut");
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
| **updateMarkdownRecord** | [**UpdateMarkdownRecord**](UpdateMarkdownRecord.md)|  | |

### Return type

[**ResourceMarkdownRecord**](ResourceMarkdownRecord.md)

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

