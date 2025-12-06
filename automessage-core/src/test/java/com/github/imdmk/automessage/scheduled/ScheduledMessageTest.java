package com.github.imdmk.automessage.scheduled;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScheduledMessageTest {

    @Test
    @DisplayName("Should create ScheduledMessage when valid arguments are provided")
    void shouldCreateScheduledMessage() {
        // given
        var notice = Notice.chat("hello");
        var rule = AudienceRule.permission("test.permission");

        // when
        ScheduledMessage message = new ScheduledMessage(
                "example",
                List.of(notice),
                List.of(rule)
        );

        // then
        assertEquals("example", message.name());
        assertEquals(List.of(notice), message.notices());
        assertEquals(List.of(rule), message.rules());
    }

    @Test
    @DisplayName("Should throw when name is null")
    void shouldFailWhenNameNull() {
        var notice = Notice.chat("test");

        assertThrows(NullPointerException.class, () ->
                new ScheduledMessage(
                        null,
                        List.of(notice),
                        List.of()
                )
        );
    }

    @Test
    @DisplayName("Should throw when notices list is null")
    void shouldFailWhenNoticesNull() {
        assertThrows(NullPointerException.class, () ->
                new ScheduledMessage(
                        "msg",
                        null,
                        List.of()
                )
        );
    }

    @Test
    @DisplayName("Should throw when notices list is empty")
    void shouldFailWhenNoticesEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                new ScheduledMessage(
                        "msg",
                        List.of(),
                        List.of()
                )
        );
    }

    @Test
    @DisplayName("Should produce unmodifiable notice and rule lists")
    void shouldMakeCollectionsUnmodifiable() {
        var notice = Notice.chat("test");
        var rule = AudienceRule.permission("perm");

        ScheduledMessage message = new ScheduledMessage(
                "msg",
                List.of(notice),
                List.of(rule)
        );

        assertThrows(UnsupportedOperationException.class, () ->
                message.notices().add(Notice.chat("other"))
        );

        assertThrows(UnsupportedOperationException.class, () ->
                message.rules().add(AudienceRule.group("vip"))
        );
    }

    @Test
    @DisplayName("Record equality should compare field values")
    void shouldRespectRecordEquality() {
        var n1 = Notice.chat("x");
        var r1 = AudienceRule.permission("p");

        ScheduledMessage a = new ScheduledMessage("name", List.of(n1), List.of(r1));
        ScheduledMessage b = new ScheduledMessage("name", List.of(n1), List.of(r1));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}

