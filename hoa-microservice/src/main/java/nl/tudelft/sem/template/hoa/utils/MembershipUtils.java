package nl.tudelft.sem.template.hoa.utils;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Singleton;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;

import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


/**
 * Utils class that makes API requests to the membership microservice.
 */
@Singleton
public class MembershipUtils {
    private static final String server = "http://localhost:8083/member/";

    // Definitely not a good way to do it, but it is a simple solution
    private static final String secretClearBoardKey = "Thisisacustomseckeyforclear";

    private static final String secretPromoteKey = "Thisisacustomseckeyforpromotion";

    private static final String invalidMembership = "Membership id invalid.";

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
            throw new IllegalArgumentException(invalidMembership);
        }
    }

    /**
     * Client for fetching all memberships (including history) of a user
     *
     * @param memberID id of member to consider
     * @param token    Authorization token used for validation
     * @return List of memberships, if any exists
     */
    public static List<MembershipResponseModel> getMembershipsForUser(String memberID, String token) {
        try {
            return client.target(server).path("getMemberships/" + memberID)
                    .request(APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .accept(APPLICATION_JSON)
                    .get(new GenericType<>() {
                    });
        } catch (Exception e) {
            throw new IllegalArgumentException(invalidMembership);
        }
    }

    /**
     * Client for fetching all active memberships of a user
     *
     * @param memberID id of member to consider
     * @param token    Authorization token used for validation
     * @return List of memberships, if any exists
     */
    public static List<MembershipResponseModel> getActiveMembershipsForUser(String memberID, String token) {
        try {
            return client.target(server).path("getActiveMemberships/" + memberID)
                    .request(APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .accept(APPLICATION_JSON)
                    .get(new GenericType<>() {
                    });
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Client for fetching all active memberships of an HOA
     *
     * @param hoaId id of hoa to consider
     * @param token Authorization token used for validation
     * @return List of memberships, if any exists
     */
    public static List<MembershipResponseModel> getActiveMembershipsOfHoa(Long hoaId, String token) {
        try {
            return client.target(server).path("getAllMemberships/" + hoaId)
                    .request(APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .accept(APPLICATION_JSON)
                    .get(new GenericType<>() {
                    });
        } catch (Exception e) {
            throw new IllegalArgumentException(invalidMembership);
        }
    }

    /**
     * Client for clearing a board of a HOA
     *
     * @param hoaId id of HOA to consider
     */
    public static void resetBoard(Long hoaId) {
        try {
            boolean res = client.target(server).path("resetBoard/" + hoaId)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .post(Entity.entity(secretClearBoardKey, APPLICATION_JSON), Boolean.class);
            if (!res) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Board could not be reset.");
        } catch (Exception e) {
            throw new IllegalArgumentException(invalidMembership);
        }
    }

    /**
     * Client for promoting winners of a board election
     *
     * @param result List of winners
     * @param hoaId  id of HOA to consider
     */
    public static void promoteWinners(Object result, Long hoaId) {
        try {
            boolean res = client.target(server).path("promoteWinners/" + hoaId + "/" + secretPromoteKey)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .post(Entity.entity(result, APPLICATION_JSON), Boolean.class);
            if (!res) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not promote winners");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid operation");
        }
    }
}

