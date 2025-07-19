package me.mbot.misc.scheduler;

import me.mbot.configuration.Constants;
import me.mbot.misc.dao.ReminderDAO;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ReminderService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JDA jda;

    public ReminderService(JDA jda) {
        this.jda = jda;
        start();
    }

    private void start() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                processReminders();
            }
        }, 0, 30_000); // every 30 seconds
    }

    private void processReminders() {
        List<ReminderDAO.Reminder> reminders = ReminderDAO.getDueReminders();
        if (reminders.isEmpty()) return;

        TextChannel logChannel = jda.getTextChannelById(Constants.getChannelLogId());
        if (logChannel == null) {
            logger.error("Failed to find log channel.");
            return;
        }

        for (ReminderDAO.Reminder reminder : reminders) {
            logChannel.sendMessage("<@" + reminder.userId() + "> Reminder: **" + reminder.note() + "**")
                    .queue(
                            success -> ReminderDAO.deleteReminder(reminder.id()),
                            failure -> logger.error("Failed to send reminder for user {}", reminder.userId())
                    );
        }
    }
}
