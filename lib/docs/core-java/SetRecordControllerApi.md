# SetRecordControllerApi

All URIs are relative to *http://localhost:8082*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createSetRecord**](SetRecordControllerApi.md#createSetRecord) | **POST** /core/api/v2/set-record |  |
| [**deleteSetRecord**](SetRecordControllerApi.md#deleteSetRecord) | **DELETE** /core/api/v2/set-record/{id} |  |
| [**readSetRecord**](SetRecordControllerApi.md#readSetRecord) | **GET** /core/api/v2/set-record/{id} |  |
| [**updateSetRecord**](SetRecordControllerApi.md#updateSetRecord) | **PUT** /core/api/v2/set-record |  |


<a id="createSetRecord"></a>
# **createSetRecord**
> ResourceSetRecord createSetRecord(newSetRecord)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.SetRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    SetRecordControllerApi apiInstance = new SetRecordControllerApi(defaultClient);
    NewSetRecord newSetRecord = new NewSetRecord(); // NewSetRecord | 
    try {
      ResourceSetRecord result = apiInstance.createSetRecord(newSetRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SetRecordControllerApi#createSetRecord");
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

<a id="deleteSetRecord"></a>
# **deleteSetRecord**
> deleteSetRecord(id)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.SetRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    SetRecordControllerApi apiInstance = new SetRecordControllerApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      apiInstance.deleteSetRecord(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling SetRecordControllerApi#deleteSetRecord");
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

<a id="readSetRecord"></a>
# **readSetRecord**
> ResourceSetRecord readSetRecord(id)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.SetRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    SetRecordControllerApi apiInstance = new SetRecordControllerApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      ResourceSetRecord result = apiInstance.readSetRecord(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SetRecordControllerApi#readSetRecord");
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

[**ResourceSetRecord**](ResourceSetRecord.md)

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

<a id="updateSetRecord"></a>
# **updateSetRecord**
> ResourceSetRecord updateSetRecord(updateSetRecord)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.SetRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    SetRecordControllerApi apiInstance = new SetRecordControllerApi(defaultClient);
    UpdateSetRecord updateSetRecord = new UpdateSetRecord(); // UpdateSetRecord | 
    try {
      ResourceSetRecord result = apiInstance.updateSetRecord(updateSetRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SetRecordControllerApi#updateSetRecord");
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

