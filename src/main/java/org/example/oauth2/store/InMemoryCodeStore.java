package org.example.oauth2.store;

import org.example.oauth2.model.AuthorizationCode;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Zero
 * Created on 2020/9/2.
 */
public class InMemoryCodeStore implements CodeStore {

    private List<AuthorizationCode> codes = new CopyOnWriteArrayList<>();

    @Override
    public void save(AuthorizationCode code) {
        codes.add(code);
    }

    @Override
    public AuthorizationCode getCode(String clientId, String code) {
        for (AuthorizationCode ac : codes) {
            if (ac.client_id.equals(clientId) && ac.code.equals(code)) {
                return ac;
            }
        }
        return null;
    }

    @Override
    public void delete(AuthorizationCode code) {
        codes.remove(code);
    }

}
