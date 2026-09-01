package com.github.imdmk.automessage.language;

import com.github.imdmk.automessage.notice.Notice;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Seeds the text a language file is written with. Not something okaeri could do on its own: every
// language shares one LanguageConfig class, so saveDefaults() writes that class's field defaults
// into every file - English under a Polish name, and an empty announcements map even in English.
//
// The shape of a language is declared once here and each one supplies only its own strings, so a
// language cannot be missing an announcement the others have or carry one nobody schedules.
final class ShippedLanguages {

    static final List<String> CODES = List.of("en", "pl", "de");

    private static final Key VOTE_SOUND = Key.key("entity.experience_orb.pickup");
    private static final String EVENT_TITLE = "<gradient:#ffd700:#ff8c00><bold>EVENT</bold></gradient>";
    private static final String SHOP_URL = "<light_purple>shop.example.com";

    private ShippedLanguages() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    static void applyDefaults(LanguageConfig config) {
        switch (config.code()) {
            case "pl" -> apply(config, POLISH_COMMANDS, POLISH_ANNOUNCEMENTS);
            case "de" -> apply(config, GERMAN_COMMANDS, GERMAN_ANNOUNCEMENTS);

            // English is what CommandMessages already defaults to; only the announcements are new.
            default -> config.announcements = announcements(ENGLISH_ANNOUNCEMENTS);
        }
    }

    private static void apply(LanguageConfig config, Commands commands, Announcements announcements) {
        config.commands.permissionMissing = Notice.chat(commands.permissionMissing());
        config.commands.usageInvalid = Notice.chat(commands.usageInvalid());
        config.commands.usageHeader = Notice.chat(commands.usageHeader());
        config.commands.usageEntry = Notice.chat(commands.usageEntry());
        config.commands.dispatcherEnabled = Notice.chat(commands.dispatcherEnabled());
        config.commands.dispatcherAlreadyEnabled = Notice.chat(commands.dispatcherAlreadyEnabled());
        config.commands.dispatcherDisabled = Notice.chat(commands.dispatcherDisabled());
        config.commands.dispatcherAlreadyDisabled = Notice.chat(commands.dispatcherAlreadyDisabled());
        config.commands.configReloadedSuccess = Notice.chat(commands.configReloadedSuccess());
        config.commands.configReloadFailed = Notice.chat(commands.configReloadFailed());
        config.commands.messagePreviewed = Notice.chat(commands.messagePreviewed());
        config.commands.messageNotFound = Notice.chat(commands.messageNotFound());
        config.commands.viewPlayerOnly = Notice.chat(commands.viewPlayerOnly());

        config.announcements = announcements(announcements);
    }

    // The keys here are the ones scheduledMessages.yml ships, and the notice kinds are what each
    // example is meant to demonstrate - one of every kind the plugin can render.
    private static Map<String, List<Notice>> announcements(Announcements text) {
        final Map<String, List<Notice>> announcements = new LinkedHashMap<>();

        announcements.put("vote-reminder", List.of(
                Notice.chat(text.voteReminder()),
                Notice.sound(VOTE_SOUND, Sound.Source.MASTER, 1.0F, 1.0F)
        ));

        announcements.put("discord-invite", List.of(
                Notice.actionBar(text.discordInvite())
        ));

        announcements.put("server-status", List.of(
                Notice.chat(text.serverStatus())
        ));

        announcements.put("event-announcement", List.of(
                Notice.title(EVENT_TITLE, text.eventSubtitle())
        ));

        announcements.put("restart-warning", List.of(
                Notice.bossBar(BossBar.Color.RED, BossBar.Overlay.PROGRESS, Duration.ofSeconds(5), text.restartWarning())
        ));

        announcements.put("shop-advert", List.of(
                Notice.chat(text.shopAdvert()),
                Notice.actionBar(SHOP_URL)
        ));

        announcements.put("vip-perk-reminder", List.of(
                Notice.chat(text.vipPerkReminder())
        ));

        announcements.put("newcomer-tip", List.of(
                Notice.chat(text.newcomerTip())
        ));

        announcements.put("first-join-welcome", List.of(
                Notice.chat(text.firstJoinWelcome())
        ));

        return announcements;
    }

