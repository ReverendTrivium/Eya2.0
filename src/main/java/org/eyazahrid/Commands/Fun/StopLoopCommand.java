package org.eyazahrid.Commands.Fun;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.eyazahrid.Commands.Category;
import org.eyazahrid.Commands.Command;
import org.eyazahrid.Eyazahrid;

import java.util.Objects;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class StopLoopCommand extends Command {

    public StopLoopCommand(Eyazahrid bot) {
        super(bot);
        this.name = "stoploop";
        this.description = "Stop NSFW loops in this channel or all channels.";
        this.permission = Permission.MANAGE_SERVER;
        this.category = Category.FUN;
        this.args.add(new OptionData(OptionType.BOOLEAN, "all", "Set to true to stop all NSFW loops. Default is just this channel."));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        boolean stopAll = event.getOption("all") != null && Objects.requireNonNull(event.getOption("all")).getAsBoolean();
        if (stopAll) {
            LoopNSFWCommand.stopAllLoops();
            event.reply("Stopped all NSFW loops across all channels.").setEphemeral(true).queue();
        } else {
            String channelId = event.getChannel().getId();
            if (LoopNSFWCommand.stopLoop(channelId)) {
                event.reply("Stopped NSFW loop in this channel.").setEphemeral(true).queue();
            } else {
                event.reply("No active NSFW loop was running in this channel.").setEphemeral(true).queue();
            }
        }
    }
}