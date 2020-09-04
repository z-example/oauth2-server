package org.example.oauth2.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.core.security.BasicAuthCredentials;
import io.javalin.plugin.json.JavalinJson;
import org.apache.commons.lang3.StringUtils;
import org.example.oauth2.AccessToken;
import org.example.oauth2.ErrorResponse;
import org.example.oauth2.model.AuthorizationCode;
import org.example.oauth2.model.Client;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zero
 * Created on 2020/8/31.
 */
public class AuthServer {


    public static void main(String[] args) {
        HttpClient httpClient = HttpClient.newHttpClient();

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

        // 认证服务器: 用户登录页面
        app.get("/login", ctx -> {
            String redirectUri = ctx.queryParam("redirect_uri");
            String clientId = ctx.queryParam("client_id");//APPID
            Model model = new Model();
            Client client = service.getClient(clientId);
            if (client == null) {
                model.setMessage("对不起, 你无权限查看该页面: 无效的clientId (appid)");
                ctx.render("/error.html", model);
                return;
            }
            // 注意: 授权码授权时, redirectUri是可选项
            if (StringUtils.isNotEmpty(redirectUri) && !client.verifyRedirectUri(redirectUri)) {
                model.setMessage("对不起, 你无权限查看该页面: url参数不合法");
                ctx.render("/error.html", model);
                return;
            }
            System.out.println(ctx.queryString());//eg: client_id=000000&return_uri=http%3A%2F%2Flocalhost%3A7000%2Fcallback&state=xyz&scope=all
            // 注意要保留所有参数信息
            ctx.sessionAttribute("queryString", ctx.queryString());
            ctx.render("/login.html");//html默认使用Thymeleaf引擎
        });

        // 认证服务器: 用户登录验证
        app.post("/login", ctx -> {
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");
            String queryString = ctx.sessionAttribute("queryString");
            // 注意: 这些需要将参数值编码
            if (service.verifyUser(username, password)) {
                ctx.sessionAttribute("username", username);
                // 将用户导向授权页面
                ctx.redirect("/auth?" + queryString);
            } else {
//                ctx.status(400).json(new ErrorResponse("invalid_username_or_password"));
                // 将用户导向登录页面
                ctx.redirect("/login?" + queryString);
            }
            ctx.sessionAttribute("queryString", "");
        });

        // 认证服务器: 用户授权页面
        app.get("/auth", ctx -> {
            ctx.sessionAttribute("queryString", ctx.queryString());
            ctx.render("/auth.html");
        });

        // 认证服务器: 处理用户授权页面授权请求
        app.post("/auth", ctx -> {
            String queryString = ctx.sessionAttribute("queryString");
            ctx.sessionAttribute("auth", ctx.formParam("auth"));
            ctx.redirect("/authorize?" + queryString);// GET /authorize?...
            ctx.sessionAttribute("queryString", "");
        });

        // 认证服务器OAuth2规范: 处理获取用户授权请求
        app.get("/authorize", ctx -> {
            String loginUsername = ctx.sessionAttribute("username");
            String client_id = ctx.queryParam("client_id");//必选项
            String response_type = ctx.queryParam("response_type");//必选项
            String redirect_uri = ctx.queryParam("redirect_uri");//可选项 (注册APP时, 已经填写了uri)
            String state = ctx.queryParam("state");
            String scope = ctx.queryParam("scope", "all");//all
            // 必须参数, 先验证
            Client client = service.getClient(client_id);
            if (client == null || !StringUtils.equalsAny(response_type, "token", "code")) {
                ctx.json(ErrorResponse.INVALID_REQUEST);
                return;
            }
            if (redirect_uri == null) {
                redirect_uri = client.redirectUri;
            } else if (!client.verifyRedirectUri(redirect_uri)) { // 验证redirect_uri
                ctx.json(ErrorResponse.INVALID_REQUEST);
                return;
            }

            // 如果用户还未登录认证服务器, 则重定向到登录页面
            if (StringUtils.isEmpty(loginUsername)) {
                System.out.println("用户未登录认证服务器, 将用户导向认证服务器登录页面");
                // 注意: 将所有参数也传递给/login
                ctx.redirect("/login?" + ctx.queryString());
                return;
            }
            // 如果为经过授权步骤, 则跳转到授权页面
            String auth = ctx.sessionAttribute("auth");
            if (!"allow".equals(auth)) {
                String url = URLEncoder.encode(redirect_uri);
                ctx.redirect("/auth?" + ctx.queryString());
                return;
            }
            // 注意: 这里需要删除授权标记, 再次获取授权码时依旧需要用户授权
            ctx.sessionAttribute("auth", null);
            // 简化授权
            if ("token".equals(response_type)) {
                // http://localhost:7000/authorize?response_type=token&client_id=000000&state=xyz&redirect_uri=http%3A%2F%2Flocalhost%3A7000%2Fcallback
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
                //http://localhost:7000/authorize?client_id=000000&response_type=code&scope=user%3Aabout_me
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

        });

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
                    ctx.json(ErrorResponse.ERROR_UNAUTHORIZED_CLIENT);
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
                // curl -H 'Authorization: MDAwMDAwOjAwMDAwMA==' -d "grant_type=authorization_code&code=hvAWMVBeuenxqrjoueNiVj&redirect_uri=http://localhost:7000/callback" -X POST http://localhost:7000/token
                String code = ctx.formParam("code");//必须
                String redirect_uri = ctx.formParam("redirect_uri");//可选
                Client client = service.getClient(bac.getUsername());
                if (client == null) {
                    ctx.json(ErrorResponse.ERROR_UNAUTHORIZED_CLIENT);
                    return;
                }
                if (StringUtils.isNotEmpty(redirect_uri)) {
                    // 必须验证redirect_uri, 如果验证不通过, 不做任何处理
                    if (!client.verifyRedirectUri(redirect_uri)) {
                        ctx.json(ErrorResponse.ERROR_INVALID_REQUEST);
                        return;
                    }
                }
                // 验证Secret
                if (!Objects.equals(client.clientSecret, bac.getPassword())) {
                    ctx.json(ErrorResponse.ERROR_UNAUTHORIZED_CLIENT);
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
/*                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url.toString()))
                            .timeout(Duration.ofSeconds(10))
                            .POST(HttpRequest.BodyPublishers.noBody())
                            .build();
                    httpClient.send(request, HttpResponse.BodyHandlers.discarding());*/
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
