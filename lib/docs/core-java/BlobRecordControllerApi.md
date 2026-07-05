# BlobRecordControllerApi

All URIs are relative to *http://localhost:8082*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createBlobRecord**](BlobRecordControllerApi.md#createBlobRecord) | **POST** /core/api/v2/blob-record |  |
| [**deleteBlobRecord**](BlobRecordControllerApi.md#deleteBlobRecord) | **DELETE** /core/api/v2/blob-record/{id} |  |
| [**readBlobRecord**](BlobRecordControllerApi.md#readBlobRecord) | **GET** /core/api/v2/blob-record/{id} |  |
| [**updateBlobRecord**](BlobRecordControllerApi.md#updateBlobRecord) | **PUT** /core/api/v2/blob-record |  |


<a id="createBlobRecord"></a>
# **createBlobRecord**
> ResourceBlobRecord createBlobRecord(newBlobRecord)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.BlobRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    BlobRecordControllerApi apiInstance = new BlobRecordControllerApi(defaultClient);
    NewBlobRecord newBlobRecord = new NewBlobRecord(); // NewBlobRecord | 
    try {
      ResourceBlobRecord result = apiInstance.createBlobRecord(newBlobRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling BlobRecordControllerApi#createBlobRecord");
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

<a id="deleteBlobRecord"></a>
# **deleteBlobRecord**
> deleteBlobRecord(id)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.BlobRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    BlobRecordControllerApi apiInstance = new BlobRecordControllerApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      apiInstance.deleteBlobRecord(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling BlobRecordControllerApi#deleteBlobRecord");
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

<a id="readBlobRecord"></a>
# **readBlobRecord**
> ResourceBlobRecord readBlobRecord(id)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.BlobRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    BlobRecordControllerApi apiInstance = new BlobRecordControllerApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      ResourceBlobRecord result = apiInstance.readBlobRecord(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling BlobRecordControllerApi#readBlobRecord");
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

[**ResourceBlobRecord**](ResourceBlobRecord.md)

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

<a id="updateBlobRecord"></a>
# **updateBlobRecord**
> ResourceBlobRecord updateBlobRecord(updateBlobRecord)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.BlobRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    BlobRecordControllerApi apiInstance = new BlobRecordControllerApi(defaultClient);
    UpdateBlobRecord updateBlobRecord = new UpdateBlobRecord(); // UpdateBlobRecord | 
    try {
      ResourceBlobRecord result = apiInstance.updateBlobRecord(updateBlobRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling BlobRecordControllerApi#updateBlobRecord");
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

