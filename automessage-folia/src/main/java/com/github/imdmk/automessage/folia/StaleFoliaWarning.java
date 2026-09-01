package com.github.imdmk.automessage.folia;

import java.util.function.Supplier;
import java.util.logging.Filter;
import java.util.logging.Logger;

final class StaleFoliaWarning {

    private static final String TEXT = "Folia extension is not enabled";

    private StaleFoliaWarning() {
    }

    // LiteCommands warns that the Folia extension is missing from BukkitScheduler's constructor,
    // and LiteBukkitFactory builds that scheduler before it hands back the builder .extension() is
    // called on - so the warning is already false when it is printed. None of the factory's six
    // builder(..) overloads accept a scheduler, so there is no way to be early enough; the record
    // is dropped instead.
    //
    // This lives in the Folia module, the only one that installs the extension, so the Bukkit jar
    // run on Folia keeps the warning - there it is true. Scoped to the call and restored after, so
    // the filter cannot outlive the one message it exists for.
    static <T> T silenced(Logger logger, Supplier<T> action) {
        final Filter previous = logger.getFilter();

        logger.setFilter(record -> {
            final String message = record.getMessage();
            if (message != null && message.contains(TEXT)) {
                return false;
            }
            return previous == null || previous.isLoggable(record);
        });

        try {
            return action.get();
        } finally {
            logger.setFilter(previous);
        }
    }
}
