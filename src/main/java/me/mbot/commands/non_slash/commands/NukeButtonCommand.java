package me.mbot.commands.non_slash.commands;

import me.mbot.commands.non_slash.api.NonSlashCommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NukeButtonCommand implements NonSlashCommandHandler {
    // key = country name, value = image url
    private final Map<String, String> countriesToBeNuked = new HashMap<>();
    private final Random random = new Random();

    // add your countries in here along with their nuke images
    public NukeButtonCommand() {
        countriesToBeNuked.put("France", "https://media.discordapp.net/attachments/1082718875038273566/1362900942630158376/Picsart_25-04-18_23-21-34-068.jpg?ex=6804bcaa&is=68036b2a&hm=3c6cc1e52b30e88052a4aa020f01974786a7347b924fa3d76ea457ae6f48cbeb&=&format=webp");
        countriesToBeNuked.put("Saudi Arabia", "https://media.discordapp.net/attachments/1082718875038273566/1363240262402572359/Picsart_25-04-19_21-49-41-562.jpg?ex=68054fee&is=6803fe6e&hm=e0d90fefa49e691de9357ea5c568cb59c7248f743c157f19c5d4ac1f61a7fa99&=&format=webp");
        countriesToBeNuked.put("Russia", "https://media.discordapp.net/attachments/1082718875038273566/1365270384588619867/Picsart_25-04-25_12-16-49-945.jpg?ex=680cb2a1&is=680b6121&hm=008b04891aa352a73bc5ac17bddd7b8ddc24e02e945088e621e87352973607a2&=&format=webp&width=648&height=648");
    }

    @Override
    public void handle(MessageReceivedEvent event) {
        ArrayList<String> countryList = new ArrayList<>(countriesToBeNuked.keySet());
        String randomCountry = countryList.get(random.nextInt(countryList.size()));
        String imageUrl = countriesToBeNuked.get(randomCountry);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("u NUKED " + randomCountry + " gg");
        eb.setColor(0x990000);
        eb.setImage(imageUrl);
        event.getChannel().sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public String getName() {
        return "Button";
    }

    @Override
    public String getDescription() {
        return "u nuked a 3rd world country ggs the earth is gone";
    }
}
