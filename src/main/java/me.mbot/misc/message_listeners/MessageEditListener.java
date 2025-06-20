package me.mbot.misc.message_listeners;

import me.mbot.configuration.Constants;
import me.mbot.misc.dao.MessageEditDAO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;

public class MessageEditListener extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(MessageEditListener.class);
    private final String CHANNEL_LOG_ID = Constants.getChannelLogId();

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        if (event.getAuthor().isBot()) return;

        String messageId = event.getMessageId();
        String newContent = event.getMessage().getContentRaw();
        long userId = event.getAuthor().getIdLong();

        String oldContent = MessageEditDAO.getOldMessageContent(messageId);
        if (oldContent != null && oldContent.equals(newContent)) {
            return;
        }

        if (oldContent == null) {
            oldContent = "*[Original content unknown]*";
        }

        MessageEditDAO.insertMessageEdit(messageId, userId, oldContent, newContent);

        TextChannel logChannel = event.getGuild().getTextChannelById(CHANNEL_LOG_ID);
        if (logChannel == null) {
            logger.warn("Log channel not found or {} is null.", CHANNEL_LOG_ID);
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Message edited");
        eb.setColor(0xECE81A);

        boolean isOldContentFile = false;
        boolean isNewContentFile = false;

        if (oldContent.length() > 1024) {
            try {
                File oldFile = File.createTempFile("old-message-", ".txt");
                Files.writeString(oldFile.toPath(), oldContent);

                eb.addField("Old content", "Attached as file (over 1024 characters)", false);

                isOldContentFile = true;
                oldFile.deleteOnExit();
            } catch (Exception e) {
                logger.error("Failed to create and upload file for old message: {}", e.getMessage());
            }
        } else {
            eb.addField("Old content", oldContent, false);
        }

        if (newContent.length() > 1024) {
            try {
                File newFile = File.createTempFile("new-message-", ".txt");
                Files.writeString(newFile.toPath(), newContent);

                eb.addField("New content", "Attached as file (over 1024 characters)", false);

                isNewContentFile = true;
                newFile.deleteOnExit();
            } catch (Exception e) {
                logger.error("Failed to create and upload file for new message: {}", e.getMessage());
            }
        } else {
            eb.addField("New content", newContent, false);
        }

        logChannel.sendMessageEmbeds(eb.build()).queue();

        // add the attachments if the booleans are set to true == long messages
        if (isOldContentFile) {
            try {
                File oldFile = File.createTempFile("old-message-", ".txt");
                Files.writeString(oldFile.toPath(), oldContent);
                logChannel.sendMessage("").addFiles(FileUpload.fromData(oldFile)).queue();
            } catch (Exception e) {
                logger.error("Failed to create and upload file for old message: {}", e.getMessage());
            }
        }

        if (isNewContentFile) {
            try {
                File newFile = File.createTempFile("new-message-", ".txt");
                Files.writeString(newFile.toPath(), newContent);
                logChannel.sendMessage("").addFiles(FileUpload.fromData(newFile)).queue();
            } catch (Exception e) {
                logger.error("Failed to create and upload file for new message: {}", e.getMessage());
            }
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String messageId = event.getMessageId();
        String content = event.getMessage().getContentRaw();
        long userId = event.getAuthor().getIdLong();

        MessageEditDAO.insertMessageEdit(messageId, userId, content, content);
    }
}