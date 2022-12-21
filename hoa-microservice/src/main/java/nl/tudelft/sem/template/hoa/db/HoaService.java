package nl.tudelft.sem.template.hoa.db;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nl.tudelft.sem.template.hoa.domain.Hoa;
import nl.tudelft.sem.template.hoa.exception.BadFormatHoaException;
import nl.tudelft.sem.template.hoa.exception.HoaDoesntExistException;
import nl.tudelft.sem.template.hoa.exception.HoaNameAlreadyTakenException;
import nl.tudelft.sem.template.hoa.models.HoaRequestModel;
import org.springframework.stereotype.Service;

/**
 * A DDD service for hoa-related queries.
 */
@Service
public class HoaService {
    private final transient HoaRepo hoaRepo;

    /**
     * Constructor for the HoaService.
     *
     * @param hoaRepo the hoa repository
     */
    public HoaService(HoaRepo hoaRepo) {
        this.hoaRepo = hoaRepo;
    }

    /**
     * Query to save a Hoa.
     *
     * @param hoa the hoa to be saved
     * @throws HoaNameAlreadyTakenException if the name already exists
     */
    public void saveHoa(Hoa hoa) throws HoaNameAlreadyTakenException {
        List<String> list = hoaRepo.findAll().stream().map(Hoa::getName).collect(Collectors.toList());
        if (list.contains(hoa.getName())) {
            throw new HoaNameAlreadyTakenException("Hoa with name " + hoa.getName() + " is already taken.");
        }
        this.hoaRepo.save(hoa);
    }

    /**
     * Query to retrieve all hoa from the database.
     *
     * @return a list of a hoa's
     */
    public List<Hoa> getAllHoa() {
        return hoaRepo.findAll();
    }

    /**
     * Query to get a hoa by its id.
     *
     * @param id the id of the queried hoa
     * @return the hoa in case it exists
     * @throws HoaDoesntExistException if there does not exist a hoa with the specified id.
     */
    public Hoa getHoaById(long id) throws HoaDoesntExistException {
        Optional<Hoa> hoa = hoaRepo.findById(id);
        if (hoa.isEmpty()) {
            throw new HoaDoesntExistException("Hoa with id " + id + " doesn't exist.");
        }
        return hoa.get();
    }

    /**
     * Method to create a new Hoa.
     *
     * @param request a hoa request model
     * @return the hoa newly created
     * @throws HoaNameAlreadyTakenException thrown if the hoa name already exists
     */
    public Hoa registerHoa(HoaRequestModel request) throws HoaNameAlreadyTakenException, BadFormatHoaException {
        String country = request.getCountry();
        String city = request.getCity();
        String name = request.getName();
        if (countryCheck(country) || countryCheck(city) || !nameCheck(name)) {
            throw new BadFormatHoaException("Wrong format for the country, city, or name!");
        }

        Hoa newHoa = Hoa.createHoa(country, city, name);
        this.saveHoa(newHoa);
        return newHoa;
    }

    /**
     * This method checks that a country/city has the correct format.
     * This means that it must have maximum 50 characters, only letters (only ASCII), whitespaces are admissible,
     * no numbers and the first letter needs to be uppercase. Of course, the string cannot contain only whitespaces.
     *
     * @param country the country or city name
     * @return true if the country name fails to adhere to the format, false otherwise
     */
    public boolean countryCheck(String country) {
        if (country == null || country.isEmpty() || country.isBlank()) {
            return true;
        }
        if (!Character.isUpperCase(country.charAt(0)) || country.length() < 4 || country.length() > 50) {
            return true;
        }
        for (int i = 1; i < country.length(); i++) {
            char c = country.charAt(i);
            if (!Character.isWhitespace(c) && !Character.isLetter(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This is a method that checks whether the format for a name is correct.
     *
     * @param name the name of the hoa
     * @return true if the format is satisfied, false otherwise
     */
    public boolean nameCheck(String name) {
        if (name == null || name.isEmpty() || name.isBlank()) {
            return false;
        }
        if (!Character.isUpperCase(name.charAt(0)) || name.length() > 50) {
            return false;
        }
        return enoughCharsAndWhitespace(name);
    }

    /**
     * Checks that a string has at least 4 characters.
     * Only letters or digits are allowed. The name can have at most 50 characters.
     *
     * @param name the name
     * @return true if the conditions are met, false otherwise.
     */
    public boolean enoughCharsAndWhitespace(String name) {
        if (name == null || name.isEmpty() || name.isBlank()) {
            return false;
        }
        String[] split = name.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(s);
        }
        String result = sb.toString();
        char[] array = result.toCharArray();
        for (Character x : array) {
            if (!Character.isLetterOrDigit(x)) {
                return false;
            }
        }
        return result.length() >= 4;
    }

    /**
     * Checks whether a Hoa exists.
     *
     * @param hoaId the hoaId
     * @return true if the hoa exists, false otherwise
     */
    public boolean findHoaById(long hoaId) {
        return hoaRepo.existsById(hoaId);
    }
}
