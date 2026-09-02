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
        config.commands.nextHeader = Notice.chat(commands.nextHeader());
        config.commands.nextEntry = Notice.chat(commands.nextEntry());
        config.commands.nextUnpredictable = Notice.chat(commands.nextUnpredictable());
        config.commands.nextDisabled = Notice.chat(commands.nextDisabled());
        config.commands.nextEmpty = Notice.chat(commands.nextEmpty());
        config.commands.statsHeader = Notice.chat(commands.statsHeader());
        config.commands.statsChannel = Notice.chat(commands.statsChannel());
        config.commands.statsChannelPending = Notice.chat(commands.statsChannelPending());
        config.commands.statsChannelIdle = Notice.chat(commands.statsChannelIdle());
        config.commands.statsEntry = Notice.chat(commands.statsEntry());
        config.commands.statsEmpty = Notice.chat(commands.statsEmpty());
        config.commands.sendDone = Notice.chat(commands.sendDone());
        config.commands.sendNobodyOnline = Notice.chat(commands.sendNobodyOnline());
        config.commands.sendNoMessages = Notice.chat(commands.sendNoMessages());
        config.commands.sendDisabled = Notice.chat(commands.sendDisabled());
        config.commands.channelNotFound = Notice.chat(commands.channelNotFound());

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
            String viewPlayerOnly,
            String nextHeader,
            String nextEntry,
            String nextUnpredictable,
            String nextDisabled,
            String nextEmpty,
            String statsHeader,
            String statsChannel,
            String statsChannelPending,
            String statsChannelIdle,
            String statsEntry,
            String statsEmpty,
            String sendDone,
            String sendNobodyOnline,
            String sendNoMessages,
            String sendDisabled,
            String channelNotFound
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
            "<dark_gray>• <red>Tylko gracze mogą oglądać podgląd zaplanowanych wiadomości.<dark_gray>",
            "<dark_gray>• <gray>Następna wiadomość na każdym kanale:",
            "<dark_gray>  • <gold>{CHANNEL}<dark_gray>: <gray>{MESSAGE} <dark_gray>- za <green>{DELAY}",
            "<dark_gray>  • <gold>{CHANNEL}<dark_gray>: <gray>losowana <dark_gray>- za <green>{DELAY}",
            "<dark_gray>  • <gold>{CHANNEL}<dark_gray>: <red>wyłączony",
            "<dark_gray>  • <gold>{CHANNEL}<dark_gray>: <red>brak wiadomości",
            "<dark_gray>• <gray>Wysłanych ogłoszeń od uruchomienia: <green>{TOTAL}<gray>.",
            "<dark_gray>  • <gold>{CHANNEL}<dark_gray>: <green>{COUNT}<gray>, ostatnio <green>{AGO} <gray>temu, "
                    + "następna za <green>{DELAY}",
            "<dark_gray>  • <gold>{CHANNEL}<dark_gray>: <gray>nic jeszcze nie wysłano, następna za <green>{DELAY}",
            "<dark_gray>  • <gold>{CHANNEL}<dark_gray>: <green>{COUNT} <gray>wysłanych, nie odlicza",
            "<dark_gray>    - <yellow>{MESSAGE}<dark_gray>: <green>{COUNT}<gray>, ostatnio <green>{AGO} <gray>temu",
            "<dark_gray>• <gray>Nic jeszcze nie zostało ogłoszone.",
            "<dark_gray>• <green>Wysłano <gray>{MESSAGE} <green>na kanale <gray>{CHANNEL}<green>. "
                    + "Następna za <gray>{DELAY}<green>.<dark_gray>",
            "<dark_gray>• <yellow>Nikt nie jest online, więc nic nie wysłano na kanale <gray>{CHANNEL}<yellow>. "
                    + "Harmonogram pozostał bez zmian.<dark_gray>",
            "<dark_gray>• <red>Kanał <gray>{CHANNEL} <red>nie ma przypisanych żadnych wiadomości.<dark_gray>",
            "<dark_gray>• <red>Kanał <gray>{CHANNEL} <red>jest wyłączony w config.yml.<dark_gray>",
            "<dark_gray>• <red>Kanał o nazwie <gray>{CHANNEL} <red>nie istnieje w config.yml."
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
            "<dark_gray>• <red>Nur Spieler können geplante Nachrichten in der Vorschau ansehen.<dark_gray>",
            "<dark_gray>• <gray>Nächste Nachricht pro Kanal:",
            "<dark_gray>  • <gold>{CHANNEL}<dark_gray>: <gray>{MESSAGE}",
            "<dark_gray>  • <gold>{CHANNEL}<dark_gray>: <gray>zufällig <dark_gray>- in <green>{DELAY}",
            "<dark_gray>  • <gold>{CHANNEL}<dark_gray>: <red>deaktiviert",
            "<dark_gray>  • <gold>{CHANNEL}<dark_gray>: <red>keine Nachrichten",
            "<dark_gray>• <gray>Gesendete Ankündigungen seit dem Start: <green>{TOTAL}<gray>.",
            "<dark_gray>  • <gold>{CHANNEL}<dark_gray>: <green>{COUNT}<gray>, zuletzt vor <green>{AGO}<gray>, "
                    + "nächste in <green>{DELAY}",
            "<dark_gray>  • <gold>{CHANNEL}<dark_gray>: <gray>noch nichts gesendet, nächste in <green>{DELAY}",
            "<dark_gray>  • <gold>{CHANNEL}<dark_gray>: <green>{COUNT} <gray>gesendet, zählt nicht herunter",
            "<dark_gray>    - <yellow>{MESSAGE}<dark_gray>: <green>{COUNT}<gray>, zuletzt vor <green>{AGO}",
            "<dark_gray>• <gray>Es wurde noch nichts angekündigt.",
            "<dark_gray>• <green>Hat <gray>{MESSAGE} <green>auf <gray>{CHANNEL} <green>gesendet. "
                    + "Die nächste in <gray>{DELAY}<green>.<dark_gray>",
            "<dark_gray>• <yellow>Niemand ist online, daher wurde auf <gray>{CHANNEL} <yellow>nichts gesendet. "
                    + "Der Zeitplan bleibt unberührt.<dark_gray>",
            "<dark_gray>• <red>Dem Kanal <gray>{CHANNEL} <red>sind keine Nachrichten zugewiesen.<dark_gray>",
            "<dark_gray>• <red>Kanal <gray>{CHANNEL} <red>ist in config.yml deaktiviert.<dark_gray>",
            "<dark_gray>• <red>Es gibt keinen Kanal namens <gray>{CHANNEL} <red>in config.yml."
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
