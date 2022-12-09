package nl.tudelft.sem.template.hoa.utils;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import org.glassfish.jersey.client.ClientConfig;

/**
 * Utils class that makes API requests to the membership microservice.
 */
public class MembershipUtils {
    private static final String server = "http://localhost:8083/";

    /**
     * Retrieves a membership (if present) from the membership microservice.
     *
     * @param membershipId the membership id for the membership being queried.
     * @return the membership response model
     */
    public MembershipResponseModel getMembershipById(long membershipId) {
        Client client = ClientBuilder.newClient(new ClientConfig());
        return client.target(server).path("member/getMembershipById/" + membershipId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

}

