package org.example.oauth2.store;

import org.example.oauth2.AccessToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zero
 * Created on 2020/9/1.
 */
public abstract class TokenStore {


    private static TokenStore tokenStore = new InMemoryTokenSotre();

    public static void setTokenStore(TokenStore store) {
        tokenStore = store;
    }

    public static TokenStore getInstance() {
        return tokenStore;
    }

    public void save(AccessToken token) {
        tokenStore.save(token);
    }

    public AccessToken getAccessToken(String accessToken) {
        return tokenStore.getAccessToken(accessToken);
    }

    public AccessToken refreshToken(String refreshToken) {
        return tokenStore.refreshToken(refreshToken);
    }

    public static class InMemoryTokenSotre extends TokenStore {
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
        public AccessToken refreshToken(String refreshToken) {
            for (AccessToken token : tokents.values()) {
                if (Objects.equals(refreshToken, token.refresh_token)) {
                    // 重新生成access_token和refresh_token, 并重置expires_in
                    return token;
                }
            }
            return null;
        }
    }

}
