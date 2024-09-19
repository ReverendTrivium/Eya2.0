package org.eyazahrid.EyazahridStartup;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.eyazahrid.Commands.BotCommands;
import org.eyazahrid.Eyazahrid;
import org.eyazahrid.Roles.RoleHierarchyManager;
import org.eyazahrid.listeners.*;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.Arrays;
import java.util.EnumSet;

public class BotInitializer {
    public static ShardManager initializeBot(String token) throws LoginException {
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);

        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.watching("Over All"));
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.MESSAGE_CONTENT);
        builder.enableCache(CacheFlag.ACTIVITY, CacheFlag.ONLINE_STATUS);

        return builder.build();
    }

    public static void registerListeners(ShardManager shardManager, Eyazahrid bot) {
        BotCommands botCommands = new BotCommands(bot); // Create a single instance of BotCommands

        shardManager.addEventListener(
                new EventListener(),
                new GalleryReactionListener(bot),
                new ButtonListener(bot),
                botCommands,  // Register BotCommands as an event listener
                new BotEventListener(bot, botCommands) // Pass BotCommands to BotEventListener
        );

        shardManager.addEventListener(new ListenerAdapter() {
            @Override
            public void onGuildReady(@NotNull GuildReadyEvent event) {
                Guild guild = event.getGuild();
                ChannelManager channelManager = new ChannelManager(bot);
                RoleManager roleManager = new RoleManager();
            }
        });
    }
}

