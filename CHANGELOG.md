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
- Added comprehensive documentation and automated GitHub Wiki sync.
- Added cross-branch documentation synchronization workflow.
- Added GitHub issue templates for bug reports, crash reports, and feature requests.
