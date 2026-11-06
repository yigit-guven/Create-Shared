# Create: Shared — Developer & Library Guide

**Create: Shared** (`create_shared`) is a shared foundation and content library for **Create** addons across supported Minecraft versions and mod loaders. This guide covers everything mod developers need to depend on Create: Shared, integrate with its unified configuration system, and reuse its blocks, registries, ventilation networks, and rendering utilities.

---

## 1. Adding Create: Shared to Your Mod

### `gradle.properties`

Define the version properties matching your target branch:

```properties
create_shared_version=1.0.0
create_shared_version_range=[1.0.0,)
```

### `build.gradle`

Whenever a release is published on GitHub (for example, tag `1.0.0`), **Create: Shared** automatically builds and publishes a Maven package with that exact tag version (`1.0.0`). You can depend on this package directly in your development environment without needing CurseMaven or Modrinth:

```groovy
repositories {
    // Option 1: GitHub Packages (Official Release Packages)
    maven {
        name = "CreateSharedGitHubPackages"
        url = uri("https://maven.pkg.github.com/YigitGuven/Create-Shared")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
        }
    }

    // Option 2: JitPack (Zero-credential GitHub Release Maven)
    maven {
        name = "JitPack"
        url = uri("https://jitpack.io")
    }

    // Option 3: Local Maven (if building locally via ./gradlew publishToMavenLocal)
    mavenLocal()
}

dependencies {
    // From GitHub Packages or Local Maven (version matches the GitHub Release tag, e.g. 1.0.0):
    implementation("net.yigitguven.create_shared:create_shared:${create_shared_version}")

    // Or from JitPack using the GitHub Release tag:
    // implementation("com.github.YigitGuven:Create-Shared:${create_shared_version}")
}
```

### Declaring the Dependency in Mod Metadata

Declare `create_shared` as a dependency in your mod's metadata file (`mods.toml` or `fabric.mod.json` depending on your target loader):

#### `mods.toml` (TOML Mod Metadata)

```toml
[[dependencies.${mod_id}]]
    modId="create_shared"
    mandatory=true # or type="required" depending on loader version
    versionRange="${create_shared_version_range}"
    ordering="AFTER"
    side="BOTH"
```

#### `fabric.mod.json` (JSON Mod Metadata)

```json
{
  "depends": {
    "create_shared": ">=${create_shared_version}"
  }
}
```

> **Important:** Declaring `create_shared` as a dependency in your mod metadata file automatically enrolls your mod into Create: Shared's **Unified Configuration Screen**.

---

## 2. Unified Ecosystem Configuration System

Create: Shared includes an automatic multi-mod configuration system (`CreateSharedConfigs` and `CreateSharedConfigScreen`) so players have a single, consistent place to configure Create: Shared and every installed addon that depends on it.

### How It Works

1. **Automatic Discovery:**
   Any mod in the modpack that lists `create_shared` as a dependency in its mod metadata file and registers a non-empty configuration spec (`CLIENT`, `SERVER`, `COMMON`, or `STARTUP`) is automatically detected.
2. **Ordering & Presentation:**
   When a player opens the configuration screen:
   - Configurations are grouped by configuration type (**Client Configuration**, **Server Configuration**, **Common Configuration**, **Startup Configuration**).
   - Inside each type screen, **Create: Shared** is always presented at the very top (when it has configs for that type), followed by all dependent mods sorted **alphabetically (`A–Z`)** by their mod `displayName`.
   - Under each mod's gold title header, that mod's config values and sections are listed inline.
3. **Client-Sided Configurations:**
   Create: Shared itself only registers a `SERVER` config (`create_shared-server.toml`). However, if your mod (or any other dependent mod in the modpack) registers a `CLIENT` config, Create: Shared automatically creates the **Client Configuration** menu entry and displays all dependent mods' client configs alphabetically.
4. **Shared Config Button Across Mods:**
   Create: Shared automatically registers (and overrides) the in-game **Config** screen factory on all participating dependent mods during client setup and load completion. Clicking **Config** on either **Create: Shared** or **your mod** in the in-game Mods menu opens the unified configuration screen. You do **not** need to register your own config screen factory.
5. **Independent File Saving:**
   Each mod still owns its own `.toml` file on disk (e.g., `your_mod-server.toml`, `your_mod-client.toml`). Edits, Undo history, and Reset actions in the shared UI write directly back to each mod's respective config spec.

---

### Registering Configurations in Your Mod

#### Option A: Standard Config Registration (Auto-Detected)

If `create_shared` is declared in your mod metadata dependencies, standard config registration works out of the box without any extra calls—Create: Shared automatically detects your mod and includes your registered `SERVER`, `CLIENT`, `COMMON`, or `STARTUP` configs.

#### Option B: Explicit Registration via `CreateSharedConfigs` API

You can also use `net.yigitguven.create_shared.config.CreateSharedConfigs` directly to either register your configs or explicitly enroll your mod ID:

```java
import net.yigitguven.create_shared.config.CreateSharedConfigs;

// Enrolls your mod ID explicitly in the shared config screen
CreateSharedConfigs.registerMod(MODID);

// Or register a config spec and enroll your mod in one call
CreateSharedConfigs.registerConfig(modContainer, ModConfig.Type.SERVER, MyAddonServerConfig.SPEC);
CreateSharedConfigs.registerConfig(modContainer, ModConfig.Type.CLIENT, MyAddonClientConfig.SPEC);
```

---

### Writing Clean Config Specs & Translations

To ensure your mod's settings and tooltips display cleanly inside the shared configuration screen, attach `.translation(...)` keys to your sections and values in your config builder:

