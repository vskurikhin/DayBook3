# XmlRecordResourceApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**apiV2RecordXmlIdDelete**](XmlRecordResourceApi.md#apiV2RecordXmlIdDelete) | **DELETE** /api/v2/record/xml/{id} | Delete BLOB record |
| [**apiV2RecordXmlPost**](XmlRecordResourceApi.md#apiV2RecordXmlPost) | **POST** /api/v2/record/xml | Create BLOB record |
| [**apiV2RecordXmlPut**](XmlRecordResourceApi.md#apiV2RecordXmlPut) | **PUT** /api/v2/record/xml | Update BLOB record |


<a id="apiV2RecordXmlIdDelete"></a>
# **apiV2RecordXmlIdDelete**
> apiV2RecordXmlIdDelete(id)

Delete BLOB record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.XmlRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    XmlRecordResourceApi apiInstance = new XmlRecordResourceApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      apiInstance.apiV2RecordXmlIdDelete(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling XmlRecordResourceApi#apiV2RecordXmlIdDelete");
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

<a id="apiV2RecordXmlPost"></a>
# **apiV2RecordXmlPost**
> ResourceXmlRecord apiV2RecordXmlPost(newXmlRecord)

Create BLOB record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.XmlRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    XmlRecordResourceApi apiInstance = new XmlRecordResourceApi(defaultClient);
    NewXmlRecord newXmlRecord = new NewXmlRecord(); // NewXmlRecord | 
    try {
      ResourceXmlRecord result = apiInstance.apiV2RecordXmlPost(newXmlRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling XmlRecordResourceApi#apiV2RecordXmlPost");
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

<a id="apiV2RecordXmlPut"></a>
# **apiV2RecordXmlPut**
> ResourceXmlRecord apiV2RecordXmlPut(updateXmlRecord)

Update BLOB record

### Example
```java
// Import classes:
import su.svn.lib.api.ApiClient;
import su.svn.lib.api.ApiException;
import su.svn.lib.api.Configuration;
import su.svn.lib.api.auth.*;
import su.svn.lib.api.models.*;
import su.svn.lib.api.api.XmlRecordResourceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: SecurityScheme
    HttpBearerAuth SecurityScheme = (HttpBearerAuth) defaultClient.getAuthentication("SecurityScheme");
    SecurityScheme.setBearerToken("BEARER TOKEN");

    XmlRecordResourceApi apiInstance = new XmlRecordResourceApi(defaultClient);
    UpdateXmlRecord updateXmlRecord = new UpdateXmlRecord(); // UpdateXmlRecord | 
    try {
      ResourceXmlRecord result = apiInstance.apiV2RecordXmlPut(updateXmlRecord);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling XmlRecordResourceApi#apiV2RecordXmlPut");
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

