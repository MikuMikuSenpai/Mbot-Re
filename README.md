# Mbot - Main Document
Build with [JDA](https://github.com/discord-jda/JDA).

## Documentation
- CONTRIBUTE.md: instructions about contributing.
- COMMANDS.md: instructions how to add commands.
- MISC.md: instructions how to add miscellaneous features.

## How to run Mbot:

> [!TIP]
> If you are not familiar with docker you can use "Play with Docker" which is an online and interactive way to learn the basics of docker. (You need to create a docker account.)
> https://training.play-with-docker.com/

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
- A ton of moderation tools: /ban, /kick, /mute, /kill-switch (can only be used by certain users (identified using their discord ID)), message listeners for edited and deleted messages and log them in a specific (restricted) channel.
- Non-slash commands start with "&". The main commands are the slash "/" commands.

## Project Structure
- **commands**: commands that Mbot supports.
- **configuration**: configuration files that are used globally.
- **main**: stores the psvm of this project should not be touched. Mbot is made using the Singleton design pattern.
- **misc**: everything that does not fit in the other categories (for example loggers: edit, delete, highlight.)

## Authors
- MikuMikuSenpai (owner)
- acexcy (contributer)
