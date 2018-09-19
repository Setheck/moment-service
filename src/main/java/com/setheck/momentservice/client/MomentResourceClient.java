package com.setheck.momentservice.client;

import com.setheck.momentservice.api.*;
import org.glassfish.jersey.client.*;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.stream.*;

public class MomentResourceClient {
    private final Client client = JerseyClientBuilder.createClient();
    private final String resourceUrl;

    public MomentResourceClient(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public void add(Moment moment) {
        client.target(resourceUrl).request()
                .buildPut(Entity.entity(moment, MediaType.APPLICATION_JSON_TYPE));
    }

    public Moment getById(Integer id) {
        return client.target(resourceUrl + id)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class)
                .readEntity(Moment.class);
    }

    public Moment getRandom() {
        return client.target(resourceUrl + "random")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class)
                .readEntity(Moment.class);
    }

    public void approveByIds(Set<Integer> ids) {
        String idsQuery = ids.stream()
                .map( i -> toString())
                .collect(Collectors.joining(","));

        client.target(resourceUrl + "approve")
                .queryParam("ids", idsQuery)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(null);
    }

    public void delete(Integer id) {
        client.target(resourceUrl + id)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete(ClientResponse.class);
    }
}
