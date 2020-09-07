package org.example.oauth2.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.core.security.BasicAuthCredentials;
import io.javalin.http.Handler;
import io.javalin.plugin.json.JavalinJson;
import org.apache.commons.lang3.StringUtils;
import org.example.oauth2.AccessToken;
import org.example.oauth2.ErrorResponse;
import org.example.oauth2.model.AuthorizationCode;
import org.example.oauth2.model.Client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.example.oauth2.ErrorResponse.ERROR_INVALID_REQUEST;

/**
 * @author Zero
 * Created on 2020/8/31.
 */
public class AuthServer {


    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        Gson gson = new GsonBuilder().create();
        JavalinJson.setFromJsonMapper(gson::fromJson);
        JavalinJson.setToJsonMapper(gson::toJson);
//        // 使用了自定义渲染引擎
//        JavalinRenderer.register((filePath, model, context) -> "Hi", ".html");

        AuthService service = new ExampleAuthService();

        // 模拟第三方应用服务器在认证服务器注册的应用中的return_uri
        app.get("/callback", ctx -> {
            Map<String, Object> map = new HashMap<>();
            map.put("url", ctx.fullUrl());
            map.put("body", ctx.body());
            ctx.json(map);
        });


        Handler authorizeHandler = ctx -> {
            String client_id = ctx.queryParam("client_id");//必选项
            String response_type = ctx.queryParam("response_type");//必选项
            String redirect_uri = ctx.queryParam("redirect_uri");//可选项 (注册APP时, 已经填写了uri)
            String state = ctx.queryParam("state");
            String scope = ctx.queryParam("scope", "all");//all
            String withLogin = ctx.queryParam("with_login");//登录并授权
            // 必须参数, 先验证
            Client client = service.getClient(client_id);
            if (client == null) {
                ctx.json(ErrorResponse.INVALID_CLIENT);
                return;
            }
            if (!StringUtils.equalsAny(response_type, "token", "code")) {
                ctx.json(ErrorResponse.INVALID_REQUEST);
                return;
            }
            if (redirect_uri == null) {
                redirect_uri = client.redirectUri;
            } else if (!client.verifyRedirectUri(redirect_uri)) { // 验证redirect_uri
                ctx.json(ErrorResponse.INVALID_REQUEST);
                return;
            }
            if (!service.verifyScope(client_id, scope)) {
                ctx.json(ErrorResponse.INVALID_SCOPE);
                return;
            }
            // 登录并授权
            String username = ctx.formParam("username");
            if (StringUtils.isNotEmpty(username)) {
                String password = ctx.formParam("password");
                if (service.verifyUser(username, password)) {
                    ctx.sessionAttribute("username", username);
                } else {
                    ctx.render("/authorize.html");
                    return;
                }
            }
            String loginUsername = ctx.sessionAttribute("username");
            // 如果用户还未登录认证服务器(或者申请的权限不是最基本权限), 则重定向到登录页面
            if (StringUtils.isEmpty(loginUsername)) {
                ctx.sessionAttribute("queryString", ctx.queryString());
                // 用户未登录认证服务器, 用户需要登录认证服务器
                // 方案一: 跳转到登录页面, 登录后再调整到授权页面(注意将所有参数也传递给/login)
//                ctx.redirect("/login?" + ctx.queryString());
                // 方案二: 直接渲染登录页, 不进行跳转
                ctx.render("/authorize.html");
                //
//              ctx.render("/login_authorize.html");
                return;
            } else {// 如果用户已经登录, 渲染授权页面
                if ("user:about_me".equals(scope)) {
                    //pass
                } else {
                    // 如果认真服务器上授权有多个, 并且是可选的话, 那么还是需要用户勾选并确认授权的
//                    ctx.render("/authorize.html");
//                    return;
                }
            }
            //scope是Client申请的权限, 这是用户授权的项
            List<String> scope_items = ctx.formParams("scope_item");
            if (scope_items.isEmpty()) {
                // 返回错误, 或者设定最基本的权限作为默认值
//                ctx.json(ErrorResponse.INVALID_SCOPE);
//                return;
                scope = "user:about_me";
            } else {
                scope = StringUtils.join(scope_items, ',');
            }
            // 简化授权
            if ("token".equals(response_type)) {
                // http://localhost:7000/authorize?response_type=token&client_id=100000&state=xyz&redirect_uri=http%3A%2F%2Flocalhost%3A7000%2Fcallback
                StringBuilder urlBuilder = new StringBuilder(redirect_uri);
                // 验证 scope
                if (!service.verifyScope(client.clientId, scope)) {
                    urlBuilder.append("#error=invalid_scope");
                    if (StringUtils.isNotEmpty(state)) {
                        urlBuilder.append("&state=").append(state);
                    }
                    ctx.redirect(urlBuilder.toString(), 302);
                    return;
                }
                AccessToken token = service.generateToken(client_id, loginUsername);
                urlBuilder.append("#access_token=").append(token.access_token);
                urlBuilder.append("&token_type=").append(token.token_type);
                urlBuilder.append("&expires_in=").append(token.expires_in);
                if (StringUtils.isNotEmpty(state)) {
                    urlBuilder.append("&state=").append(state);
                }
                ctx.redirect(urlBuilder.toString(), 302);
                return;
            }

            // 授权码授权1: 获取授权码
            if ("code".equals(response_type)) {
                //http://localhost:7000/authorize?client_id=100000&response_type=code&scope=user%3Aabout_me
                // 生成并存储授权码, 授权码是一次性有效性的
                AuthorizationCode code = service.generateCode(client_id, scope, loginUsername);
                StringBuilder urlBuilder = new StringBuilder(redirect_uri);
                urlBuilder.append(redirect_uri.contains("?") ? "&" : "?");
                urlBuilder.append("code=").append(code.code);
                if (state != null) {
                    urlBuilder.append("&state=").append(state);
                }
                ctx.redirect(urlBuilder.toString(), 302);
                return;
            }
            ctx.json(ErrorResponse.INVALID_REQUEST);
        };
        // 认证服务器OAuth2规范: 处理获取用户授权请求
        app.get("/authorize", authorizeHandler);
        // 认证服务器: 处理用户授权页面授权请求
        app.post("/authorize", authorizeHandler);

