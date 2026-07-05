# RecordViewControllerApi

All URIs are relative to *http://localhost:8082*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getAllRecords**](RecordViewControllerApi.md#getAllRecords) | **GET** /core/api/v2/records-view |  |
| [**getRecord**](RecordViewControllerApi.md#getRecord) | **GET** /core/api/v2/records-view/{id} |  |


<a id="getAllRecords"></a>
# **getAllRecords**
> PagedModelEntityModelResourceRecordView getAllRecords(filter, pageable)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.RecordViewControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    RecordViewControllerApi apiInstance = new RecordViewControllerApi(defaultClient);
    ResourceRecordViewFilter filter = new ResourceRecordViewFilter(); // ResourceRecordViewFilter | 
    Pageable pageable = new Pageable(); // Pageable | 
    try {
      PagedModelEntityModelResourceRecordView result = apiInstance.getAllRecords(filter, pageable);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RecordViewControllerApi#getAllRecords");
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
| **filter** | [**ResourceRecordViewFilter**](.md)|  | |
| **pageable** | [**Pageable**](.md)|  | |

### Return type

[**PagedModelEntityModelResourceRecordView**](PagedModelEntityModelResourceRecordView.md)

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

<a id="getRecord"></a>
# **getRecord**
> ResourceRecordView getRecord(id)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.RecordViewControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    RecordViewControllerApi apiInstance = new RecordViewControllerApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      ResourceRecordView result = apiInstance.getRecord(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RecordViewControllerApi#getRecord");
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

[**ResourceRecordView**](ResourceRecordView.md)

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

