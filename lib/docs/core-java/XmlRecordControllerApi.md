# XmlRecordControllerApi

All URIs are relative to *http://localhost:8082*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createXmlRecord**](XmlRecordControllerApi.md#createXmlRecord) | **POST** /core/api/v2/xml-record |  |
| [**deleteXmlRecord**](XmlRecordControllerApi.md#deleteXmlRecord) | **DELETE** /core/api/v2/xml-record/{id} |  |
| [**readXmlRecord**](XmlRecordControllerApi.md#readXmlRecord) | **GET** /core/api/v2/xml-record/{id} |  |
| [**updateXmlRecord**](XmlRecordControllerApi.md#updateXmlRecord) | **PUT** /core/api/v2/xml-record |  |


<a id="createXmlRecord"></a>
# **createXmlRecord**
> ResourceXmlRecord createXmlRecord(newXmlRecord)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.XmlRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    XmlRecordControllerApi apiInstance = new XmlRecordControllerApi(defaultClient);
    NewXmlRecord newXmlRecord = new NewXmlRecord(); // NewXmlRecord | 
    try {
      ResourceXmlRecord result = apiInstance.createXmlRecord(newXmlRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling XmlRecordControllerApi#createXmlRecord");
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
| **newXmlRecord** | [**NewXmlRecord**](NewXmlRecord.md)|  | |

### Return type

[**ResourceXmlRecord**](ResourceXmlRecord.md)

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

<a id="deleteXmlRecord"></a>
# **deleteXmlRecord**
> deleteXmlRecord(id)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.XmlRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    XmlRecordControllerApi apiInstance = new XmlRecordControllerApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      apiInstance.deleteXmlRecord(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling XmlRecordControllerApi#deleteXmlRecord");
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

<a id="readXmlRecord"></a>
# **readXmlRecord**
> ResourceXmlRecord readXmlRecord(id)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.XmlRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    XmlRecordControllerApi apiInstance = new XmlRecordControllerApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      ResourceXmlRecord result = apiInstance.readXmlRecord(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling XmlRecordControllerApi#readXmlRecord");
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

[**ResourceXmlRecord**](ResourceXmlRecord.md)

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

<a id="updateXmlRecord"></a>
# **updateXmlRecord**
> ResourceXmlRecord updateXmlRecord(updateXmlRecord)



### Example
```java
// Import classes:
import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.models.*;
import su.svn.lib.core.api.XmlRecordControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8082");

    XmlRecordControllerApi apiInstance = new XmlRecordControllerApi(defaultClient);
    UpdateXmlRecord updateXmlRecord = new UpdateXmlRecord(); // UpdateXmlRecord | 
    try {
      ResourceXmlRecord result = apiInstance.updateXmlRecord(updateXmlRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling XmlRecordControllerApi#updateXmlRecord");
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
| **updateXmlRecord** | [**UpdateXmlRecord**](UpdateXmlRecord.md)|  | |

### Return type

[**ResourceXmlRecord**](ResourceXmlRecord.md)

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

