package me.mbot.commands.slash;

import me.mbot.commands.non_slash.NonSlashCommandListener;
import me.mbot.commands.slash.api.SlashCommandHandler;
import me.mbot.commands.slash.commands.*;
import me.mbot.commands.slash.commands.admin.BanCommand;
import me.mbot.commands.slash.commands.admin.KickCommand;
import me.mbot.commands.slash.commands.admin.KillSwitchCommand;
import me.mbot.commands.slash.commands.admin.MuteCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Main class where SlashCommands are registered.
 */
public class SlashCommandListener extends ListenerAdapter {

    private final Map<String, SlashCommandHandler> slashCommands = new HashMap<>();
    private final NonSlashCommandListener nonSlashCommandListener = new NonSlashCommandListener();

    public SlashCommandListener() {
        // Admin commands
        register(new KickCommand());
        register(new BanCommand());
        register(new MuteCommand());
        register(new KillSwitchCommand());

        register(new EchoCommand());
        register(new XpStatsCommand());
        register(new TodoCommand());
        register(new TodoListCommand());
        register(new TodoRemoveCommand());

        // keep these two at the bottom at all times (in order for pagination to work)
        register(new HelpOPCommand(slashCommands));
        register(new HelpCommand(slashCommands));
    }

    public Map<String, SlashCommandHandler> getCommands() {
        return slashCommands;
    }

    private void register(SlashCommandHandler handler) {
        slashCommands.put(handler.getName(), handler);
    }

    /**
     * Checks whether member and handler / slash command exist if not do nothing.
     * If checks pass = handler.handle() perform command
     * @param event The slash command being used.
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommandHandler handler = slashCommands.get(event.getName());

        Member member = event.getMember();

        if (member == null) {return;}

        if (handler == null) {return;}

        handler.handle(event);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String componentId = event.getComponentId();

        // help command
        if (componentId.startsWith("help_")) {
            SlashCommandHandler handler = slashCommands.get("help");

            if (handler instanceof HelpCommand helpCommand) {
                helpCommand.handleButtonInteraction(event);
            }
        }
        // helpOP command
        if (componentId.startsWith("helpop_")) {
            SlashCommandHandler handler = slashCommands.get("help-op");
            if (handler instanceof HelpOPCommand helpOpCommand) {
                helpOpCommand.handleButtonInteraction(event);
            }
        }
    }

}
