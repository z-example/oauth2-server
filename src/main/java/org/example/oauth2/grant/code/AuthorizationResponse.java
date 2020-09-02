package org.example.oauth2.grant.code;

/**
 * <pre>
 * HTTP/1.1 302 Found
 * Location: https://client.example.com/cb?code=SplxlOBeZQQYbYS6WxSbIA&state=xyz
 * </pre>
 */
public class AuthorizationResponse {
    public String code;
    public String state;
}
