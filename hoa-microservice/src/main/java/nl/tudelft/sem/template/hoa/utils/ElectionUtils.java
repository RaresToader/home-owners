package nl.tudelft.sem.template.hoa.utils;

import nl.tudelft.sem.template.hoa.annotations.Generated;
import nl.tudelft.sem.template.hoa.models.BoardElectionRequestModel;
import nl.tudelft.sem.template.hoa.models.ProposalRequestModel;
import nl.tudelft.sem.template.hoa.models.RemoveVoteModel;
import nl.tudelft.sem.template.hoa.models.VotingModel;
import org.springframework.http.HttpStatus;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Singleton;
import javax.ws.rs.client.Entity;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Singleton
@Generated // solely contains endpoints, instead of mock-testing for cov, test other microservice's response instead
// + the endpoints are being used for other tests to pass anyway
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
        try {
            return client.target(server).path("proposal/")
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .post(Entity.entity(model, APPLICATION_JSON), Object.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }

    }

    /**
     * Creates a board election using the voting microservice
     *
     * @param model the model for the board election
     * @return the created board election
     */
    public static Object createBoardElection(BoardElectionRequestModel model) {
        try {
            return client.target(server).path("boardElection/")
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .post(Entity.entity(model, APPLICATION_JSON), Object.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    /**
     * Allows the user to vote on an election using the voting microservice
     *
     * @param model the model for vote
     * @return the status of the vote
     */
    public static HttpStatus vote(VotingModel model) {
        try {
            return client.target(server).path("vote/")
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .post(Entity.entity(model, APPLICATION_JSON), HttpStatus.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }


    /**
     * Allows the user to remove his vote on an election using the voting microservice
     *
     * @param model the model for removing a vote
     * @return the status of the removal of the vote
     */
    public static HttpStatus removeVote(RemoveVoteModel model) {
        return client.target(server).path("removeVote/")
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
        try {
            return client.target(server).path("getElection/" + electionId)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .get(Object.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    /**
     * Concludes an election with the given id using the voting microservice
     *
     * @param id the id of election to conclude
     * @return Result of the election
     */
    public static Object concludeElection(int id) {
        try {
            return client.target(server).path("conclude/" + id)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .post(null, Object.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
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
            throw new IllegalArgumentException("Could not join the election as a candidate.");
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