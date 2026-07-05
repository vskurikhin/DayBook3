import su.svn.lib.core.ApiClient;
import su.svn.lib.core.ApiException;
import su.svn.lib.core.Configuration;
import su.svn.lib.core.api.JsonRecordControllerApi;
import su.svn.lib.core.api.RecordViewControllerApi;
import su.svn.lib.core.models.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;

@SuppressWarnings("ALL")
public class Example {

    public static UUID ZeroUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8082");

        OffsetDateTime odt = OffsetDateTime.now(ZoneId.systemDefault());
        ZoneOffset zoneOffset = odt.getOffset();

        JsonRecordControllerApi recordControllerApi = new JsonRecordControllerApi(defaultClient);
        NewJsonRecord newJsonRecord = new NewJsonRecord(); // NewJsonRecord |
        newJsonRecord.setPostAt(OffsetDateTime.of(LocalDateTime.now(), zoneOffset));
        UUID id = ZeroUUID;
        try {
            ResourceJsonRecord result = recordControllerApi.createJsonRecord(newJsonRecord);
            id = result.getId();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling JsonRecordControllerApi#createJsonRecord");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
        if (id == null || id.equals(ZeroUUID)) {
            throw new RuntimeException("id is " + ZeroUUID.toString());
        }

        UpdateJsonRecord updateJsonRecord = new UpdateJsonRecord(); // UpdateJsonRecord |
        updateJsonRecord.setId(id);
        updateJsonRecord.postAt(newJsonRecord.getPostAt());
        updateJsonRecord.refreshAt(OffsetDateTime.of(LocalDateTime.now(), zoneOffset));
        try {
            ResourceJsonRecord result = recordControllerApi.updateJsonRecord(updateJsonRecord);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling JsonRecordControllerApi#updateJsonRecord");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }

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

        try {
            recordControllerApi.deleteJsonRecord(id);
        } catch (ApiException e) {
            System.err.println("Exception when calling JsonRecordControllerApi#deleteJsonRecord");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
