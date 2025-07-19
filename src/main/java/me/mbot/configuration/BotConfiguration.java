package me.mbot.configuration;

import me.mbot.commands.non_slash.NonSlashCommandListener;
import me.mbot.commands.slash.SlashCommandListener;
import me.mbot.misc.firewall.AntiSpamListener;
import me.mbot.misc.firewall.MessageFilter;
import me.mbot.misc.message_listeners.MessageDeleteListener;
import me.mbot.misc.message_listeners.MessageEditListener;
import me.mbot.misc.message_listeners.MessageHighlight;
import me.mbot.misc.scheduler.ReminderService;
import me.mbot.misc.xp_system.XpListener;
import me.mbot.misc.xp_system.XpHelper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

/**
 * Use this class for main bot configuration. Add or remove important GatewayIntents
 * keep it as minimalistic as possible.
 */
public class BotConfiguration {

    private BotConfiguration() {
        Logger logger = LoggerFactory.getLogger(BotConfiguration.class);
        try {

            String botToken = Constants.getBotToken();

            /*
            Need to make a variable instance of slashCommandListener because this is injected into BotMainListener
            BotMainListener grabs the SlashCommands from "slashCommandListener".
             */
            SlashCommandListener slashCommandListener = new SlashCommandListener();

            JDA jda = JDABuilder.createLight(botToken, EnumSet.of(
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.MESSAGE_CONTENT,
                    GatewayIntent.GUILD_MESSAGE_REACTIONS,
                    GatewayIntent.GUILD_MEMBERS))
                    .addEventListeners(
                            new NonSlashCommandListener(),
                            new SlashCommandListener(),
                            new BotMainListener(slashCommandListener),
                            // misc listeners
                            new MessageDeleteListener(),
                            new MessageEditListener(),
                            new AntiSpamListener(),
                            new MessageHighlight(),
                            new MessageFilter(),
                            new XpListener()
                    )
                    .build()
                    .awaitReady();
            /*
            Assign roles to all members based on their xp amount when bot starts,
            normally not needed but good for double check,
            makes sure that everyone has the role they should have after the bot is started
             */
            XpHelper.assignRolesOnStartup(jda);
            new ReminderService(jda);
        } catch (IllegalArgumentException e) {
            logger.info("You forgot to set {} environment variable", Constants.getBotToken());
            logger.error(e.getMessage());
        } catch (InterruptedException e) {
            logger.info("awaitReady() was interrupted");
            logger.error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        // check if env variables are set (excluding BOT_TOKEN)
        if (Constants.getChannelLogId() == null || Constants.getChannelLogId().isEmpty())
            logger.warn("{} environment variable is empty and could lead to unexpected errors.", Constants.getChannelLogId());
        if (Constants.getChannelDarwinId() == null || Constants.getChannelDarwinId().isEmpty())
            logger.warn("{} environment variable is empty and could lead to unexpected errors.", Constants.getChannelDarwinId());
    }

    /**
     * This class holds the bot instance, don't change or touch it unless you know what you are doing.
     */
    /*
    Don't touch this. This makes it so that there is only one instance.
     */
    private static class BotHolder {
        private static final BotConfiguration INSTANCE = new BotConfiguration();
    }

    /**
     * @see me.mbot.main.Main
     * @return Singleton instance of mbot.
     */
    // Don't touch this. This makes the instance accessible to the main method.
    public static BotConfiguration getInstance() {
        return BotHolder.INSTANCE;
    }

}
