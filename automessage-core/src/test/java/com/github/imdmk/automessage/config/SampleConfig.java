package com.github.imdmk.automessage.config;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;

public class SampleConfig extends ConfigSection {

    public int value = 5;

    @Override
    public OkaeriSerdesPack getSerdesPack() {
        return registry -> {};
    }

    @Override
    public String getFileName() {
        return "sample.yml";
    }
}

