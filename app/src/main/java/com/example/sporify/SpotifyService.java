package com.example.sporify;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyService {

    public interface SpotifyCallback {
        void onResult(ArrayList<FirebaseTrack> tracks);
        void onError(String error);
    }

    public static void search(Context context, String query, SpotifyCallback callback) {
        new Thread(() -> {
            try {
                SharedPreferences prefs = context.getSharedPreferences("spotify", 0);
                String token = prefs.getString("token", null);

                if (token == null) {
                    postError(callback, "No hay token de Spotify");
                    return;
                }

                OkHttpClient client = new OkHttpClient();

                String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
                String url = "https://api.spotify.com/v1/search?q=" + encodedQuery + "&type=track&limit=10";

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + token)
                        .build();

                Response response = client.newCall(request).execute();

                if (!response.isSuccessful() || response.body() == null) {
                    postError(callback, "Error Spotify: " + response.code());
                    return;
                }

                String body = response.body().string();
                JSONObject json = new JSONObject(body);
                JSONArray items = json.getJSONObject("tracks").getJSONArray("items");

                ArrayList<FirebaseTrack> list = new ArrayList<>();

                for (int i = 0; i < items.length(); i++) {
                    JSONObject t = items.getJSONObject(i);

                    String id = t.getString("id");
                    String title = t.getString("name");
                    String artist = t.getJSONArray("artists").getJSONObject(0).getString("name");
                    String album = t.getJSONObject("album").getString("name");
                    String cover = "";

                    JSONArray images = t.getJSONObject("album").getJSONArray("images");
                    if (images.length() > 0) {
                        cover = images.getJSONObject(0).getString("url");
                    }

                    list.add(new FirebaseTrack(id, title, artist, album, cover, "spotify"));
                }

                new Handler(Looper.getMainLooper()).post(() -> callback.onResult(list));

            } catch (Exception e) {
                postError(callback, e.getMessage());
            }
        }).start();
    }

    private static void postError(SpotifyCallback callback, String error) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onError(error));
    }
}