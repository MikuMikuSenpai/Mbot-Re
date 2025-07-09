# Mbot - Commands Document

## Introduction
Guide to make slash or non-slash commands for Mbot. We decided to use a design that is easily extensible and easy to understand.

> [!NOTE]
> Aim for simplicity in variable and command naming. For example "EchoCommand.java" is a simple name that lets the reader know what it does.
> Try to not use long names if they can be written shorter.

> [!IMPORTANT]
> If the command didn't show up in your bot make sure to do `docker compose up --build` (--build is important => rebuild the bot image).

## Non-Slash commands ("&")

1. Create a class in `me.mbot.commands.non_slash.commands`. Naming convention is {NameOfYourCommand}Command for example: TestCommand.
2. Add this `implements NonSlashCommandHandler`. And implement the required methods.
3. `handle(MessageReceivedEvent event)`: your command  logic goes here. `getName()`: the name that will be used to access your command. 
For example if you set it to "test" you will be able to use the command as `&test`. `getDescription()`: the description of the command (might get deprecated).
4. Register your command in `me.mbot.commands.non_slash.NonSlashCommandListener` in its constructor.
5. Test your command(s) (restart the bot).

## Slash commands ("/")

1. Create a class in `me.mbot.commands.slash.commands`. Naming convention is {NameOfYourCommand}Command for example: TestCommand.
2. Add this `implements SlashCommandHandler`. And implement the required methods.
3. `handle(SlashCommandInteractionEvent event)`: your command  logic goes here. `getName()`: the name that will be used to access your command. 
For example if you set it to "test" you will be able to use the command as `&test`. `getDescription()`: the description of the command (might get deprecated).
4. Register your command in `me.mbot.commands.slash.SlashCommandListener` in its constructor.
5. Test your command(s) (restart the bot).
