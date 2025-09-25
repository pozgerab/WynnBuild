# WynnBuild
_A simple mod that lets you generate [WynnBuilder](https://wynnbuilder.github.io/builder/) links directly in-game._

## How to Use
You can quickly create a WynnBuilder link for your build in two ways:
- Press the **BUILD** button at the bottom-right of your character info screen (you can turn this button off in settings if you want).
- Or, more reliably, just run the **/build** command.

![Generated Build](https://cdn.modrinth.com/data/1RCjAAcr/images/0fbf7df3d536bcb112874406d64fb4b8a31afbe3.png)

### Precision Settings
You can control how detailed your build stats are through the Precision option in **/build config**:
- **OFF** - Items are treated as average (default rolls)
- **ON** - Items use your exact rolls for maximum accuracy.

### Tomes & Aspects
Tomes and aspects are supported. You can choose whether to include them in your builds via the config menu.

## Crafted Items
Crafted items are automatically detected and included in your build. However, since WynnBuilder doesn’t support crafted items with custom rolls, they’ll show up as a **Normal rarity custom item**.

## Standalone Items
To share just one item with its exact rolls use the /build buildcustomitem command to generate a [WynnCustom](https://wynnbuilder.github.io/custom/) link.

![Generated Custom Item](https://cdn.modrinth.com/data/1RCjAAcr/images/1d2a338548f582b52d5cb35edae1468430de1544.png)

## Config Options
Open the config with /build config. From here, you can:
- Enable/disable buttons
- Access your encoded ability tree value
- Toggle tomes and aspects
- Set default powder level

![Config Screen](https://cdn.modrinth.com/data/1RCjAAcr/images/1656e55ed72c6ea2104f88da2fc69df903c2d42d.png)

#### _All item, tome, and aspect encoding logic comes from the [wynnbuilder](https://github.com/wynnbuilder/wynnbuilder.github.io) repo_
#### _In-game info gathering is done by [Wynntils](https://github.com/Wynntils/Wynntils)_