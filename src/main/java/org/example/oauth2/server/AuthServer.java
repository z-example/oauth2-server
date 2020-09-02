package org.example.oauth2.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJson;
import io.javalin.plugin.rendering.JavalinRenderer;
import org.example.oauth2.AccessToken;
import org.example.oauth2.BaseAuthorization;
import org.example.oauth2.ErrorResponse;
import org.example.oauth2.Helper;
import org.example.oauth2.model.ClientDetails;
import org.example.oauth2.store.ClientStore;

import java.util.Objects;

/**
 * @author Zero
 * Created on 2020/8/31.
 */
public class AuthServer {

    private PasswordGrantHandler passwordGrantHandler;

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);
        Gson gson = new GsonBuilder().create();
        JavalinJson.setFromJsonMapper(gson::fromJson);
        JavalinJson.setToJsonMapper(gson::toJson);
//        // 使用了自定义渲染引擎
//        JavalinRenderer.register((filePath, model, context) -> "Hi", ".html");
        ClientStore clientStore = ClientStore.getClientStore();
        clientStore.save(new ClientDetails() {{
            clientId = "000000";
            clientSecret = "000000";
            grantTypes = "token";
            redirectUri = "http://localhost:7000/callback";
        }});
        app.get("/", ctx -> {
            // Object user = ctx.attribute("token-to-user");
            ctx.render("/index.html");//html默认使用Thymeleaf引擎
        });
        app.get("/callback", ctx -> {
            ctx.json(ctx.fullUrl());
        });

        app.get("/authorize", ctx -> {
            String response_type = ctx.queryParam("response_type");//必选项
            String client_id = ctx.queryParam("client_id");//必选项
            String redirect_uri = ctx.queryParam("redirect_uri");//可选项 (注册APP时, 已经填写了uri)
            String state = ctx.queryParam("state");
            String scope = ctx.queryParam("scope", "all");//all

            // 简化授权
            if ("token".equals(response_type)) {
                ClientDetails clientDetails = clientStore.getClientDetails(client_id);
                if (clientDetails == null) {
                    ctx.json(ErrorResponse.INVALID_CLIENT);
                    return;
                }
                // TODO 验证 scope
                if (redirect_uri == null) {
                    redirect_uri = clientDetails.redirectUri;
                }
                // TODO 生成token 并 存储
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append(redirect_uri);
                urlBuilder.append("#access_token=").append("2YotnFZFEjr1zCsicMWpAA");
                urlBuilder.append("&token_type=").append("example");
                urlBuilder.append("&expires_in=").append("3600");
                if (state != null) {
                    urlBuilder.append("&state=").append(state);
                }
                ctx.redirect(urlBuilder.toString(), 302);
                return;
            }

            // 授权码授权1: 获取授权码
            if ("code".equals(response_type)) {
                //http://localhost:7000/authorize?client_id=000000&response_type=code
                ClientDetails clientDetails = clientStore.getClientDetails(client_id);
                if (clientDetails == null) {
                    ctx.json(ErrorResponse.INVALID_CLIENT);
                    return;
                }
                if (redirect_uri == null) {
                    redirect_uri = clientDetails.redirectUri;
                }
                // TODO 生成授权码 并 存储
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append(redirect_uri);
                urlBuilder.append(redirect_uri.contains("?") ? "&" : "?");
                urlBuilder.append("code=").append("dsfwewexzas");
                if (state != null) {
                    urlBuilder.append("&state=").append(state);
                }
                ctx.redirect(urlBuilder.toString(), 302);
                return;
            }

        });

        app.post("/token ", ctx -> {
            String grant_type = ctx.formParam("grant_type");
            String scope = ctx.formParam("scope");//OPTIONAL
            // 密码凭证授权
            if ("password".equals(grant_type)) {
                String username = ctx.formParam("username");
                String password = ctx.formParam("password");
                String authorization = ctx.header("Authorization");
                BaseAuthorization ba = Helper.readAuthorization(authorization);
                ClientDetails clientDetails = clientStore.getClientDetails(ba.username);
                if (clientDetails == null) {
                    ctx.json(ErrorResponse.INVALID_CLIENT);
                    return;
                }
                if (Objects.equals(clientDetails.clientSecret, ba.password)) {
                    ctx.json(ErrorResponse.INVALID_REQUEST);
                    return;
                }
                // TODO 查询数据库, 并验证用户名密码
                if (!"test".equals(username) || !"test".equals(password)) {
                    ctx.json(ErrorResponse.INVALID_GRANT);
                    return;
                }
                //TODO 生成AccessToken
                AccessToken token = new AccessToken();
                ctx.header("Cache-Control", "no-store");
                ctx.header("Pragma", "no-cache");
                ctx.json(token);
                return;
            }

            // 授权码授权2 : 通过授权码获取token
            if ("authorization_code".equals(grant_type)) {
                String code = ctx.formParam("code");//必须
                String redirect_uri = ctx.formParam("redirect_uri");//可选
                //TODO 验证授权码
                //TODO 生成AccessToken并存储
                AccessToken token = new AccessToken();
                ctx.header("Cache-Control", "no-store");
                ctx.header("Pragma", "no-cache");
                ctx.json(token);
            }

            // 客户端凭证授权, 这玩意其实并没有用户授权概念, 直接获取token
            if ("client_credentials".equals(grant_type)) {
                String authorization = ctx.header("Authorization");
                BaseAuthorization ba = Helper.readAuthorization(authorization);
                ClientDetails clientDetails = clientStore.getClientDetails(ba.username);
                if (clientDetails == null) {
                    ctx.json(ErrorResponse.INVALID_CLIENT);
                    return;
                }
                if (Objects.equals(clientDetails.clientSecret, ba.password)) {
                    ctx.json(ErrorResponse.INVALID_REQUEST);
                    return;
                }
                //TODO 生成AccessToken
                AccessToken token = new AccessToken();
                ctx.header("Cache-Control", "no-store");
                ctx.header("Pragma", "no-cache");
                ctx.json(token);
            }
            if ("refresh_token".equals(grant_type)) {
                String refresh_token = ctx.formParam("refresh_token");

            }
            ctx.json(ErrorResponse.INVALID_REQUEST);
            return;
        });

    }
}
