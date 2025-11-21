package models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Platform {
    SPOTIFY("Spotify", "https://accounts.spotify.com/en/password-reset"),
    AMAZON("Amazon", "https://www.amazon.com/ap/forgotpassword"),
    NETFLIX("Netflix", "https://www.netflix.com/password"),
    INSTAGRAM("Instagram", "https://www.instagram.com/accounts/password/reset/"),
    TWITTER("Twitter", "https://twitter.com/account/begin_password_reset");

    private final String displayName;
    private final String passwordResetUrl;
}
