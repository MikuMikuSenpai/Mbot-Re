package me.mbot.commands.slash.commands;

import me.mbot.commands.slash.api.SlashCommandHandler;
import me.mbot.misc.dao.ReminderDAO;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

public class RemindCommand implements SlashCommandHandler {
    @Override
    public CommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.STRING, "time", "When to remind (example: 10m, 1h, 2d)", true)
                .addOption(OptionType.STRING, "note", "Reminder note", true);
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String timeStr = Objects.requireNonNull(event.getOption("time")).getAsString();
        String note = Objects.requireNonNull(event.getOption("note")).getAsString();
        long delayMillis = parseDurationToMillis(timeStr);

        if (delayMillis <= 0) {
            event.reply("Invalid time format. Use like `10m`, `1h`, `2d`.").setEphemeral(true).queue();
            return;
        }

        Timestamp remindAt = Timestamp.from(Instant.now().plusMillis(delayMillis));
        long userId = event.getUser().getIdLong();
        String channelId = event.getChannel().getId();

        ReminderDAO.insertReminder(userId, remindAt, note, channelId);
        event.reply("Reminder set for <t:" + (remindAt.getTime() / 1000) + ":R>: " + note).queue();
    }

    private long parseDurationToMillis(String input) {
        try {
            long multiplier = switch (input.toLowerCase().replaceAll("[0-9]", "")) {
                case "s" -> 1000L;
                case "m" -> 60 * 1000L;
                case "h" -> 60 * 60 * 1000L;
                case "d" -> 24 * 60 * 60 * 1000L;
                default -> -1;
            };
            long value = Long.parseLong(input.replaceAll("[^0-9]", ""));
            return multiplier > 0 ? value * multiplier : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public boolean isModCommand() {
        return false;
    }

    @Override
    public String getName() {
        return "remind";
    }

    @Override
    public String getDescription() {
        return "Set a reminder with a note (bot will ping u)";
    }
}
