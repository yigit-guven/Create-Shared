# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] - 2026-10-18

### Added
- Initial project release for Minecraft **1.21.1** on **NeoForge** (`21.1.262+`).
- Integrated **Create Mod** (`6.0.0+`) as mandatory dependency.
- Integrated **Just Enough Items (JEI)** (`19.0.0+`) for recipe viewing and runtime compatibility.
- Integrated **Curios API** (`9.5.1+1.21.1`) into Gradle for compilation and local development runtime (`localRuntime`).
- Added dynamic RGB dyeing support for Create's existing **Wrench** (`create:wrench`) and **Engineer's Goggles** (`create:goggles`):
  - Items can be dyed in crafting tables with any vanilla dye or dye combinations via `#minecraft:dyeable` and `DataComponents.DYED_COLOR`.
  - Right-clicking water cauldrons washes off applied dye and restores the default look.
  - Custom tint-aware models and client item color handlers tint only the wrench handle grip and goggles leather headband/strap, preserving brass and mechanical textures.
  - Full support for inventory GUI, first/third-person rendering, armor head slot, and Curios accessory rendering.
- Added the **Safety Hard Hat** (`create_shared:hard_hat`):
  - Custom 3D blocky armor model featuring a domed crown, flared brim with extended front visor, 3 raised reinforcement ridges, and side accessory brackets accurately modeled after classic industrial safety hard hats.
  - **Impact Deflection**: 100% immune to falling anvils, falling stalactites, and falling blocks, deflecting them with a loud metallic clang sound while taking durability damage.
  - **Crush Cushioning**: Grants 75% damage reduction against kinetic crushing (`create:crush`, `create:run_over`), suffocation in blocks (`in_wall`), flying into walls (`fly_into_wall`), and cramming.
  - **Dyeable**: Default Safety Yellow (`0xFFF4C430`), fully dyeable via crafting with any vanilla dye, washable in cauldrons, and compatible with dynamic rainbow animations.
  - Balanced armor stats: 3 Defense, 2 Toughness, +0.1 Knockback Resistance, and 385 Durability.
- Added 18 factory-themed decorative blocks, complete with stairs, slabs, and walls:
  - **Hazard Blocks** (Yellow & Black caution stripes): Hazard Block (3-axis rotatable pillar), Hazard Stairs, Hazard Slab, Hazard Wall.
  - **Emergency Hazard Blocks** (Red & White danger stripes): Emergency Hazard Block (3-axis rotatable pillar), Emergency Hazard Stairs, Emergency Hazard Slab, Emergency Hazard Wall.
  - **Diamond Plate**: Industrial embossed steel tread plate, Diamond Plate Stairs, Diamond Plate Slab, Diamond Plate Wall.
  - **Riveted Sheet Metal**: Industrial boiler plating with seams and hex rivets, Riveted Sheet Metal Stairs, Riveted Sheet Metal Slab, Riveted Sheet Metal Wall.
  - **Factory Exhaust Vent**: Directional 6-way ventilation unit with metal louvers, heavy casing, and status LED indicator.
  - **Industrial Floor Grate**: Catwalk floor mesh featuring transparent cutout slots and waterloggable support (`SimpleWaterloggedBlock`).
- Integrated Create Mod wrench mechanics (`IWrenchable` & `#create:wrench_pickup`):
  - **Shift + Right-Click** with wrench instantly dismantles blocks back into player inventory with wrench remove sounds and particles.
  - **Right-Click** with wrench rotates block states (rotates pillar axes, stair facings, vent directions, and toggles slab halves) with wrench rotate sounds.
- Added full crafting and Stonecutter recipes, custom 16x16 pixel-art textures, loot tables, and pickaxe mineable tags for all factory blocks.
- Added dedicated **Create: Shared** creative mode tab:
  - Features an animated rainbow-tinted Create Goggles icon dynamically cycling through the color spectrum in real-time.
  - Organizes all factory decorative blocks in a single dedicated tab.
- Added comprehensive documentation and automated GitHub Wiki sync.
- Added cross-branch documentation synchronization workflow.
- Added GitHub issue templates for bug reports, crash reports, and feature requests.
