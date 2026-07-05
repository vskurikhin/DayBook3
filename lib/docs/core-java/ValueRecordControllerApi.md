# ValueRecordControllerApi

All URIs are relative to *http://localhost:8082*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createValueRecord**](ValueRecordControllerApi.md#createValueRecord) | **POST** /core/api/v2/value-record |  |
| [**deleteValueRecord**](ValueRecordControllerApi.md#deleteValueRecord) | **DELETE** /core/api/v2/value-record/{id} |  |
| [**readValueRecord**](ValueRecordControllerApi.md#readValueRecord) | **GET** /core/api/v2/value-record/{id} |  |
| [**updateValueRecord**](ValueRecordControllerApi.md#updateValueRecord) | **PUT** /core/api/v2/value-record |  |


<a id="createValueRecord"></a>
# **createValueRecord**
> ResourceValueRecord createValueRecord(newValueRecord)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.ValueRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    ValueRecordControllerApi apiInstance = new ValueRecordControllerApi(defaultClient);
    NewValueRecord newValueRecord = new NewValueRecord(); // NewValueRecord | 
    try {
      ResourceValueRecord result = apiInstance.createValueRecord(newValueRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ValueRecordControllerApi#createValueRecord");
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

<a id="deleteValueRecord"></a>
# **deleteValueRecord**
> deleteValueRecord(id)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.ValueRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    ValueRecordControllerApi apiInstance = new ValueRecordControllerApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      apiInstance.deleteValueRecord(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling ValueRecordControllerApi#deleteValueRecord");
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

<a id="readValueRecord"></a>
# **readValueRecord**
> ResourceValueRecord readValueRecord(id)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.ValueRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    ValueRecordControllerApi apiInstance = new ValueRecordControllerApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      ResourceValueRecord result = apiInstance.readValueRecord(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ValueRecordControllerApi#readValueRecord");
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

[**ResourceValueRecord**](ResourceValueRecord.md)

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

<a id="updateValueRecord"></a>
# **updateValueRecord**
> ResourceValueRecord updateValueRecord(updateValueRecord)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.ValueRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    ValueRecordControllerApi apiInstance = new ValueRecordControllerApi(defaultClient);
    UpdateValueRecord updateValueRecord = new UpdateValueRecord(); // UpdateValueRecord | 
    try {
      ResourceValueRecord result = apiInstance.updateValueRecord(updateValueRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ValueRecordControllerApi#updateValueRecord");
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

