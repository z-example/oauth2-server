package org.example.oauth2.grant.code;

/**
 * Client将用户浏览器导向AuthServer的授权页面, 用户通过浏览器登录并授权
 * <p>
 * <blockquote><pre>
 *     GET /authorize?response_type=code&client_id=s6BhdRkqt3&state=xyz
 *         &redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb HTTP/1.1
 *     Host: server.example.com
 * </pre></blockquote>
 *
 * https://tools.ietf.org/html/rfc6749#page-24
 */
public class AuthorizationRequest {
    public String response_type;//必须为code
    public String client_id;
    // eg: https://developer.github.com/apps/building-oauth-apps/understanding-scopes-for-oauth-apps/#requested-scopes-and-granted-scopes
    public String scope; // OPTIONAL, 多个值使用空格分隔(%20). eg: scope=user:email%20user:follow
    public String redirect_uri; // OPTIONAL
    public String state; //RECOMMENDED
}
