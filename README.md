# Mbot - Main Document
Build with [JDA](https://github.com/discord-jda/JDA).

## Reference to other docs
- CONTRIBUTE.md: instructions about contributing.
- COMMANDS.md (TODO: make this): instructions how to add commands.
- MISC.md (TODO: make this): instructions how to add miscellaneous features.

## How to run Mbot:
1. Copy .example.env and create .env with your credentials.
2. Use [docker](https://www.docker.com/) to start the bot:
    ```
    docker compose up
    ```
3. To stop the bot in project directory do:
    ```
    docker compose stop
    ```

## Features
- Message highlight: messages with a certain amount of stars are displayed in a specific channel.
- A ton of moderation tools: /ban, /kick, /mute, /kill-switch (not accessible to all mods), message listeners for edited and deleted messages and log them in a specific (restricted) channel.
- Non-slash commands that all start with "&" and are mainly for fun. The main commands are the slash "/" commands.

## Project Structure
- **commands**: here goes all the commands that Mbot supports (both non-slash and slash commands for their documentation read their respective README's (TODO: add these).)
- **configuration**: here goes all the configuration files that are used globally.
- **main**: stores the psvm of this project should absolutely not be touched. Mbot uses the Singleton design pattern.
- **misc**: everything goes here that does not fit in the others (for example loggers: edit, delete, highlight.)

## Authors
- MikuMikuSenpai (owner)
- acexcy (contributer)