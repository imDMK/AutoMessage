package com.github.imdmk.automessage.language;

import com.eternalcode.multification.notice.Notice;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The text a fresh install starts with, for the languages the plugin ships.
 *
 * <p>
 * Only used when a language file does not exist yet. Everything here is written to disk once and
 * then belongs to the server owner - editing lang/en.yml is the supported way to change any of it,
 * and nothing in this class overwrites a file that is already there.
 * </p>
 *
 * <p>
 * A language the plugin does not ship needs no entry here at all: an administrator copies a file,
 * translates it, and the registry finds it. This class is a convenience, not a registry.
 * </p>
 */
final class ShippedLanguages {

    private ShippedLanguages() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * Fills in a language's defaults, leaving anything already loaded from disk untouched.
     */
    static void applyDefaults(LanguageConfig config) {
        switch (config.code()) {
            case "pl" -> polish(config);
            case "de" -> german(config);
            default -> english(config);
        }
    }

    private static void english(LanguageConfig config) {
        // English is the CommandMessages defaults; nothing to override.

        config.announcements = announcementsEnglish();
    }

    private static void polish(LanguageConfig config) {
        config.commands.permissionMissing = Notice.chat(
                "<dark_gray>• <red>Nie masz wymaganych uprawnień <gray>{PERMISSIONS}<red>, aby użyć tej komendy."
        );

        config.commands.usageInvalid = Notice.chat(
                "<dark_gray>• <red>Niepoprawne użycie komendy! <gray>Poprawna składnia: <red>{USAGE}<dark_gray>."
        );

        config.commands.usageHeader = Notice.chat(
                "<dark_gray>• <red>Poprawne warianty użycia:"
        );

        config.commands.usageEntry = Notice.chat(
                "<dark_gray>• <red>{USAGE}"
        );

        config.commands.dispatcherEnabled = Notice.chat(
                "<dark_gray>• <green>Automatyczne wiadomości zostały <bold>włączone</bold>.<dark_gray>"
        );

        config.commands.dispatcherAlreadyEnabled = Notice.chat(
                "<dark_gray>• <yellow>Automatyczne wiadomości są już włączone.<dark_gray>"
        );

        config.commands.dispatcherDisabled = Notice.chat(
                "<dark_gray>• <red>Automatyczne wiadomości zostały <bold>wyłączone</bold>.<dark_gray>"
        );

        config.commands.dispatcherAlreadyDisabled = Notice.chat(
                "<dark_gray>• <yellow>Automatyczne wiadomości są już wyłączone.<dark_gray>"
        );

        config.commands.configReloadedSuccess = Notice.chat(
                "<dark_gray>• <green>Konfiguracja AutoMessage została przeładowana pomyślnie.<dark_gray>"
        );

        config.commands.configReloadFailed = Notice.chat(
                "<dark_gray>• <red>Nie udało się przeładować plików konfiguracyjnych AutoMessage. <red>Wyłącz plugin i sprawdź swoją konfigurację."
        );

        config.commands.messagePreviewed = Notice.chat(
                "<dark_gray>• <green>Podgląd zaplanowanej wiadomości <gray>{MESSAGE}<green>.<dark_gray>"
        );

        config.commands.messageNotFound = Notice.chat(
                "<dark_gray>• <red>Wiadomość o nazwie <gray>{MESSAGE} <red>nie istnieje w scheduledMessages.yml."
        );

        config.commands.viewPlayerOnly = Notice.chat(
                "<dark_gray>• <red>Tylko gracze mogą oglądać podgląd zaplanowanych wiadomości.<dark_gray>"
        );

        config.announcements = announcementsPolish();
    }