```java
BUILDER.translation("my_addon.config.visuals").push("visuals");

SHOW_OVERLAY = BUILDER
    .comment("Whether to render the custom contraption overlay.")
    .translation("my_addon.config.showOverlay")
    .define("showOverlay", true);

OVERLAY_SCALE = BUILDER
    .comment("Scale percentage of the overlay.")
    .translation("my_addon.config.overlayScale")
    .defineInRange("overlayScale", 100, 50, 200);

BUILDER.pop();
```

And provide matching entries (plus `.tooltip` entries) in your `assets/my_addon/lang/en_us.json`:

```json
{
  "my_addon.config.visuals": "Visuals & Overlays",
  "my_addon.config.visuals.tooltip": "Client-side rendering and HUD settings.",
  "my_addon.config.showOverlay": "Show Contraption Overlay",
  "my_addon.config.showOverlay.tooltip": "Whether to render the custom contraption overlay.",
  "my_addon.config.overlayScale": "Overlay Scale",
  "my_addon.config.overlayScale.tooltip": "Scale percentage of the overlay."
}
```

---

## 3. Reusable Block Classes & Registries

### Factory Building Block Classes (`net.yigitguven.create_shared.block`)

Create: Shared provides ready-to-use block classes that already implement Create's `IWrenchable` interface (supporting **Right-Click** to rotate states and **Shift + Right-Click** for instant wrench pickup):

| Class | Description |
| :--- | :--- |
| `FactoryBlock` | Standard full-cube industrial block with `IWrenchable` dismantle support. |
| `HazardPillarBlock` | 3-axis rotatable pillar block (`AXIS`) with wrench axis rotation. |
| `FactoryStairsBlock` | Industrial stairs with wrench horizontal facing rotation and half-flipping. |
| `FactorySlabBlock` | Industrial slab (`BOTTOM` / `TOP` / `DOUBLE`) with wrench half toggling. |
| `FactoryWallBlock` | Industrial wall block with `IWrenchable` pickup. |
| `IndustrialGrateBlock` | Cutout floor grate with `SimpleWaterloggedBlock` and `IWrenchable` support. |
| `FactoryVentBlock` | Directional 6-way operable hollow exhaust vent gate (`FACING`, `OPEN`, `POWERED`). |
| `FactoryVentDuctBlock` | 6-way connected hollow crawl duct with automatic internal corner clearing and vertical climbing. |

### Public Registries (`net.yigitguven.create_shared.registry`)

- **`ModBlocks`**: Access all registered factory block instances (`HAZARD_BLOCK`, `EMERGENCY_HAZARD_BLOCK`, `DIAMOND_PLATE`, `RIVETED_SHEET_METAL`, `FACTORY_VENT`, `FACTORY_VENT_DUCT`, `INDUSTRIAL_GRATE`, plus their `_STAIRS`, `_SLAB`, and `_WALL` variants).
- **`ModItems`**: Access all block items, `HARD_HAT` (`create_shared:hard_hat`), and `CREATE_ARMOR_TRIM_SMITHING_TEMPLATE` (`create_shared:create_armor_trim_smithing_template`).
- **`ModBlockEntities`**: Access `FACTORY_VENT` and `FACTORY_VENT_DUCT` block entity types.
- **`ModArmorMaterials`**: Access the `HARD_HAT` armor material.
- **`ModCreativeTabs`**: Access `CREATE_SHARED_TAB` (`create_shared:create_shared_tab`).

---

## 4. Ventilation & Airflow Network API

If your mod interacts with ventilation ducts or custom air-driven machinery, you can query or trigger Create: Shared's airflow networks via `net.yigitguven.create_shared.block.entity`:

- **`AbstractVentBlockEntity`** (implemented by both `FactoryVentBlockEntity` and `FactoryVentDuctBlockEntity`):
  - `getTotalAirflow()` — Raw summed RPM from all connected Create Encased Fans.
  - `getEffectiveAirflow()` — Effective network RPM after duct friction loss (`positive` = blowing, `negative` = vacuum suction).
  - `getVentFlow()` — Airflow RPM allocated specifically to this open exhaust vent.
  - `getConnectedFans()` / `getOpenVents()` / `getNetworkDuctCount()` — Network topology metrics.
  - `isDissipated()` — `true` if duct friction exceeded total fan input.
  - `markNetworkDirty()` — Marks the local block entity dirty so `VentNetwork` recalculates on the next tick.
- **`VentNetwork.scanAndRecalculate(Level level, BlockPos startPos, long currentTime)`**:
  - Forces an immediate BFS scan of connected ducts, vents, and Encased Fans starting at `startPos` and updates all member block entities.

---

## 5. Dyeing, Trim Tinting & Rainbow Utilities (`CreateSharedClient`)

On the client side, `net.yigitguven.create_shared.client.CreateSharedClient` exposes helpers used for dyeing and armor-trim tinting on tools and headgear:

- **`CreateSharedClient.getTrimMaterialColor(ItemStack stack)`**:
  Returns an opaque ARGB color (`0xFFRRGGBB`) corresponding to the armor trim material applied to `stack` (supporting Quartz, Iron, Netherite, Redstone, Copper, Gold, Emerald, Diamond, Lapis, Amethyst, and custom trim material text colors, defaulting to `DEFAULT_BRASS_COLOR`).
- **`CreateSharedClient.isRainbowStack(ItemStack stack)`**:
  Returns `true` if `stack` carries custom data containing `"Rainbow"` or `"CreativeTabIconRainbow"`.
- **`CreateSharedClient.getRainbowColor()`**:
  Returns a smoothly cycling opaque ARGB rainbow color synchronized to system time.
