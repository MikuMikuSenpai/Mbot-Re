package me.mbot.commands.slash.commands;

import me.mbot.commands.slash.api.SlashCommandHandler;
import me.mbot.misc.dao.XpDAO;
import me.mbot.misc.xp_system.XpHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class XpStatsCommand implements SlashCommandHandler {
    @Override
    public CommandData getCommandData() {
        return SlashCommandHandler.super.getCommandData();
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        long userId = event.getUser().getIdLong();
        int xp = XpDAO.getXP(userId);
        int level = XpHelper.getLevel(xp);

        int currentLevelXP = XpHelper.getXPForLevel(level);
        int nextLevelXP = XpHelper.getXPForLevel(level + 1);

        if (nextLevelXP == -1) {
            // -1 means max level reached
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("XP Stats");
            eb.addField("Level", String.valueOf(level), true);
            eb.addField("XP", String.valueOf(xp), true);
            eb.addField("Progress to Next Level", "You've reached the max level", false);
            eb.setFooter(event.getUser().getName(), event.getUser().getAvatarUrl());
            event.replyEmbeds(eb.build()).queue();
            return;
        }

        int gainedThisLevel = xp - currentLevelXP;
        int requiredThisLevel = nextLevelXP - currentLevelXP;
        double progressPercent = (double) gainedThisLevel / requiredThisLevel;
        int progressBarLength = 10;
        int filledBars = (int) (progressBarLength * progressPercent);

        StringBuilder progressBar = new StringBuilder();
        for (int i = 0; i < progressBarLength; i++) {
            progressBar.append(i < filledBars ? "▆" : "▁");
        }

        String percentage = String.format("%.2f", progressPercent * 100) + "%";

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("XP Stats");
        eb.addField("Level", String.valueOf(level), true);
        eb.addField("XP", String.valueOf(xp), true);
        eb.addField("Progress to Next Level", progressBar + " " + percentage, false);
        eb.setFooter(event.getUser().getName(), event.getUser().getAvatarUrl());

        event.replyEmbeds(eb.build()).queue();
    }

    @Override
    public boolean isModCommand() {
        return false;
    }

    @Override
    public String getName() {
        return "xp-info";
    }

    @Override
    public String getDescription() {
        return "See your current XP, current level and how much XP you need for next level.";
    }
}
