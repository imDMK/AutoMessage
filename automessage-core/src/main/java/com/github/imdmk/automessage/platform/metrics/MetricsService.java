package com.github.imdmk.automessage.platform.metrics;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.Plugin;

public final class MetricsService {

    private static final int METRICS_ID = 19487;
    private final Metrics metrics;

    public MetricsService(Plugin plugin) {
        this.metrics = new Metrics(plugin, METRICS_ID);
    }

    public void shutdown() {
        this.metrics.shutdown();
    }
}
