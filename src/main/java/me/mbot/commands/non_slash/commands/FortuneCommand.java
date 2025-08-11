package me.mbot.commands.non_slash.commands;

import me.mbot.commands.non_slash.api.NonSlashCommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Random;

public class FortuneCommand implements NonSlashCommandHandler {
    @Override
    public void handle(MessageReceivedEvent event) {
        ArrayList<String> fortunes = new ArrayList<>();
        fortunes.add("Excellent Luck");
        fortunes.add("Good Luck");
        fortunes.add("Godly Luck");
        fortunes.add("Bad Luck");
        fortunes.add("Very Bad Luck");
        fortunes.add("Better not tell you now");
        fortunes.add("You will meet a dark handsome stranger");
        fortunes.add("（ ´_ゝ`）ﾌｰﾝ");
        fortunes.add("ｷﾀ━━━━━━(ﾟ∀ﾟ)━━━━━━ !!!!");

        Random rand = new Random();
        String randFortune = fortunes.get(rand.nextInt(fortunes.size()));
        event.getMessage().reply(randFortune).queue();
    }

    @Override
    public String getName() {
        return "fortune";
    }

    @Override
    public String getDescription() {
        return "get to know your fortune";
    }
}