        // 认证服务器OAuth2规范: 申请令牌请求
        app.post("/token", ctx -> {
            String grant_type = ctx.formParam("grant_type");
            String authorization = ctx.header("Authorization");// 必选项
            String scope = ctx.formParam("scope");//OPTIONAL
            if (StringUtils.isEmpty(authorization)) {
                ctx.json(ErrorResponse.INVALID_GRANT);
                return;
            }
            BasicAuthCredentials bac = ctx.basicAuthCredentials();
            // 密码凭证授权 (返回JSON)
            if ("password".equals(grant_type)) {
                String username = ctx.formParam("username");
                String password = ctx.formParam("password");
                Client client = service.getClient(bac.getUsername());
                if (client == null) {
                    ctx.json(ErrorResponse.INVALID_CLIENT);
                    return;
                }
                // 验证Secret
                if (!client.verifySecret(bac.getPassword())) {
                    ctx.json(ErrorResponse.UNAUTHORIZED_CLIENT);
                    return;
                }
                // 查询数据库, 并验证用户名密码
                if (!service.verifyUser(username, password)) {
                    ctx.json(ErrorResponse.INVALID_GRANT);
                    return;
                }
                // 生成AccessToken
                AccessToken token = service.generateToken(client.clientId, username);
                ctx.header("Cache-Control", "no-store");
                ctx.header("Pragma", "no-cache");
                ctx.json(token);
                return;
            }

            // 授权码授权2 : 通过授权码获取token (返回JSON或者通过redirect_uri传递参数)
            if ("authorization_code".equals(grant_type)) {
                // curl -H 'Authorization: MTAwMDAwOmh2QVdNVkJldWVueHFyam91ZU5pVmo=' -d "grant_type=authorization_code&code=hvAWMVBeuenxqrjoueNiVj&redirect_uri=http://localhost:7000/callback" -X POST http://localhost:7000/token
                String code = ctx.formParam("code");//必须
                String redirect_uri = ctx.formParam("redirect_uri");//可选
                Client client = service.getClient(bac.getUsername());
                if (client == null) {
                    ctx.json(ErrorResponse.UNAUTHORIZED_CLIENT);
                    return;
                }
                if (StringUtils.isNotEmpty(redirect_uri)) {
                    // 必须验证redirect_uri, 如果验证不通过, 不做任何处理
                    if (!client.verifyRedirectUri(redirect_uri)) {
                        ctx.json(new ErrorResponse(ERROR_INVALID_REQUEST, "URI不匹配"));
                        return;
                    }
                }
                // 验证Secret
                if (!Objects.equals(client.clientSecret, bac.getPassword())) {
                    ctx.json(ErrorResponse.UNAUTHORIZED_CLIENT);
                    return;
                }
                // 验证授权码
                AuthorizationCode ac = service.verifyCode(client.clientId, code);
                if (ac == null) {
                    if (StringUtils.isEmpty(redirect_uri)) {
                        ctx.json(ErrorResponse.INVALID_GRANT);
                    } else {
                        StringBuilder url = new StringBuilder(redirect_uri);
                        url.append("?error=invalid_grant");
                        ctx.redirect(url.toString(), 302);
                    }
                    return;
                }
                // 生成AccessToken并存储
                AccessToken token = service.generateToken(client.clientId, ac.username);
                if (StringUtils.isEmpty(redirect_uri)) {
                    ctx.header("Cache-Control", "no-store");
                    ctx.header("Pragma", "no-cache");
                    ctx.json(token);
                } else {
                    StringBuilder url = new StringBuilder(redirect_uri);
                    url.append("?access_token=").append(token.access_token);
                    url.append("&token_type=").append(token.token_type);
                    url.append("&expires_in=").append(token.expires_in);
                    url.append("&refresh_token=").append(token.refresh_token);
                    ctx.redirect(url.toString(), 302);
                }
                //授权码使用后销毁
                service.destroyCode(ac);
                return;
            }

            // 客户端凭证授权, 这玩意其实并没有用户授权概念, 直接获取token
            if ("client_credentials".equals(grant_type)) {
                Client client = service.getClient(bac.getUsername());
                if (client == null) {
                    ctx.json(ErrorResponse.INVALID_CLIENT);
                    return;
                }
                if (!client.verifySecret(bac.getPassword())) {
                    ctx.json(ErrorResponse.INVALID_REQUEST);
                    return;
                }
                AccessToken token = service.generateToken(client.clientId, null);
                ctx.header("Cache-Control", "no-store");
                ctx.header("Pragma", "no-cache");
                ctx.json(token);
                return;
            }

            // 刷新令牌
            if ("refresh_token".equals(grant_type)) {
                String refresh_token = ctx.formParam("refresh_token");
                if (StringUtils.isEmpty(refresh_token)) {
                    ctx.json(ErrorResponse.INVALID_REQUEST);
                    return;
                }
                AccessToken token = service.refreshToken(bac.getUsername(), refresh_token);
                if (token == null) {
                    ctx.json(ErrorResponse.INVALID_REQUEST);
                    return;
                }
                ctx.json(token);
                return;
            }
            ctx.json(ErrorResponse.INVALID_REQUEST);
        });

    }
}
