package org.eyazahrid.Commands.Fun;

import com.google.gson.JsonParser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.eyazahrid.Commands.Category;
import org.eyazahrid.Commands.Command;
import com.google.gson.JsonObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.eyazahrid.Eyazahrid;

import java.io.IOException;

/**
 * Command that generates a joke from a joke API.
 *
 * @author Derrick Eberlein
 */
public class JokeCommand extends Command {

    public JokeCommand(Eyazahrid bot) {
        super(bot);
        this.name = "joke";
        this.description = "Get a random joke.";
        this.category = Category.FUN;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        OkHttpClient client = bot.httpClient;

        String url = "https://official-joke-api.appspot.com/random_joke";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                event.getHook().sendMessage("Failed to fetch a joke! Please try again later.").queue();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    event.getHook().sendMessage("Failed to fetch a joke! Please try again later.").queue();
                    return;
                }

                String responseData = response.body().string();
                JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();
                String setup = jsonObject.get("setup").getAsString();
                String punchline = jsonObject.get("punchline").getAsString();

                event.getHook().sendMessage(setup + "\n" + punchline).queue();
            }
        });
    }
}
