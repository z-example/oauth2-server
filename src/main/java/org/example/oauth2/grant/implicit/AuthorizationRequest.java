package org.example.oauth2.grant.implicit;

/**
 * <pre>
 *     GET /authorize?response_type=token&client_id=s6BhdRkqt3&state=xyz
 *         &redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb HTTP/1.1
 *     Host: server.example.com
 * </pre>
 * <p>
 * https://tools.ietf.org/html/rfc6749#page-31
 * <p>
 * 注意: 这里连client_secret都是不需要的
 */
public class AuthorizationRequest {
    // 特征: response_type=token
    // application/x-www-form-urlencoded
    public String response_type;//token
    public String client_id;
    public String scope; // OPTIONAL
    public String redirect_uri; // OPTIONAL
    public String state; //RECOMMENDED
}
