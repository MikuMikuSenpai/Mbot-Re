package me.mbot.configuration.api;

/**
 * Used by all slash and non slash commands, add more fields (for example String name) or more methods (for example someMethod())
 * This interface will probably not change a lot, make sure to tell all developers if it does as it might break a LOT of commands.
 */
public interface CommandMetaData {

    /**
     *
     * @return Name of the command as a String.
     */
    String getName();

    /**
     *
     * @return Description of the command as a String.
     */
    String getDescription();

}
