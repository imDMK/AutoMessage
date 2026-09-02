<p align="center">
  <img src="assets/readme/hero.svg" alt="AutoMessage - right message, right player, right moment" width="100%">
</p>

<p align="center">
  <a href="https://github.com/imDMK/AutoMessage/releases"><img alt="Latest release" src="https://img.shields.io/github/v/release/imDMK/AutoMessage?sort=semver&amp;style=flat-square&amp;label=release&amp;color=7C7CFF"></a>
  <a href="https://github.com/imDMK/AutoMessage/actions/workflows/gradle.yml"><img alt="Build status" src="https://img.shields.io/github/actions/workflow/status/imDMK/AutoMessage/gradle.yml?branch=main&amp;style=flat-square&amp;label=build"></a>
  <img alt="Java 21 or newer; Java 25 for Minestom" src="https://img.shields.io/badge/Java-21%2B%20%C2%B7%20Minestom%2025-E76F00?style=flat-square&amp;logo=openjdk&amp;logoColor=white">
  <img alt="Minecraft 1.21 through 26.2" src="https://img.shields.io/badge/Minecraft-1.21--26.2-45B36B?style=flat-square">
  <img alt="Six runtime targets" src="https://img.shields.io/badge/targets-6%20runtimes-45CFE1?style=flat-square">
</p>

<p align="center">
  <a href="https://modrinth.com/plugin/automessage"><img alt="Modrinth downloads" src="https://img.shields.io/modrinth/dt/automessage?style=flat-square&amp;label=modrinth&amp;color=1BD96A&amp;logo=modrinth&amp;logoColor=white"></a>
  <a href="https://www.spigotmc.org/resources/automessage.112363/"><img alt="SpigotMC downloads" src="https://img.shields.io/spiget/downloads/112363?style=flat-square&amp;label=spigotmc&amp;color=ED8106"></a>
  <a href="https://hangar.papermc.io/imDMK/AutoMessage"><img alt="Available on Hangar" src="https://img.shields.io/badge/hangar-available-2C5EA8?style=flat-square"></a>
</p>

<p align="center">
  <strong>Not another chat rotator. A complete announcement engine.</strong><br>
  Targeted, localized and event-aware broadcasts for modern Minecraft servers and networks.
</p>

<p align="center">
  <sub><strong>6</strong> runtimes &nbsp;·&nbsp; <strong>8</strong> notice surfaces &nbsp;·&nbsp; <strong>8</strong> audience rules &nbsp;·&nbsp; <strong>3</strong> event triggers &nbsp;·&nbsp; <strong>3</strong> languages shipped &nbsp;·&nbsp; <strong>one</strong> render per language &nbsp;·&nbsp; <strong>no database</strong></sub>
</p>

<table align="center">
<tr>
<td valign="top" width="34%">

**🧭 Why AutoMessage**

