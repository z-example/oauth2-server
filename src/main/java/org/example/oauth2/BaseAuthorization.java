package org.example.oauth2;

/**
 * @author Zero
 * Created on 2020/9/1.
 */
public class BaseAuthorization {
    public final String username;
    public final String password;

    public BaseAuthorization(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
