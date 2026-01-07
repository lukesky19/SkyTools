# SkyTools
## Description
* SkyTools adds unique tools to the game.
* Features a build tool and mob capture tool currently.

## Required Dependencies
* SkyLib

## Soft Dependencies
* BentoBox
* ItemsAdder
* RoseStacker
* SkyHoppers
* SkyShop
* WorldGuard

## Commands
- /skytools - Base command.
  - Alias:
    - /tool
    - /tools
- /skytools reload - Reloads the plugin.
- /skytools help - View the help message.
- /skytools build_tool <player> - Give a build tool to the player.
- /skytools mob_capture_tool <player> \[uses] - Give a mob capture tool the player with an optional number of uses.

## Permisisons
- `skytools.commands.skytools` - The permission to access the `/skytools` base command.
- `skytools.commands.skytools.reload` The permission to use `/skytools reload`.
- `skytools.commands.skytools.help` The permission to use `/skytools help`.
- `skytools.commands.skytools.build_tool` - The permission to use `/skytools build_tool [player]`.
- `skytools.commands.skytools.mob_capture_tool` - The permission to use `/skytools mob_capture_tool [player] [uses]`.

## Issues, Bugs, or Suggestions
* Please create a new [Github Issue](https://github.com/lukesky19/SkyTools/issues) with your issue, bug, or suggestion.
* If an issue or bug, please post any relevant logs containing errors related to SkyTools and your configuration files.
* I will attempt to solve any issues or implement features to the best of my ability.

## FAQ
Q: What versions does this plugin support?

A: 1.21.4, 1.21.5, 1.21.6, 1.21.7, 1.21.8, 1.21.9, 1.21.10, and 1.21.11.

Q: Are there any plans to support any other versions?

A: I will always do my best to support the latest versions of the game. I will sometimes support other versions until I no longer use them.

Q: Does this work on Spigot and Paper?

A: Only Paper is supported. There are no plans to support any other server software (i.e., Spigot, Folia).

## Building
* Go to [SkyLib](https://github.com/lukesky19/SkyLib) and follow the "For Developers" instructions.
* Then run:
  ```./gradlew build```

## Why AGPL3?
I wanted a license that will keep my code open source. I believe in open source software and in-case this project goes unmaintained by me, I want it to live on through the work of others. And I want that work to remain open source to prevent a time when a fork can never be continued (i.e., closed-sourced and abandoned).
