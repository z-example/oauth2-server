package org.example.oauth2.store;

import org.example.oauth2.AccessToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InMemoryTokenSotre implements TokenStore {
    private final Map<String, AccessToken> tokents = new HashMap<>();

    @Override
    public void save(AccessToken accessToken) {
        tokents.put(accessToken.access_token, accessToken);
    }

    @Override
    public AccessToken getAccessToken(String token) {
        return tokents.get(token);
    }

    @Override
    public AccessToken getByRefreshToken(String clientId, String refreshToken) {
        for (AccessToken token : tokents.values()) {
            if (Objects.equals(clientId, token.clientId) &&
                    Objects.equals(refreshToken, token.refresh_token)) {
                return token;
            }
        }
        return null;
    }

    @Override
    public void updateByRefreshToken(String oldRefreshToken, AccessToken newAccessToken) {
        for (AccessToken token : tokents.values()) {
            if (Objects.equals(oldRefreshToken, token.refresh_token)) {
                token.access_token = newAccessToken.access_token;
                token.refresh_token = newAccessToken.refresh_token;
            }
        }
    }
}