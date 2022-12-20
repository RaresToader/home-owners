package nl.tudelft.sem.template.authmember.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing a registration request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationModel {
    private String memberId;
    private String password;
}