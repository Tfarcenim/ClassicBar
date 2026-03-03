# ClassicBar

Replaces the vanilla HUD icon rows with compact horizontal bars. Health scales with your max HP instead of capping at 10 hearts, and most bars have configurable colors with gradient support.

Requires **NeoForge** on **Minecraft 1.21.1**. Client-side only — safe to add to any server without requiring it server-side.

## Bars

Vanilla: Health, Armor, Armor Toughness, Food, Saturation, Absorption, Air, Mount Health

Mod support (loaded automatically if the mod is present):
- **Vampirism** — blood bar
- **Tough As Nails** — thirst + hydration
- **Thirst Was Taken** — thirst bar
- **Homeostatic** — water bar
- **ParCool** — stamina bar

## Configuration

### In-Game Config Screen

Open **Mods** → **ClassicBar** → **Config** to edit settings directly in-game. The config screen is organized by section:

- **General** — core HUD settings (icons, bar styles, colors, gradients, overlays, bar order)
- **Tough as Nails** — thirst, hydration, and exhaustion settings (only visible if the mod is loaded)
- **Thirst Was Taken** — thirst bar colors (only visible if the mod is loaded)
- **Homeostatic** — water and hydration bar colors (only visible if the mod is loaded)

All settings are fully localized with tooltips explaining each option.

### Config Files

The main config file lives at `config/classicbar-client.toml`. It covers colors, gradients, exhaustion overlay, saturation bar, held food/drink preview, and bar ordering. This file is auto-generated on first launch and can be edited directly or via the in-game screen.

Each bar also gets its own JSON file in `config/classicbar/` (e.g. `health.json`, `food.json`). These let you toggle the number display and override the icon texture per bar. Files are created on first launch with defaults and won't be overwritten on subsequent loads, so edits are safe.

### Notable Options

- Per-bar number display (on/off per JSON)
- Full vs. fitted absorption and armor bars
- Dynamic health color with configurable gradient stops and fractions (normal, poisoned, withered, frozen)
- Bar render order (left and right side independently)
- Low armor durability warning flash
- Mod-specific overlays (Tough as Nails hydration, Thirst Was Taken quenched level, Homeostatic hydration)
