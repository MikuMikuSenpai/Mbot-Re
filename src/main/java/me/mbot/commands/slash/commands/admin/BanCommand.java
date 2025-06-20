package me.mbot.commands.slash.commands.admin;

import me.mbot.commands.slash.api.SlashCommandHandler;
import me.mbot.configuration.BotConfiguration;
import me.mbot.configuration.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BanCommand implements SlashCommandHandler {
    private final String CHANNEL_DARWIN_ID = Constants.getChannelDarwinId();
    @Override
    public CommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.USER, "user", "User to ban", true)
                .addOption(OptionType.STRING, "reason", "Reason to ban")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS));
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        User user = Objects.requireNonNull(event.getOption("user")).getAsUser();
        String reason = event.getOption("reason") != null
                ? Objects.requireNonNull(event.getOption("reason")).getAsString()
                : "No reason provided";

        Guild guild = Objects.requireNonNull(event.getGuild());
        Member bot = guild.getSelfMember();
        Member memberUsingCommand = event.getMember();

        if (!bot.hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("I (bot) don't have permission to ban users.").setEphemeral(true).queue();
            return;
        }

        if (memberUsingCommand == null || !memberUsingCommand.hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("You don't have permission to ban members.").setEphemeral(true).queue();
            return;
        }

        guild.retrieveMember(user).queue(member -> {
            if (memberUsingCommand.getId().equals(member.getId())) {
                event.reply("You cannot ban yourself.").setEphemeral(true).queue();
                return;
            }

            if (!memberUsingCommand.canInteract(member)) {
                event.reply("You cannot ban a member with a higher or equal role than yours.").setEphemeral(true).queue();
                return;
            }

            if (!bot.canInteract(member)) {
                event.reply("I cannot ban this user due to role hierarchy.").setEphemeral(true).queue();
                return;
            }

            // the 0 and timeunit.days is if we want to delete recent messages from banned user
            // as default its good to keep this at 0 meaning we keep their messages
            // we can later add an option for this for example if they spammed scam links
            guild.ban(user, 0, TimeUnit.DAYS)
                    .reason(reason)
                    .queue(
                            success -> {
                                event.reply("Banned <@" + user.getId() + "> \nFor reason: " + reason).setEphemeral(true).queue();

                                EmbedBuilder eb = new EmbedBuilder();
                                eb.setTitle("Som1 got banned");
                                eb.addField("User", "<@" + user.getId() + ">", false);
                                eb.addField("Banned by", "<@" + memberUsingCommand.getId() + ">", false);
                                eb.addField("Reason", reason, false);
                                //example image => we should pick a random image from a file with url links
                                eb.setImage("https://cdn.discordapp.com/attachments/1082718875038273566/1362894887049101552/meme.jpg?ex=68040e46&is=6802bcc6&hm=c700e68809ad4ce3dac25520aa3cc931e0932c8f163f156cded33d7642dc4e52&");

                                Objects.requireNonNull(guild.getTextChannelById(CHANNEL_DARWIN_ID)).sendMessageEmbeds(eb.build())
                                        .queue(null,
                                        error -> {
                                            System.err.println("Failed to send ban log message: " + error.getMessage());
                                        });
                            },
                            error -> event.reply("Failed to ban <@" + user.getId() + "> \nError: " + error.getMessage()).queue()
                    );
        });
    }

    @Override
    public boolean isModCommand() {
        return true;
    }

    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public String getDescription() {
        return "Ban a user";
    }
}
