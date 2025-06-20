package me.mbot.commands.slash.commands;

import me.mbot.commands.slash.api.SlashCommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HelpOPCommand implements SlashCommandHandler {
    private final List<Map.Entry<String, SlashCommandHandler>> modCommands;

    public HelpOPCommand(Map<String, SlashCommandHandler> slashCommands) {
        this.modCommands = slashCommands.entrySet().stream()
                .filter(entry -> entry.getValue().isModCommand())
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(getName(), getDescription());
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        int page = 0;
        int perPage = 2;
        int totalPages = (int) Math.ceil((double) modCommands.size() / perPage);
        String userId = event.getUser().getId();

        event.replyEmbeds(buildPageEmbed(page, perPage, totalPages).build())
                .addActionRow(
                        Button.primary("helpop_prev_" + page + "_" + userId, "Previous").withDisabled(true),
                        Button.primary("helpop_next_" + page + "_" + userId, "Next").withDisabled(page + 1 >= totalPages)
                )
                .queue();
    }

    public void handleButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().startsWith("helpop_")) return;

        String[] parts = event.getComponentId().split("_");
        if (parts.length < 4) return;

        String direction = parts[1];
        int currentPage = Integer.parseInt(parts[2]);
        String originalUserId = parts[3];

        if (!event.getUser().getId().equals(originalUserId)) {
            event.reply("Only the user who ran `/help-op` can use these buttons.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        int perPage = 2;
        int totalPages = (int) Math.ceil((double) modCommands.size() / perPage);
        int newPage = direction.equals("next") ? currentPage + 1 : currentPage - 1;

        event.editMessageEmbeds(buildPageEmbed(newPage, perPage, totalPages).build())
                .setActionRow(
                        Button.primary("helpop_prev_" + newPage + "_" + originalUserId, "Previous").withDisabled(newPage == 0),
                        Button.primary("helpop_next_" + newPage + "_" + originalUserId, "Next").withDisabled(newPage + 1 >= totalPages)
                )
                .queue();
    }

    private EmbedBuilder buildPageEmbed(int page, int perPage, int totalPages) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Help menu - OP commands (page " + (page + 1) + "/" + totalPages + ")");
        eb.setColor(0x2596be);

        int start = page * perPage;
        int end = Math.min(start + perPage, modCommands.size());

        for (int i = start; i < end; i++) {
            var entry = modCommands.get(i);
            eb.addField("/" + entry.getKey(), entry.getValue().getDescription(), false);
        }

        return eb;
    }

    @Override
    public boolean isModCommand() {
        return false;
    }

    @Override
    public String getName() {
        return "help-op";
    }

    @Override
    public String getDescription() {
        return "Prints a list of all the moderator commands.";
    }
}
