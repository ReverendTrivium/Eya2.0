package org.eyazahrid.Commands.Fun;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.eyazahrid.Commands.Category;
import org.eyazahrid.Commands.Command;
import org.eyazahrid.Eyazahrid;

import java.util.Objects;

public class StopLoopCommand extends Command {
    private final LoopNSFWCommand loopNSFWCommand;

    public StopLoopCommand(Eyazahrid bot, LoopNSFWCommand loopNSFWCommand) {
        super(bot);
        this.name = "stoploop";
        this.description = "Stop the looping NSFW posts.";
        this.permission = Permission.MANAGE_SERVER;
        this.category = Category.FUN;
        this.loopNSFWCommand = loopNSFWCommand;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        loopNSFWCommand.stopLoop(Long.parseLong(Objects.requireNonNull(event.getGuild()).getId()), event.getChannel().getId());
        event.reply("Stopped looping NSFW posts.").setEphemeral(true).queue();
    }
}

