package su.svn.api.services.mappers;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class DateTimeMapperTest {

    private final DateTimeMapper mapper = new DateTimeMapper() {
    };

    @Test
    void shouldMapOffsetDateTimeToLocalDateTime() {
        var offsetDateTime = OffsetDateTime.of(
                2026,
                5,
                21,
                12,
                30,
                45,
                0,
                ZoneOffset.UTC
        );

        LocalDateTime result = mapper.map(offsetDateTime);

        assertThat(result)
                .isEqualTo(LocalDateTime.of(2026, 5, 21, 12, 30, 45));
    }

    @Test
    void shouldReturnNull() {
        assertThat(mapper.map(null)).isNull();
    }
}