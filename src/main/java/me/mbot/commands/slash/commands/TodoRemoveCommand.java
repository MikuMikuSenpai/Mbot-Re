package me.mbot.commands.slash.commands;

import me.mbot.commands.slash.api.SlashCommandHandler;
import me.mbot.misc.dao.TodoDAO;
import me.mbot.misc.dao.TodoEntry;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.List;
import java.util.Objects;

public class TodoRemoveCommand implements SlashCommandHandler {
    @Override
    public CommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.INTEGER, "index", "The number of the todo to remove", true);
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        int index = Objects.requireNonNull(event.getOption("index")).getAsInt();
        long userId = event.getUser().getIdLong();
        List<TodoEntry> todos = TodoDAO.getTodos(userId);

        if (index < 1 || index > todos.size()) {
            event.reply("Invalid index. Use `/todo-list` to see your current todos. The index must be between 1-5.").setEphemeral(true).queue();
            return;
        }

        TodoEntry toRemove = todos.get(index - 1);
        TodoDAO.removeTodoById(toRemove.id());

        event.reply("Removed todo: **" + toRemove.todo() + "**").setEphemeral(true).queue();
    }

    @Override
    public boolean isModCommand() {
        return false;
    }

    @Override
    public String getName() {
        return "todo-remove";
    }

    @Override
    public String getDescription() {
        return "Remove a TODO from your todos.";
    }
}
