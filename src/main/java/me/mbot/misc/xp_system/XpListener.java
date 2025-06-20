package me.mbot.misc.xp_system;

import me.mbot.misc.dao.XpDAO;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class XpListener extends ListenerAdapter {
    private final Logger logger = LoggerFactory.getLogger(XpListener.class);
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        long userId = event.getAuthor().getIdLong();
        int currentXP = XpDAO.getXP(userId);
        int oldLevel = XpHelper.getLevel(currentXP);
        // change this however xp you want that a user gets per message
        int xpPerMessage = 10;

        XpDAO.addXP(userId, xpPerMessage);
        int newXP = currentXP + xpPerMessage;
        int newLevel = XpHelper.getLevel(newXP);

        if (newLevel > oldLevel) {
            var levelRoles = XpHelper.getLevelRoles();
            String roleId = levelRoles.get(newLevel);
            var guild = event.getGuild();
            var member = event.getMember();
            var role = guild.getRoleById(roleId);

            String message = "%s leveled up to level %d";

            if (guild != null && member != null && role != null) {
                guild.addRoleToMember(member, role).queue();
                message += ". You got a new role: " + Objects.requireNonNull(guild.getRoleById(roleId)).getName();
            }

            event.getChannel().sendMessage(
                    String.format(message, event.getAuthor().getAsMention(), newLevel)
            ).queue();
        }
    }
}
