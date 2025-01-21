# WynnBuild
_A mod for generating [WynnBuilder](https://wynnbuilder.github.io/builder/) urls in-game._

## Usage
Opening your character info gui or ability tree will gather your info for your build. If you make changes to your build, make sure to manually fetch the info again with the **Read** button on the right bottom side of your screen.

Pressing the **BUILD** button in the bottom right side of your screen will generate and output your build url to the chat that you can copy and share.

You can customize how precise your stats of your build will be with the **Precision** option inside the config menu (**/build config**):
### Precision options
- **OFF** - The item is passed to the builder as a default item (meaning item with average rols)
- **NEVER** - The item is always passed as a default item unless it's a crafted or custom item (rolls always apply)
- **ON** - The item is passed as a custom item if the item is saved (not the currently used equipment) (the stats are precise)
- **FORCE** - The item is always passed as a custom item (even forces the currently used equipments in EMPTY SAFE mode, most precision)

![Precision Option](https://cdn.modrinth.com/data/1RCjAAcr/images/b581fa86bcb87455948acbf479cd46f9139cd3be.png)

## Powders
As it isn't possible to tell what tier is a powder on an item, you can set a default value for it in the config screen. For specifying each powder level, you'll need to do that in the actual builder.

## Config
You can configure the mod with **/build config** command. You can disable all buttons or access the encoded value of your current atree.
Ability tree presets are also configurable.

## Custom / Crafted Items
You can now use crafted (or custom if you're using the builder) items in your builds. The mod detects crafted items automatically and use them in the build. Custom item are only available in the [**Builder**](#Builder)

## Ability Tree Presets
You can create or delete ability tree presets in the new menu opened by **/saveatrees** or **/build saveatrees**.
Your current class will be assigned for the saved builds.
You can also access the config in the `.minecraft/config/wynnbuild.json` file.

After saving your first preset, in the abilty tree menu, the names of your classes' available saves will appear. Clicking the **Load {savename}** button will set your ability tree to the saved atree. (Make sure to reset your atree before doing this). This may take up to half a minute or more, but don't close the inventory during the process.

You can disable the preset loader buttons in the main config screen (**/build config**).

## Saving items
You can save any item from your inventory or with the use of the [WynnCustom](https://hppeng-wynn.github.io/custom/) hash code (COPY HASH). You can use these items in the [**Builder**](#Builder). You can open this menu with ( **/build saveditems** ).

You can also save your item in your hand with the **/build saveitem** command. **Crafted accessories or renamed items' item type is not recognised.**

_This feature is in beta, filters and QoL changes are currently in developement._
![Saved item menu](https://cdn.modrinth.com/data/cached_images/4b6dcc20e856104e46acbf62c9951f9f45e50235.png)

## Builder
You can configure your builds even more with the use of the in-game builder **(/build builder)**. You can provide your currently equipped equipment or any saved item. You can calibrate how precise your stats will be by setting the **[Precision](#Precision-options)** option As default, this is set to always use the default item with average rolls. This builder ignores requirements for more customizability.
![Builder menu](https://cdn.modrinth.com/data/cached_images/9b5ae01f0888f1ea0836471d061de6139f5fdba0.png)

_Note: the mod is still under development, more features, easier usage and QoL changes are yet to come._

## (BETA) Gallery
The Item Gallery ( **/build beta gallery** ) shows all wynncraft items and saved versions of them.

A filter feature is implemented with the wynnbuild id codes (will change, not user friendly), and the character "$" at the start is used to show only the saved items

#### _All the data (items, tomes, etc...) are from [hppeng-wynn](https://github.com/hppeng-wynn/hppeng-wynn.github.io) repo_