package com.github.imdmk.automessage.feature.update;

import com.eternalcode.gitcheck.GitCheck;
import com.eternalcode.gitcheck.GitCheckResult;
import com.eternalcode.gitcheck.git.GitRepository;
import com.eternalcode.gitcheck.git.GitTag;
import com.github.imdmk.automessage.configuration.PluginConfig;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Objects;

public class UpdateService {

    private static final GitRepository GIT_REPOSITORY = GitRepository.of("imDMK", "AutoMessage");
    private static final GitCheck GIT_CHECK = new GitCheck();

    private final PluginConfig config;
    private final PluginDescriptionFile descriptionFile;

    private Instant latestCheck;

    public UpdateService(@NotNull PluginConfig config, @NotNull PluginDescriptionFile descriptionFile) {
        this.config = Objects.requireNonNull(config, "pluginConfiguration cannot be null");
        this.descriptionFile = Objects.requireNonNull(descriptionFile, "pluginDescriptionFile cannot be null");
    }

    public @NotNull GitCheckResult check() {
        this.latestCheck = Instant.now();

        GitTag tag = GitTag.of("v" + this.descriptionFile.getVersion());
        return GIT_CHECK.checkRelease(GIT_REPOSITORY, tag);
    }

    public boolean shouldCheck() {
        if (this.latestCheck == null) {
            return true;
        }

        Instant nextCheckTime = this.latestCheck.plus(this.config.updateInterval);
        return Instant.now().isAfter(nextCheckTime);
    }
}
