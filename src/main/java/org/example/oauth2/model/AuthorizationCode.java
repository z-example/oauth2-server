package org.example.oauth2.model;

import java.util.Date;
import java.util.Objects;

/**
 * 授权码是一次性的, 用完即作废
 */
public class AuthorizationCode {
    public String code;
    public String scope;
    public String client_id;
    public String username;//授权用户名
    public Date created_at;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorizationCode that = (AuthorizationCode) o;
        return code.equals(that.code) &&
                client_id.equals(that.client_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, client_id);
    }
}
