package org.example.oauth2.store;

import org.example.oauth2.model.Client;

/**
 * @author Zero
 * Created on 2020/9/1.
 */
public interface ClientStore {

    void save(Client client);

    Client getClient(String clientId);

    public static ClientStore inMemoryStore(){
        return new InMemoryClientsStore();
    }

}
