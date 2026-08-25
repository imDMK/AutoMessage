# 💬 AutoMessage — Ultra-Efficient Broadcasting System

[![Build Status](https://github.com/imDMK/AutoMessage/actions/workflows/gradle.yml/badge.svg)](https://github.com/imDMK/AutoMessage/actions/workflows/gradle.yml)
![JDK](https://img.shields.io/badge/JDK-1.21-blue.svg)
![Supported versions](https://img.shields.io/badge/Minecraft-1.21--1.21.10-green.svg)
[![SpigotMC](https://img.shields.io/badge/SpigotMC-yellow.svg)](https://www.spigotmc.org/resources/automessage.112363/)
[![Bukkit](https://img.shields.io/badge/Bukkit-blue.svg)](https://legacy.curseforge.com/minecraft/bukkit-plugins/auto-messages)
[![PaperMC](https://img.shields.io/badge/Paper-004ee9.svg)](https://hangar.papermc.io/imDMK/AutoMessage)
[![Modrinth](https://img.shields.io/badge/Modrinth-1bd96a.svg)](https://modrinth.com/plugin/automessage)
[![Polymart](https://img.shields.io/badge/Polymart-green.svg)](https://polymart.org/product/7890/automessage-1-17-1-21-5)
[![bStats](https://img.shields.io/badge/bStats-00695c)](https://bstats.org/plugin/bukkit/AutoMessages/19487)

---

### 🚀 Lightweight. Customizable. Blazing Fast.

**AutoMessage** is a high-performance plugin for fully customizable automatic server-wide broadcasts. No lag. No hassle. Just clean, dynamic message delivery across chat, actionbars, titles, and bossbars — all with nearly zero performance impact.

---

## 🧠 Features

✅ **Multiple broadcast types**  
• Chat  
• ActionBar  
• Title & Subtitle  
• BossBar (fully configurable: color, style, duration, progress, etc.)

✅ **Advanced configuration**  
• Create/edit/delete messages directly  
• Toggle automatic messages at any time  
• Support for various broadcast modes  
• Dynamic intervals between messages  
• Full MiniMessage & Adventure API formatting

✅ **Command support**  
• `/automessage enable/disable`  
• `/automessage reload`  

✅ **Zero-lag optimized**  
Built with efficiency in mind — AutoMessage has no noticeable impact on server performance, even with hundreds of players online.

✅ **Adventure API support**  
Integrates with [Kyori Adventure](https://github.com/KyoriPowered/adventure) for rich message styling and component-based formatting.

---

## ⏱️ Message interval

The interval lives in `messagesDispatcher.yml`:

```yaml
# How much time passes between two announcements.
period: 30s

# How long to wait after startup before the first announcement.
initialDelay: 10s
```

Every time value carries an explicit unit:

| Unit | Meaning      | Example  |
|------|--------------|----------|
| `ms` | milliseconds | `500ms`  |
| `s`  | seconds      | `30s`    |
| `m`  | minutes      | `5m`     |
| `h`  | hours        | `1h`     |

Units can be combined (`1m30s`). A plain number without a unit is read as **seconds**, so
`period: 10` means ten seconds — the plugin rewrites it as `10s` on the next load.

Both values are applied by `/automessage reload`; a server restart is not required.

---

## ✨ Showcase
![AutoMessage Chat Preview](assets/automessage-preview.gif)

---

## 🛠️ Feedback & Support

Found a bug? Have a suggestion? Want to contribute?  
Open an issue on [GitHub Issues](https://github.com/imDMK/AutoMessage/issues) — your feedback is welcome!

---

## ⭐ Like the plugin?

If you enjoy using **AutoMessage**, please consider leaving a ⭐ rating on [SpigotMC](https://www.spigotmc.org/resources/automessage.112363/)!  
It helps support future development and lets others discover it too.
