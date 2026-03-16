package com.example.sporify;

import android.app.Activity;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class SpotifyManager {

    public static final String CLIENT_ID = "e4d65bed8c53494b9b4718fcdd6a0c1e";
    public static final String REDIRECT_URI = "sporify://callback";
    public static final int REQUEST_CODE = 1337;

    public static void login(Activity activity) {

        AuthorizationRequest request =
                new AuthorizationRequest.Builder(
                        CLIENT_ID,
                        AuthorizationResponse.Type.TOKEN,
                        REDIRECT_URI
                )
                        .setScopes(new String[]{"user-read-private"})
                        .setShowDialog(true)
                        .build();

        AuthorizationClient.openLoginActivity(
                activity,
                REQUEST_CODE,
                request
        );
    }
}