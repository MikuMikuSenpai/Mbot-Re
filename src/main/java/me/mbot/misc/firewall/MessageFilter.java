package me.mbot.misc.firewall;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MessageFilter extends ListenerAdapter {

    private final List<String> bannedWords = Arrays.asList(
            "faggot", "fagg", "fagot",
            "nigger", "nigg", "niggr", "nggr", "ngger"
    );

    // Add variations for each letter that are possible for example a = a,4,@,A
    private static final String[][] substitutions = {
            {"a", "4", "@", "A"},
            {"b", "8", "B"},
            {"e", "3", "E"},
            {"i", "1", "!", "I"},
            {"o", "0", "O"},
            {"s", "$", "S"},
            {"t", "7", "+", "T"},
            {"z", "2", "Z"}
    };

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String messageContent = event.getMessage().getContentRaw();

        for (String word : bannedWords) {
            String regex = createRegexPattern(word);
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(messageContent);

            if (matcher.find()) {
                event.getMessage().reply("u said a banned word!").queue();
                event.getMessage().delete().queue();

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("NoooOOOOo UcantSaythatss xdDD");
                eb.setImage("https://cdn.discordapp.com/attachments/1082718875038273566/1362946222805094401/73c-2304574623.jpg?ex=68043e15&is=6802ec95&hm=639f8dff66899797a99a9abd55d97eb4f4e01a44935b04ff808b8ee70252bb0e&");
                event.getChannel().sendMessageEmbeds(eb.build()).queue();
                break;
            }
        }
    }

    private String createRegexPattern(String word) {
        StringBuilder pattern = new StringBuilder();

        for (char c : word.toCharArray()) {
            String[] subList = getSubstitutionsForCharacter(c);

            StringBuilder charPattern = new StringBuilder("[^a-zA-Z]*(");
            for (String sub : subList) {
                charPattern.append(escapeSpecialChars(sub)).append("|");
            }
            charPattern.deleteCharAt(charPattern.length() - 1);
            charPattern.append(")[^a-zA-Z]*");

            pattern.append(charPattern);
        }

        return pattern.toString();
    }

    private String escapeSpecialChars(String input) {
        // needs this for special characters otherwise error dont ask me why ngl
        return input.replaceAll("([\\[\\]\\.\\{\\}\\(\\)\\?\\+\\*\\^\\$])", "\\\\$1");
    }

    private String[] getSubstitutionsForCharacter(char c) {
        return switch (Character.toLowerCase(c)) {
            case 'a' -> substitutions[0];
            case 'b' -> substitutions[1];
            case 'e' -> substitutions[2];
            case 'i' -> substitutions[3];
            case 'o' -> substitutions[4];
            case 's' -> substitutions[5];
            case 't' -> substitutions[6];
            case 'z' -> substitutions[7];
            default -> new String[]{String.valueOf(c)};
        };
    }
}
