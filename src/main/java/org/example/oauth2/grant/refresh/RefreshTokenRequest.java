package org.example.oauth2.grant.refresh;

/**
 * Client通过自编程调用API获取Token
 *
 * <pre>
 *      POST /token HTTP/1.1
 *      Host: server.example.com
 *      Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
 *      Content-Type: application/x-www-form-urlencoded
 *
 *      grant_type=refresh_token&refresh_token=tGzv3JOkF0XG5Qx2TlKWIA
 * </pre>
 * <p>
 * refresh token 主要是为了提升用户体验,  用户不用重复输入账号密码授权给Client
 */
public class RefreshTokenRequest {
    public String grant_type;// REQUIRED.  Value MUST be set to "refresh_token".
    public String refresh_token;// REQUIRED.   The refresh token issued to the client.
    public String scope;// OPTIONAL. space-delimited
}
