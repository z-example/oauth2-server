package org.example.oauth2.grant.implicit;

import org.example.oauth2.AccessToken;

/**
 * <pre>
 *      HTTP/1.1 302 Found
 *      Location: http://example.com/cb#access_token=2YotnFZFEjr1zCsicMWpAA
 *                &state=xyz&token_type=example&expires_in=3600
 * </pre>
 */
public class AccessTokenResponse {
    // 没有refresh_token
    public String access_token;//REQUIRED
    public String token_type;//REQUIRED
    public int expires_in;//REQUIRED

    public String scope;//OPTIONAL
    public String state;//REQUIRED
}
