package org.example.oauth2;

/**
 * @author Zero
 * Created on 2020/9/1.
 */
public class AccessToken {
    public String access_token;//REQUIRED
    public String token_type;//REQUIRED
    public int expires_in;//REQUIRED

    public String refresh_token;// Client credentials grant和Implicit grant时必须为空
}
