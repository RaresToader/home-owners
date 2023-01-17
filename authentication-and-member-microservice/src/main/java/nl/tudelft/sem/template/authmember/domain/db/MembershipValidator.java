package nl.tudelft.sem.template.authmember.domain.db;

import nl.tudelft.sem.template.authmember.models.JoinHoaModel;

public final class MembershipValidator {

    /**
     * Validates the input for JoinHoaModel
     *
     * @param model JoinHoaModel model to validate
     * @return true if the name satisfies the right format
     */
    public static boolean validate(JoinHoaModel model) {
        return validateCountryCityAndStreet(model)
                && validateStreetNumber(model.getAddress().getHouseNumber())
                && validatePostalCode(model.getAddress().getPostalCode());
    }

    /**
     * Validates country, city and street of a membership
     */
    public static boolean validateCountryCityAndStreet(JoinHoaModel model) {
        return validateCountryCityStreet(model.getAddress().getCountry())
            && validateCountryCityStreet(model.getAddress().getCity())
            && validateCountryCityStreet(model.getAddress().getStreet());
    }
    
    /**
     * Validates the input for country, city, and street name. It must contain
     * only letters. It must be non-null, non-empty, non-blank.
     * The first letter must be uppercase.
     *
     * @param name the name
     * @return true if the name satisfies the right format
     */
    public static boolean validateCountryCityStreet(String name) {
        if (checkNullBlankEmpty(name)) {
            return false;
        }
        String trimmed = name.trim();
        return trimmed.matches("[A-Z][A-Za-z\\s]{3,49}");
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
        return trimmed.matches("[0-9]{4}[A-Za-z]{2}");
    }

    /**
     * Validates the street number provided. Valid street numbers are the following:
     * "80A", "23". Leading and trailing spaces are allowed.
     *
     * @param number the street number
     * @return true if the number satisfies the format, false otherwise
     */
    public static boolean validateStreetNumber(String number) {
        if (checkNullBlankEmpty(number)) return false;
        String trimmed = number.trim();
        return trimmed.matches("^[0-9]*[A-Za-z]?$");
    }

    public static boolean checkNullBlankEmpty(String toCheck) {
        return (toCheck == null || toCheck.isBlank() || toCheck.isEmpty());
    }
}