    private static void german(LanguageConfig config) {
        config.commands.permissionMissing = Notice.chat(
                "<dark_gray>• <red>Dir fehlen die nötigen Rechte <gray>{PERMISSIONS}<red>, um diesen Befehl zu nutzen."
        );

        config.commands.usageInvalid = Notice.chat(
                "<dark_gray>• <red>Falsche Verwendung! <gray>Richtige Syntax: <red>{USAGE}<dark_gray>."
        );

        config.commands.usageHeader = Notice.chat(
                "<dark_gray>• <red>Mögliche Verwendungen:"
        );

        config.commands.usageEntry = Notice.chat(
                "<dark_gray>• <red>{USAGE}"
        );

        config.commands.dispatcherEnabled = Notice.chat(
                "<dark_gray>• <green>Automatische Nachrichten wurden <bold>aktiviert</bold>.<dark_gray>"
        );

        config.commands.dispatcherAlreadyEnabled = Notice.chat(
                "<dark_gray>• <yellow>Automatische Nachrichten sind bereits aktiviert.<dark_gray>"
        );

        config.commands.dispatcherDisabled = Notice.chat(
                "<dark_gray>• <red>Automatische Nachrichten wurden <bold>deaktiviert</bold>.<dark_gray>"
        );

        config.commands.dispatcherAlreadyDisabled = Notice.chat(
                "<dark_gray>• <yellow>Automatische Nachrichten sind bereits deaktiviert.<dark_gray>"
        );

        config.commands.configReloadedSuccess = Notice.chat(
                "<dark_gray>• <green>Die AutoMessage-Konfiguration wurde erfolgreich neu geladen.<dark_gray>"
        );

        config.commands.configReloadFailed = Notice.chat(
                "<dark_gray>• <red>Die AutoMessage-Konfigurationsdateien konnten nicht neu geladen werden. <red>Deaktiviere das Plugin und prüfe deine Konfiguration."
        );

        config.commands.messagePreviewed = Notice.chat(
                "<dark_gray>• <green>Vorschau der geplanten Nachricht <gray>{MESSAGE}<green>.<dark_gray>"
        );

        config.commands.messageNotFound = Notice.chat(
                "<dark_gray>• <red>Es gibt keine geplante Nachricht namens <gray>{MESSAGE} <red>in scheduledMessages.yml."
        );

        config.commands.viewPlayerOnly = Notice.chat(
                "<dark_gray>• <red>Nur Spieler können geplante Nachrichten in der Vorschau ansehen.<dark_gray>"
        );

        config.announcements = announcementsGerman();
    }

    private static Map<String, List<Notice>> announcementsEnglish() {
        final Map<String, List<Notice>> announcements = new LinkedHashMap<>();

                announcements.put("vote-reminder", List.of(
                        Notice.chat("<dark_gray>[<gold>!<dark_gray>] <gray>Enjoying the server? <gold>Vote <gray>for us and claim your reward!"),
                        Notice.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1.0F, 1.0F)
                ));

                announcements.put("discord-invite", List.of(
                        Notice.actionbar("<dark_gray>[<blue>!<dark_gray>] <gray>Join our Discord: <blue>discord.gg/example")
                ));

                announcements.put("server-status", List.of(
                        Notice.chat("<dark_gray>[<green>!<dark_gray>] <gray>There are <green>{ONLINE}<gray>/<green>{MAX_PLAYERS} <gray>players online right now.")
                ));

                announcements.put("event-announcement", List.of(
                        Notice.title("<gradient:#ffd700:#ff8c00><bold>EVENT</bold></gradient>", "<gray>Starting at the arena in 5 minutes!")
                ));

                announcements.put("restart-warning", List.of(
                        Notice.bossBar(BossBar.Color.RED, BossBar.Overlay.PROGRESS, Duration.ofSeconds(5), "<red>The server restarts every night at 04:00")
                ));

                announcements.put("shop-advert", List.of(
                        Notice.chat("<dark_gray>[<light_purple>!<dark_gray>] <gray>Support the server at <light_purple>shop.example.com"),
                        Notice.actionbar("<light_purple>shop.example.com")
                ));

                announcements.put("vip-perk-reminder", List.of(
                        Notice.chat("<dark_gray>[<aqua>!<dark_gray>] <gray>VIP tip: use <aqua>/kit vip <gray>once every 12 hours.")
                ));

                announcements.put("newcomer-tip", List.of(
                        Notice.chat("<dark_gray>[<yellow>!<dark_gray>] <gray>New here? Type <yellow>/help <gray>to get started.")
                ));

                announcements.put("first-join-welcome", List.of(
                        Notice.chat("<dark_gray>[<green>!<dark_gray>] <gray>Welcome to the server, <green>{PLAYER}<gray>! You are player number <green>{ONLINE}<gray> online.")
                ));

