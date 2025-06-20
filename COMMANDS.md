# Mbot - Commands Document

## Introduction
This file will serve as a quick guide to start making slash or non-slash commands for Mbot. We decided to use a design that is easily extensible and easy to understand.

## Steps to create a non-slash command (starts with &)
1. Create a class in `me.mbot.commands.non_slash.commands`. Naming convention is {NameOfYourCommand}Command for example: TestCommand. Good name = reader knows what command will do by reading it's name.
2. Add this `implements NonSlashCommandHandler`. (Read more about "NonSlashCommandHandler.java", documentation is available). And implement the required methods.
3. `handle(MessageReceivedEvent event)`: your command  logic goes here. `getName()`: the name that will be used to access your command. For example if you set it to "test" you will be able to use the command as `&test`. `getDescription()`: the description of the command (might get deprecated).
4. Register your command in `me.mbot.commands.non_slash.NonSlashCommandListener` in its constructor.
5. That's all, now test your command. If the command didn't show up in your bot make sure to do `docker compose up --build` (--build is very important => rebuild the bot image).

## WIP: add slash command instructions