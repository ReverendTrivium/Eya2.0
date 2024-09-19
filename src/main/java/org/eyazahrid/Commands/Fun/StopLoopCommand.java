package org.eyazahrid.Commands.Fun;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.eyazahrid.Commands.Category;
import org.eyazahrid.Commands.Command;
import org.eyazahrid.Eyazahrid;

public class StopLoopCommand extends Command {

    public StopLoopCommand(Eyazahrid bot) {
        super(bot);
        this.name = "stoploop";
        this.description = "Stop the looping NSFW posts.";
        this.permission = Permission.MANAGE_SERVER;
        this.category = Category.FUN;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LoopNSFWCommand.stopLoop();
        event.reply("Stopped looping NSFW posts.").setEphemeral(true).queue();
    }
}

