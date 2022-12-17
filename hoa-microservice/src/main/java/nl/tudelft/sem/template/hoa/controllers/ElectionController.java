package nl.tudelft.sem.template.hoa.controllers;

import nl.tudelft.sem.template.hoa.models.ProposalRequestModel;
import nl.tudelft.sem.template.hoa.utils.ElectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/voting")
public class ElectionController {

    /**
     *  Endpoint for creating a proposal
     *
     * @param model the proposal
     * @return The created proposal or bad request
     */
    @PostMapping("/proposal")
    public ResponseEntity<String> createProposal(@RequestBody ProposalRequestModel model) {
        try {
            return ResponseEntity.ok(ElectionUtils.createProposal(model));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create proposal", e);
        }
    }
}
