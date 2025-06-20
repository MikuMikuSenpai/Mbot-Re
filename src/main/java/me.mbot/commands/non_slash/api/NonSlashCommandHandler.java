package me.mbot.commands.non_slash.api;

import me.mbot.configuration.api.CommandMetaData;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * API for NonSlashCommands comes with getName() and getDescription() (these two from its parent CommandMetaData (extends))
 * and handle().
 */
public interface NonSlashCommandHandler extends CommandMetaData {
    /**
     * Main NonSlashCommand logic goes in here
     * @param event The object where you can use methods on such as .reply()
     */
    void handle(MessageReceivedEvent event);

}
