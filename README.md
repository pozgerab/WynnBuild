# WynnBuild
_A mod for generating [WynnBuilder](https://wynnbuilder.github.io/builder/) urls in-game._

## Usage
Opening your character info gui or ability tree will gather your info for your build. If you make changes to your ability tree, make sure to manually fetch the info again with the **Read** button on the right bottom side of your screen.

Pressing the **BUILD** button in the bottom right side of your screen (or the **/build** command) will generate and output your build url to the chat that you can copy and share.

You can customize how precise your stats of your build will be with the **Precision** option inside the config menu (**/build config**):
### Precision options
- **OFF** - The item is passed to the builder as a default item (meaning item with average rols)
- **ON** - The item is passed as a custom item (uses exact rolls, most precision)

> Note that using precision mode will count the powders' stats into the base stats as it is hard to determine how much stats the powders actually give

![Precision Option](https://cdn.modrinth.com/data/cached_images/978aa614f487f251566ff5479e8c6132f2e392cc.png)

## Powders
As it isn't possible to tell what tier is a powder on an item, you can set a default value for it in the config screen. For specifying each powder level, you'll need to do that in the actual builder.

Using precision mode will not include powders in the powder section, instead in base stats

## Config
You can configure the mod with **/build config** command. You can disable all buttons or access the encoded value of your current atree.
Ability tree presets are also configurable (**/build saveatree**).

![Config Screen](https://cdn.modrinth.com/data/cached_images/7a539d812c3d0e00a1c7d3fe0f77a2d5c3f2e9cb_0.webp)


## Custom / Crafted Items
You can now use crafted items in your builds. The mod detects crafted items automatically and use them in the build.

## Ability Tree Presets
You can create or delete ability tree presets in the new menu opened by **/saveatrees** or **/build saveatrees**.
Your current class will be assigned for the saved builds.
You can also access the config in the `.minecraft/config/wynnbuild.json` file.

After saving your first preset, in the abilty tree menu, the names of your classes' available saves will appear. Clicking the **Load {savename}** button will set your ability tree to the saved atree. (Make sure to reset your atree before doing this). This may take up to half a minute or more, but don't close the inventory during the process.

You can disable the preset loader buttons in the main config screen (**/build config**).

#### _All the data (items, tomes, etc...) are from [hppeng-wynn](https://github.com/hppeng-wynn/hppeng-wynn.github.io) repo_