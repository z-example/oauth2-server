package org.example.oauth2;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author Zero
 * Created on 2020/9/1.
 */
public class Helper {

    public static BaseAuthorization readAuthorization(String authorization) {
        byte[] decode = Base64.getDecoder().decode(authorization);
        String auth = new String(decode, StandardCharsets.UTF_8);
        String[] ss = auth.split(":", 2);
        return new BaseAuthorization(ss[0], ss[1]);
    }

}
