package su.svn.api.domain.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResourcePathTest {

    @Test
    void testMethodGetValue() {
        Assertions.assertNull(ResourcePath.Null.getValue());
    }

    @Test
    void testMethodStringEquals() {
        Assertions.assertTrue(ResourcePath.Null.stringEquals(null));
        Assertions.assertTrue(ResourcePath.Record.stringEquals("/api/v2/record"));
    }

    @Test
    void testMethodLookup() {
        Assertions.assertEquals(ResourcePath.Null, ResourcePath.lookup(null));
        Assertions.assertEquals(ResourcePath.Record, ResourcePath.lookup("/api/v2/record"));
        Assertions.assertNull(ResourcePath.lookup("VmeVXS0318/Zb/O8Xots9Qr3VCWIrsH3YNGeTpdrdGKnZhEz91SW3rFQqCCupzpWb"));
    }
}