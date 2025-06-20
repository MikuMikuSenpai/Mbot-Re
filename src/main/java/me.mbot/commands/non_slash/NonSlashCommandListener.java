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

    /**
     * When a message is received on the server this method will be triggered. It will check if the author is a bot,
     * if so, the message is ignored. The message is also ignored if the member does not exist somehow (null).
     * Messages are also ignored if they don't start with ampersand ("&").
     * @param event The message that is received by the bot.
     */
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        User author = event.getAuthor();
        Member member = event.getMember();
        String message = event.getMessage().getContentDisplay();

        if(author.isBot() || member == null) return;

        if (!message.startsWith("&")) return;

        /*
        For example &somecommand some words => after substring (new string) "somecommand some words"
        split will return an array of words that were split by the space so ["somecommand", "some", "words"]
        and if we take the element of element 0 (remember array's start counting at 0) we can extract the command name.
        Then we check if the command exists in the variable stored in this class if so then use the
        handle method of the command so the logic.
         */
        String commandName = message.substring(1).split(" ")[0];
        NonSlashCommandHandler handler = nonSlashCommands.get(commandName);

        // if non slash command is not found aka handler, do nothing.
        if(handler == null) return;

        // perform logic of command for example reply with "hi"
        handler.handle(event);
    }
}
