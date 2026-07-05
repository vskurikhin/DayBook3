# MarkdownRecordControllerApi

All URIs are relative to *http://localhost:8082*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createMarkdownRecord**](MarkdownRecordControllerApi.md#createMarkdownRecord) | **POST** /core/api/v2/markdown-record |  |
| [**deleteMarkdownRecord**](MarkdownRecordControllerApi.md#deleteMarkdownRecord) | **DELETE** /core/api/v2/markdown-record/{id} |  |
| [**readMarkdownRecord**](MarkdownRecordControllerApi.md#readMarkdownRecord) | **GET** /core/api/v2/markdown-record/{id} |  |
| [**updateMarkdownRecord**](MarkdownRecordControllerApi.md#updateMarkdownRecord) | **PUT** /core/api/v2/markdown-record |  |


<a id="createMarkdownRecord"></a>
# **createMarkdownRecord**
> ResourceMarkdownRecord createMarkdownRecord(newMarkdownRecord)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.MarkdownRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    MarkdownRecordControllerApi apiInstance = new MarkdownRecordControllerApi(defaultClient);
    NewMarkdownRecord newMarkdownRecord = new NewMarkdownRecord(); // NewMarkdownRecord | 
    try {
      ResourceMarkdownRecord result = apiInstance.createMarkdownRecord(newMarkdownRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling MarkdownRecordControllerApi#createMarkdownRecord");
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

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **400** | Bad Request |  -  |
| **404** | Not Found |  -  |
| **500** | Internal Server Error |  -  |
| **200** | OK |  -  |

<a id="deleteMarkdownRecord"></a>
# **deleteMarkdownRecord**
> deleteMarkdownRecord(id)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.MarkdownRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    MarkdownRecordControllerApi apiInstance = new MarkdownRecordControllerApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      apiInstance.deleteMarkdownRecord(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling MarkdownRecordControllerApi#deleteMarkdownRecord");
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

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **400** | Bad Request |  -  |
| **404** | Not Found |  -  |
| **500** | Internal Server Error |  -  |
| **200** | OK |  -  |

<a id="readMarkdownRecord"></a>
# **readMarkdownRecord**
> ResourceMarkdownRecord readMarkdownRecord(id)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.MarkdownRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    MarkdownRecordControllerApi apiInstance = new MarkdownRecordControllerApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      ResourceMarkdownRecord result = apiInstance.readMarkdownRecord(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling MarkdownRecordControllerApi#readMarkdownRecord");
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

[**ResourceMarkdownRecord**](ResourceMarkdownRecord.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **400** | Bad Request |  -  |
| **404** | Not Found |  -  |
| **500** | Internal Server Error |  -  |
| **200** | OK |  -  |

<a id="updateMarkdownRecord"></a>
# **updateMarkdownRecord**
> ResourceMarkdownRecord updateMarkdownRecord(updateMarkdownRecord)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.MarkdownRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    MarkdownRecordControllerApi apiInstance = new MarkdownRecordControllerApi(defaultClient);
    UpdateMarkdownRecord updateMarkdownRecord = new UpdateMarkdownRecord(); // UpdateMarkdownRecord | 
    try {
      ResourceMarkdownRecord result = apiInstance.updateMarkdownRecord(updateMarkdownRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling MarkdownRecordControllerApi#updateMarkdownRecord");
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

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **400** | Bad Request |  -  |
| **404** | Not Found |  -  |
| **500** | Internal Server Error |  -  |
| **200** | OK |  -  |

