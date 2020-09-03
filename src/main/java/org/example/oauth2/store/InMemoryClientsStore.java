package org.example.oauth2.store;

import org.example.oauth2.model.Client;

import java.util.HashMap;
import java.util.Map;

class InMemoryClientsStore implements ClientStore {

    private final Map<String, Client> clients = new HashMap<>();

    public void save(Client client) {
        clients.put(client.clientId, client);
    }

    public Client getClient(String clientId) {
        return clients.get(clientId);
    }
}