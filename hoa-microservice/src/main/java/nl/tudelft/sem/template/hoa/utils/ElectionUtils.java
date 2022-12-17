package nl.tudelft.sem.template.hoa.utils;

import nl.tudelft.sem.template.hoa.models.ProposalRequestModel;
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
    public static String createProposal(ProposalRequestModel model) {
        return client.target(server).path("proposal/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(model, APPLICATION_JSON), String.class);
    }
}
