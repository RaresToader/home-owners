package nl.tudelft.sem.template.hoa.utils;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Singleton;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;

import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import java.util.List;


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

    /**
     *
     */
    public static List<MembershipResponseModel> getMembershipsForUser(String memberID, String token) {
        try {
            return client.target(server).path("getMemberships/" + memberID)
                .request(APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Membership id invalid.");
        }
    }

    /**
     *
     */
    public static List<MembershipResponseModel> getActiveMembershipsForUser(String memberID, String token) {
        try {
            return client.target(server).path("getActiveMemberships/" + memberID)
                    .request(APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .accept(APPLICATION_JSON)
                    .get(new GenericType<>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Membership id invalid.");
        }
    }

}

