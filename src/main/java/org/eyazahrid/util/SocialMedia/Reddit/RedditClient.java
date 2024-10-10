package org.eyazahrid.util.SocialMedia.Reddit;

import com.google.gson.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.*;

public class RedditClient {
    private final OkHttpClient httpClient;
    private final String accessToken;
    private final Random random;

    public RedditClient(OkHttpClient httpClient, String accessToken) {
        this.httpClient = httpClient;
        this.accessToken = accessToken;
        this.random = new Random();
    }

    public String getRandomImage(String subreddit) throws IOException {
        String[] endpoints = {"hot", "new", "top"};
        String endpoint = endpoints[random.nextInt(endpoints.length)];
        String url = "https://oauth.reddit.com/r/" + subreddit + "/" + endpoint + ".json?limit=50";

        if (random.nextBoolean()) {
            url = "https://oauth.reddit.com/r/" + subreddit + "/random.json";
        }

        System.out.println("URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + accessToken)
                .header("User-Agent", "YourAppName")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }

            String responseData = Objects.requireNonNull(response.body()).string();
            //System.out.println("Raw Response Data: " + responseData); // For debugging the raw response

            // Check if the response contains an empty 'children' list
            if (responseData.contains("\"children\": []")) {
                System.out.println("Empty response detected, retrying...");
                // Retry with a new request (recursive call or generate a new request)
                return getRandomImage(subreddit);
            }

            if (responseData.trim().isEmpty()) {
                throw new IOException("Received an empty response from Reddit API.");
            }

            JsonArray children;

            List<String> mediaUrls = new ArrayList<>();

            if (url.contains("random")) {
                // Handle random.json response
                try {
                    System.out.println("Parsing random JSON response...");
                    JsonArray arr = JsonParser.parseString(responseData).getAsJsonArray();
                    if (!arr.isEmpty()) {
                        JsonObject data = arr.get(0).getAsJsonObject().getAsJsonObject("data");
                        children = data.getAsJsonArray("children");
                    } else {
                        throw new IOException("No data found in random.json response.");
                    }
                } catch (IllegalStateException e) {
                    JsonObject obj = JsonParser.parseString(responseData).getAsJsonObject();
                    children = obj.getAsJsonObject("data").getAsJsonArray("children");
                }
            } else {
                // Handle hot, new, top responses
                JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();
                children = jsonObject.getAsJsonObject("data").getAsJsonArray("children");
            }

            // Extract valid media URLs
            for (JsonElement child : children) {
                JsonObject postData = child.getAsJsonObject().getAsJsonObject("data");
                String mediaUrl = extractMediaUrl(postData);
                if (mediaUrl != null) {
                    mediaUrls.add(mediaUrl);
                }
            }

            if (mediaUrls.isEmpty()) {
                throw new IOException("No valid media URLs found.");
            }

            // Shuffle the URLs and return a random one
            Collections.shuffle(mediaUrls);
            return mediaUrls.get(0);
        } catch (JsonSyntaxException e) {
            System.out.println("JSON Syntax Error: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to parse the Reddit response JSON.");
        }
    }

    /**
     * Extracts a media URL (image, gif, or video) from the given post data.
     * Adjust this method to your specific needs based on Reddit's post structure.
     *
     * @param postData The post's JSON data object.
     * @return The URL to the media (image or video), or null if no valid URL was found.
     */
    private String extractMediaUrl(JsonObject postData) {
        // Simplified logic: Adjust based on Reddit's JSON structure
        if (postData.has("url")) {
            return postData.get("url").getAsString();
        } else if (postData.has("media")) {
            JsonObject media = postData.getAsJsonObject("media");
            if (media.has("reddit_video")) {
                return media.getAsJsonObject("reddit_video").get("fallback_url").getAsString();
            }
        }
        return null;
    }

    /**
     * Checks if a URL is valid by performing a HEAD request.
     *
     * @param url The URL to check.
     * @return True if the URL is valid, false otherwise.
     * @throws IOException If the request fails.
     */
    public boolean isValidUrl(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .head()  // Perform a HEAD request
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    public List<String> getGalleryImages(String galleryUrl) {
        List<String> imageUrls = new ArrayList<>();
        try {
            org.jsoup.nodes.Document doc = Jsoup.connect(galleryUrl).get();

            // Log the fetched HTML to the console for inspection
           // System.out.println(doc.html());

            doc.select("a[href]").forEach(element -> {
                String url = element.attr("href");
                if (url.contains("preview.redd.it") && url.contains("format=pjpg&auto=webp")) {
                    // Unblur the URL
                    url = url.replace("&amp;", "&");
                    imageUrls.add(url);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageUrls;
    }
}


