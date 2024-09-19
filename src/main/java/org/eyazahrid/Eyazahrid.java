package org.eyazahrid;

import com.google.gson.Gson;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import net.dv8tion.jda.api.sharding.ShardManager;
import okhttp3.OkHttpClient;
import org.eyazahrid.Database.Data.GuildData;
import org.eyazahrid.Database.Database;
import org.eyazahrid.EyazahridStartup.BotInitializer;
import org.eyazahrid.util.GalleryManager;

import javax.security.auth.login.LoginException;

@Getter
public class Eyazahrid {
    public Gson gson;
    public OkHttpClient httpClient;
    public final Dotenv config;
    public final ShardManager shardManager;
    public final Database database;
    public final GalleryManager galleryManager;

    public Eyazahrid() throws LoginException {
        // Load configuration
        config = Dotenv.configure().ignoreIfMissing().load();

        // Initialize components
        gson = new Gson();
        httpClient = new OkHttpClient();

        // Initialize the database
        database = new Database(config.get("DATABASE"));

        // Initialize Guild Data
        System.out.println("Initializing GuildData...");
        GuildData.init(database);
        System.out.println("GuildData initialized");

        galleryManager = new GalleryManager();
        shardManager = BotInitializer.initializeBot(config.get("TOKEN"));

        // Register listeners
        BotInitializer.registerListeners(shardManager, this);
    }

    public static void main(String[] args) {
        try {
            new Eyazahrid();
            System.out.println("Bot started successfully");
        } catch (LoginException e) {
            System.err.println("ERROR: Provided bot token is invalid!!");
        }
    }
}