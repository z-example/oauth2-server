package org.example.oauth2.store;

import org.example.oauth2.AccessToken;

/**
 * @author Zero
 * Created on 2020/9/1.
 */
public interface TokenStore {

    public static TokenStore inMemoryStore() {
        return new InMemoryTokenSotre();
    }

    public void save(AccessToken token);

    public AccessToken getAccessToken(String accessToken);

    public AccessToken getByRefreshToken(String clientId, String refreshToken);

    public void updateByRefreshToken(String oldRefreshToken, AccessToken newAccessToken);

}
