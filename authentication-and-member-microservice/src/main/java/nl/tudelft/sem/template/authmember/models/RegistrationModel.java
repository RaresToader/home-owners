package nl.tudelft.sem.template.authmember.models;

import lombok.Data;

/**
 * Model representing a registration request.
 */
@Data
public class RegistrationModel {
    private String memberId;
    private String password;
}