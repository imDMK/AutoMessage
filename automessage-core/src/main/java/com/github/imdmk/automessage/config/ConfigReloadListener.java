package com.github.imdmk.automessage.config;

/**
 * Notified after every successful reload of the plugin configuration.
 *
 * <p>
 * Implement this to re-apply settings that are read once, outside of the request path —
 * for example a scheduled task whose interval is fixed at scheduling time.
 * </p>
 */
@FunctionalInterface
public interface ConfigReloadListener {

    void onConfigReload();
}
