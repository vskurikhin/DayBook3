# JsonRecordControllerApi

All URIs are relative to *http://localhost:8082*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createJsonRecord**](JsonRecordControllerApi.md#createJsonRecord) | **POST** /core/api/v2/json-record |  |
| [**deleteJsonRecord**](JsonRecordControllerApi.md#deleteJsonRecord) | **DELETE** /core/api/v2/json-record/{id} |  |
| [**readJsonRecord**](JsonRecordControllerApi.md#readJsonRecord) | **GET** /core/api/v2/json-record/{id} |  |
| [**updateJsonRecord**](JsonRecordControllerApi.md#updateJsonRecord) | **PUT** /core/api/v2/json-record |  |


<a id="createJsonRecord"></a>
# **createJsonRecord**
> ResourceJsonRecord createJsonRecord(newJsonRecord)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.JsonRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    JsonRecordControllerApi apiInstance = new JsonRecordControllerApi(defaultClient);
    NewJsonRecord newJsonRecord = new NewJsonRecord(); // NewJsonRecord | 
    try {
      ResourceJsonRecord result = apiInstance.createJsonRecord(newJsonRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling JsonRecordControllerApi#createJsonRecord");
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

<a id="deleteJsonRecord"></a>
# **deleteJsonRecord**
> deleteJsonRecord(id)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.JsonRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    JsonRecordControllerApi apiInstance = new JsonRecordControllerApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      apiInstance.deleteJsonRecord(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling JsonRecordControllerApi#deleteJsonRecord");
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

<a id="readJsonRecord"></a>
# **readJsonRecord**
> ResourceJsonRecord readJsonRecord(id)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.JsonRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    JsonRecordControllerApi apiInstance = new JsonRecordControllerApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      ResourceJsonRecord result = apiInstance.readJsonRecord(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling JsonRecordControllerApi#readJsonRecord");
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

[**ResourceJsonRecord**](ResourceJsonRecord.md)

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

<a id="updateJsonRecord"></a>
# **updateJsonRecord**
> ResourceJsonRecord updateJsonRecord(updateJsonRecord)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.JsonRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    JsonRecordControllerApi apiInstance = new JsonRecordControllerApi(defaultClient);
    UpdateJsonRecord updateJsonRecord = new UpdateJsonRecord(); // UpdateJsonRecord | 
    try {
      ResourceJsonRecord result = apiInstance.updateJsonRecord(updateJsonRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling JsonRecordControllerApi#updateJsonRecord");
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

