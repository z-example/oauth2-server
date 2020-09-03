package org.example.oauth2.store;

import org.example.oauth2.model.AuthorizationCode;

public interface CodeStore {

    public static CodeStore inMemoryStore(){
        return new InMemoryCodeStore();
    }

    void save(AuthorizationCode code);

    AuthorizationCode getCode(String clientId, String code);

    void delete(AuthorizationCode code);

}
