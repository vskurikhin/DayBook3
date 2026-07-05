# VectorRecordControllerApi

All URIs are relative to *http://localhost:8082*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createVectorRecord**](VectorRecordControllerApi.md#createVectorRecord) | **POST** /core/api/v2/vector-record |  |
| [**deleteVectorRecord**](VectorRecordControllerApi.md#deleteVectorRecord) | **DELETE** /core/api/v2/vector-record/{id} |  |
| [**readVectorRecord**](VectorRecordControllerApi.md#readVectorRecord) | **GET** /core/api/v2/vector-record/{id} |  |
| [**updateVectorRecord**](VectorRecordControllerApi.md#updateVectorRecord) | **PUT** /core/api/v2/vector-record |  |


<a id="createVectorRecord"></a>
# **createVectorRecord**
> ResourceVectorRecord createVectorRecord(newVectorRecord)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.VectorRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    VectorRecordControllerApi apiInstance = new VectorRecordControllerApi(defaultClient);
    NewVectorRecord newVectorRecord = new NewVectorRecord(); // NewVectorRecord | 
    try {
      ResourceVectorRecord result = apiInstance.createVectorRecord(newVectorRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling VectorRecordControllerApi#createVectorRecord");
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

<a id="deleteVectorRecord"></a>
# **deleteVectorRecord**
> deleteVectorRecord(id)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.VectorRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    VectorRecordControllerApi apiInstance = new VectorRecordControllerApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      apiInstance.deleteVectorRecord(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling VectorRecordControllerApi#deleteVectorRecord");
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

<a id="readVectorRecord"></a>
# **readVectorRecord**
> ResourceVectorRecord readVectorRecord(id)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.VectorRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    VectorRecordControllerApi apiInstance = new VectorRecordControllerApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      ResourceVectorRecord result = apiInstance.readVectorRecord(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling VectorRecordControllerApi#readVectorRecord");
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

[**ResourceVectorRecord**](ResourceVectorRecord.md)

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

<a id="updateVectorRecord"></a>
# **updateVectorRecord**
> ResourceVectorRecord updateVectorRecord(updateVectorRecord)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.VectorRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    VectorRecordControllerApi apiInstance = new VectorRecordControllerApi(defaultClient);
    UpdateVectorRecord updateVectorRecord = new UpdateVectorRecord(); // UpdateVectorRecord | 
    try {
      ResourceVectorRecord result = apiInstance.updateVectorRecord(updateVectorRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling VectorRecordControllerApi#updateVectorRecord");
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