    private record Commands(
            String permissionMissing,
            String usageInvalid,
            String usageHeader,
            String usageEntry,
            String dispatcherEnabled,
            String dispatcherAlreadyEnabled,
            String dispatcherDisabled,
            String dispatcherAlreadyDisabled,
            String configReloadedSuccess,
            String configReloadFailed,
            String messagePreviewed,
            String messageNotFound,
            String viewPlayerOnly
    ) {
    }

    private record Announcements(
            String voteReminder,
            String discordInvite,
            String serverStatus,
            String eventSubtitle,
            String restartWarning,
            String shopAdvert,
            String vipPerkReminder,
            String newcomerTip,
            String firstJoinWelcome
    ) {
    }

    private static final Announcements ENGLISH_ANNOUNCEMENTS = new Announcements(
            "<dark_gray>[<gold>!<dark_gray>] <gray>Enjoying the server? <gold>Vote <gray>for us and claim your reward!",
            "<dark_gray>[<blue>!<dark_gray>] <gray>Join our Discord: <blue>discord.gg/example",
            "<dark_gray>[<green>!<dark_gray>] <gray>There are <green>{ONLINE}<gray>/<green>{MAX_PLAYERS} <gray>players online right now.",
            "<gray>Starting at the arena in 5 minutes!",
            "<red>The server restarts every night at 04:00",
            "<dark_gray>[<light_purple>!<dark_gray>] <gray>Support the server at <light_purple>shop.example.com",
            "<dark_gray>[<aqua>!<dark_gray>] <gray>VIP tip: use <aqua>/kit vip <gray>once every 12 hours.",
            "<dark_gray>[<yellow>!<dark_gray>] <gray>New here? Type <yellow>/help <gray>to get started.",
            "<dark_gray>[<green>!<dark_gray>] <gray>Welcome to the server, <green>{PLAYER}<gray>! You are player number <green>{ONLINE}<gray> online."
    );

    private static final Commands POLISH_COMMANDS = new Commands(
            "<dark_gray>• <red>Nie masz wymaganych uprawnień <gray>{PERMISSIONS}<red>, aby użyć tej komendy.",
            "<dark_gray>• <red>Niepoprawne użycie komendy! <gray>Poprawna składnia: <red>{USAGE}<dark_gray>.",
            "<dark_gray>• <red>Poprawne warianty użycia:",
            "<dark_gray>• <red>{USAGE}",
            "<dark_gray>• <green>Automatyczne wiadomości zostały <bold>włączone</bold>.<dark_gray>",
            "<dark_gray>• <yellow>Automatyczne wiadomości są już włączone.<dark_gray>",
            "<dark_gray>• <red>Automatyczne wiadomości zostały <bold>wyłączone</bold>.<dark_gray>",
            "<dark_gray>• <yellow>Automatyczne wiadomości są już wyłączone.<dark_gray>",
            "<dark_gray>• <green>Konfiguracja AutoMessage została przeładowana pomyślnie.<dark_gray>",
            "<dark_gray>• <red>Nie udało się przeładować plików konfiguracyjnych AutoMessage. <red>Wyłącz plugin i sprawdź swoją konfigurację.",
            "<dark_gray>• <green>Podgląd zaplanowanej wiadomości <gray>{MESSAGE}<green>.<dark_gray>",
            "<dark_gray>• <red>Wiadomość o nazwie <gray>{MESSAGE} <red>nie istnieje w scheduledMessages.yml.",
            "<dark_gray>• <red>Tylko gracze mogą oglądać podgląd zaplanowanych wiadomości.<dark_gray>"
    );

