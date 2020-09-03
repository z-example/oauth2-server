package org.example.oauth2.server;

import org.example.oauth2.AccessToken;

/**
 * @author Zero
 * Created on 2020/9/1.
 */
public interface TokenGenerator {

    AccessToken generate(String clientId, String username);

    AccessToken refresh(String clientId, String refreshToken);
    
}
