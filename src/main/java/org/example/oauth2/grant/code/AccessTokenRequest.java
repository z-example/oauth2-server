package org.example.oauth2.grant.code;

/**
 * <pre>
 * POST /token HTTP/1.1
 * Host: server.example.com
 * Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
 * Content-Type: application/x-www-form-urlencoded
 *
 * grant_type=authorization_code&code=SplxlOBeZQQYbYS6WxSbIA
 * &redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb
 * </pre>
 */
public class AccessTokenRequest {
    public String grant_type;//必须为: authorization_code
    public String code;// 必须
    // 特征: 仅授权码授权模式下, 在获取token时包含可选参数redirect_uri
    // 只有在认证服务器是通过uri回传access_token的情况下才需要传递该参数, 一般情况下认证服务器会直接返回json
    public String redirect_uri;
}
