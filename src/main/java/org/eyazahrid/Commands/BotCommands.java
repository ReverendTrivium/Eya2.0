package org.eyazahrid.Commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.eyazahrid.Commands.Fun.*;
import org.eyazahrid.Commands.Utility.*;
import org.eyazahrid.Commands.Moderation.*;
import org.eyazahrid.Commands.Utility.HelpSubCommands.*;
import org.eyazahrid.Database.Data.GuildData;
import org.eyazahrid.Eyazahrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eyazahrid.util.GoogleSearch.GoogleSearchService;
import org.eyazahrid.util.embeds.EmbedUtils;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import java.util.*;

/**
 * Command Manager of Bot, all commands will be added and controlled
 * through this class.
 *
 * @author Derrick Eberlein
 */
public class BotCommands extends ListenerAdapter {
    /** List of commands in the exact order registered */
    public static final List<Command> commands = new ArrayList<>();

    /** Map of command names to command objects */
    public static final Map<String, Command> commandsMap = new HashMap<>();

    /**
     * Adds commands to a global list and registers them as event listener.
     *
     * @param bot An instance of Redacted.
     */
    public BotCommands(Eyazahrid bot) {
        // Inside your command registration method
        GoogleSearchService googleSearchService = new GoogleSearchService();
        mapCommand(
                //Fun commands
                new NSFWCommand(bot),
                new AnimeCommand(bot),
                new LoopNSFWCommand(bot),
                new StopLoopCommand(bot),
                new JokeCommand(bot),
                new welcome(bot),
                new EightBallCommand(bot),
                new InspireCommand(bot),
                new GoogleCommand(bot, googleSearchService),

                //Leveling commands

                //Suggestions commands

                //Staff commands
                new roles(bot),

                //Music commands

                //Starboard commands

                //Utility commands
                new Server(bot),
                new Ping(bot),
                new Clear(bot),
                new NSFWCleanCommand(bot),
                new Help(bot)
        );


        // Register CategoryHelpCommand for each category
        for (Category category : Category.values()) {
            mapCommand(new CategoryHelpCommand(bot, category));
        }
    }

    /**
     * Adds a command to the static list and map.
     *
     * @param cmds a spread list of command objects.
     */
    private void mapCommand(Command ...cmds) {
        for (Command cmd : cmds) {
            commandsMap.put(cmd.name, cmd);
            commands.add(cmd);
            System.out.println("Command registered: " + cmd.name);
        }
    }

    public void registerCommandsForGuild(Guild guild) {
        System.out.println("Registering commands for guild: " + guild.getName());

        // Register slash commands for the specific guild
        guild.updateCommands().addCommands(unpackCommandData()).queue(
                succ -> System.out.println("Commands registered successfully for guild: " + guild.getName()),
                fail -> System.out.println("Failed to register commands for guild: " + guild.getName())
        );
    }

    public static List<CommandData> unpackCommandData() {
        List<CommandData> commandData = new ArrayList<>();
        for (Command command : commands) {
            SlashCommandData slashCommand = Commands.slash(command.name, command.description).addOptions(command.args);
            if (command.permission != null) {
                slashCommand.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.MODERATE_MEMBERS));
            }
            if (!command.subCommands.isEmpty()) {
                slashCommand.addSubcommands(command.subCommands);
            }
            commandData.add(slashCommand);

            System.out.println("Registering command: " + command.name);
            for (SubcommandData subcommand : command.subCommands) {
                System.out.println("  Subcommand: " + subcommand.getName() + " - " + subcommand.getDescription());
            }
        }
        return commandData;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        System.out.println("Received slash command interaction: " + event.getName());

        // Get command by name
        Command cmd = commandsMap.get(event.getName());
        if (cmd != null) {
            // Check for required bot permissions
            Role botRole = Objects.requireNonNull(event.getGuild()).getBotRole();
            if (cmd.botPermission != null) {
                assert botRole != null;
                if (!botRole.hasPermission(cmd.botPermission) && !botRole.hasPermission(Permission.ADMINISTRATOR)) {
                    String text = "I need the `" + cmd.botPermission.getName() + "` permission to execute that command.";
                    event.replyEmbeds(EmbedUtils.createError(text)).setEphemeral(true).queue();
                    return;
                }
            }
            // Run command
            cmd.execute(event);
        } else {
            System.out.println("Command not found: " + event.getName());
        }
    }

    public Command getCommandByName(String name) {
        return commandsMap.get(name);  // Fetches command by its name
    }
}

