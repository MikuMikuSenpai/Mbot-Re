package me.mbot.misc.xp_system;

import me.mbot.configuration.Constants;
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

    private static final String roleOneId = Constants.getRole1Id();
    private static final String roleTwoId = Constants.getRole2Id();

    private static final Integer levelZero = 0;
    private static final Integer levelOne = 1;
    private static final Integer levelTwo = 2;
    private static final Integer levelThree = 3;
    private static final Integer levelFour = 4;
    private static final Integer levelFive = 5;

    private static final Integer xpLevelZero = 0;
    private static final Integer xpLevelOne = 100;
    private static final Integer xpLevelTwo = 520;
    private static final Integer xpLevelThree = 1_800;
    private static final Integer xpLevelFour = 35_000;
    private static final Integer xpLevelFive = 50_000;

    static {
        levelThresholds.put(levelZero, xpLevelZero);
        levelThresholds.put(levelOne, xpLevelOne); // 10 msgs
        levelThresholds.put(levelTwo, xpLevelTwo); // 52 msgs
        levelThresholds.put(levelThree, xpLevelThree); // 180 msgs
        levelThresholds.put(levelFour, xpLevelFour); // 3500 msgs
        levelThresholds.put(levelFive, xpLevelFive); // 5000 msgs

        levelRoles.put(levelZero, roleOneId);
        levelRoles.put(levelOne, roleTwoId);
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

                            if (member.getUser().isBot()) continue;

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
