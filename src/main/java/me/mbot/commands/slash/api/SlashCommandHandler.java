package me.mbot.commands.slash.api;

import me.mbot.configuration.api.CommandMetaData;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * API for SlashCommands comes with getName() and getDescription() (these two from its parent CommandMetaData (extends))
 * getCommandData() and handle().
 */
public interface SlashCommandHandler extends CommandMetaData {

    /**
     * Builds the Slash CommandData for registration with Discord.
     *
     * @return CommandData representing this command's structure. With the command's name and description
     * by default. If command needs more fields such as OPTIONTYPE.STRING, you need to add it manually in the
     * Command's class.
     */
    default CommandData getCommandData(){
        return Commands.slash(getName(), getDescription());
    }
    
    void handle(SlashCommandInteractionEvent event);

    boolean isModCommand();
}
