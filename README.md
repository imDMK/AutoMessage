# 💬 AutoMessage

**Automatic broadcasts your players actually read — and your server never feels.**

[![Build Status](https://github.com/imDMK/AutoMessage/actions/workflows/gradle.yml/badge.svg)](https://github.com/imDMK/AutoMessage/actions/workflows/gradle.yml)
![JDK](https://img.shields.io/badge/JDK-21%2B-blue.svg)
![Supported versions](https://img.shields.io/badge/Minecraft-1.21--26.2-green.svg)
[![SpigotMC](https://img.shields.io/badge/SpigotMC-yellow.svg)](https://www.spigotmc.org/resources/automessage.112363/)
[![Bukkit](https://img.shields.io/badge/Bukkit-blue.svg)](https://legacy.curseforge.com/minecraft/bukkit-plugins/auto-messages)
[![PaperMC](https://img.shields.io/badge/Paper-004ee9.svg)](https://hangar.papermc.io/imDMK/AutoMessage)
[![Modrinth](https://img.shields.io/badge/Modrinth-1bd96a.svg)](https://modrinth.com/plugin/automessage)
[![Polymart](https://img.shields.io/badge/Polymart-green.svg)](https://polymart.org/product/7890/automessage-1-17-1-21-5)
[![bStats](https://img.shields.io/badge/bStats-00695c)](https://bstats.org/plugin/bukkit/AutoMessages/19487)
[![Donate](https://img.shields.io/badge/Donate-PayPal-0070ba.svg)](https://paypal.me/dominiksuliga)

---

Rotating announcements are the cheapest way to keep a server alive — vote reminders, rank perks,
Discord links, event countdowns. Done badly they spam chat and eat ticks. **AutoMessage** does them
properly: gradient-styled messages across **chat, action bar, titles, boss bars and sounds**, shown
to exactly the players who should see them, on a schedule you control down to the millisecond.

One pass over the online players per interval. That is the entire cost.

---

## ✨ Why AutoMessage

|                              |                                                                                                                                          |
|------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| 🎨 **Every delivery channel** | Chat, ActionBar, Title & Subtitle, BossBar (color, style, progress, duration) and sounds — mix them freely inside a single message.        |
| 🎯 **Audience rules**         | Send to everyone, to a permission, or to a rank. Each message chooses its own audience.                                                   |
| 🖌️ **Full MiniMessage**       | Gradients, hover, click, colors — the whole [Kyori Adventure](https://github.com/KyoriPowered/adventure) syntax, no legacy colour codes.  |
| ⏱️ **Timing you control**     | Human-readable intervals (`30s`, `1m30s`, `2h`) with a separate startup delay.                                                            |
| 🔀 **Rotation modes**         | Sequential or random — and the rotation survives a reload instead of jumping back to the first message.                                   |
| 👁️ **Preview before you ship**| `/automessage view <name>` renders a message to you alone, audience rules ignored.                                                        |
| ♻️ **Reload without restart** | Every value re-reads live, the schedule included.                                                                                         |
| 🪶 **Genuinely lightweight**  | No database, no per-tick work, no runtime reflection.                                                                                     |

---

## 🚀 Getting started

1. Drop the jar into `plugins/` and start the server.
2. Edit `scheduledMessages.yml` — a working example is generated for you.
3. Run `/automessage reload`. That is it.

### 📦 Requirements

- **Java 21** or newer
- **Spigot / Paper 1.21 – 26.2**

---

## ⏱️ Message interval

The schedule lives in `messagesDispatcher.yml`:

```yaml
# How much time passes between two announcements.
period: 30s

# How long to wait after startup before the first announcement.
initialDelay: 10s
```

Every time value carries an explicit unit:

| Unit | Meaning      | Example |
|------|--------------|---------|
| `ms` | milliseconds | `500ms` |
| `s`  | seconds      | `30s`   |
| `m`  | minutes      | `5m`    |
| `h`  | hours        | `1h`    |

Units combine (`1m30s`). A plain number is read as **seconds**, so `period: 10` means ten seconds —
the plugin rewrites it as `10s` on the next load.

Both values are applied by `/automessage reload`; a server restart is not required.

---

## 🔐 Commands & permissions

| Command                    | Permission                    | Description                              |
|----------------------------|-------------------------------|------------------------------------------|
| `/automessage enable`      | `command.automessage.enable`  | Resume automatic broadcasts              |
| `/automessage disable`     | `command.automessage.disable` | Pause automatic broadcasts               |
| `/automessage reload`      | `command.automessage.reload`  | Re-read every configuration file         |
| `/automessage view <name>` | `command.automessage.view`    | Preview one message, with tab-completion |

---

## ✨ Showcase

![AutoMessage Chat Preview](assets/automessage-preview.gif)

---

## 🔔 Configuring notifications

Build message payloads visually with the
[EternalCode notification generator](https://eternalcode.pl/notification-generator).

---

## 💛 Donate

AutoMessage is free, open source, and maintained in my own time. If it saves you work — or your
server earns from the announcements it sends — consider buying me a coffee:

### 👉 **[paypal.me/dominiksuliga](https://paypal.me/dominiksuliga)**

Every contribution goes straight back into new features, faster support, and day-one compatibility
with each Minecraft release. Not in a position to donate? A ⭐ on GitHub or a review on
[SpigotMC](https://www.spigotmc.org/resources/automessage.112363/) helps just as much.

---

## 🛠️ Feedback & support

Found a bug? Have an idea? Want to contribute?
👉 [Open an issue](https://github.com/imDMK/AutoMessage/issues) — feedback is genuinely welcome.
