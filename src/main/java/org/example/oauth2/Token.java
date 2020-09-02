package org.example.oauth2;

/**
 * @author Zero
 * Created on 2020/8/31.
 */
public class Token {
    public String access_token;
    public String token_type;
    public int expires_in;//3600
    public String refresh_token;//OPTIONAL
    public String scope;//OPTIONAL
}
