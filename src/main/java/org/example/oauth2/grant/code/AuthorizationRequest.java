package org.example.oauth2.grant.code;

/**
 * <pre>
 *     GET /authorize?response_type=code&client_id=s6BhdRkqt3&state=xyz
 *         &redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb HTTP/1.1
 *     Host: server.example.com
 * </pre>
 * https://tools.ietf.org/html/rfc6749#page-24
 */
public class AuthorizationRequest {
    public String response_type;//code, token
    public String client_id;
    public String scope; // OPTIONAL
    public String redirect_uri; // OPTIONAL
    public String state; //RECOMMENDED
}
