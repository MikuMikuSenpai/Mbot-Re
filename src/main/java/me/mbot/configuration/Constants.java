package me.mbot.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class Constants {
    private static String BOT_VERSION;
    private static final Logger logger = LoggerFactory.getLogger(Constants.class);
    private static final String BOT_TOKEN = System.getenv("BOT_TOKEN");
    private static final String CHANNEL_LOG_ID = System.getenv("CHANNEL_LOG_ID");
    private static final String CHANNEL_DARWIN_ID = System.getenv("CHANNEL_DARWIN_ID");

    private static final List<String> OWNER_USER_IDS = List.of(
            "519536495842033665", // mikumikusenpai
            "401726025765093377" // acexcy
    );

    static {
        loadBotVersion();
    }

    private static void loadBotVersion() {
        Properties properties = new Properties();
        try (InputStream input = Constants.class.getResourceAsStream("/version.properties")) {
            if (input == null) {
                logger.error("version.properties not found");
                BOT_VERSION = "unknown";
                return;
            }
            properties.load(input);
            BOT_VERSION = properties.getProperty("version", "unknown");
            logger.info("Bot version loaded: {}", BOT_VERSION);
        } catch (IOException e) {
            logger.error("Failed to load bot version", e);
            BOT_VERSION = "unknown";
        }
    }

    public static List<String> getOwnerUserIds() {
        return OWNER_USER_IDS;
    }

    public static String getBotVersion() {
        return BOT_VERSION;
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
