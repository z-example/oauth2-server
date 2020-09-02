package org.example.oauth2.model;

import org.example.oauth2.AccessToken;

/**
 * @author Zero
 * Created on 2020/9/1.
 */
public class AccessTokenDetails extends AccessToken {
    public AccessToken token;
    public String clientId;// Client ID, APP ID
    public String clientSecret;
}
