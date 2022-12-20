package nl.tudelft.sem.template.hoa.utils;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Singleton;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;


/**
 * Utils class that makes API requests to the membership microservice.
 */
@Singleton
public class MembershipUtils {
    private static final String server = "http://localhost:8083/member/";
    private static final ResteasyClient client = new ResteasyClientBuilder().build();

    /**
     * Retrieves a membership (if present) from the membership microservice.
     *
     * @param membershipId the membership id for the membership being queried.
     * @return the membership response model
     */
    public static MembershipResponseModel getMembershipById(long membershipId) {
        try {
            return client.target(server).path("getMembershipById/" + membershipId)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .get(MembershipResponseModel.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Membership id invalid.");
        }
    }

}

