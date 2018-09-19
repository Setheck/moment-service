package com.setheck.momentservice.api;

import com.fasterxml.jackson.databind.*;
import io.dropwizard.jackson.*;
import org.junit.*;
import org.slf4j.*;

import java.sql.*;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.*;

public class MomentTest {
    private static final Logger log = LoggerFactory.getLogger(MomentTest.class);
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializeToJSON() throws Exception {
        final Moment moment = new Moment(1, "Some text", "Seth", false, new Timestamp(0));

        final String expected =
                MAPPER.writeValueAsString(MAPPER.readValue(fixture("fixtures/moment.json"), Moment.class));
        assertThat(MAPPER.writeValueAsString(moment)).isEqualTo(expected);
    }

    @Test
    public void deserializeFromJSON() throws Exception {
        final Moment moment = new Moment(1, "Some text", "Seth", false, new Timestamp(0));
        assertThat(MAPPER.readValue(fixture("fixtures/moment.json"), Moment.class)).isEqualTo(moment);
    }
}
