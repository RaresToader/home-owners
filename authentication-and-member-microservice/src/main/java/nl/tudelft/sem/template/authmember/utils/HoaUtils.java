package nl.tudelft.sem.template.authmember.utils;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import nl.tudelft.sem.template.authmember.models.HoaResponseModel;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


/**
 * Utils class to send requests to the HOA microservice.
 */
@Singleton
public class HoaUtils {
    private static final String SERVER = "http://localhost:8084/hoa/";
    private static final ResteasyClient CLIENT = new ResteasyClientBuilder().build();

    /**
     * Calls and endpoint from the HOA microservice, to retrieve that specific HOA, if it exists.
     *
     * @param hoaId the hoa id
     * @return the hoa response model
     */
    public static HoaResponseModel getHoaById(long hoaId, String token) {
        try {
            return CLIENT.target(SERVER).path("getById/" + hoaId)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .get(HoaResponseModel.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
