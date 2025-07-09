package me.mbot.configuration;

import me.mbot.commands.slash.SlashCommandListener;
import me.mbot.commands.slash.api.SlashCommandHandler;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This class automatically loads SlashCommands that the bot will have such as "EchoCommand.java"
 * These SlashCommands come from SlashCommandListener.java which has a variable slashCommands that holds the commands
 * And via a public method this class uses "getCommands" to retrieve all SlashCommands
 */
public class BotMainListener extends ListenerAdapter {
    private final SlashCommandListener slashCommandListener;

    // Constructor for initializing slashCommandListener (final => must not change during runtime)
    public BotMainListener(SlashCommandListener slashCommandListener) {
        this.slashCommandListener = slashCommandListener;
    }

    /**
     * This method has a list "commandData" where slash commands will be put into using a for loop
     * after all the slash commands are put into the list they will be added to the guild using
     * updateCommands().addCommands(commandData) so we basically give a list of slash commands to the guild
     * and tell it to update.
     *
     * @param event Variable when guild/ discord server is ready loading/ setting up.
     */
    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();

        for (SlashCommandHandler handler : slashCommandListener.getCommands().values()) {
            commandData.add(handler.getCommandData());
        }

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}
