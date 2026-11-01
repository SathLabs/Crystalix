# Crystalix

[![Maven](https://img.shields.io/maven-metadata/v?metadataUrl=https://maven.satherov.dev/releases/dev/satherov/crystalix/crystalix/maven-metadata.xml&style=for-the-badge&label=Maven&logo=apachemaven&color=C71A36)](https://maven.satherov.dev/#/releases/dev/satherov/crystalix/crystalix)
[![Curseforge](https://img.shields.io/curseforge/dt/1187033?style=for-the-badge&label=Downloads&logo=curseforge&color=F16436)](https://www.curseforge.com/minecraft/mc-mods/crystalix)
[![GitHub](https://img.shields.io/badge/GitHub-Crystalix-181717?style=for-the-badge&logo=github)](https://github.com/SathLabs/Crystalix)

Crystalix is a mod centered around glass with a bunch of properties that can be applied to it.

## Information

- Adds `Crystalix Glass`, a glass block which can hold any mix and match of properties you like.
- Adds the `Crystalix Wand`, which is used to apply properties to the Crystalix Glass.
- Any property can be mixed with any other property, so you can decide yourself which combination works best.

![Recipe](https://github.com/SathLabs/Crystalix/blob/26.1/.github/assets/recipe.png?raw=true)

## The Crystalix Wand

- Using the wand on a Crystalix Glass block applies the stored properties to that block.
- **Crouching** while using the wand will apply the stored properties to all connected blocks up to a configurable limit.
- If the wand is held in your **Offhand**, the properties will be applied immediately when placing the block.
- Pressing `V` opens the radial editor, which allows you to customize the settings of the wand, including a color picker screen.
- Pressing `X` while looking at a Crystalix Glass block will copy its properties to the wand.
- Pressing `Left Control` toggles whether the wand should apply color as well or the properties only.

![Radial Menu](https://github.com/SathLabs/Crystalix/blob/26.1/.github/assets/radial-menu.png?raw=true)

## Properties

### Invisibility

- Makes the block fully invisible.
- Useful for creating barrier-like blocks or if you simply want an invisible light or redstone source.

### Material

- Changes the base texture of the block.
- Cycles between the available glass looks:
    - `Normal` : Default Vanilla glass look.
    - `Bordered` : Glass without the scratches on the texture and only a border.
    - `Clear` : Completely clear glass with no texture.

### Tinted

- Switches between a tinted and a clear texture style.
- Matches the vanilla tinted glass vs default glass look.

### Color

- Allows you to select any color via the color picker.
- You can use any RGB or HEX color freely.

![](https://github.com/SathLabs/Crystalix/blob/26.1/.github/assets/color-picker.png?raw=true)

### Waterloggable

- Decides whether the block is allowed to change its waterlogged state.
- If disabled, you cannot remove water from the block if any is applied or add any if none is applied.
- If enabled, you can add and remove water like any other waterloggable block.

### Reinforced

- Makes the block completely indestructible to any force but players mining it.
- Fully wither- and blastproof and will also contain explosions within it to prevent any damage from passing through the block.
- Also increases the time it takes for the block to be mined by 10x.

### Shadeless

- Removes the shading effect from the block.
- Means no shadows will be rendered on the block which can be used for a more flat look.

### Redstone

- Makes the block emit a configurable redstone signal from `0` to `15`.
- Lets you use the block as a redstone source with a specific signal level.

### Conductor

- Makes the block conduct redstone like a solid block.
- Useful if you want to change the typical behavior of glass not transmitting signals.

### Ghost

- Changes who can pass through the block:
  - nothing
  - everything
  - **only** players
  - everything **except** players
  - **only** monsters
  - everything **except** monsters
  - **only** animals
  - everything **except** animals
  - **only** adult creatures
  - everything **except** adult creatures
- Useful for mob filtering or creating passageways for players only

### Light

- Cycles through several lighting modes:
  - `None` for no special behavior.
  - `Light` to emit a light level of `15`.
  - `Dark` to block light from passing through the block.
  - `Fake Light` to visually glow without the real gameplay impact of normal light emission.
  - `Fake Dark` to visually appear dark while keeping the real light behavior separate.

## Compatibility

## Jade

- Displays the stored properties of the glass block in the tooltip.
- By default the tooltip is only shown while the player is holding the wand.
- This can be changed via the `jadeMode` client config:
    - `ALWAYS` : Always show the tooltip.
    - `WAND` : Show the tooltip only while holding a wand. (Default)
    - `NEVER` : Never show the tooltip.

![Jade](https://github.com/SathLabs/Crystalix/blob/26.1/.github/assets/jade.png?raw=true)

## Framed Blocks

- Crystalix Glass can be placed into framed blocks; however, due to the way framed blocks function, unfortunately not all properties will function properly.
- Texture changes will function as normal, including `color`, `material`, `tinted`, `shadeless` and the ability to switch between `light` and no effect.
- All other properties cannot be supported correctly
