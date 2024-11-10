package org.eyazahrid.Database.Data;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.eyazahrid.Database.Database;
import org.eyazahrid.Database.cache.Config;
import org.jetbrains.annotations.NotNull;
import org.eyazahrid.Handlers.*;
import org.eyazahrid.Eyazahrid;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Local cache of settings and data for each guild. Interacts with various commands, modules,
 * and the MongoDB database to always keep data updated locally.
 *
 * @author Derrick Eberlein
 */
@Getter
@Setter
public class GuildData {

    private static final Map<Long, GuildData> guilds = new HashMap<>();
    private static Database database;
    private Config config;

    private final long guildId;
    private final GreetingHandler greetingHandler;
    // Method to retrieve the EconomyHandler

    public GuildData(Guild guild, Eyazahrid bot) {
        this.guildId = guild.getIdLong();

        // Ensure collections are set up for the guild
        database.setupCollectionsForGuild(guildId);

        // Retrieve the configuration for the guild
        this.config = database.getConfigForGuild(guildId);
        if (this.config == null) {
            System.out.println("Config for guild " + guildId + " is not found, creating a new one.");

            // Create default config for the guild with required values
            this.config = new Config(guildId);

            // Insert the default config into the database
            database.insertConfig(guildId, this.config);
            System.out.println("Inserted default configuration for guild: " + guild.getName());

            // Retrieve the config again after insertion to ensure it is properly inserted
            this.config = database.getConfigForGuild(guildId);
            if (this.config == null) {
                throw new IllegalStateException("Failed to create or retrieve config for guild " + guildId);
            }
        }

        this.greetingHandler = new GreetingHandler(guild, database);
    }


    public static void init(Database db) {
        database = db;
    }

    public static GuildData get(@NotNull Guild guild, Eyazahrid bot) {
        return guilds.computeIfAbsent(Objects.requireNonNull(guild).getIdLong(), id -> new GuildData(guild, bot)); // Pass bot instance here
    }

    public MongoCollection<Document> getBlacklistCollection() {
        return database.getGuildCollection(guildId, "blacklist");
    }

    public MongoCollection<Document> getScheduledMessagesCollection() {
        return database.getGuildCollection(guildId, "scheduled_messages");
    }

    public MongoCollection<Document> getStickyMessagesCollection() {
        return database.getGuildCollection(guildId, "sticky_messages");
    }

    public MongoCollection<Document> getUserIntroMessagesCollection() {
        return database.getGuildCollection(guildId, "user_intro_messages");
    }

    public MongoCollection<Document> getLoopNSFWStateCollection() {
        return database.getGuildCollection(guildId, "LoopNSFWCommand");
    }

    public String getGuildId() {
        return String.valueOf(guildId);
    }


    public Config getConfig() {
        // Fetch the config for this guild
        Config fetchedConfig = database.getConfigCollection().find(new Document("guildId", guildId)).first();

        // If the config is still null, throw an exception to catch the error early
        if (fetchedConfig == null) {
            throw new IllegalStateException("Config for guild " + guildId + " could not be found or created.");
        }
        return fetchedConfig;
    }


    public void updateConfig(Bson update) {
        database.updateConfigForGuild(guildId, update);
        // Also update the in-memory config object if necessary
        this.config = database.getConfigForGuild(guildId);
    }
}
