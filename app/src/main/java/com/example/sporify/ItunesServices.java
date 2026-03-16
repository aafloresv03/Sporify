package com.example.sporify;

import android.content.Context;
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

public class ItunesServices {

    public interface ItunesCallback {
        void onResult(ArrayList<FirebaseTrack> tracks);
        void onError(String error);
    }

    public static void search(Context context, String query, ItunesCallback callback) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
                String url = "https://itunes.apple.com/search?term=" + encodedQuery + "&entity=song&limit=10";

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();

                if (!response.isSuccessful() || response.body() == null) {
                    postError(callback, "Error iTunes: " + response.code());
                    return;
                }

                String body = response.body().string();
                JSONObject json = new JSONObject(body);
                JSONArray results = json.getJSONArray("results");

                ArrayList<FirebaseTrack> tracks = new ArrayList<>();

                for (int i = 0; i < results.length(); i++) {
                    JSONObject item = results.getJSONObject(i);

                    String trackId = item.optString("trackId", "");
                    String title = item.optString("trackName", "Sin título");
                    String artist = item.optString("artistName", "Sin artista");
                    String album = item.optString("collectionName", "Sin álbum");
                    String coverUrl = item.optString("artworkUrl100", "");
                    String previewUrl = item.optString("previewUrl", "");

                    tracks.add(new FirebaseTrack(
                            trackId,
                            title,
                            artist,
                            album,
                            coverUrl,
                            previewUrl
                    ));
                }

                new Handler(Looper.getMainLooper()).post(() -> callback.onResult(tracks));

            } catch (Exception e) {
                postError(callback, e.getMessage());
            }
        }).start();
    }

    private static void postError(ItunesCallback callback, String error) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onError(error));
    }
}