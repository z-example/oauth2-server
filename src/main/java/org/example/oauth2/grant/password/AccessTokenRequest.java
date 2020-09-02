package org.example.oauth2.grant.password;

/**
 * <pre>
 *      POST /token HTTP/1.1
 *      Host: server.example.com
 *      Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
 *      Content-Type: application/x-www-form-urlencoded
 *
 *      grant_type=password&username=johndoe&password=A3ddj3w
 * </pre>
 *
 * Authorization: Basic base64(client_id:client_client_secret)
 */
public class AccessTokenRequest {
    public String grant_type;// REQUIRED.  Value MUST be set to "password".
    public String username;// REQUIRED.  The resource owner username.
    public String password;// REQUIRED.  The resource owner password.
    public String scope;// OPTIONAL.
}
