package org.example.oauth2.server;

import java.util.LinkedHashMap;

/**
 * @author Zero
 * Created on 2020/9/4.
 */
public class Model extends LinkedHashMap<String, Object> {
    public Model set(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public Model setMessage(String message) {
        return this.set("message", message);
    }

}
