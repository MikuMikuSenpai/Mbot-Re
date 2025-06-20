package me.mbot.misc.firewall;

import me.mbot.configuration.Constants;
import me.mbot.misc.dao.AntiSpamFilterDAO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class AntiSpamListener extends ListenerAdapter {

    // the max amount of messages between certain time
    private static final int MESSAGE_LIMIT = 5;
    // time in ms 2000 = 2 seconds
    private static final long TIME_WINDOW_MS = 2000;
    private static final Duration TIMEOUT_DURATION = Duration.ofMinutes(10);
    private final String CHANNEL_LOG_ID = Constants.getChannelLogId();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getMember() == null) return;

        String userId = event.getAuthor().getId();
        long now = System.currentTimeMillis();
        long cutoff = now - TIME_WINDOW_MS;

        AntiSpamFilterDAO.insertMessageTimestamp(userId, now);
        List<Long> recent = AntiSpamFilterDAO.getRecentTimestamps(userId, cutoff);

        if (recent.size() >= MESSAGE_LIMIT) {
            AntiSpamFilterDAO.deleteUserTimestamps(userId);

            String reason = String.format("Auto timeout: Spamming (%d+ messages in %d seconds)",
                    MESSAGE_LIMIT, TIME_WINDOW_MS / 1000);

            event.getGuild().timeoutFor(event.getMember(), TIMEOUT_DURATION)
                    .reason(reason)
                    .queue();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Auto timeout");
            eb.setDescription(reason);
            eb.addField("User", "<@" + userId + ">", true);
            eb.setColor(0xFF0800);

            Objects.requireNonNull(event
                            .getGuild()
                            .getTextChannelById(CHANNEL_LOG_ID))
                    .sendMessageEmbeds(eb.build()).queue();
        }
    }
}
