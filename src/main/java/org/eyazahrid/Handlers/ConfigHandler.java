package org.eyazahrid.Handlers;

import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.conversions.Bson;
import org.eyazahrid.Database.cache.Config;
import org.eyazahrid.Eyazahrid;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * Handles config data for the guild and various modules.
 *
 * @author Derrick Eberlein
 */
public class ConfigHandler {

    private static final ScheduledExecutorService expireScheduler = Executors.newScheduledThreadPool(10);
    private static final Map<String, ScheduledFuture> expireTimers = new HashMap<>();

    private final Guild guild;
    private final Eyazahrid bot;
    private final Bson filter;
    private Config config;

    public ConfigHandler(Eyazahrid bot, Guild guild) {
        this.bot = bot;
        this.guild = guild;

        // Get POJO object from database
        this.filter = Filters.eq("guild", guild.getIdLong());
        if (this.config == null) {
            this.config = new Config(guild.getIdLong());
        }
    }

    /**
     * Access the config cache.
     *
     * @return a cache instance of the Config from database.
     */
    public Config getConfig() { return config; }

}
