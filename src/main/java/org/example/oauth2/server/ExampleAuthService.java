package org.example.oauth2.server;

import org.apache.commons.lang3.RandomStringUtils;
import org.example.oauth2.AccessToken;
import org.example.oauth2.model.AuthorizationCode;
import org.example.oauth2.model.Client;
import org.example.oauth2.store.ClientStore;
import org.example.oauth2.store.CodeStore;
import org.example.oauth2.store.TokenStore;

import java.util.Date;

/**
 * @author Zero
 * Created on 2020/9/3.
 */
public class ExampleAuthService implements AuthService {

    // 以下三个接口需要自己实现, 以持久化到数据库中
    private static final CodeStore codeStore = CodeStore.inMemoryStore();
    private static final TokenStore tokenStore = TokenStore.inMemoryStore();
    private static final ClientStore clientStore = ClientStore.inMemoryStore();

    public ExampleAuthService() {
        clientStore.save(new Client() {{
            clientId = "100000";
            clientSecret = "hvAWMVBeuenxqrjoueNiVj";
            grantTypes = "token";
            redirectUri = "http://localhost:7000/callback";
        }});
    }

    @Override
    public Client getClient(String clientId) {
        return clientStore.getClient(clientId);
    }

    @Override
    public boolean verifyUser(String username, String password) {
        // TODO 这里需要自己实现, 比如查询数据库验证用户
        return true;
    }

    @Override
    public AuthorizationCode generateCode(String clientId, String scope, String username) {
        AuthorizationCode code = new AuthorizationCode();
        code.code = RandomStringUtils.randomAlphabetic(22);
        code.client_id = clientId;
        code.scope = scope;
        code.created_at = new Date();
        code.username = username;//TODO 登录认证服务器时, 会创建session
        codeStore.save(code);
        return code;
    }

    @Override
    public AuthorizationCode verifyCode(String clientId, String code) {
        return codeStore.getCode(clientId, code);
    }

    @Override
    public boolean verifyScope(String clientId, String scope) {
        // TODO 这里需要自己实现
        return true;
    }

    @Override
    public void destroyCode(AuthorizationCode code) {
        codeStore.delete(code);
    }

    @Override
    public AccessToken generateToken(String clientId, String username) {
        AccessToken token = new AccessToken();
        token.access_token = RandomStringUtils.randomAlphabetic(22);
        token.refresh_token = RandomStringUtils.randomAlphabetic(22);
        token.expires_in = 3600;
        token.token_type = "example";
        tokenStore.save(token);
        return token;
    }

    @Override
    public AccessToken refreshToken(String clientId, String refreshToken) {
        AccessToken token = tokenStore.getByRefreshToken(clientId, refreshToken);
        String old_refresh_token = token.refresh_token;
        token.access_token = RandomStringUtils.randomAlphabetic(16);
        token.refresh_token = RandomStringUtils.randomAlphabetic(16);
        tokenStore.updateByRefreshToken(old_refresh_token, token);
        return token;
    }

}
