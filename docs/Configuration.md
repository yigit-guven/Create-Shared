# Configuration

**Create: Shared** provides a unified in-game and file-based configuration system for both Create: Shared and any addon mods that depend on it.

---

## 🖥️ Unified In-Game Configuration Screen

When you open the **Mods** menu in Minecraft and click **Config** on **Create: Shared** (or on any mod that depends on `create_shared`), you are presented with the shared ecosystem configuration screen:

1. **Grouped by Configuration Type**:
   - **Server Configuration**: World-specific gameplay and network rules. Shows **Create: Shared** at the very top followed by all dependent mods sorted alphabetically (`A–Z`).
   - **Client Configuration**: Automatically created and displayed whenever one or more dependent mods in the modpack provide client-side configuration options, listing those mods alphabetically (`A–Z`).
   - **Common / Startup Configuration**: Displayed whenever any dependent mod registers common or startup configuration files.
2. **Inline Sections & Settings**:
   - Each participating mod is shown under a distinct gold title header with its config entries and subsections listed directly below it.
   - Full **Undo** and **Reset** support is provided across all participating mods, saving changes directly to each mod's respective `.toml` file.

---

## 📂 Create: Shared Server Options (`create_shared-server.toml`)

Saved per world in `saves/<world>/serverconfig/create_shared-server.toml` under the `[ventilation]` section:

| Option | Type | Default | Range | Description |
| :--- | :--- | :--- | :--- | :--- |
| `distributionMode` | Enum | `DISTANCE_ATTENUATED` | `DISTANCE_ATTENUATED`, `EQUAL` | How airflow is distributed among open exhaust vents (`DISTANCE_ATTENUATED` = vents closer to fans blow harder; `EQUAL` = all open vents receive equal airflow). |
| `distanceDecayRatio` | Double | `0.05` | `0.0` – `1.0` | Decay ratio per duct distance in `DISTANCE_ATTENUATED` mode (higher = faster dropoff with distance). |
| `frictionLossPerDuct` | Double | `0.25` | `0.0` – `10.0` | Airflow (RPM) lost per duct in the network due to internal friction. |
| `minAirflowPerVent` | Double | `4.0` | `0.0` – `64.0` | Minimum airflow (RPM) required at an open vent to produce wind or suction. |
| `maxWindRange` | Integer | `20` | `1` – `64` | Maximum range in blocks that wind/suction can reach from an open vent. |
| `windStrength` | Double | `1.0` | `0.1` – `5.0` | Multiplier for physical push/pull force applied to entities by vents. |
| `maxNetworkBlocks` | Integer | `1024` | `16` – `10000` | Maximum number of connected ducts/vents scanned in a single ventilation network. |

---

## 🧩 For Mod Developers

See the root **[Developer & Library Guide](../DEVELOPER_GUIDE.md)** for full instructions on integrating your mod's configurations with Create: Shared.
