package me.mbot.configuration;

import java.util.List;

public class Constants {

    private static final String BOT_TOKEN = System.getenv("BOT_TOKEN");
    private static final String CHANNEL_LOG_ID = System.getenv("CHANNEL_LOG_ID");
    private static final String CHANNEL_DARWIN_ID = System.getenv("CHANNEL_DARWIN_ID");
    private static final String MODERATOR_ROLE_ID = System.getenv("MODERATOR_ROLE_ID");
    private static final String ROLE_1_ID = System.getenv("ROLE_1_ID");
    private static final String ROLE_2_ID = System.getenv("ROLE_2_ID");

    private static final List<String> OWNER_USER_IDS = List.of(
            "519536495842033665", // mikumikusenpai
            "401726025765093377" // acexcy
    );

    public static List<String> getOwnerUserIds() {
        return OWNER_USER_IDS;
    }

    public static String getBotToken() {
        return BOT_TOKEN;
    }

    public static String getChannelLogId() {
        return CHANNEL_LOG_ID;
    }

    public static String getChannelDarwinId() {
        return CHANNEL_DARWIN_ID;
    }

    public static String getModeratorRoleId() {
        return MODERATOR_ROLE_ID;
    }

    public static String getRole1Id() {
        return ROLE_1_ID;
    }

    public static String getRole2Id() {
        return ROLE_2_ID;
    }

    public static String getDBUrl() {
        return System.getenv("DB_URL");
    }

    public static String getDBUser() {
        return System.getenv("DB_USER");
    }

    public static String getDBPassword() {
        return System.getenv("DB_PASSWORD");
    }

}
