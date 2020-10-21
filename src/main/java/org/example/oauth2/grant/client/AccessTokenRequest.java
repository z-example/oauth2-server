package org.example.oauth2.grant.client;

/**
 * 客户端模式（Client Credentials Grant）指客户端以自己的名义，而不是以用户的名义，向"服务提供商"进行认证。<br/>
 * 严格地说，客户端模式并不属于OAuth框架所要解决的问题。<br/>
 * 在这种模式中，用户直接向客户端注册，客户端以自己的名义要求"服务提供商"提供服务，其实不存在用户授权问题。
 * <p>
 * <p>
 * Client通过自编程调用API获取Token
 *
 * <blockquote><pre>
 *      POST /token HTTP/1.1
 *      Host: server.example.com
 *      Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
 *      Content-Type: application/x-www-form-urlencoded
 *
 *      grant_type=client_credentials
 * </pre></blockquote>
 * Authorization: Basic base64(client_id:client_client_secret)
 */
public class AccessTokenRequest {
    public String grant_type;// REQUIRED.  Value MUST be set to "client_credentials".
    public String scope;// OPTIONAL
}
