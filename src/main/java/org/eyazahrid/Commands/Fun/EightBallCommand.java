package org.eyazahrid.Commands.Fun;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import org.eyazahrid.Commands.Command;
import org.eyazahrid.Commands.Category;
import org.eyazahrid.Eyazahrid;
import org.eyazahrid.util.embeds.EmbedUtils;
import org.eyazahrid.util.embeds.EmbedColor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Command that generates a cute picture from reddit.
 *
 * @author Derrick Eberlein
 */
public class EightBallCommand extends Command {

    private static final List<String> responses = Arrays.asList(
            "I can tell you certainly, no.",
            "I'm not sure but ur def stupid.",
            "It is certain.",
            "Without a doubt.",
            "You may rely on it.",
            "As I see it, yes.",
            "Most likely.",
            "Signs point to yes.",
            "Reply hazy try again.",
            "Better not tell you now.",
            "Hmm imma just let u figure it out.",
            "Don't count on it.",
            "Outlook not so good.",
            "My sources say no.");

    public EightBallCommand(Eyazahrid bot) {
        super(bot);
        this.name = "8ball";
        this.description = "Ask the magic 8ball a question.";
        this.category = Category.FUN;
        this.args.add(new OptionData(OptionType.STRING, "question", "The question to ask the 8ball", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String question = Objects.requireNonNull(event.getOption("question")).getAsString();
        if (question.length() > 250) {
            event.replyEmbeds(EmbedUtils.createError("The 8ball doesn't like questions longer than 250 characters!")).queue();
            return;
        }

        int index = ThreadLocalRandom.current().nextInt(responses.size());
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(EmbedColor.DEFAULT.color)
                .setTitle(question)
                .setDescription(":8ball: " + responses.get(index));
        event.replyEmbeds(embed.build()).queue();
    }
}
