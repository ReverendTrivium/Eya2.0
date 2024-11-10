package org.eyazahrid.Commands.Fun;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.eyazahrid.Commands.BotCommands;
import org.eyazahrid.Commands.Category;
import org.eyazahrid.Commands.Command;
import org.eyazahrid.Eyazahrid;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class LoopNSFWCommand extends Command {

    private static Timer timer;
    private String currentCategory;

    public LoopNSFWCommand(Eyazahrid bot) {
        super(bot);
        this.name = "loopnsfw";
        this.description = "Loop NSFW posts from a specific category every 10 minutes.";
        this.args.add(new OptionData(OptionType.STRING, "category", "The type of nsfw image to generate")
                .addChoice("porn", "porn")
                .addChoice("boobs", "boobs")
                .addChoice("gay", "gay")
                .addChoice("lesbian", "lesbian")
                .addChoice("furry", "furry")
                .addChoice("hentai", "hentai")
                .addChoice("public", "public")
                .addChoice("bg3", "bg3")
                .addChoice("raven", "raven")
                .addChoice("mihoyo", "mihoyo")
                .addChoice("cyberpunk", "cyberpunk")
                .addChoice("milf", "milf")
                .addChoice("japanese", "japanese")
                .addChoice("asian", "asian")
                .addChoice("black", "black")
                .addChoice("white", "white")
                .addChoice("india", "india")
                .addChoice("arab", "arab")
                .addChoice("native", "native").setRequired(true));
        this.permission = Permission.MANAGE_SERVER;
        this.category = Category.FUN;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().setEphemeral(true).queue();

        String category = Objects.requireNonNull(event.getOption("category")).getAsString();
        NSFWCommand nsfwCommand = (NSFWCommand) BotCommands.commandsMap.get("nsfw");

        currentCategory = category;

        // Check to ensure this is an NSFW Channel
        if (!event.getChannel().asTextChannel().isNSFW()) {
            System.out.println("This is not an NSFW Channel");
            event.getHook().sendMessage("This is not an NSFW Channel, cannot run NSFW Command in this channel").queue();
            return;
        }

        // Print the first image immediately
        nsfwCommand.executeCategory(event.getChannel().getId(), category, event);

        // Start the timer for subsequent images
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                nsfwCommand.executeCategory(event.getChannel().getId(), category, event);
            }
        }, 60000, 60000); // 600000ms = 10 minutes

        // Save the loop state to the database
        bot.getDatabase().saveLoopNSFWState(Long.parseLong(Objects.requireNonNull(event.getGuild()).getId()), true, category, event.getChannel().getId());


        event.getHook().sendMessage("Looping NSFW posts from category: " + category).queue();
    }

    // Restart NSFWLoop if it was running when bot shutdown
    public void startLoop(String channelId, String category, long guildId) {
        // Ensure the loop is stopped if already running
        stopLoop(guildId, channelId);

        NSFWCommand nsfwCommand = (NSFWCommand) BotCommands.commandsMap.get("nsfw");

        // Print the first image immediately
        nsfwCommand.executeCategory(channelId, category, null);

        // Initialize the timer and schedule the NSFW loop task
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Fetch NSFWCommand and execute it with the saved category
                NSFWCommand nsfwCommand = (NSFWCommand) BotCommands.commandsMap.get("nsfw");
                if (nsfwCommand != null) {
                    nsfwCommand.executeCategory(channelId, category, null);
                }
            }
        }, 60000, 60000); // Runs every 10 minutes

        // Save the loop state to the database
        bot.getDatabase().saveLoopNSFWState(guildId, true, category, channelId);

        System.out.println("Started NSFW loop in channel: " + channelId + " with category: " + category);
    }

    public void stopLoop(long guildId, String channelId ) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        // Set NSFW Loop status to false
        setStatus(guildId, channelId);
    }

    public void setStatus(long guildId, String channelId) {
        // Set NSFW Loop status to false
        bot.getDatabase().saveLoopNSFWState(guildId, false, currentCategory, channelId);
    }
}