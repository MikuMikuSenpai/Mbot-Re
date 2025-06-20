package me.mbot.misc.xp_system;

import me.mbot.misc.dao.XpDAO;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class XpHelper {

    private static final Logger logger = LoggerFactory.getLogger(XpHelper.class);
    private static final NavigableMap<Integer, Integer> levelThresholds = new TreeMap<>();
    private static final Map<Integer, String> levelRoles = new TreeMap<>();

    static {
        // x, y == x = level / y = xp threshold xp needed
        levelThresholds.put(0, 0);
        levelThresholds.put(1, 100); // 10 msgs
        levelThresholds.put(2, 520); // 52 msgs
        levelThresholds.put(3, 18000); // 180 msgs
        levelThresholds.put(4, 35000); // 350 msgs
        levelThresholds.put(5, 50000); // 500 msgs

        // x, y == x = level / y = role ID (from .env = env var name String value is stored in BotConfiguration class)
        levelRoles.put(0, "1364229036863393853");
        levelRoles.put(1, "1364229114818728006");
    }

    public static int getLevel(int xp) {
        int level = 0;
        for (var entry : levelThresholds.entrySet()) {
            if (xp >= entry.getValue()) {
                level = entry.getKey();
            } else {
                break;
            }
        }
        return level;
    }

    public static void assignRolesOnStartup(JDA jda) {
        for (Guild guild : jda.getGuilds()) {
            logger.info("Loading members for guild: {}", guild.getName());

            guild.loadMembers()
                    .onSuccess(members -> {
                        for (Member member : members) {
                            logger.info("Checking member: {}. Is bot?: {}", member.getEffectiveName(), member.getUser().isBot());

                            if (member.getUser().isBot()) continue; // skip bot

                            int xp = XpDAO.getXP(member.getIdLong());
                            int level = XpHelper.getLevel(xp);

                            logger.info("Member {} has {} XP and level {}", member.getEffectiveName(), xp, level);

                            for (Map.Entry<Integer, String> entry : levelRoles.entrySet()) {
                                if (level >= entry.getKey()) {
                                    Role role = guild.getRoleById(entry.getValue());
                                    logger.info("Target role: {}", role != null ? role.getName() : "null");

                                    if (role == null) {
                                        logger.warn("Role (id) not found: {}. Perhaps you forgot to update .env file?", entry.getValue());
                                    }

                                    if (role != null && !member.getRoles().contains(role)) {
                                        logger.info("Assigning role {} to {}", role.getName(), member.getEffectiveName());
                                        guild.addRoleToMember(member, role).queue(
                                                success -> logger.info("Assigned role {} to {}", role.getName(), member.getEffectiveName()),
                                                error -> logger.error("Failed to assign role: {}", error.getMessage())
                                        );
                                    }
                                }
                            }
                        }
                    })
                    .onError(error -> logger.error("Failed to load members for guild {}: {}", guild.getName(), error.getMessage()));
        }
    }

    public static int getXPForLevel(int level) {
        return levelThresholds.getOrDefault(level, -1);
    }


    public static Map<Integer, String> getLevelRoles() {
        return levelRoles;
    }

}
