package com.github.imdmk.automessage.scheduled.selector;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class MessageSelectorFactoryTest {

    // Every value is listed, because the failure this guards against is a branch of the switch
    // pointing at the neighbouring selector - which a test covering half the enum would miss.
    @ParameterizedTest(name = "{0} selects with {1}")
    @CsvSource({
            "RANDOM,     RandomMessageSelector",
            "SHUFFLE,    ShuffleMessageSelector",
            "SEQUENTIAL, SequentialMessageSelector",
            "WEIGHTED,   WeightedMessageSelector",
    })
    @DisplayName("builds the selector the configured strategy names")
    void buildsTheNamedSelector(MessageSelectorType type, String expected) {
        assertThat(MessageSelectorFactory.create(type))
                .extracting(selector -> selector.getClass().getSimpleName())
                .isEqualTo(expected);
    }
}