        return announcements;
    }

    private static Map<String, List<Notice>> announcementsPolish() {
        final Map<String, List<Notice>> announcements = new LinkedHashMap<>();

                announcements.put("vote-reminder", List.of(
                        Notice.chat("<dark_gray>[<gold>!<dark_gray>] <gray>Podoba Ci się serwer? <gold>Zagłosuj <gray>i odbierz nagrodę!"),
                        Notice.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1.0F, 1.0F)
                ));

                announcements.put("discord-invite", List.of(
                        Notice.actionbar("<dark_gray>[<blue>!<dark_gray>] <gray>Dołącz na nasz Discord: <blue>discord.gg/example")
                ));

                announcements.put("server-status", List.of(
                        Notice.chat("<dark_gray>[<green>!<dark_gray>] <gray>Aktualnie online: <green>{ONLINE}<gray>/<green>{MAX_PLAYERS} <gray>graczy.")
                ));

                announcements.put("event-announcement", List.of(
                        Notice.title("<gradient:#ffd700:#ff8c00><bold>EVENT</bold></gradient>", "<gray>Start na arenie za 5 minut!")
                ));

                announcements.put("restart-warning", List.of(
                        Notice.bossBar(BossBar.Color.RED, BossBar.Overlay.PROGRESS, Duration.ofSeconds(5), "<red>Serwer restartuje się codziennie o 04:00")
                ));

                announcements.put("shop-advert", List.of(
                        Notice.chat("<dark_gray>[<light_purple>!<dark_gray>] <gray>Wesprzyj serwer na <light_purple>shop.example.com"),
                        Notice.actionbar("<light_purple>shop.example.com")
                ));

                announcements.put("vip-perk-reminder", List.of(
                        Notice.chat("<dark_gray>[<aqua>!<dark_gray>] <gray>Wskazówka VIP: użyj <aqua>/kit vip <gray>raz na 12 godzin.")
                ));

                announcements.put("newcomer-tip", List.of(
                        Notice.chat("<dark_gray>[<yellow>!<dark_gray>] <gray>Nowy? Wpisz <yellow>/help<gray>, aby zacząć.")
                ));

                announcements.put("first-join-welcome", List.of(
                        Notice.chat("<dark_gray>[<green>!<dark_gray>] <gray>Witaj na serwerze, <green>{PLAYER}<gray>! Jesteś <green>{ONLINE}<gray> graczem online.")
                ));

        return announcements;
    }

    private static Map<String, List<Notice>> announcementsGerman() {
        final Map<String, List<Notice>> announcements = new LinkedHashMap<>();

                announcements.put("vote-reminder", List.of(
                        Notice.chat("<dark_gray>[<gold>!<dark_gray>] <gray>Gefällt dir der Server? <gold>Stimme <gray>für uns und hol dir deine Belohnung!"),
                        Notice.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1.0F, 1.0F)
                ));

                announcements.put("discord-invite", List.of(
                        Notice.actionbar("<dark_gray>[<blue>!<dark_gray>] <gray>Tritt unserem Discord bei: <blue>discord.gg/example")
                ));

                announcements.put("server-status", List.of(
                        Notice.chat("<dark_gray>[<green>!<dark_gray>] <gray>Aktuell online: <green>{ONLINE}<gray>/<green>{MAX_PLAYERS} <gray>Spieler.")
                ));

                announcements.put("event-announcement", List.of(
                        Notice.title("<gradient:#ffd700:#ff8c00><bold>EVENT</bold></gradient>", "<gray>Start in der Arena in 5 Minuten!")
                ));

                announcements.put("restart-warning", List.of(
                        Notice.bossBar(BossBar.Color.RED, BossBar.Overlay.PROGRESS, Duration.ofSeconds(5), "<red>Der Server startet täglich um 04:00 Uhr neu")
                ));

                announcements.put("shop-advert", List.of(
                        Notice.chat("<dark_gray>[<light_purple>!<dark_gray>] <gray>Unterstütze den Server auf <light_purple>shop.example.com"),
                        Notice.actionbar("<light_purple>shop.example.com")
                ));

                announcements.put("vip-perk-reminder", List.of(
                        Notice.chat("<dark_gray>[<aqua>!<dark_gray>] <gray>VIP-Tipp: Nutze <aqua>/kit vip <gray>alle 12 Stunden.")
                ));

                announcements.put("newcomer-tip", List.of(
                        Notice.chat("<dark_gray>[<yellow>!<dark_gray>] <gray>Neu hier? Tippe <yellow>/help<gray>, um zu starten.")
                ));

                announcements.put("first-join-welcome", List.of(
                        Notice.chat("<dark_gray>[<green>!<dark_gray>] <gray>Willkommen auf dem Server, <green>{PLAYER}<gray>! Du bist Spieler Nummer <green>{ONLINE}<gray> online.")
                ));

        return announcements;
    }
}
