package me.mbot.misc.message_listeners;

import me.mbot.misc.dao.MessageDeleteDAO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;


import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MessageDeleteListener extends ListenerAdapter {

    private final String CHANNEL_LOG_ID = System.getenv("CHANNEL_LOG_ID");

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        Guild guild = event.getGuild();
        TextChannel logChannel = guild.getTextChannelById(CHANNEL_LOG_ID);
        if (logChannel == null) return;

        String messageId = event.getMessageId();
        String content = MessageDeleteDAO.getDeletedContent(messageId);
        String authorId = MessageDeleteDAO.getAuthorId(messageId);
        String channelId = MessageDeleteDAO.getChannelId(messageId);
        List<String> imageUrls = MessageDeleteDAO.getImageUrls(messageId);
        List<String> videoUrls = MessageDeleteDAO.getVideoUrls(messageId);

        if (content == null || authorId == null || channelId == null) return;

        String authorMention = "<@" + authorId + ">";
        String channelMention = "<#" + channelId + ">";

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Message was deleted")
                .setColor(0xFF0800)
                .addField("Author", authorMention, false)
                .addField("Channel", channelMention, false);

        if (content.length() > 1024) {
            try {
                File file = File.createTempFile("deleted-message-", ".txt");
                Files.writeString(file.toPath(), content);
                eb.addField("Message", "Attached as file (over 1024 characters)", false);

                logChannel.sendMessageEmbeds(eb.build()).queue();
                logChannel.sendMessage("").addFiles(FileUpload.fromData(file)).queue();
                file.deleteOnExit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (!content.isEmpty()) {
                eb.addField("Message", content, false);
            }
            logChannel.sendMessageEmbeds(eb.build()).queue();
        }

        for (String url : imageUrls) {
            logChannel.sendMessage(url).queue();
        }
        for (String url : videoUrls) {
            logChannel.sendMessage(url).queue();
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String messageId = event.getMessageId();
        String content = event.getMessage().getContentRaw();
        String authorId = event.getAuthor().getId();
        String channelId = event.getChannel().getId();

        List<String> imageUrls = new ArrayList<>();
        List<String> videoUrls = new ArrayList<>();

        for (var attachment : event.getMessage().getAttachments()) {
            if (attachment.isImage()) {
                imageUrls.add(attachment.getUrl());
            } else if (attachment.isVideo()) {
                videoUrls.add(attachment.getUrl());
            }
        }

        MessageDeleteDAO.insertDeletedMessage(messageId, Long.parseLong(authorId), channelId, content, imageUrls, videoUrls);
    }
}
