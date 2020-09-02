package org.example.oauth2.store;

import org.example.oauth2.model.ClientDetails;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zero
 * Created on 2020/9/1.
 */
public abstract class ClientStore {

    public static ClientStore clientStore = new InMemoryClientDetailsStore();


    public static void setClientStore(ClientStore store) {
        clientStore = store;
    }

    public static ClientStore getClientStore() {
        return clientStore;
    }

    public void save(ClientDetails client) {
        clientStore.save(client);
    }

    public ClientDetails getClientDetails(String clientId) {
        if (clientId == null || clientId.isEmpty()) return null;
        return clientStore.getClientDetails(clientId);
    }

    public static class InMemoryClientDetailsStore extends ClientStore {

        private final Map<String, ClientDetails> clients = new HashMap<>();

        @Override
        public void save(ClientDetails client) {
            clients.put(client.clientId, client);
        }

        @Override
        public ClientDetails getClientDetails(String clientId) {
            return clients.get(clientId);
        }
    }

}
