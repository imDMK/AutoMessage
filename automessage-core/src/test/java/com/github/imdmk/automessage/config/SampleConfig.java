package com.github.imdmk.automessage.config;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

public class SampleConfig extends ConfigSection {

    public int value = 5;

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {};
    }

    @Override
    public @NotNull String getFileName() {
        return "sample.yml";
    }
}

