package me.mbot.main;

import me.mbot.configuration.BotConfiguration;

/**
 * This is the main method that will start the bot. It grabs an instance of the singleton bot in BotConfiguration.
 * @link <a href="https://www.geeksforgeeks.org/singleton-class-java/">Singleton more info</a>
 * @see BotConfiguration
 */
public class Main {
    public static void main(String[] args) {
        BotConfiguration.getInstance();
    }
}
