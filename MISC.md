# Mbot - Miscellaneous Features Document

> [!NOTE]
> Use the database if you need to store something that takes a lot of space (for example: messages).
> The use of DAOs (`me.mbot.misc.dao`) is advised.

## How to make misc features:
1. Make a package in `me.mbot.misc` or use a pre-existing package if possible.
2. Make the feature in that package.
3. Register the listener in `me.mbot.configuration.BotConfiguration.java` in `.addEventListeners()`
