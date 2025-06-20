package me.mbot.commands.non_slash.commands;

import me.mbot.commands.non_slash.api.NonSlashCommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HowardTheAlienCommand implements NonSlashCommandHandler {
    @Override
    public void handle(MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("alien dancing 👽👽");
        eb.setColor(0x6CC417);
        event.getChannel().sendMessageEmbeds(eb.build()).queue();
        event.getChannel()
                .sendMessage("https://cdn.discordapp.com" +
                        "/attachments/1082718875038273566/1364220780954783845/" +
                        "dancing_mysterious_creature.mp4" +
                        "?ex=6808e11c&is=68078f9c&hm" +
                        "=de31c9fc45fa2c29e5120995121a477e14203444112e422a68f90e3b4da9860b&").queue();
    }

    @Override
    public String getName() {
        return "howard";
    }

    @Override
    public String getDescription() {
        return "Spawn a mysterious alien";
    }
}
