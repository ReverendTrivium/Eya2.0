package org.eyazahrid.Commands.Utility;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.eyazahrid.Eyazahrid;
import org.eyazahrid.Commands.Command;
import org.eyazahrid.Commands.Category;


public class Clear extends Command {

    public Clear(Eyazahrid bot) {
        super(bot);
        this.name = "clear";
        this.description = "Clears all messages in the channel or all messages from a specific user in the channel.";
        this.category = Category.STAFF;
        this.permission = Permission.KICK_MEMBERS;
        this.args.add(new OptionData(OptionType.USER, "user", "The user whose messages you want to delete"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        TextChannel channel = event.getChannel().asTextChannel();
        Member member = event.getOption("user") != null ? event.getOption("user").getAsMember() : null;

        if (member == null) {
            // Clear all messages in the channel
            event.reply("Clearing all messages in this channel...").setEphemeral(true).queue();
            clearAllMessagesInChannel(channel);
        } else {
            // Clear all messages from the specified user in the channel
            event.reply("Clearing all messages from " + member.getEffectiveName() + " in this channel...").setEphemeral(true).queue();
            clearMessagesFromUserInChannel(channel, member);
        }
    }

    private void clearAllMessagesInChannel(TextChannel channel) {
        channel.getIterableHistory().queue(messages -> {
            for (Message message : messages) {
                message.delete().queue();
            }
        });
    }

    private void clearMessagesFromUserInChannel(TextChannel channel, Member member) {
        channel.getIterableHistory().queue(messages -> {
            for (Message message : messages) {
                if (message.getAuthor().equals(member.getUser())) {
                    message.delete().queue();
                }
            }
        });
    }
}
