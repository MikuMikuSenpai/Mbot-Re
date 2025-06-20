package me.mbot.commands.slash.commands.admin;

import me.mbot.commands.slash.api.SlashCommandHandler;
import me.mbot.configuration.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.time.Instant;
import java.util.List;


public class KillSwitchCommand implements SlashCommandHandler {

    List<String> OWNER_USER_IDS = Constants.getOwnerUserIds();

    @Override
    public CommandData getCommandData() {
        return SlashCommandHandler.super.getCommandData();
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String authorId = event.getUser().getId();

        if (!OWNER_USER_IDS.contains(authorId)) {
            event.reply("You are not authorized to use this command.").setEphemeral(true).queue();
            return;
        }

        for (String userId : OWNER_USER_IDS) {
            event.getJDA().retrieveUserById(userId).queue(user -> {
                user.openPrivateChannel().queue(channel -> {
                    var timeNow = Instant.now();
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Kill switch used");
                    eb.addField("Done by:",  "<@" + user.getId() + ">", false);
                    eb.setDescription("The bot is shutting down");
                    eb.setTimestamp(timeNow);
                    eb.setImage("https://media.discordapp.net/attachments/" +
                            "1082718875038273566/1365304158336585869/" +
                            "umkgagjyt5831.webp?ex=680cd215&is=680b8095&hm=" +
                            "ab25255ad2a98c785c69ada805019648477f7f5a94d034e24078c54eb219cd58&=&format=webp&width=564&height=544");
                    eb.setColor(0xff0f0f);
                    channel.sendMessageEmbeds(eb.build()).queue();
                });
            });
        }

        event.reply("Shutting down the bot. WARNING: mysql container is probably still running check if so.").setEphemeral(true).queue();

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}

            event.getJDA().shutdown();
        }).start();
    }

    @Override
    public boolean isModCommand() {
        return true;
    }

    @Override
    public String getName() {
        return "kill-switch";
    }

    @Override
    public String getDescription() {
        return "Immediately shuts down the bot. Restricted use.";
    }
}
