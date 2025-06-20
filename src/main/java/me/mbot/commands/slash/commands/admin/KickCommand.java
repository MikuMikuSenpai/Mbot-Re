package me.mbot.commands.slash.commands.admin;

import me.mbot.commands.slash.api.SlashCommandHandler;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class KickCommand implements SlashCommandHandler {
    private final String CHANNEL_DARWIN_ID = Constants.getChannelDarwinId();
    private final Logger logger = LoggerFactory.getLogger(KickCommand.class);

    @Override
    public CommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.USER, "user", "User to kick", true)
                .addOption(OptionType.STRING, "reason", "Reason to kick")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS));
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

        if (!bot.hasPermission(Permission.KICK_MEMBERS)) {
            event.reply("I (bot) don't have permission to kick users.").setEphemeral(true).queue();
            return;
        }

        if (memberUsingCommand == null || !memberUsingCommand.hasPermission(Permission.KICK_MEMBERS)) {
            event.reply("You don't have permission to kick members.").setEphemeral(true).queue();
            return;
        }

        guild.retrieveMember(user).queue(member -> {
            if (memberUsingCommand.getId().equals(member.getId())) {
                event.reply("You cannot kick yourself.").setEphemeral(true).queue();
                return;
            }

            if (!memberUsingCommand.canInteract(member)) {
                event.reply("You cannot kick a member with a higher or equal role than yours.").setEphemeral(true).queue();
                return;
            }

            if (!bot.canInteract(member)) {
                event.reply("I cannot kick this user due to role hierarchy.").setEphemeral(true).queue();
                return;
            }

            guild.kick(member)
                    .reason(reason)
                    .queue(
                            success -> {
                                event.reply("Kicked <@" + user.getId() + "> \nFor reason: " + reason).setEphemeral(true).queue();

                                EmbedBuilder eb = new EmbedBuilder();
                                eb.setTitle("Som1 got kicked");
                                eb.addField("User", "<@" + user.getId() + ">", false);
                                eb.addField("Kicked by", "<@" + memberUsingCommand.getId() + ">", false);
                                eb.addField("Reason", reason, false);
                                // example => we should add a doc with links and randomly choose url on each kick
                                eb.setImage("https://cdn.discordapp.com/attachments/1082718875038273566/1362894887049101552/meme.jpg?ex=68040e46&is=6802bcc6&hm=c700e68809ad4ce3dac25520aa3cc931e0932c8f163f156cded33d7642dc4e52&");

                                Objects.requireNonNull(guild.getTextChannelById(CHANNEL_DARWIN_ID))
                                        .sendMessageEmbeds(eb.build()).queue(null, error -> {
                                            System.err.println("Failed to send log message: " + error.getMessage());
                                        });
                            },
                            error -> event.reply("Failed to kick <@" + user.getId() + "\nError: " + error.getMessage()).queue()
                    );
        });
    }

    @Override
    public boolean isModCommand() {
        return true;
    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getDescription() {
        return "Kick a user";
    }
}
