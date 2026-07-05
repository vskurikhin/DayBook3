# AuthApi

All URIs are relative to *http://localhost:64148/auth/api*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**v2AuthPost**](AuthApi.md#v2AuthPost) | **POST** /v2/auth | Authenticate user |
| [**v2LogoutPost**](AuthApi.md#v2LogoutPost) | **POST** /v2/logout | Logout user |
| [**v2RefreshPost**](AuthApi.md#v2RefreshPost) | **POST** /v2/refresh | Refresh tokens |
| [**v2RegisterPost**](AuthApi.md#v2RegisterPost) | **POST** /v2/register | Register user |


<a id="v2AuthPost"></a>
# **v2AuthPost**
> V2AuthPost200Response v2AuthPost(request)

Authenticate user

Authenticates user using login and password. Returns access token and sets refresh token cookie.

### Example
```java
// Import classes:
import su.svn.lib.auth.ApiClient;
import su.svn.lib.auth.ApiException;
import su.svn.lib.auth.Configuration;
import su.svn.lib.auth.models.*;
import su.svn.lib.auth.api.AuthApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:64148/auth/api");

    AuthApi apiInstance = new AuthApi(defaultClient);
    Login request = new Login(); // Login | User credentials
    try {
      V2AuthPost200Response result = apiInstance.v2AuthPost(request);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AuthApi#v2AuthPost");
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
| **request** | [**Login**](Login.md)| User credentials | |

### Return type

[**V2AuthPost200Response**](V2AuthPost200Response.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **400** | bad request |  -  |
| **500** | server error &#39;success&#39;: false |  -  |
| **503** | service unavailable |  -  |
| **504** | gateway timeout |  -  |

<a id="v2LogoutPost"></a>
# **v2LogoutPost**
> ModelAPIResponse v2LogoutPost()

Logout user

Invalidates user session. Requires JWT authentication.

### Example
```java
// Import classes:
import su.svn.lib.auth.ApiClient;
import su.svn.lib.auth.ApiException;
import su.svn.lib.auth.Configuration;
import su.svn.lib.auth.auth.*;
import su.svn.lib.auth.models.*;
import su.svn.lib.auth.api.AuthApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:64148/auth/api");
    
    // Configure API key authorization: BearerAuth
    ApiKeyAuth BearerAuth = (ApiKeyAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //BearerAuth.setApiKeyPrefix("Token");

    AuthApi apiInstance = new AuthApi(defaultClient);
    try {
      ModelAPIResponse result = apiInstance.v2LogoutPost();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AuthApi#v2LogoutPost");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**ModelAPIResponse**](ModelAPIResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | unauthorized |  -  |
| **500** | server error &#39;success&#39;: false |  -  |
| **503** | service unavailable |  -  |
| **504** | gateway timeout |  -  |

<a id="v2RefreshPost"></a>
# **v2RefreshPost**
> V2AuthPost200Response v2RefreshPost(request)

Refresh tokens

Refreshes access token using refresh token from cookie. Sets new refresh cookie.

### Example
```java
// Import classes:
import su.svn.lib.auth.ApiClient;
import su.svn.lib.auth.ApiException;
import su.svn.lib.auth.Configuration;
import su.svn.lib.auth.models.*;
import su.svn.lib.auth.api.AuthApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:64148/auth/api");

    AuthApi apiInstance = new AuthApi(defaultClient);
    Object request = null; // Object | User credentials by cookie string
    try {
      V2AuthPost200Response result = apiInstance.v2RefreshPost(request);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AuthApi#v2RefreshPost");
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
| **request** | **Object**| User credentials by cookie string | [optional] |

### Return type

[**V2AuthPost200Response**](V2AuthPost200Response.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **206** | Partial Content |  -  |
| **400** | bad request |  -  |
| **401** | unauthorized |  -  |
| **500** | server error &#39;success&#39;: false |  -  |
| **503** | service unavailable |  -  |
| **504** | gateway timeout |  -  |

<a id="v2RegisterPost"></a>
# **v2RegisterPost**
> V2AuthPost200Response v2RegisterPost(request)

Register user

Registers a new user and returns access token with refresh cookie.

### Example
```java
// Import classes:
import su.svn.lib.auth.ApiClient;
import su.svn.lib.auth.ApiException;
import su.svn.lib.auth.Configuration;
import su.svn.lib.auth.models.*;
import su.svn.lib.auth.api.AuthApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:64148/auth/api");

    AuthApi apiInstance = new AuthApi(defaultClient);
    CreateUser request = new CreateUser(); // CreateUser | User registration data
    try {
      V2AuthPost200Response result = apiInstance.v2RegisterPost(request);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AuthApi#v2RegisterPost");
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
| **request** | [**CreateUser**](CreateUser.md)| User registration data | |

### Return type

[**V2AuthPost200Response**](V2AuthPost200Response.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **400** | bad request |  -  |
| **409** | status conflict |  -  |
| **500** | server error &#39;success&#39;: false |  -  |
| **503** | service unavailable |  -  |
| **504** | gateway timeout |  -  |

