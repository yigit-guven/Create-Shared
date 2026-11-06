# Create Shared

[![Create Mod](https://img.shields.io/badge/Create-Addon%20%26%20Library-blue.svg)](https://curseforge.com/minecraft/mc-mods/create)
[![JEI Compatible](https://img.shields.io/badge/JEI-Compatible-purple.svg)](https://curseforge.com/minecraft/mc-mods/jei)
[![License: LGPL v3](https://img.shields.io/badge/License-LGPL_v3-blue.svg)](LICENSE)

**Create Shared** is a shared library and content addon for the **[Create](https://curseforge.com/minecraft/mc-mods/create)** mod across multiple Minecraft versions and mod loaders. It introduces industrial building blocks, ventilation & airflow networks, protective gear, tool/goggle customization, and a unified configuration system for dependent Create addons while maintaining full recipe lookup compatibility with **[Just Enough Items (JEI)](https://curseforge.com/minecraft/mc-mods/jei)**.

---

## 🚀 Features

- **Shared Ecosystem Configuration**: Automatically aggregates configurations for Create: Shared and all dependent mods into a unified in-game configuration UI.
- **Mandatory Create Integration**: Deeply integrates with Create's rotational power network, Encased Fans, wrench interactions, and contraptions.
- **Ponder Scenes**: Visual in-game animated tutorials powered by Create's Ponder system.
- **JEI Recipe Support**: Inspect recipes, kinetic processing steps, and crafting recipes right in JEI.

---

## 📋 Requirements

| Requirement | Mandatory? | Notes |
| ----------- | ---------- | ----- |
| **Minecraft** | **Yes** | Match the release branch/file for your Minecraft version |
| **Mod Loader** | **Yes** | Match the mod loader for your chosen release |
| **Create Mod** | **Yes** (Mandatory) | Matching version for your Minecraft & loader |
| **JEI** | *Optional (Recommended)* | For viewing crafting and processing recipes |

---

## 📥 Installation

1. Install the supported **Mod Loader** for your target **Minecraft** version.
2. Download the matching version of **[Create](https://curseforge.com/minecraft/mc-mods/create)**.
3. Download the matching build of **Create Shared** from [Releases](https://github.com/yigit-guven/Create-Shared/releases).
4. *(Optional)* Download **[JEI](https://curseforge.com/minecraft/mc-mods/jei)** for your Minecraft version.
5. Place all downloaded `.jar` files in your `.minecraft/mods` directory and launch the game!

---

## 📖 Documentation & Wiki

All guides, mechanical explanations, and developer references are synchronized with our GitHub Wiki:
- 📚 **[GitHub Wiki](https://github.com/yigit-guven/Create-Shared/wiki)**
- 🧩 **[Developer & Library Guide](DEVELOPER_GUIDE.md)** *(Using Create: Shared as a dependency, shared config system, & APIs)*
- 🚀 **[Getting Started Guide](docs/Getting-Started.md)**
- ⚙️ **[Features & Mechanics](docs/Features.md)**
- 🛠️ **[Configuration Guide](docs/Configuration.md)**
- 🔗 **[Compatibility Guide](docs/Compatibility.md)**
- ❓ **[FAQ](docs/FAQ.md)**

---

## 🛠️ Development & Building

To clone and compile the project locally:

```bash
git clone https://github.com/yigit-guven/Create-Shared.git
cd Create-Shared
./gradlew build
```

To run the development client in your IDE:
```bash
./gradlew runClient
```

---

## 🤝 Contributing

Contributions, bug reports, and suggestions are welcome!
- Review our [Contribution Guidelines](CONTRIBUTING.md).
- Report bugs or crashes via [GitHub Issues](https://github.com/yigit-guven/Create-Shared/issues).
- Please adhere to our [Code of Conduct](CODE_OF_CONDUCT.md).

---

## 📜 License

This project is licensed under the terms of the **GNU Lesser General Public License v3.0 (LGPL-3.0)**. See [LICENSE](LICENSE) for details.
