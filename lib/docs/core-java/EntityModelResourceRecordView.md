

# EntityModelResourceRecordView


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**id** | **UUID** |  |  [optional] |
|**visible** | **Boolean** |  |  [optional] |
|**flags** | **Integer** |  |  [optional] |
|**parentId** | **UUID** |  |  [optional] |
|**type** | [**TypeEnum**](#TypeEnum) |  |  [optional] |
|**userName** | **String** |  |  [optional] |
|**postAt** | **OffsetDateTime** |  |  [optional] |
|**refreshAt** | **OffsetDateTime** |  |  [optional] |
|**lastChangedTime** | **OffsetDateTime** |  |  [optional] |
|**enabled** | **Boolean** |  |  [optional] |
|**title** | **String** |  |  [optional] |
|**aHref** | **String** |  |  [optional] |
|**blob** | **byte[]** |  |  [optional] |
|**json** | **Map&lt;String, String&gt;** |  |  [optional] |
|**texts** | **Set&lt;String&gt;** |  |  [optional] |
|**fileName** | **String** |  |  [optional] |
|**html** | **String** |  |  [optional] |
|**link** | **String** |  |  [optional] |
|**markdown** | **String** |  |  [optional] |
|**value** | **String** |  |  [optional] |
|**vector** | **List&lt;Float&gt;** |  |  [optional] |
|**xml** | **String** |  |  [optional] |
|**tags** | **List&lt;String&gt;** |  |  [optional] |
|**links** | [**Map&lt;String, Link&gt;**](Link.md) |  |  [optional] |



## Enum: TypeEnum

| Name | Value |
|---- | -----|
| BASE | &quot;Base&quot; |
| BLOB | &quot;Blob&quot; |
| JSON | &quot;Json&quot; |
| SET | &quot;Set&quot; |
| TEXT | &quot;Text&quot; |
| VECTOR | &quot;Vector&quot; |
| XML | &quot;Xml&quot; |



