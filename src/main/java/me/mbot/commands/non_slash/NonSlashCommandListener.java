package me.mbot.commands.non_slash;

import me.mbot.commands.non_slash.api.NonSlashCommandHandler;
import me.mbot.commands.non_slash.commands.NukeButtonCommand;
import me.mbot.commands.non_slash.commands.HowardTheAlienCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Main class where NonSlashCommands are registered.
 */
public class NonSlashCommandListener extends ListenerAdapter {

    private final Map<String, NonSlashCommandHandler> nonSlashCommands = new HashMap<>();

    public NonSlashCommandListener() {
        register(new NukeButtonCommand());
        register(new HowardTheAlienCommand());
    }

    private void register(NonSlashCommandHandler handler) {
        nonSlashCommands.put(handler.getName(), handler);
    }

    public Map<String, NonSlashCommandHandler> getCommands() {
        return nonSlashCommands;
    }
    
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        User author = event.getAuthor();
        Member member = event.getMember();
        String message = event.getMessage().getContentDisplay();

        if(author.isBot() || member == null) return;

        if (!message.startsWith("&")) return;

        /*
        Example: "&somecommand test test2" => substring begin at index 1 so it excludes "&" and split words
        with a whitespace this will return ["somecommand", "test", "test2"] then extract the first element at index 0
        being the nonSlashCommand / stuff after the ampersand "&"
         */
        String commandName = message.substring(1).split(" ")[0];
        NonSlashCommandHandler handler = nonSlashCommands.get(commandName);

        // if non slash command is not found aka handler, do nothing.
        if(handler == null) return;

        handler.handle(event);
    }
}
