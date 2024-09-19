package org.eyazahrid.EyazahridStartup;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;
import org.eyazahrid.Commands.BotCommands;
import org.eyazahrid.Database.Data.GuildData;
import org.eyazahrid.Database.cache.Config;
import org.eyazahrid.Eyazahrid;

import java.util.ArrayList;

public class BotEventListener extends ListenerAdapter {

    private final Eyazahrid bot;
    private final BotCommands botCommands; // Reference to the BotCommands instance

    public BotEventListener(Eyazahrid bot, BotCommands botCommands) {
        this.bot = bot;
        this.botCommands = botCommands;
    }

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("Bot is ready and connected as " + event.getJDA().getSelfUser().getName());

        // Initialize for all guilds the bot is already in
        for (Guild guild : event.getJDA().getGuilds()) {
            initializeGuild(guild);
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        Guild guild = event.getGuild();
        System.out.println("Joined new guild: " + guild.getName());

        // Initialize the newly joined guild
        initializeGuild(guild);
    }

    private void initializeGuild(Guild guild) {
        // Step 1: Initialize the database for the new guild
        setupDatabaseForGuild(guild);

        // Step 2: Register commands for the guild using BotCommands
        botCommands.registerCommandsForGuild(guild);

        System.out.println("Setup completed for guild: " + guild.getName());
    }

    private void setupDatabaseForGuild(Guild guild) {
        System.out.println("Setting up database for guild: " + guild.getName());

        // Check and create default configuration if it doesn't exist
        Config guildConfig = bot.database.config.find(new Document("guildId", guild.getIdLong())).first();
        if (guildConfig == null) {
            // Create default config for the guild
            Config defaultConfig = new Config(guild.getIdLong());
            bot.database.config.insertOne(defaultConfig);
            System.out.println("Created default configuration for guild: " + guild.getName());
        } else {
            System.out.println("Configuration already exists for guild: " + guild.getName());
        }

        // Check and create a greetings collection entry if it doesn't exist
        bot.database.initializeGreetingsForGuild(guild.getIdLong());
    }
}
