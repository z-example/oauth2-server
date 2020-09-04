package org.example.oauth2.model;

import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * @author Zero
 * Created on 2020/9/1.
 */
public class Client {

    public static final String TYPE_CONFIDENTIAL = "confidential";
    public static final String TYPE_PUBLIC = "public";
    //  web应用程序是运行在web服务器上的机密客户机。
    //客户端凭据以及颁发给客户端的任何访问令牌都存储在web服务器上，并且不向资源所有者公开或由资源所有者访问。
    /**
     * web应用程序是运行在web服务器上的机密客户机。
     * 资源所有者通过在资源所有者使用的设备上的用户代理中呈现的HTML用户界面访问客户端。
     * 客户端凭据以及颁发给客户端的任何访问令牌都存储在web服务器上，并且不向资源所有者公开或由资源所有者访问。
     */
    public static final String TYPE_WEB_APPLICATION = "web application";
    /**
     * 基于用户代理的应用程序是一种公共客户端，其中客户端代码从web服务器下载并在资源所有者使用的设备上的用户代理（例如，web浏览器）中执行。
     * 协议数据和凭证对于资源所有者来说很容易访问（并且经常可见）。
     * 由于此类应用程序驻留在用户代理中，因此在请求授权时，它们可以无缝地使用用户代理功能。
     * <p>
     * 通俗的讲: 这是一种代码是从 Web 服务器上下载然后运行在用户设备上的 user-agent（例如浏览器）之上的 client。
     * 它没有执行代码的后端服务器，只有负责托管前端资源服务器，因此只能借助 user-agent 的能力来调用 resource server、authorization server 的接口。
     * 使用的是Implicit Grant
     */
    public static final String TYPE_USER_AGENT_BASED = "user-agent-based";
    /**
     * 比较手机APP之类的
     */
    public static final String TYPE_NATIVE_APPLICATION = "native application";

    // https://tools.ietf.org/html/rfc6749  Client Types
    // https://github.com/spring-projects/spring-security-oauth/blob/master/spring-security-oauth2/src/test/resources/schema.sql
    public String clientTypes;

    public String clientId;// Client ID, APP ID
    public String clientSecret;
    public String grantTypes;
    public String redirectUri;

    public String userId;

    public boolean verifyRedirectUri(String redirectUri) {
        if (StringUtils.isEmpty(redirectUri)) {
            return false;
        }
        try {
            URI defUri = new URI(this.redirectUri);
            URI uri = new URI(redirectUri);
            if (Objects.equals(defUri.getHost(), uri.getHost()) && Objects.equals(defUri.getPath(), uri.getPath())) {
                return true;
            }
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public boolean verifySecret(String secret) {
        return Objects.equals(this.clientSecret, secret);
    }

}
