package org.example.oauth2.grant.refresh;

/**
 * <pre>
 *      POST /token HTTP/1.1
 *      Host: server.example.com
 *      Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
 *      Content-Type: application/x-www-form-urlencoded
 *
 *      grant_type=refresh_token&refresh_token=tGzv3JOkF0XG5Qx2TlKWIA
 * </pre>
 */
public class RefreshTokenRequest {
    public String grant_type;// REQUIRED.  Value MUST be set to "refresh_token".
    public String refresh_token;// REQUIRED.   The refresh token issued to the client.
    public String scope;// OPTIONAL. space-delimited
}
