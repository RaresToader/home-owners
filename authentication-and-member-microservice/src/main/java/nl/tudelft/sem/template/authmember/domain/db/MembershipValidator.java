package nl.tudelft.sem.template.authmember.domain.db;

import nl.tudelft.sem.template.authmember.domain.exceptions.BadJoinHoaModelException;
import nl.tudelft.sem.template.authmember.models.JoinHoaModel;

public final class MembershipValidator {

    /**
     * Validates the input for JoinHoaModel
     *
     * @param model JoinHoaModel model to validate
     * @return true if the name satisfies the right format
     */
    public static boolean validate(JoinHoaModel model) {
        return validateCountryCityStreet(model.getAddress().getCity())
                && validateCountryCityStreet(model.getAddress().getCountry())
                && validateCountryCityStreet(model.getAddress().getStreet())
                && validateStreetNumber(model.getAddress().getHouseNumber())
                && validatePostalCode(model.getAddress().getPostalCode());
    }
    
    /**
     * Validates the input for country, city, and street name. It must contain
     * only letters. It must be non-null, non-empty, non-blank and must contain only letters.
     * The first letter must be uppercase.
     *
     * @param name the name
     * @return true if the name satisfies the right format
     */
    public static boolean validateCountryCityStreet(String name) {
        if (name == null || name.isEmpty() || name.isBlank()) {
            return false;
        }
        String trimmed = name.trim();
        if (trimmed.length() < 4 || trimmed.length() > 50) {
            return false;
        }
        if (!Character.isUpperCase(trimmed.charAt(0))) {
            return false;
        }
        for (int i = 1; i < trimmed.length(); i++) {
            if (!Character.isLetter(trimmed.charAt(i)) && !Character.isWhitespace(trimmed.charAt(i))) {
                return false;
            }
        }
        return true;

    }

    /**
     * Method that validates the format of a postal code. It must be of type
     * "DDDDLL", where "D" represents a digit, and "L" a letter.
     *
     * @param postalCode the postal code
     * @return true if the postal code matches the format, false otherwise
     */
    public static boolean validatePostalCode(String postalCode) {
        if (postalCode == null) {
            return false;
        }
        String trimmed = postalCode.trim();
        final int max = 6;
        if (trimmed.length() != max) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            if (!Character.isDigit(trimmed.charAt(i))) {
                return false;
            }
        }
        for (int j = 4; j < 6; j++) {
            if (!Character.isLetter(trimmed.charAt(j))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates the street number provided. Valid street numbers are the following:
     * "80A", "23". Leading and trailing spaces are allowed.
     *
     * @param number the street number
     * @return true if the number satisfies the format, false otherwise
     */
    public static boolean validateStreetNumber(String number) {
        if (number == null || number.isBlank() || number.isEmpty()) {
            return false;
        }
        String trimmed = number.trim();
        for (int i = 0; i < trimmed.length() - 1; i++) {
            if (!Character.isDigit(trimmed.charAt(i))) {
                return false;
            }
        }
        if (!Character.isDigit(trimmed.charAt(trimmed.length() - 1))
                && !Character.isLetter(trimmed.charAt(trimmed.length() - 1))) {
            return false;
        }
        return true;
    }
}