    private static final Announcements POLISH_ANNOUNCEMENTS = new Announcements(
            "<dark_gray>[<gold>!<dark_gray>] <gray>Podoba Ci się serwer? <gold>Zagłosuj <gray>i odbierz nagrodę!",
            "<dark_gray>[<blue>!<dark_gray>] <gray>Dołącz na nasz Discord: <blue>discord.gg/example",
            "<dark_gray>[<green>!<dark_gray>] <gray>Aktualnie online: <green>{ONLINE}<gray>/<green>{MAX_PLAYERS} <gray>graczy.",
            "<gray>Start na arenie za 5 minut!",
            "<red>Serwer restartuje się codziennie o 04:00",
            "<dark_gray>[<light_purple>!<dark_gray>] <gray>Wesprzyj serwer na <light_purple>shop.example.com",
            "<dark_gray>[<aqua>!<dark_gray>] <gray>Wskazówka VIP: użyj <aqua>/kit vip <gray>raz na 12 godzin.",
            "<dark_gray>[<yellow>!<dark_gray>] <gray>Nowy? Wpisz <yellow>/help<gray>, aby zacząć.",
            "<dark_gray>[<green>!<dark_gray>] <gray>Witaj na serwerze, <green>{PLAYER}<gray>! Jesteś <green>{ONLINE}<gray> graczem online."
    );

    private static final Commands GERMAN_COMMANDS = new Commands(
            "<dark_gray>• <red>Dir fehlen die nötigen Rechte <gray>{PERMISSIONS}<red>, um diesen Befehl zu nutzen.",
            "<dark_gray>• <red>Falsche Verwendung! <gray>Richtige Syntax: <red>{USAGE}<dark_gray>.",
            "<dark_gray>• <red>Mögliche Verwendungen:",
            "<dark_gray>• <red>{USAGE}",
            "<dark_gray>• <green>Automatische Nachrichten wurden <bold>aktiviert</bold>.<dark_gray>",
            "<dark_gray>• <yellow>Automatische Nachrichten sind bereits aktiviert.<dark_gray>",
            "<dark_gray>• <red>Automatische Nachrichten wurden <bold>deaktiviert</bold>.<dark_gray>",
            "<dark_gray>• <yellow>Automatische Nachrichten sind bereits deaktiviert.<dark_gray>",
            "<dark_gray>• <green>Die AutoMessage-Konfiguration wurde erfolgreich neu geladen.<dark_gray>",
            "<dark_gray>• <red>Die AutoMessage-Konfigurationsdateien konnten nicht neu geladen werden. <red>Deaktiviere das Plugin und prüfe deine Konfiguration.",
            "<dark_gray>• <green>Vorschau der geplanten Nachricht <gray>{MESSAGE}<green>.<dark_gray>",
            "<dark_gray>• <red>Es gibt keine geplante Nachricht namens <gray>{MESSAGE} <red>in scheduledMessages.yml.",
            "<dark_gray>• <red>Nur Spieler können geplante Nachrichten in der Vorschau ansehen.<dark_gray>"
    );

    private static final Announcements GERMAN_ANNOUNCEMENTS = new Announcements(
            "<dark_gray>[<gold>!<dark_gray>] <gray>Gefällt dir der Server? <gold>Stimme <gray>für uns und hol dir deine Belohnung!",
            "<dark_gray>[<blue>!<dark_gray>] <gray>Tritt unserem Discord bei: <blue>discord.gg/example",
            "<dark_gray>[<green>!<dark_gray>] <gray>Aktuell online: <green>{ONLINE}<gray>/<green>{MAX_PLAYERS} <gray>Spieler.",
            "<gray>Start in der Arena in 5 Minuten!",
            "<red>Der Server startet täglich um 04:00 Uhr neu",
            "<dark_gray>[<light_purple>!<dark_gray>] <gray>Unterstütze den Server auf <light_purple>shop.example.com",
            "<dark_gray>[<aqua>!<dark_gray>] <gray>VIP-Tipp: Nutze <aqua>/kit vip <gray>alle 12 Stunden.",
            "<dark_gray>[<yellow>!<dark_gray>] <gray>Neu hier? Tippe <yellow>/help<gray>, um zu starten.",
            "<dark_gray>[<green>!<dark_gray>] <gray>Willkommen auf dem Server, <green>{PLAYER}<gray>! Du bist Spieler Nummer <green>{ONLINE}<gray> online."
    );
}
