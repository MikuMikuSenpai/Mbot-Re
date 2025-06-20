package me.mbot.misc.message_listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageHighlight extends ListenerAdapter {

    private String CHANNEL_LOG_ID = System.getenv("CHANNEL_LOG_ID");

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (event.getUser() == null || event.getUser().isBot()) return;
        if (!event.getReaction().getEmoji().getName().equals("⭐")) return;

        event.retrieveMessage().queue(message -> {
            long starCount = message.getReactions().stream()
                    .filter(reaction -> reaction.getEmoji().getName().equals("⭐"))
                    .findFirst()
                    .map(MessageReaction::getCount)
                    .orElse(0);

            if (starCount >= 1) {
                TextChannel highlightChannel = event.getGuild().getTextChannelById(CHANNEL_LOG_ID);
                if (highlightChannel != null) {
                    highlightChannel.sendMessage(String.format(
                            "⭐ **%d** in <#%s>",
                            starCount,
                            event.getChannel().getId()
                    )).queue();
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle(message.getAuthor().getName());

                    if (!message.getContentDisplay().isEmpty() || message.getAttachments().isEmpty()) {
                        eb.addField("Message content:", message.getContentDisplay(), false);
                    }

                    if (!message.getAttachments().isEmpty()) {
                        String imageUrl = message.getAttachments().getFirst().getUrl();
                        eb.setImage(imageUrl);
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                    String formattedDate = sdf.format(new Date(message.getTimeCreated().toEpochSecond() * 1000));

                    eb.setFooter(message.getAuthor().getId() + " ● " + formattedDate);
                    eb.setColor(0xFFF999);

                    highlightChannel.sendMessageEmbeds(eb.build()).queue();
                }
            }
        });
    }
}
