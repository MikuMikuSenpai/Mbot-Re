package me.mbot.commands.slash.commands.admin;

import me.mbot.commands.slash.api.SlashCommandHandler;
import me.mbot.configuration.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MuteCommand implements SlashCommandHandler {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Logger logger = LoggerFactory.getLogger(MuteCommand.class);
    private final String CHANNEL_DARWIN_ID = Constants.getChannelDarwinId();

    @Override
    public CommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.USER, "user", "user to mute", true)
                .addOption(OptionType.STRING, "duration", "Duration (example: 10m, 1h, 2d)", true)
                .addOption(OptionType.STRING, "reason", "reason to mute")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS));
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        Member issuer = Objects.requireNonNull(event.getMember());
        User user = Objects.requireNonNull(event.getOption("user")).getAsUser();
        String durationInput = Objects.requireNonNull(event.getOption("duration")).getAsString();
        String reason = event.getOption("reason") != null ? Objects.requireNonNull(event.getOption("reason")).getAsString() : "No reason provided";

        Duration duration = parseDuration(durationInput);
        if (duration == null) {
            event.reply("Invalid duration format. Use `s` for seconds, `m` for minutes, `h` for hours, `d` for days (examples: `30s`, `10m`, `1h`, `2d`).").setEphemeral(true).queue();
            return;
        }

        Objects.requireNonNull(event.getGuild()).retrieveMember(user).queue(member -> {
            member.timeoutFor(duration).reason(reason).queue(
                    success -> {
                        String roleName = "mute";
                        Role muteRole = event.getGuild().getRolesByName(roleName, true)
                                .stream().findFirst().orElse(null);

                        if (muteRole != null) {
                            event.getGuild().addRoleToMember(member, muteRole).queue();

                            scheduler.schedule(() -> {
                                event.getGuild().retrieveMember(user).queue(
                                        m -> event.getGuild().removeRoleFromMember(m, muteRole).queue(),
                                        err -> System.err.println("Could not retrieve member to remove mute role: " + err.getMessage())
                                );
                            }, duration.toMillis(), TimeUnit.MILLISECONDS);
                        } else {
                            logger.error("Could not find role with name: {}", roleName);
                        }

                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle("User muted (timedout)");
                        eb.addField("User", "<@" + user.getId() + ">", false);
                        eb.addField("Muted by", "<@" + issuer.getId() + ">", false);
                        eb.addField("Duration", durationInput, false);
                        eb.addField("Reason", reason, false);
                        Objects.requireNonNull(event.getGuild().getTextChannelById(CHANNEL_DARWIN_ID))
                                .sendMessageEmbeds(eb.build()).queue();
                    },
                    error -> event.reply("Failed to timeout user. Check bot permissions.").setEphemeral(true).queue()
            );
        }, error -> event.reply("Could not find the user in this guild.").setEphemeral(true).queue());
    }

    private Duration parseDuration(String input) {
        long days = 0, hours = 0, minutes = 0, seconds = 0;

        // searches for pattern number + d/h/m/s
        String regex = "(\\d+)([dhms])";
        Matcher matcher = Pattern.compile(regex).matcher(input.toLowerCase());

        while (matcher.find()) {
            long value = Long.parseLong(matcher.group(1));
            switch (matcher.group(2)) {
                case "d": days += value; break;
                case "h": hours += value; break;
                case "m": minutes += value; break;
                case "s": seconds += value; break;
                default: return null;
            }
        }

        Duration duration = Duration.ofDays(days)
                .plusHours(hours)
                .plusMinutes(minutes)
                .plusSeconds(seconds);

        if (duration.isZero() || duration.isNegative() || duration.compareTo(Duration.ofDays(365)) > 0) {
            return null;
        }

        return duration;
    }

    @Override
    public boolean isModCommand() {
        return true;
    }

    @Override
    public String getName() {
        return "mute";
    }

    @Override
    public String getDescription() {
        return "Mute a user.";
    }
}
