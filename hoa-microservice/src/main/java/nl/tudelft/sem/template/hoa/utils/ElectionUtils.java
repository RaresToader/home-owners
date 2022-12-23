package nl.tudelft.sem.template.hoa.utils;

import nl.tudelft.sem.template.hoa.models.BoardElectionRequestModel;
import nl.tudelft.sem.template.hoa.models.ProposalRequestModel;
import nl.tudelft.sem.template.hoa.models.VotingModel;
import org.springframework.http.HttpStatus;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.inject.Singleton;
import javax.ws.rs.client.Entity;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Singleton
public class ElectionUtils {
    private static final String server = "http://localhost:8085/voting/";
    private static final ResteasyClient client = new ResteasyClientBuilder().build();


    /**
     * Creates a proposal using the voting microservice
     *
     * @param model the model for the proposal
     * @return the created proposal
     */
    public static Object createProposal(ProposalRequestModel model) {
        return client.target(server).path("proposal/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(model, APPLICATION_JSON), Object.class);
    }

    /**
     * Creates a board election using the voting microservice
     *
     * @param model the model for the board election
     * @return the created board election
     */
    public static Object createBoardElection(BoardElectionRequestModel model) {
        return client.target(server).path("boardElection/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(model, APPLICATION_JSON), Object.class);
    }

    /**
     * Creates a board election request that is cyclically gated to this microservice's controller
     * Used for automatic board election creation
     *
     * @param model the model for the board election
     * @return the created board election
     */
    public static Object cyclicCreateBoardElection(BoardElectionRequestModel model) {
        return client.target("http://localhost:8084/voting/").path("boardElection")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(model, APPLICATION_JSON), Object.class);
    }

    /**
     * Allows the user to vote on an election using the voting microservice
     *
     * @param model the model for vote
     * @return the status of the vote
     */
    public static HttpStatus vote(VotingModel model) {
        return client.target(server).path("vote/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(model, APPLICATION_JSON), HttpStatus.class);
    }

    /**
     * Getter for elections using the voting microservice
     *
     * @param electionId the id of the election
     * @return the fetched election
     */
    public static Object getElectionById(int electionId) {
        return client.target(server).path("getElection/" + electionId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Object.class);
    }

    /**
     * Concludes an election with the given id using the voting miccroservice
     *
     * @param id the id of election to conclude
     * @return Result of the election
     */
    public static Object concludeElection(int id) {
        return client.target(server).path("conclude/" + id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(null, Object.class);
    }

    /**
     *
     */
    public static boolean joinElection(String memberID, long hoaID) {
        try {
            return client.target(server).path("joinElection/" + memberID + "/" + hoaID)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .post(null, Boolean.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("The HOA has no running election.");

        }
    }

    /**
     *
     */
    public static boolean leaveElection(String memberID, long hoaID) {
        try {
            return client.target(server).path("leaveElection/" + memberID + "/" + hoaID)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .post(null, Boolean.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("The HOA has no running election or the member did not participate.");

        }
    }
}