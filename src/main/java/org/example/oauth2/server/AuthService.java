package org.example.oauth2.server;

import org.example.oauth2.AccessToken;
import org.example.oauth2.model.AuthorizationCode;
import org.example.oauth2.model.Client;
import org.example.oauth2.store.ClientStore;
import org.example.oauth2.store.CodeStore;
import org.example.oauth2.store.TokenStore;

public interface AuthService {

    Client getClient(String clientId);

    boolean verifyUser(String username, String password);

    AuthorizationCode generateCode(String clientId, String scope, String username);

    AuthorizationCode verifyCode(String clientId, String code);

    void destroyCode(AuthorizationCode code);

    boolean verifyScope(String clientId, String scope);

    AccessToken generateToken(String clientId, String username);

    AccessToken refreshToken(String clientId, String refreshToken);


}
