package org.eyazahrid.Commands;

/**
 * Category that represents a group of similar commands.
 * Each category has a name and an emoji.
 *
 * @author Derrick Eberlein
 */
public enum Category {
    STAFF(":computer:", "Staff"),
    COLOR(":rainbow:", "Color"),
    MUSIC(":musical_note:", "Music"),
    FUN(":smile:", "Fun"),
    AUTOMATION(":gear:", "Automation"),
    UTILITY(":tools:", "Utility"),
    GREETINGS(":wave:", "Greetings"),
    SUGGESTIONS(":thought_balloon:", "Suggestions");

    public final String emoji;
    public final String name;

    Category(String emoji, String name) {
        this.emoji = emoji;
        this.name = name;
    }
}
