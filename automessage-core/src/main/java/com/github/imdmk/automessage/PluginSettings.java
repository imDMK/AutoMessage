package com.github.imdmk.automessage;

import com.github.imdmk.automessage.config.ConfigSection;

import java.util.List;

public interface PluginSettings {

    List<Class<? extends ConfigSection>> configs();

}
