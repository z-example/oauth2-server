package org.example.oauth2.server;

/**
 * @author Zero
 * Created on 2020/9/1.
 */
public interface PasswordGrantHandler {
    boolean handle(String username, String password);
}
