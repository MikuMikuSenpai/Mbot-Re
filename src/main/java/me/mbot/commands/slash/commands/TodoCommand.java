package me.mbot.commands.slash.commands;

import me.mbot.commands.slash.api.SlashCommandHandler;
import me.mbot.misc.dao.TodoDAO;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Objects;

public class TodoCommand implements SlashCommandHandler {
    @Override
    public CommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.STRING, "todo", "Your todo item", true);
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String todo = Objects.requireNonNull(event.getOption("todo")).getAsString();
        long userId = event.getUser().getIdLong();

        if (TodoDAO.getTodoCount(userId) >= 5) {
            event.reply("You already have 5 todos. Please remove some first.").setEphemeral(true).queue();
            return;
        }

        int todoLength = Objects.requireNonNull(event.getOption("todo")).getAsString().length();

        if (todoLength >= 129) {
            event.reply("Your todo is too long. The maximum length is 128 chars.").setEphemeral(true).queue();
            return;
        }

        TodoDAO.addTodo(userId, todo);

        event.reply("Todo added: **" + todo + "**").setEphemeral(true).queue();
    }

    @Override
    public boolean isModCommand() {
        return false;
    }

    @Override
    public String getName() {
        return "todo";
    }

    @Override
    public String getDescription() {
        return "Add something to your list of to-do's";
    }
}
