package org.eyazahrid.EyazahridStartup;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.eyazahrid.Eyazahrid;
import org.eyazahrid.Roles.RoleHierarchyManager;

import java.util.Objects;

public class ChannelManager {
    private final Eyazahrid bot;

    public ChannelManager(Eyazahrid bot) {
        this.bot = bot;
    }

    public TextChannel getOrCreateTextChannel(Guild guild, String name, String categoryName) {
        Category category = getOrCreateCategory(guild, categoryName);
        TextChannel channel = guild.getTextChannelsByName(name, true).stream().findFirst().orElse(null);
        if (channel == null) {
            try {
                channel = guild.createTextChannel(name)
                        .setParent(category)
                        .complete();
                System.out.println("Created new text channel: " + name + " under category: " + category.getName());
            } catch (Exception e) {
                System.err.println("Failed to create text channel: " + name);
                e.printStackTrace();
            }
        }

        switch (categoryName) {
            case "Introductions" -> {
                try {
                    Objects.requireNonNull(channel).upsertPermissionOverride(guild.getPublicRole())
                            .grant(Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY, Permission.VIEW_CHANNEL)
                            .queue();
                    System.out.println("Updated permissions for text channel: " + name + " under category: " + categoryName);
                } catch (Exception e) {
                    System.err.println("Failed to update permissions for text channel: " + name);
                    e.printStackTrace();
                }
            }
            case "Moderation", "Staff" -> {
                try {
                    RoleManager roleManager = new RoleManager();
                    Role developerRole = roleManager.getOrCreateRole(guild, "Developer", RoleHierarchyManager.ALL_PERMISSIONS, RoleHierarchyManager.DEVELOPER_COLOR);
                    Role adminRole = roleManager.getOrCreateRole(guild, "Admin", RoleHierarchyManager.ALL_PERMISSIONS, RoleHierarchyManager.ADMIN_COLOR);
                    Role headDJRole = roleManager.getOrCreateRole(guild, "Head DJ", RoleHierarchyManager.HEAD_DJ_PERMISSIONS, RoleHierarchyManager.HEAD_DJ_COLOR);
                    Role eventStaffRole = roleManager.getOrCreateRole(guild, "Event Staff", RoleHierarchyManager.EVENT_STAFF_PERMISSIONS, RoleHierarchyManager.EVENT_STAFF_COLOR);

                    Objects.requireNonNull(channel).upsertPermissionOverride(guild.getPublicRole())
                            .deny(Permission.VIEW_CHANNEL)
                            .queue();
                    channel.upsertPermissionOverride(developerRole)
                            .grant(Permission.VIEW_CHANNEL)
                            .queue();
                    channel.upsertPermissionOverride(adminRole)
                            .grant(Permission.VIEW_CHANNEL)
                            .queue();
                    channel.upsertPermissionOverride(headDJRole)
                            .grant(Permission.VIEW_CHANNEL)
                            .queue();
                    channel.upsertPermissionOverride(eventStaffRole)
                            .grant(Permission.VIEW_CHANNEL)
                            .queue();
                    System.out.println("Updated permissions for text channel: " + name + " under category: " + categoryName);
                } catch (Exception e) {
                    System.err.println("Failed to update permissions for text channel: " + name);
                    e.printStackTrace();
                }
            }
        }

        return channel;
    }

    public Category getOrCreateCategory(Guild guild, String name) {
        Category category = guild.getCategoriesByName(name, true).stream().findFirst().orElse(null);
        if (category == null) {
            try {
                category = guild.createCategory(name)
                        .complete();
                System.out.println("Created new category: " + name);
            } catch (Exception e) {
                System.err.println("Failed to create category: " + name);
                e.printStackTrace();
            }
        }

        switch (name) {
            case "Introductions" -> {
                try {
                    Objects.requireNonNull(category).upsertPermissionOverride(guild.getPublicRole())
                            .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                            .queue();
                    System.out.println("Updated permissions for category: " + name);
                } catch (Exception e) {
                    System.err.println("Failed to update permissions for category: " + name);
                    e.printStackTrace();
                }
            }
            case "Moderation", "Staff" -> {
                try {
                    RoleManager roleManager = new RoleManager();
                    Role developerRole = roleManager.getOrCreateRole(guild, "Developer", RoleHierarchyManager.ALL_PERMISSIONS, RoleHierarchyManager.DEVELOPER_COLOR);
                    Role adminRole = roleManager.getOrCreateRole(guild, "Admin", RoleHierarchyManager.ALL_PERMISSIONS, RoleHierarchyManager.ADMIN_COLOR);
                    Role headDJRole = roleManager.getOrCreateRole(guild, "Head DJ", RoleHierarchyManager.HEAD_DJ_PERMISSIONS, RoleHierarchyManager.HEAD_DJ_COLOR);
                    Role eventStaffRole = roleManager.getOrCreateRole(guild, "Event Staff", RoleHierarchyManager.EVENT_STAFF_PERMISSIONS, RoleHierarchyManager.EVENT_STAFF_COLOR);

                    Objects.requireNonNull(category).upsertPermissionOverride(guild.getPublicRole())
                            .deny(Permission.VIEW_CHANNEL)
                            .queue();
                    category.upsertPermissionOverride(developerRole)
                            .grant(Permission.VIEW_CHANNEL)
                            .queue();
                    category.upsertPermissionOverride(adminRole)
                            .grant(Permission.VIEW_CHANNEL)
                            .queue();
                    category.upsertPermissionOverride(headDJRole)
                            .grant(Permission.VIEW_CHANNEL)
                            .queue();
                    category.upsertPermissionOverride(eventStaffRole)
                            .grant(Permission.VIEW_CHANNEL)
                            .queue();
                    System.out.println("Updated permissions for category: " + name);
                } catch (Exception e) {
                    System.err.println("Failed to update permissions for category: " + name);
                    e.printStackTrace();
                }
            }
        }

        return category;
    }
}