- [📣 Broadcasts should earn attention](#broadcasts)
- [⚖️ More than a rotating list](#rotating-list)
- [🎯 Reach exactly who should hear it](#audience-rules)
- [⚡ Fire on an event, not a clock](#event-triggers)
- [🪶 Costs your server almost nothing](#performance)
- [🌐 One engine, six runtimes](#runtimes)

</td>
<td valign="top" width="33%">

**⚙️ Install and configure**

- [📥 **Get AutoMessage**](#get-automessage)
- [🚀 Quick start](#quick-start)
- [🛠 Build your first announcement](#first-announcement)
- [🗂 Configuration model](#configuration-model)
- [📋 Platform support](#platform-support)
- [🔨 Building from source](#building-from-source)
- [💛 Support the project](#support)

</td>
<td valign="top" width="33%">

[**📖 Reference**](#reference)

- [📡 Channels and selectors](#ref-channels)
- [🎯 Audience rules](#ref-audience)
- [⚡ Event triggers](#ref-triggers)
- [🌍 Languages and fallback](#ref-languages)
- [🎨 MiniMessage and placeholders](#ref-minimessage)
- [⏱ Time format](#ref-time)
- [💬 Discord webhook mirror](#ref-discord)
- [⌨️ Commands and permissions](#commands)

</td>
</tr>
</table>

<a id="broadcasts"></a>

## 📣 Broadcasts should earn attention

Players learn to ignore announcements when every message reaches everyone. AutoMessage makes server
communication deliberate: every stream keeps its own schedule, every message chooses its own
audience, and the text arrives in the language each player actually plays in - from one shared core
running on **Bukkit, Folia, Sponge, Velocity, Fabric and Minestom**.

<p align="center">
  <img src="assets/readme/announcement-ingame.png" alt="Three announcement surfaces in game at once: a boss bar countdown across the top of the screen, a full-screen title in the middle, and a chat line below it" width="800">
</p>

<p align="center">
  <sub>One announcement set, in game, on one server: a boss bar countdown, a full-screen title and a chat line.</sub>
</p>

### A complete announcement, start to finish

Greet returning veterans three seconds after they join - never on a timer, never the newcomers, and
in their own language. Two files say all of it:

```yaml
# scheduledMessages.yml - who gets it, and when
messages:
  - name: welcome-back
    trigger:
      type: JOIN
      delay: 3s
    rules:
      - type: PLAYTIME
        min: 10h
```

```yaml
# lang/en.yml - what it says
announcements:
  welcome-back:
    - chat: "<gray>Welcome back, <aqua>{PLAYER}<gray>."
      sound: "entity.player.levelup MASTER 1.0 1.2"
```

And this is what the server does with them:

<p align="center">
  <img src="assets/readme/start-to-finish.svg" alt="Two players join. Three seconds later the veteran with 240 hours played passes the playtime rule and receives the greeting with a level-up sound; the newcomer with twelve minutes played receives nothing at all." width="100%">
</p>

That is the whole feature set in miniature: an event instead of a clock, an audience instead of
everyone, and text that lives beside every other translation.

<a id="rotating-list"></a>

## ⚖️ More than a rotating list

The tagline is not marketing. A rotator answers one question - *what do I say next?* - and sends
the answer to everyone, in one language, on one clock.

| | A plain rotator | AutoMessage |
|:--|:--|:--|
| **Who receives it** | Everyone online | Permission, group, world, online count, playtime - composed with `ANY_OF`, `NONE_OF`, `NOT` |
| **When it fires** | One global interval | A schedule per channel, plus `JOIN`, `FIRST_JOIN` and re-arming `PLAYER_COUNT` triggers |
| **What it looks like** | A line of chat | Chat, action bar, title, subtitle, boss bar and sound, combined in one announcement |
| **What it says** | One text for everybody | The player's own language, with a per-message fallback |
| **Where it runs** | One server type | Bukkit, Folia, Sponge, Velocity, Fabric and Minestom, each a native artifact |


### The details underneath

|  |  |  |
|:--|:--|:--|
| **🔀 Four rotation strategies**<br><br>`SHUFFLE`, `SEQUENTIAL`, `RANDOM` or weighted, chosen per channel. Shuffle deals a whole deck before anything repeats. | **🧩 Configuration that fits the runtime**<br><br>A proxy never sees a world rule or a first-join trigger. Options the platform cannot honor are left out of the file, not written and ignored. | **👁 Preview before publishing**<br><br>`/automessage view <name>` renders the real localized message to you alone, ignoring its audience rules. |
| **↗ Beyond the server**<br><br>Mirror chat content to a Discord webhook, with player-only placeholders stripped and automatic mentions disabled. | **♻ Reload, do not restart**<br><br>`/automessage reload` applies schedules, messages and languages in place - including a translation file added while the server was running. | **🪶 Lean dispatch path**<br><br>No database. An empty server short-circuits, placeholders are scanned once per message rather than once per announcement, and everyone reading the same text in the same language is served from a single render. |


<a id="audience-rules"></a>

## 🎯 Reach exactly who should hear it

Rules compose. Every top-level rule must pass, and `ANY_OF`, `NONE_OF` and `NOT` nest inside them
when the audience needs more than an AND.

<p align="center">
  <img src="assets/readme/audience.svg" alt="Twelve players online; a rule requiring the rank.vip permission and at least ten hours of playtime selects five of them, the rest stay dark" width="100%">
</p>

The same rules apply to a message sent by an event, so a join greeting restricted to a permission
never reaches somebody who lacks it just because they walked through the door.

<a id="event-triggers"></a>

## ⚡ Fire on an event, not a clock

A message with a trigger leaves its channel rotation entirely and waits for something to happen.

<p align="center">
  <img src="assets/readme/triggers.svg" alt="Three lanes: a channel firing on a five-minute period, a join trigger firing three seconds after a player connects, and a player-count milestone firing once at one hundred online and re-arming only after the count falls below" width="100%">
</p>

`PLAYER_COUNT` is the one worth reading twice: it fires **once** on the way up and re-arms only
after the server drops back below the threshold, so a population hovering on the boundary does not
announce the same milestone every time somebody logs in.

<a id="performance"></a>

## 🪶 Costs your server almost nothing

Formatting is the expensive part of an announcement. Colors, gradients, hover text and placeholders
all have to be parsed before a single character reaches anybody - and the obvious way to do that is
once for each player who receives it. Five hundred players online means the same sentence built five
hundred times to say one thing, on the main thread, while the rest of the tick waits its turn.

AutoMessage builds it once per language and hands the finished result to everyone reading it.

<p align="center">
  <img src="assets/readme/performance.svg" alt="One announcement reaching 500 players. Formatting once per player means 500 parses of the same text and costs 5.16 ms, about a tenth of a server tick. AutoMessage formats once per language and costs 0.34 ms." width="100%">
</p>

| | Formatting per player | AutoMessage |
|:--|:--|:--|
| **Parses per announcement** | One per player, so 500 | One per language actually being read |
| **Time on the main thread** | 5.16 ms | 0.34 ms |
| **Share of a 50 ms tick** | ~10%, every announcement | ~0.7% |

That is **15× less main-thread time per announcement**, measured on the real send path with 500
viewers online - not on a microbenchmark of one function. What remains is handing the finished text
to each connection, which no plugin can avoid.

The saving grows with your player count, and it costs you nothing to configure: there is no cache to
size, no setting to tune, and no behavior to give up.

**It never trades correctness for speed.** A message that reads differently for each player - one
naming `{PLAYER}`, or carrying any PlaceholderAPI token - is still built for each of them, because
only the expansion behind that token knows whether two players would see the same thing. Grouping
applies exactly where the text is genuinely identical, and nowhere else.

Three more places the dispatch path refuses to do needless work:

- **Placeholders are scanned once per message, not once per announcement.** Which tokens a message
  contains is decided by the text, so it is worked out when the files are read and remembered until
  a reload - rather than re-derived every time the channel fires.
- **An empty server short-circuits.** Nobody online means no selection, no formatting and no work.
- **Nothing touches disk while announcing.** No database, no player records, no I/O on the hot path.

<a id="runtimes"></a>

## 🌐 One engine. Six runtimes

AutoMessage uses a shared platform-neutral core and a small runtime adapter for each server type.
That means the behavior stays familiar while scheduling, viewers, permissions and lifecycle hooks
remain native to the platform underneath.

<p align="center">
  <img src="assets/readme/platforms.svg" alt="One platform-neutral core connected to six artifacts: Bukkit, Folia, Sponge, Velocity, Fabric and Minestom" width="100%">
</p>

<a id="platform-support"></a>

| Artifact | Runtime | Supported line | Java | Platform-specific notes |
|:--|:--|:--|:--:|:--|
| **Bukkit** | Spigot, Paper, Purpur and other Bukkit-derived servers | Minecraft `1.21–26.2` | 21+ | Full feature set; optional PlaceholderAPI integration |
| **Folia** | Folia | Minecraft `1.21–26.2` | 21+ | Dedicated regionized scheduling; optional PlaceholderAPI integration |
| **Sponge** | Sponge API 17 | Minecraft `1.21.10` | 21+ | Full feature set except PlaceholderAPI |
| **Velocity** | Velocity `3.5.1` proxy | Proxy runtime | 21+ | No world, playtime, first-join or PlaceholderAPI capabilities |
| **Fabric** | Dedicated Fabric server | Minecraft `1.21.11` | 21+ | Server-side mod; Fabric API required; no PlaceholderAPI |
| **Minestom** | Embedded Minestom library | Protocol `1.21.11` | 25+ | Classpath library; no world, playtime, first-join or PlaceholderAPI; permission/group rules require your callback |

> [!NOTE]
> Java 21 is the bytecode floor for the plugin artifacts, not a promise that every Minecraft
> server version runs on Java 21. Use the JVM required by your server; Minestom itself requires
> Java 25.

Each artifact writes a platform-aware configuration. Options the runtime cannot honor are left out
instead of being generated and silently ignored.

<p align="center">
  <img src="assets/readme/platform-config.svg" alt="The same generated file on Paper and on a Velocity proxy: the world rule, playtime rule, first-join trigger, world placeholder, PlaceholderAPI note and playtime example are absent from the proxy file" width="100%">
</p>

<details>
<summary><strong>What about Quilt?</strong></summary>

There is no separate Quilt artifact. Quilt can read Fabric metadata, but this branch does not claim
a tested Quilt version. Treat the Fabric jar on Quilt as experimental and bring a compatible
Fabric API yourself.

</details>

<a id="configuration-model"></a>

## 🗂 Configuration model

AutoMessage separates **when**, **who** and **what**. Adding a language never requires copying
scheduling rules, and changing a channel interval never touches the message text.

```mermaid
flowchart LR
    schedule["config.yml<br/>WHEN<br/>channels · periods · selectors"]
    audience["scheduledMessages.yml<br/>WHO<br/>rules · weights · triggers"]
    content["lang/*.yml<br/>WHAT<br/>localized MiniMessage notices"]
    minecraft["Minecraft<br/>PLAYER OUTPUT"]
    discord["discordWebhook.yml<br/>OPTIONAL MIRROR"]

    schedule --> audience --> content --> minecraft
    content -. "chat text" .-> discord

    classDef config fill:#11182d,stroke:#8b7cf6,color:#eef0ff,stroke-width:1px;
    classDef output fill:#0d2028,stroke:#45d7e8,color:#ecfeff,stroke-width:1px;
    class schedule,audience,content config;
    class minecraft,discord output;
```

| File | Responsibility |
|:--|:--|
| `config.yml` | Master switch, fallback language, channels, startup delays, periods and selectors |
| `scheduledMessages.yml` | Message names, channel assignment, weights, audience rules and event triggers |
| `lang/<code>.yml` | Localized command replies and the actual MiniMessage announcement payloads |
| `discordWebhook.yml` | Opt-in Discord webhook, display name and avatar |

<a id="get-automessage"></a>

## 📥 Get AutoMessage

| What you need | Where to get it |
|:--|:--|
| Current public Bukkit build | [Modrinth](https://modrinth.com/plugin/automessage) · [SpigotMC](https://www.spigotmc.org/resources/automessage.112363/) · [Hangar](https://hangar.papermc.io/imDMK/AutoMessage) |
| Release history and changelogs | [GitHub Releases](https://github.com/imDMK/AutoMessage/releases) |
| Multiplatform artifacts from this branch | [Build all six from source](#building-from-source) |

> [!IMPORTANT]
> **Release status:** this README documents the local multiplatform branch. The current public
> listings still represent the earlier Bukkit-focused release line. Until a new GitHub release
> publishes all six artifacts, build the multiplatform jars with `./gradlew dist`.

<a id="quick-start"></a>

## 🚀 Quick start

1. Get the artifact built for your runtime. Platform jars are intentionally separate.
2. Put it in your platform's `plugins/` or `mods/` directory. For Minestom, add the jar to the
   application classpath and use the builder shown below.
3. Start the server once. AutoMessage generates annotated configuration plus working example
   announcements.
4. Edit `config.yml`, `scheduledMessages.yml` and the files inside `lang/`.
5. Run `/automessage reload`, then test a message with `/automessage view <name>`.

> [!TIP]
> Start with the generated files. They are not empty templates: every available field is explained
> next to a working example, and the contents adapt to the platform that created them.

<a id="first-announcement"></a>

## 🛠 Build your first announcement

The example below creates an independent event stream, adds one announcement to it, and renders
multiple notice types from a single localized payload.

### 1. Schedule the stream

```yaml
# config.yml
enabled: true
fallbackLanguage: en

channels:
  - name: default
    enabled: true
    initialDelay: 1m
    period: 5m
    selector: SHUFFLE

  - name: events
    enabled: true
    initialDelay: 30s
    period: 15m
    selector: SEQUENTIAL
```

### 2. Register the announcement

```yaml
# scheduledMessages.yml
messages:
  - name: event-night
    channel: events
```

### 3. Design what players receive

```yaml
# lang/en.yml
announcements:
  event-night:
    - chat:
        - "<dark_gray>[<gold>EVENT<dark_gray>] <gray>The arena opens in <gold>5 minutes<gray>."
        - "<gray>Click <click:run_command:'/warp arena'><hover:show_text:'<yellow>Teleport'><aqua><u>here</u></aqua></hover></click> to join."
      actionbar: "<yellow>Prepare your loadout - the event starts soon"
      title: "<gradient:#ffd166:#ff6b6b><bold>EVENT NIGHT</bold></gradient>"
      subtitle: "<gray>The arena opens in 05:00"
      times: "500ms 3s 500ms"
      bossbar:
        message: "<light_purple>Event countdown"
        duration: 10s
        color: PURPLE
        overlay: PROGRESS
        progress: 0.75
      sound: "block.note_block.pling MASTER 1.0 1.0"
```

<p align="center">
  <img src="assets/readme/announcement.svg" alt="The same announcement on four surfaces at once: a boss bar countdown, a gradient title and subtitle, two chat lines and an action bar above the hotbar" width="100%">
</p>

Use only the parts that fit the message. A simple chat announcement can be a single string; a
showcase announcement can combine every surface above.

<a id="reference"></a>

## 📖 Reference

<a id="ref-channels"></a>

<details>
<summary><strong>Announcement channels and selectors</strong></summary>

Every channel has its own schedule and independent selector state:

```yaml
channels:
  - name: tips
    enabled: true
    initialDelay: 1m
    period: 5m
    selector: SHUFFLE

  - name: promotions
    enabled: true
    initialDelay: 2m
    period: 20m
    selector: WEIGHTED
```

| Selector | Behavior |
|:--|:--|
| `SHUFFLE` | Randomizes the order and shows every message once before anything repeats |
| `SEQUENTIAL` | Walks straight through the configured list |
| `RANDOM` | Draws independently; the same message may appear twice in a row |
| `WEIGHTED` | Draws randomly in proportion to each message's `weight` |

`SEQUENTIAL` and `SHUFFLE` keep their position across a normal configuration reload. A weight of
`0` parks a message only in a `WEIGHTED` channel; other selectors do not consult weights.

</details>

<a id="ref-audience"></a>

<details>
<summary><strong>Audience rules</strong></summary>

Top-level rules behave like logical **AND**. Nest `ANY_OF`, `NONE_OF` and `NOT` when the audience
needs more expressive logic.

| Rule | Matches |
|:--|:--|
| `PERMISSION` | Viewers holding a permission node |
| `GROUP` | Viewers holding `group.<name>`; this is permission-based, not a direct LuckPerms/Vault lookup |
| `WORLD` | Viewers in one of the configured worlds |
| `PLAYER_COUNT` | While the live online count is inside a configured range |
| `PLAYTIME` | Viewers whose native platform playtime is inside a configured duration range |
| `ANY_OF` | At least one nested rule |
| `NONE_OF` | None of the nested rules |
| `NOT` | The inverse of one nested rule |

```yaml
messages:
  - name: vip-event
    channel: events
    rules:
      - type: ANY_OF
        rules:
          - type: PERMISSION
            permission: rank.vip
          - type: PERMISSION
            permission: rank.staff
      - type: PLAYER_COUNT
        min: 10
        max: 100
```

Rules still apply to messages sent by an event trigger. Platform-specific rules are generated only
where the runtime can supply the required data.

</details>

<a id="ref-triggers"></a>

<details>
<summary><strong>Event triggers</strong></summary>

A triggered message leaves its timed channel rotation completely:

```yaml
messages:
  - name: welcome-back
    trigger:
      type: JOIN
      delay: 3s

  - name: first-join-welcome
    trigger:
      type: FIRST_JOIN
      delay: 3s

  - name: one-hundred-online
    trigger:
      type: PLAYER_COUNT
      threshold: 100
```

`PLAYER_COUNT` fires once when the threshold is reached and re-arms only after the online count
drops below it. `FIRST_JOIN` is unavailable on runtimes that do not expose a persistent first-join
signal.

> [!NOTE]
> `/automessage enable` and `/automessage disable` control scheduled channels. Event-triggered
> announcements operate independently.

</details>

<a id="ref-languages"></a>

<details>
<summary><strong>Languages and fallback behavior</strong></summary>

AutoMessage ships `en`, `pl` and `de`. Add another language by copying a file to
`lang/<code>.yml`, translating it and running `/automessage reload`.

For a player using `pt_br`, AutoMessage looks for:

1. `lang/pt_br.yml`
2. `lang/pt.yml`
3. the `fallbackLanguage` from `config.yml`

A missing announcement falls back individually, so a new translation does not have to be complete
before it is useful.

</details>

<a id="ref-minimessage"></a>

<details>
<summary><strong>MiniMessage, placeholders and notification parts</strong></summary>

All text uses [Kyori MiniMessage](https://docs.papermc.io/adventure/minimessage/format/), including colors,
gradients, decorations, hover text and click actions.

Built-in placeholders:

| Player-scoped | Server-scoped |
|:--|:--|
| `{PLAYER}` · `{DISPLAY_NAME}` · `{UUID}` · `{WORLD}` | `{ONLINE}` · `{MAX_PLAYERS}` · `{DATE}` · `{TIME}` |

On Bukkit and Folia, an installed PlaceholderAPI is detected automatically. Standard
`%placeholder_name%` tokens containing letters, digits and underscores can then be used alongside
the built-ins.

Available notification keys:

| Key | Value |
|:--|:--|
| bare string / `chat` | One chat line or a list of lines |
| `actionbar` | MiniMessage text shown above the hotbar |
| `title` / `subtitle` | Title layers; either can be used independently |
| `times` | Three durations: fade in, stay, fade out |
| `hideTitle` | Explicitly clear the current title |
| `bossbar` | Message, duration, color, overlay and optional progress `0.0–1.0` |
| `sound` | Sound key, or `key source volume pitch` |

Need a visual starting point? Try the external
[EternalCode Notification Generator](https://eternalcode.pl/notification-generator) and adapt the
generated notification to the language-file structure above.

</details>

<a id="ref-time"></a>

<details>
<summary><strong>Time format</strong></summary>

Every duration accepts an explicit unit, and units can be combined:

| Unit | Meaning | Example |
|:--:|:--|:--|
| `ms` | milliseconds | `500ms` |
| `s` | seconds | `30s` |
| `m` | minutes | `5m` |
| `h` | hours | `2h` |
| `d` | days | `1d` |

`1m30s` is valid. A plain number is interpreted as seconds. Channel periods shorter than `50ms`
are normalized, and tick-based runtimes ultimately execute on tick boundaries.

</details>

<a id="ref-discord"></a>

<details>
<summary><strong>Discord webhook mirror</strong></summary>

Set `enabled: true` and provide a Discord webhook URL in `discordWebhook.yml` to mirror scheduled
and triggered announcements.

The integration is deliberately conservative:

- only chat parts are mirrored;
- MiniMessage is flattened to plain text;
- the configured fallback language is used;
- server placeholders are resolved, while player-scoped values are removed;
- automatic Discord mentions are disabled;
- the webhook must use HTTPS and a Discord webhook host/path;
- rate-limit responses are retried with backoff.

The webhook is opt-in and makes no outbound request while disabled or missing a valid URL. Restart
AutoMessage after changing the webhook's enabled state or URL so the connection is rebuilt.

</details>

<a id="commands"></a>

### Commands and permissions

| Command | Permission | Description |
|:--|:--|:--|
| `/automessage enable` | `command.automessage.enable` | Resume scheduled announcement channels |
| `/automessage disable` | `command.automessage.disable` | Pause scheduled announcement channels |
| `/automessage reload` | `command.automessage.reload` | Reload schedules, message definitions and languages |
| `/automessage view <name>` | `command.automessage.view` | Preview one localized message; player-only, with tab completion |
| `/automessage next` | `command.automessage.next` | Show what each channel will announce next, without using it up |
| `/automessage stats` | `command.automessage.stats` | Show how often each message has been announced, and how long ago |

<details>
<summary><strong>Embedding AutoMessage in Minestom</strong></summary>

Minestom has no plugin directory. Initialize it, place AutoMessage on the classpath and enable the
library after Minestom itself is initialized:

```java
MinecraftServer.init();

AutoMessageMinestom autoMessage = AutoMessageMinestom.builder()
        .dataDirectory(Path.of("automessage"))
        .permissions((sender, node) -> myPermissions.check(sender, node)) // optional
        .enable();

// During your server shutdown:
autoMessage.shutdown();
```

Without a permission callback, AutoMessage uses Minestom's operator level to protect its commands.
Permission and group audience rules are then omitted because the runtime cannot answer them
accurately.

</details>

<a id="building-from-source"></a>

## 🔨 Building from source

Building every runtime requires **JDK 25**. From the repository root, run:

```bash
./gradlew dist
```

The task runs all checks, builds each platform-specific jar and synchronizes the six installable
artifacts into `build/dist`:

- Bukkit
- Folia
- Sponge
- Velocity
- Fabric
- Minestom

If any verification fails, the distribution is not produced. A normal `./gradlew build` performs
the same project checks but leaves artifacts inside their individual modules.

<a id="support"></a>

## 💛 Support the project

AutoMessage is free, open source and maintained independently.

| Found a problem? | Want to contribute? | Want to support development? |
|:--|:--|:--|
| [Open a bug report or feature request](https://github.com/imDMK/AutoMessage/issues/new/choose) | Read the [contribution guide](.github/CONTRIBUTING.md) and open a pull request | [GitHub Sponsors](https://github.com/sponsors/imDMK) · [PayPal](https://paypal.me/dominiksuliga) |

Please follow the project's [Code of Conduct](.github/CODE_OF_CONDUCT.md) in all community spaces.
AutoMessage is distributed under the [GNU General Public License v3.0](LICENSE).

<p align="center">
  <strong>If AutoMessage makes your server communication better, leave the repository a star.</strong><br>
  It helps other server owners discover the project.
</p>
