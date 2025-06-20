package me.mbot.commands.slash.commands;

import me.mbot.commands.slash.api.SlashCommandHandler;
import me.mbot.misc.dao.TodoDAO;
import me.mbot.misc.dao.TodoEntry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;

public class TodoListCommand implements SlashCommandHandler {
    @Override
    public CommandData getCommandData() {
        return SlashCommandHandler.super.getCommandData();
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        long userId = event.getUser().getIdLong();
        List<TodoEntry> todos = TodoDAO.getTodos(userId);

        if (todos.isEmpty()) {
            event.reply("You have no todos. Do `/todo` to add a TODO task.").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Your TODO list")
                .setColor(0xCD7F32); // bronze color

        for (int i = 0; i < todos.size(); i++) {
            TodoEntry todo = todos.get(i);
            eb.addField(
                    (i + 1) + ". " + todo.todo(),
                    "",
                    false
            );
        }

        event.replyEmbeds(eb.build()).queue();
    }

    @Override
    public boolean isModCommand() {
        return false;
    }

    @Override
    public String getName() {
        return "todo-list";
    }

    @Override
    public String getDescription() {
        return "List all your TODO items.";
    }
}
